/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.csp.constant.CSPbool;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public class XBool implements UnaryConstraint {

	private CSPvar var;
	private boolean filtering;
	
	
	/**
	 * 
	 */
	public XBool(CSPvar var) {
		this.var = var;
		filtering = false;	
		var.install(this);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		String d = "";
		try {
			d = var.toString();
			CSPbool b = new CSPbool(false);
			var.remove(b);
		}
		catch(TamagoCSPException e) {
			TamagoCCLogger.println(3, "Domain before: "+d);
			TamagoCCLogger.println(3, "Domain after: "+var);
			throw new TamagoCSPException(toString(), e);
		}
		finally {
			filtering = false;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> var = new ArrayList<CSPvar>();
		var.add(this.var);
		return var;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 1;
	}

	public String toString() {
		return var.getName();
	}
}
