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
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public class IneqLinear implements LinearConstraint {

	class IneqLinearTuple {
		IneqLinearTuple(FDvar v, CSPconst coef) {
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
	
	
	private ArrayList<IneqLinearTuple> vars;
	private CSPconst constant;
	private boolean filtering;
	private IneqOperator ope;
	
	private transient int min;
	private transient int max;
	
	/**
	 * 
	 */
	public IneqLinear(IneqOperator ope) {
		vars = new ArrayList<IneqLinearTuple>();
		constant = new CSPinteger(0);
		filtering = false;
		this.ope = ope;
	}
	public void put(FDvar var,CSPconst coef) {
		vars.add(new IneqLinearTuple(var,coef));
		var.install(this);
	}

	public void put(CSPconst coef,FDvar var) {
		put(var,coef);
	}
	
	public void setConstant(CSPconst constant) {
		this.constant = constant;
	}

	
	private void updateMinMax(IneqLinearTuple tuple) {
		min = constant.intValue();
		max = constant.intValue();
		
		for (IneqLinearTuple eq : vars) {
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
		/*if(tuple.coef() < 0) {
			int tswap = min;
			min = max;
			max = tswap;
		}*/
		
		min = (int) Math.ceil((double) min / (double)Math.abs(tuple.coef()));
		max = (int) Math.floor((double) max / (double)Math.abs(tuple.coef()));
	}
	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			for (IneqLinearTuple tuple : vars) {
				if(!tuple.var.isFixed()) {					
					updateMinMax(tuple);
					CSPconst cmin = new CSPinteger(min);
					CSPconst cmax = new CSPinteger(max);
					
					if(max < min) {
						throw new TamagoCSPException("IneqLinear: max smaller than min");
					}
					
					switch(ope) {
					case GE:
						if(tuple.coef() >= 0) {							
							if(tuple.var.isInDomain(cmin))
								tuple.var.setMin(cmin);
						}
						else {
							cmin = new CSPinteger(-max);
							cmax = new CSPinteger(-min);
							if(tuple.var.isInDomain(cmax))
								tuple.var.setMax(cmax);
						}
						break;
					case GT:
						if(tuple.coef() >= 0) {							
							if(tuple.var.isInDomain(cmin))
								tuple.var.setMinEx(cmin);
						}
						else {
							cmin = new CSPinteger(-max);
							cmax = new CSPinteger(-min);
							if(tuple.var.isInDomain(cmax))
								tuple.var.setMaxEx(cmax);
						}
						break;
					case LE:
						if(tuple.coef() >= 0) {							
							if(tuple.var.isInDomain(cmax))
								tuple.var.setMax(cmax);
						}
						else {
							cmin = new CSPinteger(-max);
							cmax = new CSPinteger(-min);
							if(tuple.var.isInDomain(cmin))
								tuple.var.setMin(cmin);
						}
						break;
					case LT:
						if(tuple.coef() >= 0) {							
							if(tuple.var.isInDomain(cmax))
								tuple.var.setMaxEx(cmax);
						}
						else {
							cmin = new CSPinteger(-max);
							cmax = new CSPinteger(-min);
							if(tuple.var.isInDomain(cmin))
								tuple.var.setMinEx(cmin);
						}
						break;
					case NE:
						if(min == max) {
							tuple.var.remove(cmin);
						}
						break;
					case EQ:
						TamagoCCLogger.print(1, "*Warning* a better constraint could be used, call me -> this msg indicates an error in the conversion");
						if(min == max) {
							tuple.var.fix(cmin);
						}
						break;
					default: throw new TamagoCSPException("Unsupported");
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
		for (IneqLinearTuple pvar : vars) {
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
		Iterator<IneqLinearTuple> tuples = vars.iterator();
		while(tuples.hasNext()) {
			IneqLinearTuple tuple = tuples.next();
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
		sb.append(" "+ope.toString()+" ");
		sb.append(constant.toString());
		return sb.toString();
	}
	
	
	private static void loop() throws TamagoCSPException {
		IneqLinear ineq = new IneqLinear(IneqOperator.LE);
		TamagoCSP csp = new TamagoCSP(); 
		Intvar a = new Intvar("a",csp.getBacktrack(),-50,50);
		Intvar b = new Intvar("b",csp.getBacktrack(),-50,50);
		Intvar c = new Intvar("c",csp.getBacktrack(),-50,50);
		csp.addVariable(a);
		csp.addVariable(b);
		csp.addVariable(c);
		
		ineq.put(a, new CSPinteger(1));
		ineq.put(b, new CSPinteger(1));
		ineq.put(c, new CSPinteger(-1));
		
		csp.addConstraint(ineq);
		csp.solve();
		System.out.println(csp.toString());
	}
	
	public static void main(String args[]) {
		
		try {
			
			for(int i=0;i < 2;i++) {
				System.out.println(i);
				loop();		
			}
		} catch (TamagoCSPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}

}
