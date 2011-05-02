/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;
import java.util.Iterator;

import tamago.csp.TamagoCSP;
import tamago.csp.constant.CSPinteger;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.var.Intvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class EqLinear implements LinearConstraint {

	class EqLinearTuple {
		EqLinearTuple(FDvar v, CSPconst coef) {
			this.var = v;
			this.coef = coef;
		}
		FDvar var;
		CSPconst coef;
		boolean isFixed() {
			return var.isFixed();
		}
		
		int coef() {
			return coef.intValue();
		}
		
		int min() {
			return var.getMin().intValue();
		}
		int max() {
			return var.getMax().intValue();
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(coef.toString());
			sb.append("*");
			sb.append(var.getName());
			return sb.toString();
		}
	}
	
	
	private ArrayList<EqLinearTuple> vars;
	private CSPconst constant;
	private boolean filtering;
	
	private transient int min;
	private transient int max;
	
	/**
	 * 
	 */
	public EqLinear() {
		vars = new ArrayList<EqLinearTuple>();
		constant = new CSPinteger(0);
		filtering = false;
	}
	
	public void put(FDvar var,CSPconst coef) {
		vars.add(new EqLinearTuple(var,coef));
		var.install(this);
	}

	public void put(CSPconst coef,FDvar var) {
		put(var,coef);
	}
	
	public void setConstant(CSPconst constant) {
		this.constant = constant;
	}

	
	private void updateMinMax(EqLinearTuple tuple) {
		min = constant.intValue();
		max = constant.intValue();
		
		for (EqLinearTuple eq : vars) {
			if(eq != tuple) {
				if(eq.coef() < 0) {
					min -= (eq.coef() * eq.min());
					max -= (eq.coef() * eq.max());
				}
				else {
					min -= (eq.coef() * eq.max());
					max -= (eq.coef() * eq.min()); 
				}
			}
		}
		if(tuple.coef() < 0) {
			int tswap = min;
			min = max;
			max = tswap;
		}
		
		min = (int) Math.ceil((double) min / (double)tuple.coef());
		max = (int) Math.floor((double) max / (double)tuple.coef());
	}
	
	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			for (EqLinearTuple tuple : vars) {
				if(!tuple.var.isFixed()) {					
					updateMinMax(tuple);
					CSPconst cmin = new CSPinteger(min);
					CSPconst cmax = new CSPinteger(max);
					if(min == max) {
						tuple.var.fix(cmin);
					}
					else {
					if(max < min) {
						throw new TamagoCSPException("EqLinear: max smaller than min");
					}
					if(tuple.var.isInDomain(cmin))
						tuple.var.setMin(cmin);
					if(tuple.var.isInDomain(cmax))
						tuple.var.setMax(cmax);
					}
				}
			}
		}
		finally {
			filtering = false;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> renvoie = new ArrayList<CSPvar>();
		for (EqLinearTuple pvar : vars) {
			renvoie.add(pvar.var);
		}
		return renvoie;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return vars.size();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<EqLinearTuple> tuples = vars.iterator();
		while(tuples.hasNext()) {
			EqLinearTuple tuple = tuples.next();
			switch(tuple.coef.intValue()) {
			case -1:
				sb.append("-");
				sb.append(tuple.var.getName());
				break;
			case 1:
				sb.append(tuple.var.getName());
				break;
			default:
				sb.append(tuple.coef.toString());
				sb.append("*");
				sb.append(tuple.var.getName());
			}
			
			if(tuples.hasNext()) {
				sb.append("+");
			}
		}
		sb.append(" = ");
		sb.append(constant.toString());
		return sb.toString();
	}
	
	
	public static void main(String[] args) throws TamagoCSPException {
		TamagoCSP csp = new TamagoCSP();
		
		
		CSPinteger un = new CSPinteger(1);
		CSPinteger mun = new CSPinteger(-1);
		
		Intvar idx2 = new Intvar("idx2", csp.getBacktrack(),1);
		Intvar __internal_idx_min_0 = new Intvar("__internal_idx_min_0",csp.getBacktrack(),-2,9999);
		Intvar __internal_tabrange0_idx2 = new Intvar("__internal_tabrange0[idx2]",csp.getBacktrack());
		Intvar length = new Intvar("__internal_tabrange0[].length",csp.getBacktrack(),3);
		Intvar __internal_idx_max_0 = new Intvar("__internal_idx_max_0", csp.getBacktrack(),2);
		Intvar chier = new Intvar("idx_(2 * __internal_tabrange0[idx2])", csp.getBacktrack());
		csp.addVariable(__internal_tabrange0_idx2);
		csp.addVariable(__internal_idx_min_0);
		csp.addVariable(__internal_idx_max_0);
		csp.addVariable(chier);
		csp.addVariable(length);
		csp.addVariable(idx2);
		
		EqLinear eq = new EqLinear();
		eq.put(un, idx2);
		eq.put(un, __internal_idx_min_0);
		eq.put(mun, __internal_tabrange0_idx2);
		csp.addConstraint(eq);
		System.out.println("Eq: "+eq.toString());
		
		eq = new EqLinear();
		eq.put(un, __internal_tabrange0_idx2);
		eq.put(un, length);
		eq.put(mun, __internal_idx_max_0);
		eq.put(mun, idx2);
		eq.setConstant(un);
		csp.addConstraint(eq);
		System.out.println("Eq: "+eq.toString());
		
		eq = new EqLinear();
		eq.put(new CSPinteger(2), new Intvar("1", csp.getBacktrack(),1));
		eq.put(un, __internal_tabrange0_idx2);
		eq.put(mun, chier);
		csp.addConstraint(eq);
		System.out.println("Eq: "+eq.toString());
		
		csp.solve();
		System.out.println("csp: "+ csp.toString());
	}
}
