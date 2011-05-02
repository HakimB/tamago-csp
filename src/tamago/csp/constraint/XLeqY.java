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
public class XLeqY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XLeqY(CSPvar x, CSPvar y) {
		super(" <= ", x, y);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)y).setMin(((FDvar)x).getMin());
			((FDvar)x).setMax(((FDvar)y).getMax());
		}
		finally {
			filtering = false;
		}
	}

}
