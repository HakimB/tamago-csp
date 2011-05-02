/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public abstract class XOpeC implements UnaryConstraint {

	protected CSPvar x;
	protected CSPconst c;
	protected String ope;
	protected boolean filtering;
	
	/**
	 * 
	 */
	public XOpeC(String op,CSPvar x, CSPconst c) {
		this.x = x;
		this.c = c;
		this.ope = op;
		filtering = false;
		x.install(this);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> array = new ArrayList<CSPvar>();
		array.add(x);
		return array;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 1;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(x.getName());
		sb.append(ope);
		sb.append(c);
		sb.append(")");
		return sb.toString();
	}

}
