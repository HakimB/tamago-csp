/**
 * 
 */
package tamago.csp.generic;

import tamagocc.exception.TamagoCCException;

/**
 * @author belhaouari
 *
 */
public interface Obvar extends CSPvar {
	
	boolean canBeNull();
	
	void forceNull() throws TamagoCCException;
	
	void forceNotNull() throws TamagoCCException;
}
