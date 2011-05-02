/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public abstract class XOpeY implements BinaryConstraint {

	protected CSPvar x;
	protected CSPvar y;
	protected String ope;
	protected boolean filtering;

	/**
	 * 
	 */
	public XOpeY(String ope,CSPvar x,CSPvar y) {
		this.x = x;
		this.y = y;
		this.ope = ope;
		filtering = false;
		x.install(this);
		y.install(this);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> array = new ArrayList<CSPvar>();
		array.add(x);
		array.add(y);
		return array;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 2;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(x.getName());
		sb.append(ope);
		sb.append(y.getName());
		sb.append(")");
		return sb.toString();
	}
}
