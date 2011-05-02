/**
 * 
 */
package tamago.csp.generic;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamagocc.exception.TamagoCCException;

/**
 * This class represents the root of all CSPvar.
 * A CSP variable have a name with a set of constraint.
 * 
 * @author Hakim Belhaouari
 *
 */
public interface CSPvar {
	/**
	 * Return the name of the variable that represents
	 * @return Return the name
	 */
	String getName();
	
	
	/**
	 * Indicates if the object is in the current domain
	 * @param o : argument to test
	 * @return
	 */
	boolean isInDomain(CSPconst o);
	
	/**
	 * Return all constraints of the CSP.
	 * @return all constraints where the current variable is present.
	 */
	Iterable<CSPConstraint> getConstraint();
	
	/**
	 * Return the count of constraint
	 * @return Return the count of constraint
	 */
	int count();
	
	/**
	 * This function calls the filter method from all associate constraint
	 * @throws TamagoCSPException
	 */
	void filter() throws TamagoCSPException;

	
	/**
	 * Not use currently
	 * @return
	 */
	boolean isPrimitive();
	
	/**
	 * Remove
	 * @param value
	 * @throws TamagoCSPException
	 */
	void remove(CSPconst value) throws TamagoCSPException;
	
	/**
	 * Indicates if the variable is instantiate.
	 * @return <b>true</b> if the variable has only one value, <b>false</b> else.
	 */
	boolean isFixed();
	
	
	CSPconst getValue();
	
	
	/**
	 * 
	 * @param n
	 * @throws TamagoCSPException
	 */
	void fix(CSPconst n) throws TamagoCSPException;
	
	/**
	 * This method is used for the backtracking processing. 
	 * @param triplet
	 * @throws TamagoCSPException 
	 */
	void retrieve(Triplet triplet) throws TamagoCSPException;
	
	
	/**
	 * Instantiate the current variable.
	 * @throws TamagoCSPException
	 */
	void forward() throws TamagoCSPException;
	
	/**
	 * Extends the set of associate constraints.
	 * <b>Caution</b> This method is automatically called in constraint.
	 * @param c
	 */
	void install(CSPConstraint c);
	
	/**
	 * Remove an already installed constraint. If the constraint does not exist
	 * The call of this method is simply ignored
	 * @param c: constraint to remove
	 */
	void uninstall(CSPConstraint c);
	
	/**
	 * Change the stack where save all informations
	 * @param b
	 */
	void setBacktrack(Backtracking b);
	
	/**
	 * Indicates if this variable must be instantiated
	 */
	boolean mustInstantiate();
	
	/**
	 * Specify if this variable must be instantiate in the CSP
	 * @param b
	 */
	void setMustInstantiate(boolean b);

	/**
	 * Remove all constraint included in this CSPvar 
	 */
	void uninstallAllConstraints();
	
	/**
	 * Adds a new repercussion object, (in order to inform him when the CSPvar submit a
	 * modification of its domain)
	 * @param rep: repercussion object
	 */
	void addRepercussion(CSPrepercussion rep);
	 
	/**
	 * Remove a specific repercussion objet. Or ignore it if the object is not
	 * listed
	 * @param rep: repercussion object to remove
	 */
	void removeRepercussion(CSPrepercussion rep);
	
	/**
	 * getter of all repercussion known for the variable
	 */
	Iterable<CSPrepercussion> getRepercussions();
	
	/**
	 * Remove all repercussion object of this cspvar
	 */
	void removeAllRepercussiions();
	
	/**
	 * 
	 * @return Return the abstract domainof the current variable or null if impossible to get it
	 */
	CSPAbstractDomain getAbstractDomain();
	
	boolean load(CSPAbstractDomain domain);
}
