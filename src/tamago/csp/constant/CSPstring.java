/**
 * 
 */
package tamago.csp.constant;

import tamago.csp.generic.CSPconst;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AINoInstruction;
import tamagocc.ast.impl.AIString;

/**
 * @author Hakim Belhaouari
 *
 */
public class CSPstring implements CSPconst {

	private String val;
	
	/**
	 * 
	 */
	public CSPstring(String value) {
		val = value;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	public boolean boolValue() {
		return val.length() == 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	public int intValue() {
		return val.length();
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		return val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		return val.length();
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		return val;
	}


	public boolean equals(Object o) {
		if(o instanceof CSPstring) {
			return val.equals(((CSPstring)o).stringValue());
		}
		return false;
	}
	
	public String toString() {
		return val;
	}

	public AExpression toAExpression() {
		return new AIString(val);
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}
	public AInstruction toPostExpression() {
		return AINoInstruction.getNoInstruction();
	}
}
