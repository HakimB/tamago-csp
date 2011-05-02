package tamago.csp.generic;

import tamago.csp.exception.TamagoCSPException;

/**
 * This represents the root of all Constraint
 * @author Hakim Belhaouari
 *
 */
public interface CSPConstraint {
	/**
	 * Call filter on variable.
	 * @throws TamagoCSPException
	 */
	void filter() throws TamagoCSPException;
	
	/**
	 * Gets all variables that refers the current constraint
	 * @return
	 */
	Iterable<CSPvar> getVariables();
	
	/**
	 * Return the arity of this constraint
	 * @return Return the count of variable that use this constraint.
	 */
	int size();
}
