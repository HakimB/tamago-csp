/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.generic.CSPconst;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public interface LinearConstraint  extends NaryConstraint {
	void put(CSPconst coef,FDvar var);
	void put(FDvar var,CSPconst coef);
	void setConstant(CSPconst constant);
}
