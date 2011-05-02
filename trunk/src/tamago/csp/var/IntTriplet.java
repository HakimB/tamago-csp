/**
 * 
 */
package tamago.csp.var;

import tamago.csp.Triplet;
import tamago.csp.constant.CSPinteger;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class IntTriplet implements Triplet {

	protected FDTripletType type;
	protected int value;
	protected Intvar var;
	
	/**
	 * 
	 */
	public IntTriplet(Intvar var,FDTripletType type, int val) {
		this.type = type;
		value = val;
		this.var = var;
	}

	/**
	 * @see tamago.csp.Triplet#isForward()
	 */
	public boolean isForward() {
		return (type == FDTripletType.FORWARD);
	}
	
	public int value() {
		return value;
	}
	
	public FDTripletType type() {
		return type;
	}

	public CSPvar getVariable() {
		return var;
	}

	public CSPconst getValue() {
		return new CSPinteger(value);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IntTriplet[");
		sb.append(type);
		sb.append(" on ");
		sb.append(var.getName());
		sb.append(" value ");
		sb.append(value);
		sb.append("]");
		return sb.toString();
	}
}
