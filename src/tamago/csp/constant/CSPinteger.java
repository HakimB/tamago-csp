package tamago.csp.constant;

import tamago.csp.generic.CSPconst;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AIInteger;
import tamagocc.ast.impl.AINoInstruction;

/**
 * 
 */

/**
 * @author Hakim Belhaouari
 *
 */
public final class CSPinteger implements CSPconst {

	private int val;
	
	/**
	 * 
	 */
	public CSPinteger(int value) {
		val = value;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	public boolean boolValue() {
		return (val == 0);
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	public int intValue() {
		return val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		return new Integer(val);
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		return (double)val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		return (new Integer(val)).toString();
	}

	public boolean equals(Object o) {
		if(o instanceof CSPinteger) {
			return val == ((CSPinteger)o).intValue();
		}
		return false;
	}
	
	public String toString() {
		return (new Integer(val)).toString();
	}

	public AExpression toAExpression() {
		return new AIInteger(val);
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}
	public AInstruction toPostExpression() {
		return AINoInstruction.getNoInstruction();
	}
}
