/**
 * 
 */
package tamago.csp.constraint;

 import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPVarEqInterface;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class XEqY extends XOpeY {

	/**
	 * @param ope
	 * @param x
	 * @param y
	 */
	public XEqY(CSPvar x, CSPvar y) {
		super(" == ", x, y);
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
				y.fix(x.getValue());
			else if(y.isFixed())
				x.fix(y.getValue());
			else {
				if((x instanceof FDvar) && (y instanceof FDvar)) {
					((FDvar)x).setMax(((FDvar)y).getMax());
					((FDvar)y).setMax(((FDvar)x).getMax());
					
					((FDvar)x).setMin(((FDvar)y).getMin());
					((FDvar)y).setMin(((FDvar)x).getMin());
				}
				else if(x instanceof CSPVarEqInterface) {
					((CSPVarEqInterface)x).equality(y);
				}
				else if(y instanceof CSPVarEqInterface) {
					((CSPVarEqInterface)y).equality(x);
				}
				/*else if((x instanceof Stringvar) && (y instanceof Stringvar)) {
					SAuto autox = ((Stringvar)x).getRegExp();
					SAuto autoy = ((Stringvar)y).getRegExp();
					
					if(!((Stringvar)x).canfusion(autoy))
						throw new TamagoCSPException("XeqY (stringvar) fails");
					if(!((Stringvar)y).canfusion(autox))
						throw new TamagoCSPException("YeqX (stringvar) fails");
					((Stringvar)x).fusion(autoy);
					((Stringvar)y).fusion(autox);
				}*/
			}
		}
		finally {
			filtering = false;
		}
	}

}
