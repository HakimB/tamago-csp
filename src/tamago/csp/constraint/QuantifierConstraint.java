package tamago.csp.constraint;

import tamago.builder.TamagoBuilder;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.FDvar;

/**
 * this interface is the root interface ofthe quantifier constraints (forall and exists). 
 * The goal of this interface is the centralisation in the arrayvar (or collection var) 
 * the ability to prevent the dynamic constraint of presence of a new variable and constraints
 * in the CSP variable.
 * 
 * @author Hakim Belhaouari
 */
public interface QuantifierConstraint extends CSPConstraint {
	void addVariable(FDvar idx, TamagoBuilder tb) throws TamagoCSPException;
}