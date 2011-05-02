/**
 * 
 */
package tamago.builder;

import tamago.builder.impl.Builderint;
import tamago.csp.Backtracking;
import tamagocc.generic.api.GType;

/**
 * @author hakim
 *
 */
public class IntegerBuilderFactory implements TamagoBuilderFactory {

	/**
	 * 
	 */
	public IntegerBuilderFactory() {
			}

	/**
	 * @see tamago.builder.TamagoBuilderFactory#searchBuilder(tamago.builder.TamagoEnvironment, java.lang.String, tamagocc.generic.api.GType, tamago.csp.Backtracking)
	 */
	public TamagoBuilder searchBuilder(TamagoEnvironment env, String name,
			GType type, Backtracking b) {
		return new Builderint(env, name, type, b);
	}

}
