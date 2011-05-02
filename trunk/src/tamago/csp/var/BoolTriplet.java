/**
 * 
 */
package tamago.csp.var;

import tamago.csp.Triplet;
import tamago.csp.constant.CSPbool;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class BoolTriplet implements Triplet {

	private FDTripletType type;
	private CSPvar var;
	private boolean domain[];
	
	
	/**
	 * 
	 */
	public BoolTriplet(FDTripletType type,CSPvar var,boolean[] domain) {
		this.type = type;
		this.var = var;
		this.domain = new boolean[2];
		this.domain[0] = domain[0];
		this.domain[1] = domain[1];
	}
	
	public BoolTriplet(CSPvar var,boolean value) {
		this.type = FDTripletType.FORWARD;
		this.var = var;
		this.domain = new boolean[2];
		if(value) {
			domain[0] = false;
			domain[1] = true;
		}
		else {
			domain[0] = true;
			domain[1] = false;
		}
	}

	/**
	 * @see tamago.csp.Triplet#getVariable()
	 */
	public CSPvar getVariable() {
		return var;
	}

	public FDTripletType getType() {
		return type;
	}
	
	/**
	 * @see tamago.csp.Triplet#isForward()
	 */
	public boolean isForward() {
		return type == FDTripletType.FORWARD;
	}
	
	public boolean domaine(int pos) {
		return domain[pos];
	}

	public CSPconst getValue() {
		if(domain[0])
			return new CSPbool(false);
		else
			return new CSPbool(true);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BoolTriplet[");
		sb.append(type);
		sb.append(" on ");
		sb.append(var.getName());
		sb.append(" values {");
		if(domain[0])
			sb.append("false ");
		if(domain[1])
			sb.append("true ");
		sb.append("}]");
		return sb.toString();
	}

}
