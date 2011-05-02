/**
 * 
 */
package tamago.csp.generic;

import tamago.csp.exception.TamagoCSPException;

/**
 * @author hakim
 *
 */
public interface CSPVarEqInterface {
	/**
	 * Function for optimize the (lazy) equality constraint. This function is recognize by the 
	 * solver in order to improve the filter or interpret the domain by a variable from
	 * an equality constraint.
	 * @param v
	 * @throws TamagoCSPException
	 */
	public void equality(CSPvar v) throws TamagoCSPException;
}
