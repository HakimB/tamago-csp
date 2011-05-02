/**
 * 
 */
package tamago.csp.constant;

import tamago.csp.generic.CSPconst;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AIBool;
import tamagocc.ast.impl.AINoInstruction;

/**
 * @author Hakim Belhaouari
 *
 */
public final class CSPbool implements CSPconst {

	private boolean val;
	
	/**
	 * 
	 */
	public CSPbool(boolean value) {
		val = value;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	public boolean boolValue() {
		return val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	public int intValue() {
		if(val)
			return 1;
		else
			return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		return new Boolean(val);
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		if(val)
			return 1.0;
		else
			return 0.0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		if(val)
			return "true";
		else
			return "false";
	}

	public String toString() {
		if(val)
			return "true";
		else
			return "false";
	}
	
	public boolean equals(Object o) {
		if(o instanceof CSPbool) {
			return val == ((CSPbool)o).boolValue();
		}
		return false;
	}

	public AExpression toAExpression() {
		return new AIBool(val);
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}

	public AInstruction toPostExpression() {
		return AINoInstruction.getNoInstruction();
	}
	
}
