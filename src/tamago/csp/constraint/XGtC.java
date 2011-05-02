/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XGtC extends XOpeC {
	/**
	 * 
	 */
	public XGtC(FDvar x, CSPconst c) {
		super(" > ",x,c);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)x).setMinEx(c);
		}
		finally {
			filtering = false;
		}
	}
}
