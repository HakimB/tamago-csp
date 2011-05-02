/**
 * 
 */
package tamago.csp.generic;

import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;

/**
 * This interface represents a constant in the CSPvar
 * 
 * @author Hakim Belhaouari
 *
 */
public interface CSPconst {
	
	boolean boolValue();
	
	int intValue();
	
	double realValue();
	
	String stringValue();
	
	Object objectValue();
	
	String toString();
	
	boolean equals(Object c);
	
	AInstruction toPreExpression();
	AInstruction toPostExpression();
	AExpression toAExpression();
}
