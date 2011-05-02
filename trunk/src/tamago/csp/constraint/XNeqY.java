/**
 * 
 */
package tamago.csp.constraint;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XNeqY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XNeqY(CSPvar x, CSPvar y) {
		super(" != ", x, y);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			if(x.isFixed())
				y.remove(x.getValue());
			else if(y.isFixed())
				x.remove(y.getValue());
		}
		finally {
			filtering = false;
		}
	}

}
