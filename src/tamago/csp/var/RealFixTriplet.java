/**
 * 
 */
package tamago.csp.var;

/**
 * @author Hakim Belhaouari
 *
 */
public class RealFixTriplet extends RealTriplet {

	private double max;
	/**
	 * @param var
	 * @param type
	 * @param value
	 */
	public RealFixTriplet(Realvar var, double min,double max) {
		super(var, FDTripletType.FIX, min);
		this.max = max;
	}
	
	public double getMin() {
		return value;
	}
	
	public double getMax() {
		return max;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RealTriplet[");
		sb.append(getType());
		sb.append(" on ");
		sb.append(var.getName());
		sb.append(" min=");
		sb.append(value);
		sb.append(" max=");
		sb.append(max);
		sb.append("]");
		return sb.toString();
	}
}
