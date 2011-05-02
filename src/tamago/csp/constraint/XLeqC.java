/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XLeqC extends XOpeC {

	/**
	 * @param op
	 * @param x
	 * @param c
	 */
	public XLeqC(CSPvar x, CSPconst c) {
		super(" <= ", x, c);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			((FDvar)x).setMax(c);
		}
		finally {
			filtering = false;
		}
	}

}
