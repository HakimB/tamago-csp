/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XEqC extends XOpeC {

	/**
	 * 
	 */
	public XEqC(CSPvar x,CSPconst c) {
		super(" == ",x,c);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		
		if(filtering)
			return;
		
		filtering = true;
		try {
			x.fix(c);
			filtering = false;
		}
		finally {
			filtering = false;
		}
	}
}
