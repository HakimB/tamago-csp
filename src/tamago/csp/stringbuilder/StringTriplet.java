/**
 * 
 */
package tamago.csp.stringbuilder;

import tamago.csp.Triplet;
import tamago.csp.constant.CSPstring;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class StringTriplet<T> implements Triplet {

	private boolean isforward;
	private CSPvar var;
	private T str;
	
	/**
	 * 
	 */
	public StringTriplet(boolean isforward, CSPvar var, T str) {
		this.isforward = isforward;
		this.var = var;
		this.str = str;
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
		return isforward;
	}

	public T value() {
		return str;
	}

	public CSPconst getValue() {
		if(str instanceof String)
			return new CSPstring((String) str);
		else
			return new CSPstring("unknow value");
	}
}
