/**
 * 
 */
package tamago.builder;

import tamago.csp.Triplet;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public abstract class GenericTripletForwardChecking implements Triplet {

	private CSPvar var;
	
	/**
	 * 
	 */
	public GenericTripletForwardChecking(CSPvar var) {
		this.var = var;
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
		return true;
	}

}
