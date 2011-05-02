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
public class XNeqC extends XOpeC {

	/**
	 * @param op
	 * @param x
	 * @param c
	 */
	public XNeqC(FDvar x, CSPconst c) {
		super(" != ", x, c);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			x.remove(c);
		}
		finally {
			filtering = false;
		}
	}

}
