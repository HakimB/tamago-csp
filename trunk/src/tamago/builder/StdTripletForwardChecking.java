/**
 * 
 */
package tamago.builder;

import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class StdTripletForwardChecking extends GenericTripletForwardChecking {

	int pos;
	/**
	 * @param var
	 */
	public StdTripletForwardChecking(CSPvar var,int pos) {
		super(var);
		this.pos = pos;
	}
	
	public int curPos() {
		return pos;
	}

	public CSPconst getValue() {
		throw new RuntimeException("Fowardchecking: backtrack fail for: "+this.getVariable().toString());
	}
}
