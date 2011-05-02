/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.builder.impl.Builderarray;
import tamago.builder.impl.Builderint;
import tamago.csp.TamagoCSP;
import tamago.csp.array.ArrayIndexvar;
import tamago.csp.array.Arrayvar;
import tamago.csp.constant.CSPinteger;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.var.Intvar;
import tamagocc.generic.impl.GIType;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoFreshVar;

/**
 * @author Hakim Belhaouari
 *
 */
public class IntervalArrayConstraint implements QuantifierConstraint,CSPrepercussion {

	
	private Builderarray barray;
	
	private ArrayIndexvar aivmin;
	private Builderint transmin;
	
	private ArrayIndexvar aivmax;
	private Builderint transmax;
	
	public IntervalArrayConstraint(Builderarray barray, FDvar min, FDvar max, TamagoCSP csp, TamagoEnvironment env) throws TamagoCSPException {
		this.barray = barray;
		
		Arrayvar array = (Arrayvar) barray.getCSPvar();
		Intvar length = array.getLength();
		csp.addVariable(array);
		
		aivmin = new ArrayIndexvar(length, csp.getBacktrack());
		transmin = new Builderint(env,TamagoFreshVar.Default.getName("__internal_idx_min_"), GIType.TYPE_INT, csp.getBacktrack());
		XEqC mineg0 = new XEqC(aivmin, new CSPinteger(0));
		csp.addConstraint(mineg0);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+mineg0.toString());
		
		aivmax = new ArrayIndexvar(length, csp.getBacktrack());
		transmax = new Builderint(env, TamagoFreshVar.Default.getName("__internal_idx_max_"), GIType.TYPE_INT, csp.getBacktrack());
		EqLinear maxaivmax = new EqLinear();
		maxaivmax.put(new CSPinteger(1), length);
		maxaivmax.put(new CSPinteger(-1), aivmax);
		maxaivmax.setConstant(new CSPinteger(1));
		csp.addConstraint(maxaivmax);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+maxaivmax.toString());
		
		// length = max - min +1
		// <-> 
		EqLinear eqlin = new EqLinear();
		eqlin.put(new CSPinteger(1), length);
		eqlin.put(new CSPinteger(1), (FDvar)min);
		eqlin.put(new CSPinteger(-1), (FDvar)max);
		eqlin.setConstant(new CSPinteger(1));
		csp.addConstraint(eqlin);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+eqlin.toString());
		
		XEqY eqmin = new XEqY(transmin.getCSPvar(),min);
		csp.addConstraint(eqmin);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+eqmin.toString());
		
		XEqY eqmax = new XEqY(transmax.getCSPvar(), max);
		csp.addConstraint(eqmax);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+eqmax.toString());
		
		
		// verifie l'ordre des bornes
		XLeqY mininfmax = new XLeqY(min,max);
		csp.addConstraint(mininfmax);
		TamagoCCLogger.println(4, "\t\t\tInterval Constraint:"+mininfmax.toString());
				
		// 
		barray.getCSPvar().install(this);
		
		array.putElem(this.aivmin, transmin);
		array.putElem(this.aivmax, transmax);
		
		array.addQuantifierConstraint(this);
		array.addRepercussion(this);
		length.addRepercussion(this);
		
	}
	
	/**
	 * @see tamago.csp.constraint.QuantifierConstraint#addVariable(tamago.csp.generic.FDvar, tamago.builder.TamagoBuilder)
	 */
	public void addVariable(FDvar idx, TamagoBuilder tb)
			throws TamagoCSPException {
		idx.addRepercussion(this);
		CSPinteger un = new CSPinteger(1);
		CSPinteger mun = new CSPinteger(-1);
		
		EqLinear eqlin = new EqLinear();
		eqlin.put(un, idx); // k
		eqlin.put(un, (FDvar) transmin.getCSPvar()); // a
		eqlin.put(new CSPinteger(-1), (FDvar) tb.getCSPvar()); // tab[k]
		// tab[k] = a + k;
		
		// tab[k] = b  - (l - 1 - k);
		// <-> tab[k] - b + l - k = 1
		Arrayvar array = (Arrayvar) barray.getCSPvar();
		EqLinear eqlinmax = new EqLinear();
		eqlinmax.put(un, (FDvar)tb.getCSPvar());
		eqlinmax.put(mun, (FDvar) transmax.getCSPvar());
		eqlinmax.put(un, (FDvar)array.getLength() );
		eqlinmax.put(mun, idx);
		eqlinmax.setConstant(un);
	}

	public void filter() throws TamagoCSPException {
		
	}

	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> cspvars = new ArrayList<CSPvar>();
		cspvars.add(barray.getCSPvar());
		return cspvars;
	}

	public int size() {
		return 1;
	}

	public void updateDomain(CSPvar v) throws TamagoCSPException {
		Arrayvar array = (Arrayvar) barray.getCSPvar();
		Intvar length = array.getLength();	
		if(v == length && length.isFixed()) {
			array.forward();
		}
	}

}
