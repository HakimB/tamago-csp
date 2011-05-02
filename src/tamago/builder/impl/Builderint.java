/**
 * 
 */
package tamago.builder.impl;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.Backtracking;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.generic.CSPvar;
import tamago.csp.var.Intvar;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GRead;
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.impl.GIType;

/**
 * @author Hakim Belhaouari
 *
 */
public class Builderint extends TamagoBuilder {
	private Intvar me;
	
	/**
	 * @param type
	 */
	public Builderint(TamagoEnvironment env,String name,GType type,Backtracking b) {
		super(env,name,GIType.TYPE_INT,b);
		me = new Intvar(name,b);
	}
	
	public Builderint(TamagoEnvironment env,String name,GType type,Backtracking b,boolean bounded) {
		this(env,name,GIType.TYPE_INT,b);
		me.setBounded(bounded);
	}
	
	public Builderint(Intvar substitute, TamagoEnvironment env,String name,GType type,Backtracking b) {
		super(env,name,GIType.TYPE_INT,b);
		me = substitute;
	}

	public Builderint(Intvar substitute, TamagoEnvironment env,String name,GType type,Backtracking b,boolean bounded) {
		this(substitute, env,name,GIType.TYPE_INT,b);
		me.setBounded(bounded);
	}
	
	/**
	 * @see tamago.builder.TamagoBuilder#getCSPvar(tamagocc.generic.api.GExpression)
	 */
	public CSPvar getCSPvar(TamagoCSPInferConstraint infer,GExpression e) throws TamagoBuilderException {
		if(e == null)
			return me;
		if((e.getCategory() == GExpression.GExprType.VARIABLE)
				&& (((GVariable)e).getName().equals(name) ))
		{
			return me; 
		}

		if((e.getCategory() == GExpression.GExprType.READ)
				&& (((GRead)e).getName().equals(name) ))
		{
			return me; 
		}

		return me;
		//throw new TamagoBuilderException("The argument does not seems to the correct variable name!");
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getType()
	 */
	public GType getType() {
		return GIType.TYPE_INT;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	public Object instantiate() throws TamagoBuilderException {
		return new Integer(me.getValue().intValue());
	}

	public void setBacktrack(Backtracking b) {
		me.setBacktrack(b);
	}

	public void setName(String name) {
		this.name = name;
		me.setName(name);
	}
	
	public String toString() {
		return "BUILDER OF "+me.toString();
	}
}
