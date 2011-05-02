/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XGtY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XGtY(CSPvar x, CSPvar y) {
		super(" > ", x, y);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)x).setMinEx(((FDvar)y).getMin());
			((FDvar)y).setMaxEx(((FDvar)x).getMax());
		}
		finally {
			filtering = false;
		}
	}

}
