/**
 * 
 */
package tamago.csp.array;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoBuilderFactory;
import tamago.builder.impl.Builderarray;
import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.constant.CSParray;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constraint.QuantifierConstraint;
import tamago.csp.constraint.XEqY;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.CSPvariableForQuantifier;
import tamago.csp.generic.DefaultCSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.var.Intvar;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.Pair;

/**
 * @author Hakim Belhaouari
 *
 */
public class Arrayvar extends DefaultCSPvar implements CSPvar,CSPrepercussion, CSPvariableForQuantifier {
	private Intvar size;
	private ArrayList<Pair<FDvar, TamagoBuilder>> vars;
	private TamagoBuilderFactory factory;
	private boolean fixed;
	private CSParray value;

	private Builderarray owner;
	private ArrayList<QuantifierConstraint> quantifierconstraint;


	public void putElem() throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, TamagoCSPException {
		ArrayIndexvar idx = new ArrayIndexvar(size,b);
		putElem(idx);
	}
	
	public void putElem(FDvar idx) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TamagoCSPException {
		TamagoBuilder tb = factory.searchBuilder(null, name+"["+idx.getName()+"]", owner.getType().getArrayType(), b);
		putElem(idx,tb);
	}
	
	public void putElem(FDvar idx, TamagoBuilder tb) throws TamagoCSPException {
		vars.add(new Pair<FDvar, TamagoBuilder>(idx,tb));
		idx.addRepercussion(this);
		for(QuantifierConstraint quant : quantifierconstraint) {
			quant.addVariable(idx, tb);
		}

		try {
			tb.getCSPvar(null, null).setMustInstantiate(false);
		} catch (TamagoBuilderException e1) {
			TamagoCCLogger.println(3, "*Warning* I can not disable the instantiation for item in array. Error may occur in the output");
		}
		try {
			idx.filter();
		}
		catch(Exception e) {

		}
		finally {
			try {
				updated();
				fixed = false;
			} catch (TamagoCSPException e) {

			}
		}
	}

	public Iterable<Pair<FDvar,TamagoBuilder>> getElements() {
		return vars;
	}

	/**
	 * @see tamago.csp.array.CSPvariableForQuantifier#addQuantifierConstraint(tamago.csp.constraint.QuantifierConstraint)
	 */
	public void addQuantifierConstraint(QuantifierConstraint quant) {
		quantifierconstraint.add(quant);
	}

	public TamagoBuilder getElem(FDvar idx) {
		for (Pair<FDvar,TamagoBuilder> item : vars) {
			if(item.l() == idx)
				return item.r();
		}
		throw new TamagoCSPRuntime("Unkown element in the array");
	}

	public TamagoBuilder getElemByName(String name) {
		for (Pair<FDvar,TamagoBuilder> item : vars) {
			if(item.l().getName().equals(name))
				return item.r();
		}
		throw new TamagoCSPRuntime("Unkown name in the array: "+name);
	}

	/**
	 * 
	 */
	public Arrayvar(Builderarray owner,TamagoBuilderFactory factory, String name,Backtracking b) {
		super(name,b);
		size = new Intvar(name+"[].length",b,0,Intvar.MAXINT);
		size.addRepercussion(this);
		vars = new ArrayList<Pair<FDvar,TamagoBuilder>>();
		this.factory = factory;
		fixed = false;
		constraints = new ArrayList<CSPConstraint>();
		value = null;
		this.owner = owner;
		quantifierconstraint = new ArrayList<QuantifierConstraint>();
	}

	public Intvar getLength() {
		return size;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		updated();
		for (CSPConstraint cons : constraints) {
			cons.filter();
		}

		for (Pair<FDvar,TamagoBuilder> item : vars) {
			item.r().getCSPvar().filter();
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	public void fix(CSPconst n) throws TamagoCSPException {
		TamagoCCLogger.println(4,"Array operator FIX not yet implemented");
		throw new TamagoCSPException("Array: not yet implemented");
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

	public void setName(String name) {
		this.name = name;
		size.setName(name+"[].length");
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
		
		if(!size.isFixed())
			size.forward();
		
		ArrayList<Integer> fillpositions = new ArrayList<Integer>();
		ArrayList<Pair<FDvar, TamagoBuilder>> clonevars = new ArrayList<Pair<FDvar,TamagoBuilder>>(vars);
		Random r = new Random();
		while(clonevars.size() > 0) {
			int pos = r.nextInt(clonevars.size());
			Pair<FDvar, TamagoBuilder> pair = clonevars.get(pos);
			clonevars.remove(pos);
			if(!pair.l().isFixed())
				pair.l().forward();
			fillpositions.add(pair.l().getValue().intValue());
		}

		// now we fulfill the array for empty positions
		for(int i = 0; i < size.getValue().intValue(); i++) {
			if(fillpositions.contains(i))
				TamagoCCLogger.println(3,"Arrayvar "+ getName()+": Position "+i+" already fulfilled");
			else {
				TamagoCCLogger.println(3,"Arrayvar "+ getName()+": Fill empty position "+i);
				ArrayIndexvar idx = new ArrayIndexvar(size,i,b);
				try {
					putElem(idx);
					getElem(idx).getCSPvar().filter();
					TamagoCCLogger.println(3,"	Creation OK");
				} catch (SecurityException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				} catch (IllegalArgumentException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				} catch (NoSuchMethodException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				} catch (InstantiationException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				} catch (IllegalAccessException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				} catch (InvocationTargetException e) {
					TamagoCCLogger.print(3,"	Creation KO");
					TamagoCCLogger.infoln(3, e);
				}
			}
		}

		// marqueur pour ne pas outrepasser nos droits
		ArrayTriplet triplet = new ArrayTriplet(this,null,false);
		b.push(triplet);

		// faire la boucle en 2 fois pour eviter d'instancier des variables n'ayant pas toutes leurs
		// contrainte du au position dans le tableau

		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			if(!pair.r().getCSPvar().isFixed()) {
				TamagoCCLogger.println(4, "Contenue non instanciee "+pair.r().getCSPvar().toString());
				pair.r().getCSPvar().forward();
				TamagoCCLogger.println(4, "ARRAYVAR forward\t "+pair.r().getCSPvar().toString());
			}
		}
		/*
		Pair<FDvar, TamagoBuilder> pair;
		do {
			pair = chooseVariable();
			if(pair != null) {
				try {
					TamagoCCLogger.println(4, "Instance "+pair.r().toString());
					pair.r().getCSPvar().forward();
					filtersContents();
					TamagoCCLogger.println(4,"Success value "+pair.r().toString());
					TamagoCCLogger.println(4,getContentString());
				}
				catch(TamagoCSPException cspex) {
					boolean cont = true;
					while(cont) {
						try {
							backtrack();
							cont = false;
						}
						catch(TamagoCSPException exb) {
							TamagoCCLogger.println(4, "ARRAYVAR: backtrack error but I continue ");
							TamagoCCLogger.infoln(4, exb);
						}
					}
				}

			}
		} while(pair != null);
		*/
		
		fixed = true;
		compress();
		value = new CSParray(vars,owner.getType(),(CSPinteger) size.getValue(),name);
	}


	private void compress() {
		ArrayList<Pair<FDvar, TamagoBuilder>> clonevars = new ArrayList<Pair<FDvar,TamagoBuilder>>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			int idx = pair.l().getValue().intValue();
			if(!visited.contains(idx)) {
				Intvar iidx = new Intvar("idx_"+idx, b,idx);
				clonevars.add(new Pair<FDvar, TamagoBuilder>(iidx,pair.r()));
			}
		}
		ArrayCompressTriplet act = new ArrayCompressTriplet(this, vars);
		b.push(act);
		this.vars = clonevars;
	}

	private void filtersContents() throws TamagoCSPException {
		int lg = b.size();
		do {
			lg = b.size();
			for (Pair<FDvar, TamagoBuilder> pair : vars) {
				pair.r().getCSPvar().filter();			
			}
		} while(b.size() != lg);
	}

	private void backtrack() throws TamagoCSPException {
		Triplet t = null;
		boolean cont = true;
		do {
			t = b.pop();
			if((t instanceof ArrayTriplet) && (((ArrayTriplet)t).getVariable() == this)) {
				throw new TamagoCSPException("No more solution in the array");
			}
			CSPvar v = t.getVariable();
			v.retrieve(t);
			if(t.isForward()) {
				cont = false;
			}
		} while(cont) ;
	}

	private Pair<FDvar, TamagoBuilder> chooseVariable() {
		Pair<FDvar, TamagoBuilder> tmp = null;
		FDvar tvar = null;
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			if((tmp == null) && !(pair.r().getCSPvar().isFixed())) {
				tmp = pair;
				tvar = null;
				if(tmp.r().getCSPvar() instanceof FDvar) {
					tvar = (FDvar) pair.r().getCSPvar();
				}
			}
			if((tmp != null)&& (pair.r() instanceof FDvar) && !pair.r().getCSPvar().isFixed()) {
				if(!(tmp.r().getCSPvar() instanceof FDvar)) {
					tmp = pair;
					tvar = (FDvar) pair.r().getCSPvar();
				}
				else if(((FDvar)pair.r().getCSPvar()).size() < tvar.size()) {
					tmp = pair;
					tvar = (FDvar) pair.r().getCSPvar();
				}
			}
		}
		return tmp;
	}

	public String getContentString() {
		StringBuilder sb = new StringBuilder();
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			sb.append("Position: ");
			sb.append(pair.l().getName());
			sb.append("\n");
			sb.append("\tContents: ");
			sb.append(pair.r());
			sb.append("\n");
		}
		return sb.toString();
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
		return false;
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

	}

	/**
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	public void retrieve(Triplet triplet) {
		if(triplet instanceof ArrayAddConstraintTriplet) {
			ArrayAddConstraintTriplet t = (ArrayAddConstraintTriplet)triplet;
			for (CSPvar var : t.getConstraint().getVariables()) {
				var.uninstall(t.getConstraint());
			} 
		}
		if(triplet instanceof ArrayCompressTriplet) {
			ArrayCompressTriplet t = (ArrayCompressTriplet)triplet;
			this.vars = t.getVars();
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#setBacktrack(tamago.csp.Backtracking)
	 */
	public void setBacktrack(Backtracking b) {
		this.b = b;
		size.setBacktrack(b);
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			pair.l().setBacktrack(b);
			pair.r().setBacktrack(b);
			pair.r().getCSPvar().setBacktrack(b);
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#setMustInstantiate(boolean)
	 */
	public void setMustInstantiate(boolean b) {
		size.setMustInstantiate(b);
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			try {
				pair.r().getCSPvar(null, null).setMustInstantiate(b);
			} catch (TamagoBuilderException e) {		}
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#uninstallAllConstraints()
	 */
	public void uninstallAllConstraints() {
		constraints.clear();
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			try {
				pair.r().getCSPvar(null, null).uninstallAllConstraints();
			} catch (TamagoBuilderException e) {	}
		}
	}

	public void uninstall(CSPConstraint c) {
		constraints.remove(c);
		for (Pair<FDvar, TamagoBuilder> pair : vars) {
			try {
				pair.r().getCSPvar(null, null).uninstall(c);
			} catch (TamagoBuilderException e) {		}
		}
	}

	public void updateDomain(CSPvar v) throws TamagoCSPException {
		TamagoCCLogger.println(3,"ARRAY: update domain: "+v.toString());
		if(!v.isFixed())
			return; // a priori on doit attendre
		if(v == size) {
			// rien a faire les contraintes sont deja satisfaite normalement
		}
		else {
			// 
				TamagoBuilder tb = getElem((FDvar) v);
				CSPvar vtb = tb.getCSPvar();
				for (Pair<FDvar, TamagoBuilder> pair : vars) {
					if((pair.l() != v) && (pair.l().isFixed())) {
						CSPvar v2 = pair.l();
						//TamagoCCLogger.println(6,getName() + " (ARRAY) compare pos: "+v2.getValue().intValue());
						if(v.getValue().intValue() == v2.getValue().intValue()) {
							TamagoCCLogger.println(5,getName() + " (ARRAY) already position find: "+v2.getValue().intValue());
							TamagoCCLogger.println(4,getName() + " ARRAY bind between "+v2.getName()+ " <-> "+v.getName());
							XEqY c = new XEqY(pair.r().getCSPvar(),vtb);
							ArrayAddConstraintTriplet triplet = new ArrayAddConstraintTriplet(this,vtb,(FDvar) v,c);
							this.b.push(triplet);
							if((pair.r().getCSPvar() instanceof CSPrepercussion) && (vtb instanceof CSPrepercussion)) {
								TamagoCCLogger.println(4, getName() + " (Array) DYNAMIC Constraint between 2 elements weaved through CSPrepercussion");
								pair.r().getCSPvar().addRepercussion((CSPrepercussion)vtb);
								vtb.addRepercussion((CSPrepercussion)pair.r().getCSPvar());
							}
							c.filter();
						}
					}
				}
			
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(getName());
		sb.append(" : ");
		sb.append(size.toString());
		return sb.toString();
	}

	public CSPAbstractDomain getAbstractDomain() {
		return null;
	}

	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}
}
