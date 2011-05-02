/**
 * 
 */
package tamago.builder;

import tamago.csp.Backtracking;
import tamagocc.generic.api.GType;

/**
 * @author Hakim Belhaouari
 *
 */
public interface TamagoBuilderFactory {
	TamagoBuilder searchBuilder(TamagoEnvironment env, String name,GType type,Backtracking b);
}
