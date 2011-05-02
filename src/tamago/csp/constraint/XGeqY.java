/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author aliquando
 *
 */
public class XGeqY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XGeqY(CSPvar x, CSPvar y) {
		super(" >= ", x, y);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)x).setMin(((FDvar)y).getMin());
			((FDvar)y).setMax(((FDvar)x).getMax());
		}
		finally {
			filtering = false;
		}
	}

}
