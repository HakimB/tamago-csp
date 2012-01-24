/**
 * 
 */
package tamago.csp.convert;

import java.util.ArrayList;
import java.util.Iterator;

import tamago.builder.IntegerBuilderFactory;
import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.builder.TransparencyBuilderFactory;
import tamago.builder.impl.Builderarray;
import tamago.builder.impl.Builderint;
import tamago.builder.impl.Buildertransparency;
import tamago.csp.Backtracking;
import tamago.csp.TamagoCSP;
import tamago.csp.array.ArrayIndexvar;
import tamago.csp.array.Arrayvar;
import tamago.csp.constant.CSPbool;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constant.CSPnull;
import tamago.csp.constant.CSPreal;
import tamago.csp.constant.CSPstring;
import tamago.csp.constraint.AllDifferent;
import tamago.csp.constraint.EqLinear;
import tamago.csp.constraint.ExistConstraintArray;
import tamago.csp.constraint.ForallConstraintArray;
import tamago.csp.constraint.IneqLinear;
import tamago.csp.constraint.IneqOperator;
import tamago.csp.constraint.IntervalArrayConstraint;
import tamago.csp.constraint.LinearConstraint;
import tamago.csp.constraint.NotXBool;
import tamago.csp.constraint.XBool;
import tamago.csp.constraint.XEqC;
import tamago.csp.constraint.XEqY;
import tamago.csp.constraint.XGeqC;
import tamago.csp.constraint.XGeqY;
import tamago.csp.constraint.XGtC;
import tamago.csp.constraint.XGtY;
import tamago.csp.constraint.XLeqC;
import tamago.csp.constraint.XLeqY;
import tamago.csp.constraint.XNeqC;
import tamago.csp.constraint.XNeqY;
import tamago.csp.constraint.XUnary;
import tamago.csp.constraint.XltC;
import tamago.csp.constraint.XltY;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.CSPvariableForQuantifier;
import tamago.csp.generic.FDvar;
import tamago.csp.generic.Obvar;
import tamago.csp.var.Intvar;
import tamagocc.api.TOpeName;
import tamagocc.exception.TamagoCCException;
import tamagocc.generic.TamagoCCGExpressionVisitor;
import tamagocc.generic.api.GAtPre;
import tamagocc.generic.api.GBool;
import tamagocc.generic.api.GCall;
import tamagocc.generic.api.GCast;
import tamagocc.generic.api.GExistColl;
import tamagocc.generic.api.GExistRange;
import tamagocc.generic.api.GExistSet;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GForallColl;
import tamagocc.generic.api.GForallRange;
import tamagocc.generic.api.GForallSet;
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
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIQuantifierVariable;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoFreshVar;

/**
 * @author Hakim Belhaouari
 *
 */
public class TamagoCSPInferConstraint {

	private TamagoEnvironment ctx;
	private TamagoCSP csp;


	public TamagoCSP getCSP() {
		return csp;
	}

	/**
	 * @param csp 
	 * 
	 */
	public TamagoCSPInferConstraint(TamagoCSP csp, TamagoEnvironment ctx) {
		this.ctx = ctx;
		this.csp = csp;
	}

	public boolean generate(GExpression e)  {
		TamagoCSPInferConstrainMain cont = new TamagoCSPInferConstrainMain(this);
		try {
			Object res = e.visitExpression(cont); 
			{
				if(res instanceof CSPConstraint) {
					csp.addConstraint((CSPConstraint) res);
					return true;
				}
				else if(res instanceof CSPvar) {
					csp.addConstraint(new XBool((CSPvar)res));
					return true;
				}
				else {
					return false;
				}
			}
		} catch (TamagoCCException e1) {
			TamagoCCLogger.info(2,e1);
			return false;
		}
		catch(Exception ex) {
			TamagoCCLogger.info(2, ex);
			return false;
		}
	}


	public Object convert(GExpression e) {
		TamagoCSPInferConstrainMain cont = new TamagoCSPInferConstrainMain(this);
		try {
			return e.visitExpression(cont);
		}
		catch(Exception ex) {
			TamagoCCLogger.info(3,ex);
			return null;
		}

	}

	/**
	 * Sub class for identifying the correct constraint
	 * @author Hakim Belhaouari
	 *
	 */
	class TamagoCSPInferConstrainMain implements TamagoCCGExpressionVisitor {

		private TamagoCSPInferConstraint owner;

		public TamagoCSPInferConstrainMain(TamagoCSPInferConstraint constraint) {
			owner = constraint;
		}

		public Object visitPre(GAtPre atpre) throws TamagoCCException {
			TamagoCCLogger.print(3,"Warning unsupported @pre");
			return null;
		}

		public Object visitBool(GBool bool) throws TamagoCCException {
			return new CSPbool(bool.getValue());
		}

		public Object visitCall(GCall call) throws TamagoCCException {
			Iterator<GExpression> exprs = call.getArguments();
			while(exprs.hasNext()) {
				GExpression expr = exprs.next();
				CSPvar cspvar = (CSPvar) expr.visitExpression(this);
				
			}
			
			throw new TamagoCCException("Unsupported element : call");
		}

		public Object visitExpression(GExpression expression) throws TamagoCCException {
			throw new TamagoCCException("Unsupported element");
		}

		public Object visitInLabel(GInLabel inlabel) throws TamagoCCException {
			GExpression scope = inlabel.getTarget();
			String var = "";
			if(scope.getCategory() == GExprType.VARIABLE) {
				var = ((GVariable)scope).getName();
			}
			if(scope.getCategory() == GExprType.READ) {
				var = ((GRead)scope).getName();
			}
			if(scope.getCategory() == GExprType.RETURN) {
				var = ((GReturn)scope).getVariable().getName();
			}

			if(ctx.containsKey(var)) {
				TamagoBuilder builder = ctx.get(var);
				TamagoEnvironment env = builder.getEnvironment();
				builder.setEnvironment(ctx);
				CSPvar res = builder.getCSPvar(owner,inlabel);
				builder.setEnvironment(env);
				return res;
			}
			else
				throw new TamagoCCException("Unfound the builder of the variable : "+var);
		}

		public Object visitInteger(GInteger interger) throws TamagoCCException {
			return new CSPinteger(interger.getValue());
		}

		public Object visitNil(GNil nil) throws TamagoCCException {
			return new CSPnull();
		}

		public Object visitNoContract(GNoContract nocontract) throws TamagoCCException {
			//throw new TamagoCCException("No contract");
			return null;
		}

		public Object visitNot(GNot not) throws TamagoCCException {
			Object onf = not.getTerm().visitExpression(this);
			// TODO verifie si le terme est bien un atome;
			if(onf instanceof CSPvar) {
				return new NotXBool((CSPvar)onf);
			}
			else {
				TamagoCCLogger.print(3, "Unsupported element");
				return null;
			}
		}

		private IneqOperator convertOpeName(TOpeName op) throws TamagoCCException {
			switch (op.getID()) {
			case TOpeName.EG:
				return IneqOperator.EQ;
			case TOpeName.NE:
				return IneqOperator.NE;
			case TOpeName.INF:
				return IneqOperator.LT;
			case TOpeName.INFEG:
				return IneqOperator.LE;
			case TOpeName.SUP:
				return IneqOperator.GT;
			case TOpeName.SUPEG:
				return IneqOperator.GE;
			default:
				throw new TamagoCCException("Unkown operator");
			}
		}

		public Object visitOperator(GOperator operator) throws TamagoCCException {
			if(operator.getArity() == 2  && operator.getOperator().getID() == TOpeName.EG && 
					(hasArithOper(operator)))
			{
				TamagoCCLogger.println(3, "Linear equation detected!");
				EqLinear eqlin = new EqLinear();
				Iterator<GExpression> opes = operator.getOperands();
				boolean inv = false;
				while(opes.hasNext()) {
					GExpression ope = opes.next();
					parseArgument(eqlin,ope,inv);
					inv = !inv;
				}
				return eqlin;
			}

			if(operator.getArity() == 2  && (hasArithOper(operator)))
			{
				TamagoCCLogger.println(3, "Linear inequation detected!");
				IneqLinear ineqlin = new IneqLinear(convertOpeName(operator.getOperator()));
				Iterator<GExpression> opes = operator.getOperands();
				boolean inv = false;
				while(opes.hasNext()) {
					GExpression ope = opes.next();
					parseArgument(ineqlin,ope,inv);
					inv = !inv;
				}
				return ineqlin;
			}


			if(operator.getArity() > 2) {
				TamagoCCLogger.print(3, "Arity different of 2 -> unsupported");
				return null;
			}
			switch(operator.getOperator().getID()) {
			case TOpeName.AND:
			case TOpeName.OR:
				throw new TamagoCCException("Problem during the construction in DNF");
			case TOpeName.EG: {
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					GExpression tmp = fsd.next();
					ops[i] = tmp.visitExpression(this);
					i++;
				}
				if((ops[0] instanceof CSPvar)&&(ops[1] instanceof CSPvar))
					return new XEqY((CSPvar) ops[0], (CSPvar)ops[1]);
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst)) {
					if(ops[1] instanceof CSPnull) {
						((Obvar)ops[0]).forceNull();
						return new XUnary((CSPvar) ops[0]);
					}
					else
						return new XEqC((CSPvar)ops[0],(CSPconst)ops[1]);
				}
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst)) {
					if(ops[0] instanceof CSPnull) {
						((Obvar)ops[1]).forceNull();
						return new XUnary((CSPvar) ops[1]);
					}
					else
						return new XEqC((CSPvar)ops[1],(CSPconst)ops[0]);
				}
				else {
					throw new TamagoCCException("Unsupported Constraint");
				}
			}
			case TOpeName.INF: {
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					ops[i] = fsd.next().visitExpression(this);
					i++;
				}
				if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst))
					return new XltC((FDvar)ops[0],(CSPconst)ops[1]);
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst))
					return new XGtC((FDvar)ops[1],(CSPconst)ops[0]);
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPvar))
					return new XltY((FDvar)ops[0],(FDvar)ops[1]);
				else
					throw new TamagoCCException("Unsupported Constraint");
			}
			case TOpeName.INFEG: {
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					ops[i] = fsd.next().visitExpression(this);
					i++;
				}

				if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst))
					return new XLeqC((FDvar)ops[0],(CSPconst)ops[1]);
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst))
					return new XGeqC((FDvar)ops[1],(CSPconst)ops[0]);
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPvar))
					return new XLeqY((FDvar)ops[0],(FDvar)ops[1]);
				else
					throw new TamagoCCException("Unsupported Constraint");
			}
			case TOpeName.NE: {
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					ops[i] = fsd.next().visitExpression(this);
					i++;
				}
				if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst)) {
					if(ops[1] instanceof CSPnull) {
						((Obvar)ops[0]).forceNotNull();
						return new XUnary((CSPvar) ops[0]);
					}
					else
						return new XNeqC((FDvar)ops[0],(CSPconst)ops[1]);
				}
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst)) {
					if(ops[0] instanceof CSPnull) {
						((Obvar)ops[1]).forceNotNull();
						return new XUnary((CSPvar) ops[1]);
					}
					else
						return new XNeqC((FDvar)ops[1],(CSPconst)ops[0]);
				}
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPvar))
					return new XNeqY((CSPvar)ops[0],(CSPvar)ops[1]);
				else
					throw new TamagoCCException("Unsupported Constraint");	
			}
			case TOpeName.SUP: {
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					ops[i] = fsd.next().visitExpression(this);
					i++;
				}
				if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst))
					return new XGtC((FDvar)ops[0],(CSPconst)ops[1]);
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst))
					return new XltC((FDvar)ops[1],(CSPconst)ops[0]);
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPvar))
					return new XGtY((FDvar)ops[0],(FDvar)ops[1]);
				else
					throw new TamagoCCException("Unsupported Constraint");
			}
			case TOpeName.SUPEG:{
				Object[] ops = new Object[2];
				Iterator<GExpression> fsd=  operator.getOperands();
				int i= 0;
				while(fsd.hasNext()) {
					ops[i] = fsd.next().visitExpression(this);
					i++;
				}
				if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPconst))
					return new XGeqC((FDvar)ops[0],(CSPconst)ops[1]);
				else if((ops[1] instanceof CSPvar) && (ops[0] instanceof CSPconst))
					return new XLeqC((FDvar)ops[1],(CSPconst)ops[0]);
				else if((ops[0] instanceof CSPvar) && (ops[1] instanceof CSPvar))
					return new XGeqY((FDvar)ops[0],(FDvar)ops[1]);
				else
					throw new TamagoCCException("Unsupported Constraint");
			}
			}
			throw new TamagoCCException("Unsupported Constraint");
		}

		private Intvar number(int i) throws TamagoCCException {
			Intvar var = new Intvar(""+i,new Backtracking(),i,i);
			try {
				var.fix(new CSPinteger(i));
			} catch (TamagoCSPException e) {
				throw new TamagoCCException(e);
			}
			return var;
		}

		/*private Realvar number(double i) throws TamagoCCException {
			Realvar var = new Realvar(""+i,new Backtracking(),i,i);
			try {
				var.fix(new CSPreal(i));
			} catch (TamagoCSPException e) {
				throw new TamagoCCException(e);
			}
			return var;
		}*/

		private void parseArgument(LinearConstraint eqlin, GExpression ope, boolean inv) throws TamagoCCException {
			switch(ope.getCategory()) {
			case READ:
				if(inv) {
					eqlin.put(new CSPinteger(-1),(FDvar) ope.visitExpression(this));
				}
				else {
					eqlin.put(new CSPinteger( 1),(FDvar) ope.visitExpression(this));
				}
				break;
			case RETURN:
				if(inv) {
					eqlin.put(new CSPinteger(-1),(FDvar) ope.visitExpression(this));
				}
				else {
					eqlin.put(new CSPinteger( 1),(FDvar) ope.visitExpression(this));
				}
				break;
			case INLABEL:
				if(inv) {
					eqlin.put(new CSPinteger(-1),(FDvar) ope.visitExpression(this));
				}
				else {
					eqlin.put(new CSPinteger( 1),(FDvar) ope.visitExpression(this));
				}
				break;
			case VARIABLE:
				if(inv) {
					eqlin.put(new CSPinteger(-1),(FDvar) ope.visitExpression(this));
				}
				else {
					eqlin.put(new CSPinteger( 1),(FDvar) ope.visitExpression(this));
				}
				break;
			case INT:
				if(inv) {
					eqlin.put((CSPconst)ope.visitExpression(this),number(-1));
				}
				else {
					eqlin.put((CSPconst)ope.visitExpression(this),number(1));
				}
				break;
			case OPERATOR:
				GOperator expr = (GOperator) ope;
				switch(expr.getOperator().getID()) {
				case TOpeName.PLUS: {
					Iterator<GExpression> exprs = expr.getOperands();
					while(exprs.hasNext()) {
						parseArgument(eqlin, exprs.next(),inv);
					}
					break;
				}
				case TOpeName.MINUS: {
					Iterator<GExpression> exprs = expr.getOperands();
					if(expr.getArity() < 2)
						throw new TamagoCCException("Unsupported operation (subtract)");
					parseArgument(eqlin, exprs.next(),inv);
					boolean ninv = !inv;
					while(exprs.hasNext()) {
						parseArgument(eqlin, exprs.next(),ninv);
					}
					break;
				}
				case TOpeName.TIMES:{
					Iterator<GExpression> exprs = expr.getOperands();
					while(exprs.hasNext()) {
						Object o = exprs.next().visitExpression(this);
						if(o instanceof CSPconst) {
							if(inv)
								eqlin.put((CSPconst)o,number(-1));
							else
								eqlin.put((CSPconst)o,number( 1));
						}
						else {
							if(inv)
								eqlin.put((FDvar)o,new CSPinteger(-1));
							else
								eqlin.put((FDvar)o,new CSPinteger( 1));
						}
					}
					break;
				}
				case TOpeName.MOD:
					throw new TamagoCCException("Unsupported expression (modulo)");
				case TOpeName.QUO:
					throw new TamagoCCException("Unsupported expression (quotient)");
				default:
					throw new TamagoCCException("Unsupported expression");
				}
				break;
				// end operator;
			default:
				throw new TamagoCCException("Unsupported expression ("+ope.getCategory()+"): "+ope.toString());
			}

		}

		private boolean hasArithOper(GOperator operator) {
			Iterator<GExpression> opes = operator.getOperands();
			boolean res = false;
			while(opes.hasNext()) {
				GExpression ope = opes.next();
				res = isArith(ope) || res;
			}
			return res;
		}

		private boolean isArith(GExpression ope) {
			try {
				switch(((GOperator)ope).getOperator().getID()) {
				case TOpeName.PLUS:
				case TOpeName.MINUS:
				case TOpeName.MOD:
				case TOpeName.QUO:
				case TOpeName.TIMES:
					return true;
				default:
					return false;
				}
			}
			catch(Exception e) {
				return false;
			}
		}

		public Object visitQuantifierVariable(GIQuantifierVariable variable) throws TamagoCCException {
			GType settype = variable.getQuantifier().getType();
			GType settypetab = GIType.generateType(variable.getQuantifier().getType().getType() + "[]");
			
			switch(variable.getQuantifier().getQuantifierType()) {
			case EXISTCOLL: {
				TamagoCCLogger.println(4, "\t\tQuantifier: EXISTS on collection");
				GExpression collection = ((GExistColl) variable.getQuantifier()).getCollection();
				CSPvar var = (CSPvar) collection.visitExpression(this);
				ExistConstraintArray eca = new ExistConstraintArray((CSPvariableForQuantifier) var,variable,csp,ctx);
				return eca;
			}
			case EXISTRANGE: {
				TamagoCCLogger.println(4, "\t\tQuantifier: EXISTS on range");
				Builderarray barray = new Builderarray(new IntegerBuilderFactory(),
						owner.ctx,TamagoFreshVar.Default.getName("__internal_range"),
						 settypetab,csp.getBacktrack());
				
				String name_range_min = TamagoFreshVar.Default.getName("__range_min");
				String name_range_max = TamagoFreshVar.Default.getName("__range_max");
				
				Builderint minint = new Builderint(ctx,name_range_min,GIType.TYPE_INT,csp.getBacktrack());
				Builderint maxint = new Builderint(ctx,name_range_max,GIType.TYPE_INT,csp.getBacktrack());
				ctx.put(name_range_min, minint);
				ctx.put(name_range_max, maxint);
				
				GExistRange range = (GExistRange)variable.getQuantifier();
				IntervalArrayConstraint iac;
				try {
					GIOperator opemin = new GIOperator(TOpeName.opEg);
					opemin.addOperand(new GIVariable(name_range_min, GIType.TYPE_INT));
					opemin.addOperand(range.getMin());
					
					GIOperator opemax = new GIOperator(TOpeName.opEg);
					opemax.addOperand(new GIVariable(name_range_max, GIType.TYPE_INT));
					opemax.addOperand(range.getMax());
					
					generate(opemin);
					generate(opemax);
					
					ctx.remove(name_range_min);
					ctx.remove(name_range_max);
					
					iac = new IntervalArrayConstraint(barray,(FDvar) minint.getCSPvar(), (FDvar)maxint.getCSPvar(), csp, ctx);
					csp.addConstraint(iac);
				} catch (TamagoCSPException e) {
					throw new TamagoCCException(e);
				}
				ExistConstraintArray eca = new ExistConstraintArray((CSPvariableForQuantifier) barray.getCSPvar(),variable,csp,ctx);
				return eca;
			}
			case EXISTSET: {
				TamagoCCLogger.println(4, "\t\tQuantifier: EXISTS on set");
				
				Builderarray barray = new Builderarray(new TransparencyBuilderFactory(),owner.ctx,"__internal_set__tamagocsp_",settypetab,csp.getBacktrack());
				Arrayvar array = (Arrayvar) barray.getCSPvar();

				csp.addVariable(array);
				
				XEqC cst1 = new XEqC(array.getLength(),new CSPinteger( ((GExistSet) variable.getQuantifier()).getSet().size()));
				AllDifferent alldif = new AllDifferent();
				for (GExpression element : ((GExistSet) variable.getQuantifier()).getSet().getElements() )	{
					Object o = convert(element);
					if(o instanceof CSPconst) {
						ArrayIndexvar idx = new ArrayIndexvar(array.getLength(),csp.getBacktrack());
						Buildertransparency bt = new Buildertransparency(owner.ctx,"__internal_set_element_cst_"+idx.getName()+"_"+o.toString(),settype,csp.getBacktrack());
						alldif.add(idx);
						try {
							array.putElem(idx, bt);
						} catch (TamagoCSPException e) {
							TamagoCCLogger.println(2,"EXIST SET CONSTRAINT: A variable failed, system may be instable");
							TamagoCCLogger.infoln(3, e);
						}
						XEqC cst = new XEqC(bt.getCSPvar(),(CSPconst)o);
						csp.addConstraint(cst);
					}
					else if(o instanceof CSPvar) {
						CSPvar evar = (CSPvar) o;
						ArrayIndexvar idx = new ArrayIndexvar(array.getLength(),csp.getBacktrack());
						Buildertransparency bt = new Buildertransparency(owner.ctx,"__internal_set_element_tamagocsp_"+idx.getName(),settype,csp.getBacktrack());
						alldif.add(idx);
						try {
							array.putElem(idx, bt);
						} catch (TamagoCSPException e) {
							TamagoCCLogger.println(2,"EXIST SET CONSTRAINT: A variable failed, system may be instable");
							TamagoCCLogger.infoln(3, e);
						}

						XEqY cst = new XEqY(evar,bt.getCSPvar());
						csp.addConstraint(cst);
					}
				}
				csp.addConstraint(cst1);
				csp.addConstraint(alldif);

				ExistConstraintArray eca = new ExistConstraintArray((CSPvariableForQuantifier) array,variable,csp,ctx);
				return eca;
			}
			case FORALLCOLL: {
				TamagoCCLogger.println(4, "\t\tQuantifier: FORALL on collection");
				// 1 faire une contrainte sur la collection
				// 2 enregistrer la contrainte dynamique pour reperer les ajouts
				//    de variables de positions

				GExpression collection = ((GForallColl) variable.getQuantifier()).getCollection();
				CSPvar var = (CSPvar) collection.visitExpression(this);

				ForallConstraintArray fca = new ForallConstraintArray((CSPvariableForQuantifier) var,variable,csp,ctx);
				return fca;
			}
			case FORALLRANGE: {
				TamagoCCLogger.println(4, "\t\tQuantifier: FORALL on range");
				Builderarray barray = new Builderarray(new IntegerBuilderFactory(),owner.ctx,TamagoFreshVar.Default.getName("__internal_tabrange"),settypetab,csp.getBacktrack());
				
				String name_range_min = TamagoFreshVar.Default.getName("__range_min");
				String name_range_max = TamagoFreshVar.Default.getName("__range_max");
				
				Builderint minint = new Builderint(ctx,name_range_min,GIType.TYPE_INT,csp.getBacktrack());
				Builderint maxint = new Builderint(ctx,name_range_max,GIType.TYPE_INT,csp.getBacktrack());
				ctx.put(name_range_min, minint);
				ctx.put(name_range_max, maxint);				
				
				GForallRange range = (GForallRange)variable.getQuantifier();
				IntervalArrayConstraint iac;
				try {
					GIOperator opemin = new GIOperator(TOpeName.opEg);
					opemin.addOperand(new GIVariable(name_range_min, GIType.TYPE_INT));
					opemin.addOperand(range.getMin());
					
					GIOperator opemax = new GIOperator(TOpeName.opEg);
					opemax.addOperand(new GIVariable(name_range_max, GIType.TYPE_INT));
					opemax.addOperand(range.getMax());
					
					generate(opemin);
					generate(opemax);
					
					ctx.remove(name_range_min);
					ctx.remove(name_range_max);
					
					iac = new IntervalArrayConstraint(barray, (FDvar)minint.getCSPvar(), (FDvar)maxint.getCSPvar(), csp, ctx);
					csp.addConstraint(iac);
				} catch (TamagoCSPException e) {
					throw new TamagoCCException(e);
				}
				ForallConstraintArray fca = new ForallConstraintArray((CSPvariableForQuantifier) barray.getCSPvar(),variable,csp,ctx);
				return fca;
			}
			case FORALLSET: {
				TamagoCCLogger.println(4, "\t\tQuantifier: FORALL on set");
				Builderarray barray = new Builderarray(new TransparencyBuilderFactory(),owner.ctx,TamagoFreshVar.Default.getName("__internal_set__tamagocsp_"),settypetab,csp.getBacktrack());
				Arrayvar array = (Arrayvar) barray.getCSPvar();

				csp.addVariable(array);
				
				XEqC cst1 = new XEqC(array.getLength(),new CSPinteger( ((GForallSet) variable.getQuantifier()).getSet().size()));
				AllDifferent alldif = new AllDifferent();
				for (GExpression element : ((GForallSet) variable.getQuantifier()).getSet().getElements() )	{
					Object o = convert(element);
					if(o instanceof CSPconst) {
						ArrayIndexvar idx = new ArrayIndexvar(array.getLength(),csp.getBacktrack());
						Buildertransparency bt = new Buildertransparency(owner.ctx,"__internal_set_element_cst_"+idx.getName()+"_"+o.toString(),settype,csp.getBacktrack());
						alldif.add(idx);
						try {
							array.putElem(idx, bt);
						} catch (TamagoCSPException e) {
							TamagoCCLogger.println(2,"FORALL SET CONSTRAINT: A variable failed, system may be instable");
							TamagoCCLogger.infoln(3, e);
						}
						XEqC cst = new XEqC(bt.getCSPvar(),(CSPconst)o);
						csp.addConstraint(cst);
					}
					else if(o instanceof CSPvar) {
						CSPvar evar = (CSPvar) o;
						ArrayIndexvar idx = new ArrayIndexvar(array.getLength(),csp.getBacktrack());
						Buildertransparency bt = new Buildertransparency(owner.ctx,"__internal_set_element_tamagocsp_"+idx.getName(),settype,csp.getBacktrack());
						alldif.add(idx);
						try {
							array.putElem(idx, bt);
						} catch (TamagoCSPException e) {
							TamagoCCLogger.println(2,"FORALL SET CONSTRAINT: A variable failed, system may be instable");
							TamagoCCLogger.infoln(3, e);
						}

						XEqY cst = new XEqY(evar,bt.getCSPvar());
						csp.addConstraint(cst);
					}
				}
				csp.addConstraint(cst1);
				csp.addConstraint(alldif);

				ForallConstraintArray fca = new ForallConstraintArray((CSPvariableForQuantifier) array,variable,csp,ctx);
				return fca;		
			}
			}
			return null;
		}

		public Object visitRead(GRead read) throws TamagoCCException {
			if(ctx.containsKey(read.getName())) {
				TamagoBuilder builder = ctx.get(read.getName());
				return builder.getCSPvar(owner,read);
			}
			else
				throw new TamagoCCException("Unfound the builder of the property : "+read.getName());
		}

		public Object visitReal(GReal real) throws TamagoCCException {
			return new CSPreal(real.getValue());
		}

		public Object visitReturn(GReturn ret) throws TamagoCCException {
			if(ctx.containsKey(ret.getVariable().getName())) {
				TamagoBuilder builder = ctx.get(ret.getVariable().getName());
				return builder.getCSPvar(owner,ret);
			}
			else
				throw new TamagoCCException("Unfound the builder of the return value : "+ret.getVariable().getName());
		}

		public Object visitString(GString string) throws TamagoCCException {
			return new CSPstring(string.getValue());
		}

		public Object visitVariable(GVariable variable) throws TamagoCCException {
			if(ctx.containsKey(variable.getName())) {
				TamagoBuilder builder = ctx.get(variable.getName());
				return builder.getCSPvar(owner,variable);
			}
			else
				throw new TamagoCCException("Unfound the builder of the variable : "+variable.getName());
		}

		public Object visitLanguageExpr(GLanguageExpr languageExpr)
		throws TamagoCCException {
			throw new TamagoCCException("Unsupported element : languageExpr");
		}

		/**
		 * @see tamagocc.generic.TamagoCCGExpressionVisitor#visitCast(tamagocc.generic.api.GCast)
		 */
		public Object visitCast(GCast cast) throws TamagoCCException {
			throw new TamagoCCException("Unsupported element : Cast type");
		}

		@Override
		public Object visitInState(GInState giInState) throws TamagoCCException {
			throw new TamagoCCException("Unsupported element (@instate)");
		}
		
		@Override
		public Object visitIsBound(GIsBound giIsBound) throws TamagoCCException {
			throw new TamagoCCException("Unsupported element (@isBound)");
		}
		
	}// end inner class

	/**
	 * @return
	 */
	public TamagoEnvironment getEnvironment() {
		return ctx;
	}
}