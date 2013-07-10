/**
 * 
 */
package tamago.csp.stringbuilder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.TreeSet;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.stringbuilder.automaton.SAnyLetter;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.stringbuilder.automaton.SGenericLetter;
import tamago.csp.stringbuilder.automaton.SGreaterLetter;
import tamago.csp.stringbuilder.automaton.SLessLetter;
import tamago.csp.stringbuilder.automaton.SLetter;
import tamago.csp.stringbuilder.automaton.SLetterType;
import tamago.csp.stringbuilder.automaton.SPrintAutomaton;
import tamago.csp.stringbuilder.automaton.SState;
import tamago.csp.stringbuilder.automaton.STransition;
import tamago.csp.var.Intvar;
import tamagocc.exception.TamagoCCException;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.NilCollection;
import tamagocc.util.Pair;

/**
 * @author Hakim Belhaouari
 *
 */
public class CompareStringvar implements Stringvar, CSPrepercussion {
	private Intvar compare;
	private Stringvar parent;

	private Stringvar argument;
	private String name;
	@SuppressWarnings("unused")
	private Backtracking b;

	private ArrayList<CSPrepercussion> repercussions;

	private boolean filterComparable;
	private boolean filterArgument;
	private boolean filterParent;
	//private boolean filtrage;

	/**
	 * 
	 */
	public CompareStringvar(String name,Backtracking b,Stringvar parent, Stringvar argument) {
		this.parent = parent;
		this.argument = argument;
		this.compare = new Intvar(name,b);
		this.compare.addRepercussion(this);
		this.name = name;
		this.b = b;
		repercussions = new ArrayList<CSPrepercussion>(1);
		this.argument.addRepercussion(this);
		compare.setMustInstantiate(false);
	}


	public Intvar getCompareTo() {
		return compare;
	}


	/**
	 * @see tamago.csp.stringbuilder.Stringvar#addDepends(tamago.csp.stringbuilder.Stringvar)
	 */
	public void addDepends(Stringvar sv) {
	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#canfusion(tamago.csp.stringbuilder.automaton.SAuto)
	 */
	public boolean canfusion(SAuto auto) {
		return false;
	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#clearDepends()
	 */
	public void clearDepends() {

	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#fusion(tamago.csp.stringbuilder.automaton.SAuto)
	 */
	public void fusion(SAuto auto) throws TamagoCSPException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#getFusion(tamago.csp.stringbuilder.automaton.SAuto)
	 */
	public SAuto getFusion(SAuto auto) throws TamagoCSPException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#getIntvar()
	 */
	public StringIntvar getIntvar() {
		return parent.getIntvar();
	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#getRegExp()
	 */
	public SAuto getRegExp() {
		return null;
	}



	/**
	 * @see tamago.csp.generic.Obvar#canBeNull()
	 */
	public boolean canBeNull() {
		return parent.canBeNull();
	}

	/**
	 * @see tamago.csp.generic.Obvar#forceNotNull()
	 */
	public void forceNotNull() throws TamagoCCException {
		parent.forceNotNull();
	}

	/**
	 * @see tamago.csp.generic.Obvar#forceNull()
	 */
	public void forceNull() throws TamagoCCException {
		parent.forceNull();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#count()
	 */
	public int count() {
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		
		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	public void fix(CSPconst n) throws TamagoCSPException {
		throw new TamagoCSPException("CompareStringvar: cannot be fixed directly");
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getConstraint()
	 */
	public Iterable<CSPConstraint> getConstraint() {
		return new NilCollection<CSPConstraint>();
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
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#install(tamago.csp.generic.CSPConstraint)
	 */
	public void install(CSPConstraint c) {

	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		this.compare.forward();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isPrimitive()
	 */
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#mustInstantiate()
	 */
	public boolean mustInstantiate() {
		return true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#remove(tamago.csp.generic.CSPconst)
	 */
	public void remove(CSPconst value) throws TamagoCSPException {
	}

	/**
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	public void retrieve(Triplet triplet) {

	}

	/**
	 * @see tamago.csp.generic.CSPvar#setBacktrack(tamago.csp.Backtracking)
	 */
	public void setBacktrack(Backtracking b) {
		this.b = b;
		this.compare.setBacktrack(b);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#setMustInstantiate(boolean)
	 */
	public void setMustInstantiate(boolean b) {
	}

	/**
	 * @see tamago.csp.generic.CSPvar#uninstall(tamago.csp.generic.CSPConstraint)
	 */
	public void uninstall(CSPConstraint c) {
	}

	/**
	 * @see tamago.csp.generic.CSPvar#uninstallAllConstraints()
	 */
	public void uninstallAllConstraints() {
	}


	private static SAuto prepare(SAuto regexp, SLetterType type, boolean inclusive) throws TamagoCSPException {
		SAuto renvoie = new SAuto();
		SState init = renvoie.creatState();
		SState fin = renvoie.creatState();
		renvoie.addFinalState(fin);

		new STransition(fin,fin,new SAnyLetter()); // LOOP on the last element for padding right
		// debut du parcours
		SState state = regexp.init();

		TreeSet<SState> visited = new TreeSet<SState>();
		Stack<Pair<SState,SState>> pile = new Stack<Pair<SState,SState>>();
		Hashtable<SState, SState> map = new Hashtable<SState, SState>();

		pile.push(new Pair<SState, SState>(state,init));
		map.put(state, init);

		while(pile.size() > 0) {
			Pair<SState,SState> ss = pile.pop();
			SState cur = ss.l();
			SState curm = ss.r();

			if(!visited.contains(cur)) {
				visited.add(cur);
				for (STransition trans : SAuto.getAccessibleTransition(cur)) {
					SState next = trans.getEnd();
					SState nextm = null;
					if(map.containsKey(next))
						nextm = map.get(next);
					else {
						nextm = renvoie.creatState();
						map.put(next, nextm);
					}
					pile.push(new Pair<SState, SState>(next,nextm));

					SLetter sletter = trans.getLetter();
					char car = sletter.select();
					switch(type) {
					case DOWN: { // on doit chercher la lettre la plus haute (borne inf)
						new STransition(curm,nextm,new SGenericLetter(""+car));
						SGreaterLetter sgl = null;
						if(inclusive)
							sgl = new SGreaterLetter(car);
						else
							sgl = new SGreaterLetter(((char)(++car)));
						new STransition(curm,fin,sgl);
						break;
					}
					case TOP: { // on doit chercher la lettre la plus basse (borne sup)
						new STransition(curm,nextm,new SGenericLetter(""+car));
						SLessLetter sgl = null;
						if(inclusive)
							sgl = new SLessLetter(car);
						else
							sgl = new SLessLetter(((char)(--car)));
						new STransition(curm,fin,sgl);
						break;
					}
					default:
						throw new TamagoCSPException("CompareTo: error unknown letter");
					}
				}// end for
			} // end if
		}

		if(TamagoCCLogger.getLevel() >= 6) {
			try {
				new SPrintAutomaton(renvoie,"preparation");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return renvoie;
	}


	private boolean checkOrForceParentArgument() throws TamagoCSPException {
		boolean res = false;
		try {
			if(TamagoCCLogger.getLevel() >= 6) {
				long fig = TamagoCCLogger.getUniqueID();
				TamagoCCLogger.println(4, "p:"+parent.getName()+" -> fenetre: "+fig);
				new SPrintAutomaton(parent.getRegExp(),"p:"+parent.getName()+" -> fenetre: "+fig);
				fig = TamagoCCLogger.getUniqueID();
				TamagoCCLogger.println(4, "a:"+argument.getName()+" -> fenetre: "+fig);
				new SPrintAutomaton(argument.getRegExp(),"a:"+argument.getName()+" -> fenetre: "+fig);
			}
			TamagoCCLogger.println(4, compare.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if(parent.isFixed()) {
			if((compare.isFixed() && (compare.getMin().intValue() == 0))) {
				if(argument.canfusion(parent.getRegExp())) {
					argument.fusion(parent.getRegExp());
					res = true;
				}
				else
					throw new TamagoCSPException("String::compareTo equality fails");
			}
			else if(compare.getMax().intValue() < 0) {
				SAuto regexp = prepare(parent.getRegExp(), SLetterType.DOWN,false);
				if(argument.canfusion(regexp)) { 
					argument.fusion(regexp);
					res = true;
				}
				else {
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMin().intValue() > 0) {
				SAuto regexp = prepare(parent.getRegExp(), SLetterType.TOP,false);
				if(argument.canfusion(regexp)) {
					argument.fusion(regexp);
					res = true;
				}
				else
				{
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMax().intValue() <= 0) {
				SAuto regexp = prepare(parent.getRegExp(), SLetterType.DOWN,true);
				if(argument.canfusion(regexp)) {
					argument.fusion(regexp);
					res = true;
				}
				else {
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMin().intValue() >= 0) {
				SAuto regexp = prepare(parent.getRegExp(), SLetterType.TOP,true);
				if(argument.canfusion(regexp)) {
					argument.fusion(regexp);
					res = true;
				}
				else
				{
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
		}
		else {
			TamagoCCLogger.println(4, "p:"+parent.getName()+" is not fixed");
		}
		return res;
	}

	private boolean checkOrForceArgumentParent() throws TamagoCSPException {
		boolean res = false;
		try {
			if(TamagoCCLogger.getLevel() >= 6) {
				long fig = TamagoCCLogger.getUniqueID();
				TamagoCCLogger.println(4, "a:"+argument.getName()+" -> fenetre: "+fig);
				new SPrintAutomaton(argument.getRegExp(),"a:"+argument.getName()+" -> fenetre: "+fig);
				fig = TamagoCCLogger.getUniqueID();
				TamagoCCLogger.println(4, "p:"+parent.getName()+" -> fenetre: "+fig);
				new SPrintAutomaton(parent.getRegExp(),"p:"+parent.getName()+" -> fenetre: "+fig);
			}
			TamagoCCLogger.println(4, compare.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if(argument.isFixed()) {
			if((compare.isFixed() && (compare.getMin().intValue() == 0))) {
				if(parent.canfusion(argument.getRegExp())) {
					parent.fusion(argument.getRegExp());
					res = true;
				}
				else
					throw new TamagoCSPException("String::compareTo equality fails");
			}
			else if(compare.getMax().intValue() < 0) {
				SAuto regexp = prepare(argument.getRegExp(), SLetterType.TOP,false);
				if(parent.canfusion(regexp)) {
					parent.fusion(regexp);
					res = true;
				}
				else {
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMin().intValue() > 0) {
				SAuto regexp = prepare(argument.getRegExp(), SLetterType.DOWN,false);
				if(parent.canfusion(regexp)) {
					parent.fusion(regexp);
					res = true;
				}
				else
				{
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMax().intValue() <= 0) {
				SAuto regexp = prepare(argument.getRegExp(), SLetterType.TOP,true);
				if(parent.canfusion(regexp)) {
					parent.fusion(regexp);
					if(TamagoCCLogger.getLevel() >= 6) {
						try {
							new SPrintAutomaton(parent.getRegExp(),"p: entity apres modif");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else {
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
			else if(compare.getMin().intValue() >= 0) {
				SAuto regexp = prepare(argument.getRegExp(), SLetterType.DOWN,true);
				if(parent.canfusion(regexp)) {
					parent.fusion(regexp);
					res = true;
				}
				else
				{
					throw new TamagoCSPException("String::compareTo: wrong instancies");
				}
			}
		}
		else {
			TamagoCCLogger.println(4, "a:"+argument.getName()+" is not fixed");
		}
		return res;
	}


	/**
	 * @throws TamagoCSPException 
	 * @see tamago.csp.generic.CSPrepercussion#updateDomain(tamago.csp.generic.CSPvar)
	 */
	public void updateDomain(CSPvar v) throws TamagoCSPException {
		// une modification de l'entier impose un changement dans le
		if(filterArgument || filterComparable || filterParent)
			return; // on evite les cycles
		if(v == this.compare) {
			if(filterComparable)
				return;
			try {
				filterComparable = true;
				checkOrForceArgumentParent();
				checkOrForceParentArgument();
			}
			finally {
				filter();
				filterComparable = false;
			}
		}
		else if(v == this.argument) {
			if(filterArgument)
				return;
			try {
				filterArgument = true;
				checkOrForceArgumentParent();
			}
			finally {
				filter();
				filterArgument = false;
			}
		}

	}

	/**
	 * @see tamago.csp.stringbuilder.Stringvar#updateSubString(boolean)
	 */
	public void updateSubString(boolean affect) throws TamagoCSPException {
		if(filterArgument || filterComparable || filterParent)
			return; // on evite les cycles
		
		// TODO Auto-generated method stub
		// verifier si on est toujours en coherence avec la valeur de compareTo
		// cad si positif alors on est plus grand que le regexp argument,
		// si negatif alors on est plus petit que le regexp argument
		// si null alors on est egal aux regexp de argument(on doit d'ailleurs fixer la valeur).
		if(filterParent)
			return;
		try {
			filterParent = true;
			checkOrForceParentArgument();
		}
		finally {
			filter();
			filterParent = false;
		}


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


	public static void main(String args[]) {
		SAuto s = SAuto.convertStringToAuto("bcde");
		try {
			new SPrintAutomaton(s,"bcde");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			SAuto q = prepare(s, SLetterType.TOP,false);
			new SPrintAutomaton(q,"preparation");
		} catch (TamagoCSPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}


	public Iterable<CSPrepercussion> getRepercussions() {
		return repercussions;
	}


	public void equality(CSPvar v) throws TamagoCSPException {
		// TODO Auto-generated method stub
		
	}


	public CSPAbstractDomain getAbstractDomain() {
		return null;
	}


	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}


	public void setRegExp(SAuto auto) {
		// TODO Auto-generated method stub
		
	}
}
