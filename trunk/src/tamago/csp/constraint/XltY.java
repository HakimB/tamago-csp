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
public class XltY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XltY(CSPvar x, CSPvar y) {
		super(" < ",x, y);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)y).setMinEx(((FDvar)x).getMin());
			((FDvar)x).setMaxEx(((FDvar)y).getMax());
		}
		finally {
			filtering = false;
		}

	}

}
