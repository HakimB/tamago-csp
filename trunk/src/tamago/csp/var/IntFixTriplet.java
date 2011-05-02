/**
 * 
 */
package tamago.csp.var;

	
/**
 * @author Hakim Belhaouari
 *
 */
public class IntFixTriplet extends IntTriplet {
	private int max;
	
	/**
	 * 
	 */
	public IntFixTriplet(Intvar var,int min,int max) {
		super(var,FDTripletType.FIX,min);
		this.max = max;
	}
	
	public int getMin() {
		return this.value();
	}
	
	public int getMax() {
		return this.max;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IntTriplet[");
		sb.append(type);
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
