/**
 * 
 */
package tamago.csp.generic;

import tamago.csp.exception.TamagoCSPException;

/**
 * @author Hakim Belhaouari
 *
 */
public interface CSPrepercussion {
	void updateDomain(CSPvar v) throws TamagoCSPException;
}
