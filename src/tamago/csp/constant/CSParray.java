/**
 * 
 */
package tamago.csp.constant;

import java.util.ArrayList;
import java.util.Collection;

import tamago.builder.TamagoBuilder;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.FDvar;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AIAffectation;
import tamagocc.ast.impl.AIIdent;
import tamagocc.ast.impl.AIInteger;
import tamagocc.ast.impl.AINewArray;
import tamagocc.ast.impl.AINoInstruction;
import tamagocc.ast.impl.AISequence;
import tamagocc.ast.impl.AIType;
import tamagocc.ast.impl.AIVariable;
import tamagocc.generic.api.GType;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.Pair;

/**
 * @author Hakim Belhaouari
 *
 */
public class CSParray implements CSPconst {
	private ArrayList<Pair<FDvar,TamagoBuilder>> vars;
	private GType type;
	private CSPinteger size;
	private String name;
	
	/**
	 * 
	 */
	public CSParray(Collection<Pair<FDvar,TamagoBuilder>> vars,GType type,CSPinteger size,String name) {
		this.vars = new ArrayList<Pair<FDvar,TamagoBuilder>>(vars);
		this.type = type;
		this.size = size;
		this.name = name;
	}

	public CSPinteger getSize() {
		return size;
	}
	
	public Pair<FDvar,TamagoBuilder> getElement(int i) {
		return vars.get(i);
	}
	
	public Iterable<Pair<FDvar,TamagoBuilder>> getElements() {
		return vars;
	}
	
	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	public boolean boolValue() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	public Object objectValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	public double realValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	public String stringValue() {
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#toAExpression()
	 */
	public AExpression toAExpression() {
		AINewArray ar = new AINewArray(AIType.generateType(type.getArrayType().getType()),size.intValue());
		return ar;
	}

	public AInstruction toPreExpression() {
		return AINoInstruction.getNoInstruction();
	}
	public AInstruction toPostExpression() {
		AISequence seq = new AISequence();
		for (Pair<FDvar,TamagoBuilder> item : vars) {
			CSPinteger pos = (CSPinteger) item.l().getValue();
			AIInteger ipos = new  AIInteger(pos.intValue());
			AIVariable var = new AIVariable(new AIIdent(name),ipos);
			try {
				AIAffectation affect = new AIAffectation(var,item.r().getCSPvar(null, null).getValue().toAExpression());
				seq.addInstruction(item.r().getCSPvar(null, null).getValue().toPreExpression());
				seq.addInstruction(affect);
			} catch (TamagoBuilderException e) {
				TamagoCCLogger.println(4, "Erreur durant the treatment of the index:"+pos.intValue());
			}
		}
		
		return seq;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		for (Pair<FDvar,TamagoBuilder> couple : vars) {
			sb.append("<");
			if(couple.l().isFixed())
				sb.append(couple.l().getValue().toString());
			else
				sb.append(couple.l().toString());
			sb.append(";");
			if(couple.r().getCSPvar().isFixed())
				sb.append(couple.r().getCSPvar().getValue().toString());
			else
				sb.append(couple.r().getCSPvar().toString());
			sb.append("> ");
		}
		sb.append("}");
		return sb.toString();
	}
}
