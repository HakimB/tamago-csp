/**
 * 
 */
package tamago.csp.domain;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;

/**
 * @author Hakim Belhaouari
 *
 */
public interface CSPAbstractDomain {
	boolean sameDomain(CSPAbstractDomain domain);
	boolean isInDomain(CSPconst o);
	CSPAbstractDomain intersect(CSPAbstractDomain domain) throws TamagoCSPException;
}
