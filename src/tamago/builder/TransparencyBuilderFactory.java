/**
 * 
 */
package tamago.builder;

import tamago.builder.impl.Buildertransparency;
import tamago.csp.Backtracking;
import tamagocc.generic.api.GType;

/**
 * @author Hakim Belhaouari
 *
 */
public class TransparencyBuilderFactory implements TamagoBuilderFactory {

	/**
	 * 
	 */
	public TransparencyBuilderFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see tamago.builder.TamagoBuilderFactory#searchBuilder(tamago.builder.TamagoEnvironment, java.lang.String, tamagocc.generic.api.GType, tamago.csp.Backtracking)
	 */
	public TamagoBuilder searchBuilder(TamagoEnvironment env, String name,
			GType type, Backtracking b) {
		
		return new Buildertransparency(env,name,type,b);
	}

}
