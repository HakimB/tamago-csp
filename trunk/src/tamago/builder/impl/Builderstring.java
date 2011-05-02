/**
 * 
 */
package tamago.builder.impl;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.Backtracking;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constant.CSPreal;
import tamago.csp.constraint.EqLinear;
import tamago.csp.constraint.XLeqY;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.stringbuilder.CompareStringvar;
import tamago.csp.stringbuilder.InferAuto;
import tamago.csp.stringbuilder.InferAutoConst;
import tamago.csp.stringbuilder.InferAutoFD;
import tamago.csp.stringbuilder.MainStringvar;
import tamago.csp.stringbuilder.Stringvar;
import tamago.csp.stringbuilder.SubStringvar;
import tamagocc.api.TOpeName;
import tamagocc.generic.api.GCall;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GInLabel;
import tamagocc.generic.api.GInteger;
import tamagocc.generic.api.GRead;
import tamagocc.generic.api.GReal;
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIInteger;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class Builderstring extends TamagoBuilder {

	private static int pos = 0;
	private static String freshTmpName() {
		return "__builderstring#substring#tmp"+(pos++);
	}
	
	
	private MainStringvar str;
	// variable pour la taille
	private Backtracking b;
	
	/**
	 * @param name : name of the current string builder
	 */
	public Builderstring(TamagoEnvironment env,String name,GType type, Backtracking b) {
		super(env,name, GIType.TYPE_STRING,b);
		str = new MainStringvar(name,b);
		this.b = b;
	}
	
	
	public Builderstring(MainStringvar substitute, TamagoEnvironment env,String name,GType type, Backtracking b) {
		super(env,name, GIType.TYPE_STRING,b);
		str = substitute;
		this.b = b;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getCSPvar(tamagocc.generic.api.GExpression)
	 */
	public CSPvar getCSPvar(TamagoCSPInferConstraint infer,GExpression e) throws TamagoBuilderException {
		if(e == null)
			return str;
		
		if(e.getCategory() == GExprType.VARIABLE)
			return str;
		else if(e.getCategory() == GExprType.READ)
			return str;
		else if(e.getCategory() == GExprType.RETURN)
			return str;
		else if(e.getCategory() == GExprType.INLABEL) {
			GExpression call = ((GInLabel)e).getSubExpression();
			if(call.getCategory() == GExprType.CALL) {
				if(((GCall)call).getName().equals("length"))
					return str.getIntvar();
				else if(((GCall)call).getName().equals("compareTo") && (((GCall)call).getArgCount() == 1)) {
					GExpression arg = ((GCall)call).getArgument(0);
					Stringvar v = (Stringvar) infer.convert(arg);
					CompareStringvar cs = new CompareStringvar(str.getName()+".compareTo("+v.getName()+")",b,str,v);
					str.addDepends(cs);
					return cs.getCompareTo();
				}
				else if(((GCall)call).getName().equals("substring") && (((GCall)call).getArgCount() == 2)) {
					 InferAuto arg0 = convertInferAuto(infer,((GCall)call).getArgument(0));
					 InferAuto arg1 = convertInferAuto(infer,((GCall)call).getArgument(1));
					 SubStringvar subvar = new SubStringvar(str.getName()+".substring",b,str,arg0,arg1);
					 // la taille est la différence des deux indexes
					 // Donc contraintes du type
					 // aX+bY=cS
					 EqLinear eql = new EqLinear();
					 eql.put(new CSPinteger(-1), arg0.length());
					 eql.put(new CSPinteger( 1), arg1.length());
					 eql.put(new CSPinteger(-1), subvar.getIntvar());
					 str.addDepends(subvar);
					 
					 str.getIntvar().install(new XLeqY(subvar.getIntvar(),str.getIntvar()));
					 return subvar;
				}
				throw new TamagoBuilderException("Unsupported expression");
			}
			else
				throw new  TamagoBuilderException();
		}
			
		return null;
	}

	private InferAuto convertInferAuto(TamagoCSPInferConstraint infer,GExpression argument) throws TamagoBuilderException {
		switch(argument.getCategory()) {
		case INT:
			return new InferAutoConst(new CSPinteger(((GInteger)argument).getValue()),b);
		case REAL:
			return new InferAutoConst(new CSPreal(((GReal)argument).getValue()),b);
		case VARIABLE:
			if(env.containsKey(((GVariable)argument).getName())) {
				TamagoBuilder tb = env.get(((GVariable)argument).getName());
				return new InferAutoFD((FDvar)tb.getCSPvar(infer,null));
			}
			throw new TamagoBuilderException("convertInferAuto("+TamagoCCMakeReadableGExpression.toString(argument)+")");
		case INLABEL:
		{
			GInLabel inlabel = (GInLabel)argument;
			GExpression scope = inlabel.getTarget();
			String var = "";
			if(scope.getCategory() == GExprType.VARIABLE) {
				var = ((GVariable)scope).getName();
			}
			if(scope.getCategory() == GExprType.READ) {
				var = ((GRead)scope).getName();
			}
			
			if(env.containsKey(var)) {
				TamagoBuilder builder = env.get(var);
				return new InferAutoFD((FDvar) builder.getCSPvar(infer,argument));
			}
			throw new TamagoBuilderException("Unsupported argument for the substring (inlabel)");
		}
		case OPERATOR: 
		{
			String var = freshTmpName();
			Builderint bint = new Builderint(this.env,var,null,infer.getCSP().getBacktrack());
			//Intvar tmp = new Intvar(var,infer.getCSP().getBacktrack());
			GIVariable gvar = new GIVariable(var,GIType.TYPE_INT);
			
			GIOperator ope = new GIOperator(TOpeName.opEg);
			ope.addOperand(gvar);
			ope.addOperand(argument);
			TamagoBuilder backup = env.put(var, bint);
			
			GIOperator ope0 = new GIOperator(TOpeName.opSupEg);
			ope0.addOperand(gvar);
			ope0.addOperand(new GIInteger(0));
			try {
				infer.getCSP().addVariable(bint.getCSPvar(null, null));
				if(!infer.generate(ope0)) {
					throw new TamagoBuilderException("Dynamic rewritten failed in substring");
				}
				
				if(infer.generate(ope)) {
					return new InferAutoFD((FDvar)bint.getCSPvar(infer,null));
				}
				else {
					throw new TamagoBuilderException("Unsupported expression in substring");
				}
			}
			finally {
				if(backup != null)
					env.put(var, backup);
				else
					env.remove(var);
			}
		}
		default:
		}
		throw new TamagoBuilderException("Unsupported argument for the substring");
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getType()
	 */
	public GType getType() {
		return GIType.TYPE_STRING;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	public Object instantiate() throws TamagoBuilderException {
		try {
			str.forward();
			return str.getValue();
		} catch (TamagoCSPException e) {
			throw new TamagoBuilderException(e);
		}
	}

	public void setBacktrack(Backtracking b) {
		this.b = b;
		this.str.setBacktrack(b);
	}

	public void setName(String name) {
		this.name = name;
		str.setName(name);
		str.clearDepends();
	}

}
