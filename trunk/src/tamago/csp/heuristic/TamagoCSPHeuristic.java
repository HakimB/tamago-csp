/**
 * 
 */
package tamago.csp.heuristic;

import tamago.csp.exception.TamagoCSPHeuristicException;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public interface TamagoCSPHeuristic {
	/**
	 * Return the variable to be filtered during the variable, Or return null is no more
	 * variable is found
	 * @return
	 * @throws TamagoCSPHeuristicException
	 */
	public CSPvar select() throws TamagoCSPHeuristicException;
	
	/**
	 * Allow to the heuristic to fall back, when he selects a bad previous value
	 */
	public void undo();	
}
