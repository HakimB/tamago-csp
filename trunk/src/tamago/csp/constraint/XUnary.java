/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;

/**
 * Constraint that re-filter the domain of the inner variable without modification.
 * Usefull for forcing the null value on the variable when the converter identify
 * a constraint like that:
 *  X == null
 *  then the converter X.forceNull() fonctionality and creat this constraint to force at least one filtering 
 *  process.
 * 
 * @author Hakim Belhaouari
 *
 */
public class XUnary implements CSPConstraint {

	private CSPvar v;
	private boolean filtering;
	
	/**
	 * 
	 */
	public XUnary(CSPvar v) {
		this.v = v;
		filtering = false;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			v.filter();
		}
		finally {
			filtering = false;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> var = new ArrayList<CSPvar>();
		var.add(this.v);
		return var;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 1;
	}

	public String toString() {
		return v.getName();
	}
}
