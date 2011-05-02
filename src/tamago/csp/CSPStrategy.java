/**
 * 
 */
package tamago.csp;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.heuristic.TamagoCSPHeuristic;
import tamagocc.generic.api.GMethod;
import tamagocc.generic.api.GType;

/**
 * @author Hakim Belhaouari
 *
 */
public interface CSPStrategy {

	TamagoCSPHeuristic getHeuristicForwardChecking();
	TamagoBuilder searchBuilder(TamagoEnvironment env,String variable,GType type,GMethod ctx);
	TamagoBuilder searchBuilder(TamagoEnvironment env,String variable,GType type);
	
}
