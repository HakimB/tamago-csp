/**
 * 
 */
package tamago.builder.impl;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoBuilderFactory;
import tamago.builder.TamagoEnvironment;
import tamago.csp.Backtracking;
import tamago.csp.array.ArrayIndexvar;
import tamago.csp.array.Arrayvar;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamagocc.api.TOpeName;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GInLabel;
import tamagocc.generic.api.GReturn;
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class Builderarray extends TamagoBuilder {

	private Arrayvar var;

	/**
	 * @param env
	 * @param name
	 * @throws ClassNotFoundException 
	 */
	public Builderarray(TamagoBuilderFactory cb,TamagoEnvironment env, String name,GType type,Backtracking b) {
		super(env, name, type,b);
		var = new Arrayvar(this,cb,name,b);
	}

	/*private static String searchBuilder(GType type) {

		switch(type.catType()) {
		case ARRAY:
			return "tamago.builder.impl.Builderarray";
		case BOOL:
		case INTEGER:
		case OBJECT:
		case REAL:
		case STRING:
		case VOID: {
			return "tamago.builder.impl.Builder"+type.SimpleType().toLowerCase();
		}
		}
		throw new TamagoCSPRuntime("Problem occur for searching the builder dedicated to the type "+type.getType());
	}*/

	
	private String genNameIdx(GExpression var2) {
		if(var2 instanceof GVariable)
			return "idx_"+TamagoCCMakeReadableGExpression.toString(((GVariable)var2).getIndex());
		else if (var2 instanceof GReturn)
			return "idx_"+TamagoCCMakeReadableGExpression.toString(((GReturn)var2).getIndex());
		return "UNKOWN"+TamagoCCMakeReadableGExpression.toString(var2);
	}

	private TamagoBuilder searchIndex(TamagoCSPInferConstraint owner,GExpression var) throws TamagoBuilderException {
		// ici je suis dans une return qui a un index
		try {
			TamagoBuilder tb = this.var.getElemByName(genNameIdx(var));
			//return tb.getCSPvar(owner, null);
			TamagoCCLogger.println(4, "ArrayBuilder: already exist index: "+TamagoCCMakeReadableGExpression.toString(var));
			return tb;
		}
		catch(Exception runtime) {
			TamagoCCLogger.println(3,runtime.getMessage());
		}
		
		TamagoEnvironment env = owner.getEnvironment();
		ArrayIndexvar ivar = new ArrayIndexvar(genNameIdx(var),this.var.getLength(),b);
		Builderint bi = new Builderint(ivar,this.env,ivar.getName(),GIType.TYPE_INT,b);
		GExpression expr = null;
		if(var instanceof GVariable) {
			expr = ((GVariable)var).getIndex();
		}
		else if(var instanceof GReturn) {
			expr = ((GReturn)var).getIndex();
		}
		
		//modifier ici les pour qu'on genere des arrayindexvar
		// TODO
		GIOperator ope = new GIOperator(TOpeName.opEg);
		ope.addOperand(expr);
		ope.addOperand(new GIVariable(bi.getName(),GIType.TYPE_INT));

		TamagoBuilder tb = env.get(bi.getName()); // save l'ancien builder si il y a lieu
		try {
			env.put(bi.getName(),bi);
			owner.generate(ope);
			FDvar idx = (FDvar) bi.getCSPvar();
			this.var.putElem(idx);
			TamagoBuilder stb = this.var.getElem(idx);
			return stb;
		}
		catch(Exception ex) {
			throw new TamagoBuilderException(ex);
		}
		finally {
			if(tb != null)
				env.put(bi.getName(), tb);
		}

	}
	
	/**
	 * @see tamago.builder.TamagoBuilder#getCSPvar(tamago.csp.convert.TamagoCSPInferConstraint, tamagocc.generic.api.GExpression)
	 */
	public CSPvar getCSPvar(TamagoCSPInferConstraint owner, GExpression e)
	throws TamagoBuilderException {
		CSPvar res = null;
		if(e==null) {
			return var;
		}

		switch(e.getCategory()) {
		case VARIABLE:
		case READ:
			GVariable var = (GVariable)e;
			if(var.hasIndex()) {
				return searchIndex(owner, var).getCSPvar(owner, null);
			}
			else
				return this.var;
		case RETURN:
			GReturn ret = (GReturn)e;
			if(ret.hasIndex())
				return searchIndex(owner, ret).getCSPvar(owner, null);
			else
				return this.var;
		case INLABEL:
			GInLabel inlabel = (GInLabel)e;
			if((inlabel.getTarget().getCategory() == GExprType.VARIABLE)
					|| (inlabel.getTarget().getCategory() == GExprType.READ)) 
			{
				if(!((GVariable)inlabel.getTarget()).hasIndex()) {
					// on demande surement la longueur
					if( (inlabel.getSubExpression().getCategory() == GExprType.VARIABLE) && 
							(((GVariable)inlabel.getSubExpression()).getName().equals("length")) )
						res = this.var.getLength();
					else
						throw new TamagoBuilderException("Unkown element in an array: "+((GVariable)inlabel.getSubExpression()).getName());
				}
				else {
					TamagoBuilder scope = searchIndex(owner, ((GVariable)inlabel.getTarget()));
					res = scope.getCSPvar(owner, inlabel);
				}
			}
			else if(inlabel.getTarget().getCategory() == GExprType.RETURN) {
				if(!((GReturn)inlabel.getTarget()).hasIndex()) {
					// on demande surement la longueur
					if( (inlabel.getSubExpression().getCategory() == GExprType.VARIABLE) && 
							(((GVariable)inlabel.getSubExpression()).getName().equals("length")) )
						res = this.var.getLength();
					else
						throw new TamagoBuilderException("Unkown element in an array: "+((GVariable)inlabel.getSubExpression()).getName());
				}
				else {
					TamagoBuilder scope = searchIndex(owner, ((GReturn)inlabel.getTarget()));
					res = scope.getCSPvar(owner, inlabel);
				}
			}
			break;
		}
		return res;
	}

	/*
	private String genName(GVariable var) throws TamagoBuilderException{
		return TamagoCCMakeReadableGExpression.toString(var.getIndex());
	}*/

	/**
	 * @see tamago.builder.TamagoBuilder#getType()
	 */
	public GType getType() {
		return this.type;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	public Object instantiate() throws TamagoBuilderException {
		try {
			var.forward();
		} catch (TamagoCSPException e) {
			throw new TamagoBuilderException(e);
		}
		return var.getValue();
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setBacktrack(tamago.csp.Backtracking)
	 */
	public void setBacktrack(Backtracking b) {
		var.setBacktrack(b);
		this.b = b;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setName(java.lang.String)
	 */
	public void setName(String name) {
		var.setName(name);
	}

}
