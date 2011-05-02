/**
 * 
 */
package tamago.csp.array;

import tamago.csp.Triplet;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class ArrayAddConstraintTriplet implements Triplet {

	private CSPConstraint constraint;
	private FDvar idx;
	private CSPvar target;
	private CSPvar var;
	
	
	/**
	 * 
	 */
	public ArrayAddConstraintTriplet(CSPvar var,CSPvar target,FDvar idx,CSPConstraint constraint) {
		this.var = var;
		this.idx = idx;
		this.target = target;
		this.constraint = constraint;
	}

	/**
	 * @see tamago.csp.Triplet#getValue()
	 */
	public CSPconst getValue() {
		return null;
	}

	/**
	 * @see tamago.csp.Triplet#getVariable()
	 */
	public CSPvar getVariable() {
		return var;
	}

	/**
	 * @see tamago.csp.Triplet#isForward()
	 */
	public boolean isForward() {
		return false;
	}

	public CSPvar getTarget() {
		return target;
	}
	
	public FDvar getIndex() {
		return idx;
	}
	
	public CSPConstraint getConstraint() {
		return constraint;
	}
}
