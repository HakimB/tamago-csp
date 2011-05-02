/**
 * 
 */
package tamago.csp.array;

import tamago.csp.Triplet;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class ArrayTriplet implements Triplet {

	private Arrayvar var;
	private CSPconst tmp;
	private boolean isforward;
	
	/**
	 * 
	 */
	public ArrayTriplet(Arrayvar var,CSPconst tmp,boolean isforward) {
		this.var = var;
		this.tmp = tmp;
		this.isforward = isforward;
	}

	/**
	 * @see tamago.csp.Triplet#getValue()
	 */
	public CSPconst getValue() {
		return tmp;
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
		return isforward;
	}

}
