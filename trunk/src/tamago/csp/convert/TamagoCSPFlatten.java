/**
 * 
 */
package tamago.csp.convert;

import java.util.ArrayList;
import java.util.Iterator;

import tamagocc.api.TOpeName;
import tamagocc.exception.TamagoCCException;
import tamagocc.generic.TamagoCCGExpressionVisitor;
import tamagocc.generic.api.GAtPre;
import tamagocc.generic.api.GBool;
import tamagocc.generic.api.GCall;
import tamagocc.generic.api.GCast;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GInLabel;
import tamagocc.generic.api.GInState;
import tamagocc.generic.api.GInteger;
import tamagocc.generic.api.GIsBound;
import tamagocc.generic.api.GLanguageExpr;
import tamagocc.generic.api.GNil;
import tamagocc.generic.api.GNoContract;
import tamagocc.generic.api.GNot;
import tamagocc.generic.api.GOperator;
import tamagocc.generic.api.GRead;
import tamagocc.generic.api.GReal;
import tamagocc.generic.api.GReturn;
import tamagocc.generic.api.GString;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GICall;
import tamagocc.generic.impl.GICast;
import tamagocc.generic.impl.GINot;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIQuantifierVariable;

/**
 * @author Hakim Belhaouari
 *
 */
public class TamagoCSPFlatten implements TamagoCCGExpressionVisitor {

	private GExpression expr;
	private GExpression flatten;
	/**
	 * 
	 */
	public TamagoCSPFlatten(GExpression expr) {
		this.expr = expr;
		flatten = null;
	}
	
	public GExpression flatten() throws TamagoCCException {
		if(flatten == null) {
			flatten = (GExpression) expr.visitExpression(this);
		}
		return flatten;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitBool(tamagocc.generic.api.GBool)
	 */
	public Object visitBool(GBool bool) throws TamagoCCException {
		return bool;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitCall(tamagocc.generic.api.GCall)
	 */
	public Object visitCall(GCall call) throws TamagoCCException {
		ArrayList<GExpression> args = new ArrayList<GExpression>();
		Iterator<GExpression> gargs = call.getArguments();
		while(gargs.hasNext()) {
			GExpression expr = gargs.next();
			TamagoCSPFlatten flat = new TamagoCSPFlatten(expr);
			args.add(flat.flatten());
		}
		return new GICall(call.getName(),args); 
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitExpression(tamagocc.generic.api.GExpression)
	 */
	public Object visitExpression(GExpression expression)
			throws TamagoCCException {
		throw new TamagoCCException("Unsupported");
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitInLabel(tamagocc.generic.api.GInLabel)
	 */
	public Object visitInLabel(GInLabel inlabel) throws TamagoCCException {
		return inlabel;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitInteger(tamagocc.generic.api.GInteger)
	 */
	public Object visitInteger(GInteger interger) throws TamagoCCException {
		return interger;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitNil(tamagocc.generic.api.GNil)
	 */
	public Object visitNil(GNil nil) throws TamagoCCException {
		return nil;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitNoContract(tamagocc.generic.api.GNoContract)
	 */
	public Object visitNoContract(GNoContract nocontract)
			throws TamagoCCException {
		return nocontract;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitNot(tamagocc.generic.api.GNot)
	 */
	public Object visitNot(GNot not) throws TamagoCCException {
		TamagoCSPFlatten flat = new TamagoCSPFlatten(not.getTerm());
		if(flat.flatten() == not.getTerm())
			return not;
		else 
			return new GINot(flat.flatten());
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitOperator(tamagocc.generic.api.GOperator)
	 */
	public Object visitOperator(GOperator operator) throws TamagoCCException {
		Iterator<GExpression> exprs = operator.getOperands();
		ArrayList<GExpression> args = new ArrayList<GExpression>();
		while(exprs.hasNext()) {
			TamagoCSPFlatten flat = new TamagoCSPFlatten(exprs.next());
			if(flat.flatten().getCategory() == GExprType.OPERATOR) {
				GOperator ope = (GOperator) flat.flatten();
				if(ope.getOperator().equals(operator.getOperator()) 
						&& ((TOpeName.opAnd.equals(ope.getOperator()) ||TOpeName.opOr.equals(ope.getOperator()) )))
					addIterator(args,ope.getOperands());
				else
					args.add(ope);
					
			}
			else
				args.add(flat.flatten());
		}
		
		return new GIOperator(operator.getOperator(),args.iterator());
	}

	private void addIterator(ArrayList<GExpression> args, Iterator<GExpression> operands) {
		while(operands.hasNext()) {
			args.add(operands.next());
		}
		
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitPre(tamagocc.generic.api.GAtPre)
	 */
	public Object visitPre(GAtPre atpre) throws TamagoCCException {
		return atpre;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitQuantifierVariable(tamagocc.generic.impl.GIQuantifierVariable)
	 */
	public Object visitQuantifierVariable(GIQuantifierVariable variable)
			throws TamagoCCException {
		return variable;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitRead(tamagocc.generic.api.GRead)
	 */
	public Object visitRead(GRead read) throws TamagoCCException {
		return read;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitReal(tamagocc.generic.api.GReal)
	 */
	public Object visitReal(GReal real) throws TamagoCCException {
		return real;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitReturn(tamagocc.generic.api.GReturn)
	 */
	public Object visitReturn(GReturn ret) throws TamagoCCException {
		return ret;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitString(tamagocc.generic.api.GString)
	 */
	public Object visitString(GString string) throws TamagoCCException {
		return string;
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitVariable(tamagocc.generic.api.GVariable)
	 */
	public Object visitVariable(GVariable variable) throws TamagoCCException {
		return variable;
	}

	public Object visitLanguageExpr(GLanguageExpr languageExpr)
			throws TamagoCCException {
		return languageExpr;
	}

	public static GExpression flattern(GExpression expr2) throws TamagoCCException {
		TamagoCSPFlatten flat = new TamagoCSPFlatten(expr2);
		return flat.flatten();
	}

	/**
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitCast(tamagocc.generic.api.GCast)
	 */
	public Object visitCast(GCast cast) throws TamagoCCException {
		TamagoCSPFlatten flat = new TamagoCSPFlatten(cast.getExpression());
		return new GICast(cast.getType(), flat.flatten());
	}

	@Override
	public Object visitInState(GInState giInState) throws TamagoCCException {
		return giInState;
	}
	
	@Override
	public Object visitIsBound(GIsBound giIsBound) throws TamagoCCException {
		return giIsBound;
	}
}
