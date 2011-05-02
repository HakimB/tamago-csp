/**
 * 
 */
package tamago.csp.convert;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

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
import tamagocc.generic.impl.GIAtPre;
import tamagocc.generic.impl.GICall;
import tamagocc.generic.impl.GICast;
import tamagocc.generic.impl.GIInLabel;
import tamagocc.generic.impl.GINot;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIQuantifierVariable;
import tamagocc.generic.impl.GIRead;
import tamagocc.generic.impl.GIReturn;
import tamagocc.generic.impl.GIVariable;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author hakim
 *
 */
public class TamagoCSPGenericRename implements TamagoCCGExpressionVisitor {

	/**
	 * 
	 */
	public TamagoCSPGenericRename(boolean keepatpre,Hashtable<String, String> env,Hashtable<String, String> envar) {
		this.envprop = env;
		this.envvar = envar;
		atpre = false;
		this.keepatpre = keepatpre;
	}
	private Hashtable<String, String> envprop;
	private Hashtable<String, String> envvar;
	private transient boolean atpre;
	private boolean keepatpre;
	

	public Object visitBool(GBool bool) throws TamagoCCException {
		return bool;
	}

	public Object visitCall(GCall call) throws TamagoCCException {
		ArrayList<GExpression> arguments = new ArrayList<GExpression>();
		Iterator<GExpression> args = call.getArguments();
		while(args.hasNext()) {
			GExpression arg = args.next();
			arguments.add((GExpression) arg.visitExpression(this));
		}
		return new GICall(call.getName(),arguments);
	}

	public Object visitExpression(GExpression expression) throws TamagoCCException {
		throw new TamagoCCException("unsupported expression");
	}

	public Object visitInLabel(GInLabel inlabel) throws TamagoCCException {
		GExpression target  = (GExpression) inlabel.getTarget().visitExpression(this);
		GExpression subexpr = (GExpression) inlabel.getSubExpression().visitExpression(this);
		
		GIInLabel lab = new GIInLabel(target,subexpr);
		return lab;
	}

	public Object visitInteger(GInteger interger) throws TamagoCCException {
		return interger;
	}

	public Object visitLanguageExpr(GLanguageExpr languageExpr) throws TamagoCCException {
		return languageExpr;
	}

	public Object visitNil(GNil nil) throws TamagoCCException {
		return nil;
	}

	public Object visitNoContract(GNoContract nocontract) throws TamagoCCException {
		return nocontract;
	}

	public Object visitNot(GNot not) throws TamagoCCException {
		return new GINot((GExpression) not.getTerm().visitExpression(this));
	}

	public Object visitOperator(GOperator operator) throws TamagoCCException {
		
		GIOperator renvoie = new GIOperator(operator.getOperator());
		
		Iterator<GExpression> opes = operator.getOperands();
		while(opes.hasNext()) {
			GExpression ope = opes.next();
			renvoie.addOperand((GExpression) ope.visitExpression(this));
		}

		return renvoie;
	}

	public Object visitPre(GAtPre atpre) throws TamagoCCException {
		this.atpre = true;
		GExpression expr = (GExpression) atpre.getRawExpr().visitExpression(this);
		this.atpre = false;
		if(keepatpre)
			return new GIAtPre(expr,atpre.getRawType());//((GVariable)atpre.getTerm()).getType());
		else
			return expr;
	}

	public Object visitQuantifierVariable(GIQuantifierVariable variable) throws TamagoCCException {
		return variable;
	}

	public Object visitRead(GRead read) throws TamagoCCException {
		
		/*if(this.atpre) {
			return read;
		}
		else*/ 
		{
			if(envprop != null && envprop.containsKey(read.getName())) {
				String name = envprop.get(read.getName());
				if(read.hasIndex()) {
					return new GIRead(name,(GExpression) read.getIndex().visitExpression(this));
				}
				else
					return new GIRead(name);
				
			}
			else {
				TamagoCCLogger.infoln(3,"*Warning*: Unfound property in renaming environment: "+read.getName());
				if(read.hasIndex()) {
					GExpression index = (GExpression)read.getIndex().visitExpression(this);
					if(index == read.getIndex())
						return read;
					else
						return new GIRead(read.getName(), index);
				}
				else
					return read;
			}
		}
	}

	public Object visitReal(GReal real) throws TamagoCCException {
		return real;
	}

	public Object visitReturn(GReturn ret) throws TamagoCCException {
		if(ret.hasIndex()) {
			GExpression index = (GExpression)ret.getIndex().visitExpression(this);
			if(index == ret.getIndex())
				return ret;
			else
				return new GIReturn(ret.getVariable(), index);
		}
		return ret;
	}

	public Object visitString(GString string) throws TamagoCCException {
		return string;
	}

	public Object visitVariable(GVariable variable) throws TamagoCCException {
		if(envvar != null && envvar.containsKey(variable.getName())) {
			String name = envvar.get(variable.getName());
			if(variable.hasIndex())
				return new GIVariable(name,variable.getType(),(GExpression) variable.getIndex().visitExpression(this));
			else
				return new GIVariable(name,variable.getType());
		}
		else {
			if(variable.hasIndex()) {
				GExpression index = (GExpression)variable.getIndex().visitExpression(this);
				if(index == variable.getIndex())
					return variable; // aucune modif
				else
					return new GIVariable(variable.getName(),variable.getType(),index);
			}
			else
				return variable;
		}
	}

	/**
	 * TODO pas sur de l'implantation
	 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitCast(tamagocc.generic.api.GCast)
	 */
	public Object visitCast(GCast cast) throws TamagoCCException {
		return new GICast(cast.getType(), (GExpression)cast.getExpression().visitExpression(this));
	}

	@Override
	public Object visitInState(GInState giInState) throws TamagoCCException {
		return giInState;
	}

}
