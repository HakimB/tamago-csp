/**
 * 
 */
package tamago.csp;

import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public interface Triplet {
	boolean isForward();
	CSPvar getVariable();
	CSPconst getValue();
}
