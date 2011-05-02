/**
 * 
 */
package tamago.csp.stringbuilder;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import tamago.csp.Backtracking;
import tamago.csp.TamagoCSP;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constant.CSPnull;
import tamago.csp.constant.CSPstring;
import tamago.csp.constraint.EqLinear;
import tamago.csp.constraint.XEqC;
import tamago.csp.constraint.XEqY;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.stringbuilder.automaton.SAnyLetter;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.stringbuilder.automaton.SAutoFusion;
import tamago.csp.var.Intvar;
import tamagocc.exception.TamagoCCException;

/**
 * @author Hakim Belhaouari
 *
 */
public class MainStringvar implements Stringvar, CSPrepercussion {

	protected boolean canbenull;
	protected String name;
	protected TreeSet<String> badwords;

	protected CSPconst value;
	protected Backtracking b;
	protected ArrayList<CSPConstraint> constraints;
	protected boolean fixed;

	protected SAuto regexp;
	protected StringIntvar size;
	protected ArrayList<Stringvar> depends;

	protected transient SAuto cacheA;
	protected transient SAuto cacheR;

	protected boolean mustinstantiate;
	private ArrayList<CSPrepercussion> repercussions;

	/**
	 * 
	 */
	public MainStringvar(String name,Backtracking b,boolean canbenull) {
		this.canbenull = canbenull;
		this.b = b;
		this.name = name;
		constraints = new ArrayList<CSPConstraint>();
		fixed = false;
		value = null;
		badwords = new TreeSet<String>();
		regexp = SAuto.motifChar(new SAnyLetter());
		regexp = SAuto.motifRepeat(regexp);
		size = new StringIntvar(this,b);
		depends = new ArrayList<Stringvar>();
		mustinstantiate = true;
		repercussions = new ArrayList<CSPrepercussion>(1);
		size.setMustInstantiate(false);
	}

	/**
	 * 
	 */
	public MainStringvar(String name,Backtracking b) {
		this(name,b,true);
	}


	public StringIntvar getIntvar() {
		return size;
	}

	/**
	 * @see tamago.csp.generic.Obvar#canBeNull()
	 */
	public boolean canBeNull() {
		return canbenull;
	}

	public void updateSubString(boolean affect) throws TamagoCSPException {
		for (CSPrepercussion rep : repercussions) {
			rep.updateDomain(this);
		}
		for (Stringvar var : depends) {
			var.updateSubString(affect);
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#count()
	 */
	public int count() {
		return constraints.size();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		for (CSPConstraint constraint : constraints) {
			constraint.filter();
		}
		updateSubString(false);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	public void fix(CSPconst n) throws TamagoCSPException {
		if(fixed) {
			if(n.stringValue().equals(value.stringValue()))
				return; // nothing to do
			else
				throw new TamagoCSPException(getName()+" - FIX value already done for another value");
		}

		if(isInDomain(n)) {
			StringTriplet<String> st = new StringTriplet<String>(false,this,n.stringValue());
			StringTriplet<SAuto> stbis = new StringTriplet<SAuto>(false,this,regexp);
			size.fix(new CSPinteger(n.stringValue().length())); // on fixe la taille pour voir si c possible
			regexp = SAuto.convertStringToAuto(n.stringValue());
			value = new CSPstring(n.stringValue());
			b.push(st);
			b.push(stbis);
			fixed = true;
			filter();

		}
		else{
			StringBuilder sb = new StringBuilder(toString());
			if(value != null) {
				sb.append(" - current value : ");
				sb.append(value.toString());
			}
			sb.append(" - FIX value not possible : ");
			sb.append(n.stringValue());
			throw new TamagoCSPException(sb.toString());
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getConstraint()
	 */
	public Iterable<CSPConstraint> getConstraint() {
		return constraints;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getValue()
	 */
	public CSPconst getValue() {
		if(!isFixed())
			throw new TamagoCSPRuntime("variable not fixed");
		return value;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#install(tamago.csp.generic.CSPConstraint)
	 */
	public void install(CSPConstraint c) {
		constraints.add(c);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		if(isFixed())
			return;

		StringTriplet<String> st = new StringTriplet<String>(true,this,"");
		this.b.push(st);

		if(!size.isFixed())
			size.forward();
		ArrayList<Stringvar> clonedepends = new ArrayList<Stringvar>(depends);
		Random r = new Random();
		while (clonedepends.size() > 0) {
			int pos = r.nextInt(clonedepends.size());
			Stringvar var = clonedepends.get(pos);
			clonedepends.remove(pos);
			if(!var.isFixed()) {
				var.forward();
			}
		}

		updateSubString(true);
		if(regexp.getFinals().size() == 0)
			throw new TamagoCSPException(getName() +" - No final state in this regexp");
		String f = SAuto.searchRandomWord(regexp);

		StringTriplet<SAuto> stbis = new StringTriplet<SAuto>(false,this,regexp);
		regexp = SAuto.convertStringToAuto(f);
		value = new CSPstring(f);
		b.push(stbis);
		fixed = true;
		filter();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		if(badwords.contains(o.stringValue()))
			return false;
		else {
			return size.isInDomain(new CSPinteger(o.stringValue().length())) && regexp.isAccepted(o.stringValue());
		}
	}


	public void fusion(SAuto auto) throws TamagoCSPException {
		if(isFixed()) {
			if(canfusion(auto))
				return;
		}
		try {
			
			// tester si la longueur des mots de auto est compatible 
			// avec la taille courante
			SAuto nregexp;
			if(cacheA != null && auto==cacheA)
				nregexp = cacheR;
			else
				nregexp = SAutoFusion.fusion(regexp, auto);
			/*try {
				int potmin = SAuto.evalMinSize(nregexp);
				int potmax = SAuto.evalMaxSize(nregexp);
				if(size.isInDomain(new CSPinteger(potmin)))
					size.setMinCore(potmin);
				if(size.isInDomain(new CSPinteger(potmax)))
					size.setMaxCore(potmax);
			}
			catch(Exception e) {
				TamagoCCLogger.infoln(3, "Can't eval size of the automaton\n");
				throw e;
			}*/
			/*if(this.getName().equals("tab[idx___internal_tabrange0[idx3]]") || getName().equals("tab[idx_(__internal_tabrange0[idx2] + 1)]")) {
				new SPrintAutomaton(regexp,getName());
				new SPrintAutomaton(auto,"argument");
				new SPrintAutomaton(nregexp,"fusion");
			}*/
			// Ajout de la sauvegarde du regexp au cas ou il y a un probleme
			setRegExp(nregexp);
			/*StringTriplet<SAuto> stbis = new StringTriplet<SAuto>(false,this,regexp);
			b.push(stbis);
			regexp = nregexp;*/
			// on doit voir si la taille peut etre deduite ...
		} catch (Exception e) {
			throw new TamagoCSPException(e);
		}
	}

	public SAuto getFusion(SAuto auto) {
		try {
			if(cacheA != null && cacheA == auto)
				return cacheR;
			else {
				SAuto nregexp = SAutoFusion.fusion(regexp, auto);
				cacheA = auto;
				cacheR = nregexp;
				return nregexp;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public boolean canfusion(SAuto auto) {
		try {
			if((cacheA != null) && (cacheA == auto))
				return true;
			else {
				cacheR = SAutoFusion.fusion(regexp, auto);
				cacheA = auto;
				/*if(this.getName().equals("tab[idx___internal_tabrange0[idx3]]") || getName().equals("tab[idx_(__internal_tabrange0[idx2] + 1)]")) {
					new SPrintAutomaton(regexp,getName());
					new SPrintAutomaton(auto,"argument");
					new SPrintAutomaton(cacheR);
				}*/
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public SAuto getRegExp() {
		return regexp;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isPrimitive()
	 */
	public boolean isPrimitive() {
		return true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#remove(tamago.csp.generic.CSPconst)
	 */
	public void remove(CSPconst value) throws TamagoCSPException {
		badwords.add(value.stringValue());
	}

	/**
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	@SuppressWarnings("unchecked")
	public void retrieve(Triplet triplet) {
		StringTriplet st = (StringTriplet)triplet;
		if(st.isForward()) {
			// managed by the CSP
			// rien a faire on avait mis une chaine vide inutile
			fixed = false;
		}
		else {
			if(st.value() instanceof String)
				badwords.remove(st.value());
			else {
				this.regexp = (SAuto) st.value();
			}
		}

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if(isFixed()) {
			sb.append("FIXED ");
			sb.append(getValue().stringValue());
		}
		else {
			sb.append(" { ");
			sb.append(size.toString());
			sb.append(" } ");
		}
		return sb.toString();
	}


	public static void main(String args[]) {
		TamagoCSP csp = new TamagoCSP();
		MainStringvar v = new MainStringvar("str",csp.getBacktrack());
		Intvar taille = new Intvar("taille",csp.getBacktrack());
		CSPConstraint c = new XEqY(v.getIntvar(),taille);
		csp.addVariable(taille);
		csp.addVariable(v);
		csp.addConstraint(new XEqC(taille,new CSPinteger(34)));
		csp.addConstraint(c);
		// on ajoute une autre contrainte lie au substring

		int deb = 0;
		int fin = 2;

		SubStringvar sv = new SubStringvar("str#substring",csp.getBacktrack(),v,
				new InferAutoConst(new CSPinteger(deb),csp.getBacktrack()),
				new InferAutoConst(new CSPinteger(fin),csp.getBacktrack()));
		v.addDepends(sv);
		EqLinear eql = new EqLinear();
		eql.put(new CSPinteger(-1), new Intvar(""+deb,csp.getBacktrack(),deb,deb));
		eql.put(new CSPinteger( 1), new Intvar(""+fin,csp.getBacktrack(),fin,fin));
		eql.put(new CSPinteger(-1), sv.getIntvar());
		csp.addConstraint(eql);
		csp.addConstraint(new XEqC(sv,new CSPstring("FR")));

		try {
			csp.solve();
		} catch (TamagoCSPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(csp.toString());
	}

	public void addDepends(Stringvar sv) {
		depends.add(sv);	
		sv.setMustInstantiate(false);
	}

	public boolean mustInstantiate() {
		return true;
		//return mustinstantiate;
	}

	public void setBacktrack(Backtracking b) {
		if(this.b != b) {
			this.b = b;
			size.setBacktrack(b);
			for (Stringvar sv: depends) {
				sv.setBacktrack(b);
			}
		}
	}

	public void setMustInstantiate(boolean b) {
		
		mustinstantiate = true;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public void clearDepends() {
		for (Stringvar var : depends) {
			var.clearDepends();
		}
		depends.clear();
	}

	public void forceNotNull() throws TamagoCCException {
		canbenull = false;
	}

	public void forceNull() throws TamagoCCException {
		if(fixed) {
			if(value instanceof CSPnull)
				return;
			else
				throw new TamagoCCException("Force Null pointer is not possible");
		}
		else {
			if(canBeNull()) {
				if(depends.size() > 0) {
					throw new TamagoCCException("Dependance implies an incoherence in calling object");
				}
				StringTriplet<String> st = new StringTriplet<String>(true,this,null);
				try {
					size.fix(new CSPinteger(0)); // on fixe la taille pour voir si c possible
					value = new CSPnull();
					b.push(st);
					fixed = true;
					filter();
				}
				catch(TamagoCSPException ex) {
					throw new TamagoCCException(ex);
				}
			}
			else
				throw new TamagoCCException("This object can not be null");
		}
	}

	public void uninstallAllConstraints() {
		constraints.clear();
		for (Stringvar sv : depends) {
			sv.uninstallAllConstraints();
		}
		clearDepends();
	}

	public void uninstall(CSPConstraint c) {
		constraints.remove(c);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#addRepercussion(tamago.csp.generic.CSPrepercussion)
	 */
	public void addRepercussion(CSPrepercussion rep) {
		repercussions.add(rep);		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#removeAllRepercussiions()
	 */
	public void removeAllRepercussiions() {
		repercussions.clear();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#removeRepercussion(tamago.csp.generic.CSPrepercussion)
	 */
	public void removeRepercussion(CSPrepercussion rep) {
		repercussions.remove(rep);		
	}
	
	public Iterable<CSPrepercussion> getRepercussions() {
		return repercussions;
	}

	protected boolean updating;
	
	public void updateDomain(CSPvar v) throws TamagoCSPException {
		if(updating)
			return;
		try {
			updating = true;
			if(v instanceof MainStringvar) {
				MainStringvar m = (MainStringvar)v;
				if(canfusion(m.getRegExp()))
					fusion(m.getRegExp());
				else
					throw new TamagoCSPException(this.getName()+" Update Domain with FSM "+m.getName());
			}
			// on propage la mise a jour
			for (Stringvar var : depends) {
				if(var != v)
					var.updateSubString(false);
			}	
		}
		finally {
			updating = false;
		}
	}

	public void equality(CSPvar v) throws TamagoCSPException {
		if(v instanceof Stringvar) {
			Stringvar sv = (Stringvar)v;
			size.setMax(sv.getIntvar().getMax());
			sv.getIntvar().setMax(size.getMax());
			
			size.setMin(sv.getIntvar().getMin());
			sv.getIntvar().setMin(size.getMin());
			
			SAuto auto = getFusion(sv.getRegExp());
			System.out.println("toto");
			if(auto != null) {
				sv.setRegExp(auto);
				setRegExp(auto);
				filter();
				sv.filter();
			}
			else
				throw new TamagoCSPException("Equality impossible between 2 strings");
		}
	}

	public CSPAbstractDomain getAbstractDomain() {
		return null;
	}

	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRegExp(SAuto auto) {
		if(!SAutoFusion.isBisim(getRegExp(), auto)) {
			StringTriplet<SAuto> stbis = new StringTriplet<SAuto>(false,this,regexp);
			b.push(stbis);
			this.regexp = auto;
		}
	}
	
	/*public void updateOtherString(Stringvar sv) throws TamagoCSPException {
		for (Stringvar v : depends) {
			if(sv != v)
				v.updateSubString(false);
		}
	}*/
}
