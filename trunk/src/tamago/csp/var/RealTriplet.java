/**
 * 
 */
package tamago.csp.var;

import tamago.csp.Triplet;
import tamago.csp.constant.CSPreal;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class RealTriplet implements Triplet {

	private FDTripletType type;
	protected double value;
	protected Realvar var;
	
	/**
	 * 
	 */
	public RealTriplet(Realvar var,FDTripletType type,double value) {
		this.type = type;
		this.var = var;
		this.value = value;
	}

	/**
	 * @see tamago.csp.Triplet#getVariable()
	 */
	public CSPvar getVariable() {
		return var;
	}

	/**
	 * @see tamago.csp.Triplet#isForward()
	 */
	public boolean isForward() {
		return (type == FDTripletType.FORWARD);
	}
	
	public FDTripletType getType() {
		return type;
	}
	
	public double value() {
		return value;
	}

	public CSPconst getValue() {
		return new CSPreal(value);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RealTriplet[");
		sb.append(type);
		sb.append(" on ");
		sb.append(var.getName());
		sb.append(" value ");
		sb.append(value);
		sb.append("]");
		return sb.toString();
	}
}
