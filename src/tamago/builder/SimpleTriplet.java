/**
 * 
 */
package tamago.builder;

import tamago.csp.Triplet;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class SimpleTriplet implements Triplet {

	private CSPvar var;
	private CSPconst value;
	private boolean flag;
	
	/**
	 * 
	 */
	public SimpleTriplet(CSPvar var, CSPconst value, boolean isforward) {
		this.var = var;
		this.value = value;
		this.flag = isforward;
	}

	/**
	 * @see tamago.csp.Triplet#getValue()
	 */
	public CSPconst getValue() {
		return value;
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
		return flag;
	}

}
