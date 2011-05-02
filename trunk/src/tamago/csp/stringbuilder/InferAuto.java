/**
 * 
 */
package tamago.csp.stringbuilder;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.FDvar;
import tamago.csp.stringbuilder.automaton.SAuto;

/**
 * @author Hakim Belhaouari
 *
 */
public interface InferAuto extends CSPConstraint {
	SAuto infer();
	void updateAuto();
	FDvar length();
	
	boolean isFixed();
	void fix() throws TamagoCSPException;
	
	void install(CSPConstraint constraint);
}
