/**
 * 
 */
package tamago.csp.exception;

/**
 * @author Hakim Belhaouari
 *
 */
public class TamagoCSPRuntime extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4839303527055946672L;

	/**
	 * 
	 */
	public TamagoCSPRuntime() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public TamagoCSPRuntime(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public TamagoCSPRuntime(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TamagoCSPRuntime(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
