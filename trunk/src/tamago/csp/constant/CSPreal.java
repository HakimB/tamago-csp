/**
 * 
 */
package tamago.csp.constant;

import tamago.csp.generic.CSPconst;
import tamago.csp.var.Realvar;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AINoInstruction;
import tamagocc.ast.impl.AIReal;

/**
 * @author Hakim Belhaouari
 *
 */
public final class CSPreal implements CSPconst {

	private double val;
	
	/**
	 * 
	 */
	public CSPreal(double value) {
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
		return (int)val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		return new Double(val);
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		return val;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		return (new Double(val)).toString();
	}


	public boolean equals(Object o) {
		if(o instanceof CSPreal) {
			return Realvar.q(val, ((CSPreal)o).realValue());
		}
		return false;
	}
	
	public String toString() {
		return (new Double(val)).toString();
	}

	public AExpression toAExpression() {
		return new AIReal(val);
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}
	public AInstruction toPostExpression() {
		return AINoInstruction.getNoInstruction();
	}
}
