/**
 * 
 */
package tamago.csp.constant;

import tamago.csp.generic.CSPconst;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AINil;
import tamagocc.ast.impl.AINoInstruction;

/**
 * @author Hakim Belhaouari
 *
 */
public class CSPnull implements CSPconst {

	/**
	 * 
	 */
	public CSPnull() {
	}

	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	public boolean boolValue() {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	public int intValue() {
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		return 0.0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		return null;
	}


	public boolean equals(Object o) {
		return o instanceof CSPnull;
	}
	
	public String toString() {
		return "null";
	}

	public AExpression toAExpression() {
		return new AINil();
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}
	public AInstruction toPostExpression() {
		return AINoInstruction.getNoInstruction();
	}
}
