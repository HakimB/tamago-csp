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
import tamago.csp.var.Boolvar;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GRead;
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GVariable;
import tamagocc.generic.impl.GIType;

/**
 * @author Hakim Belhaouari
 *
 */
public class Builderbool extends TamagoBuilder {

	private Boolvar me;
	
	/**
	 * @param env
	 * @param name
	 * @param type
	 */
	public Builderbool(TamagoEnvironment env, String name,GType type, Backtracking b) {
		super(env, name, GIType.TYPE_BOOL,b);
		me = new Boolvar(name,b);
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
		return GIType.TYPE_BOOL;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	public Object instantiate() throws TamagoBuilderException {
		return new Boolean(me.getValue().boolValue());
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setBacktrack(tamago.csp.Backtracking)
	 */
	public void setBacktrack(Backtracking b) {
		me.setBacktrack(b);
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setName(java.lang.String)
	 */
	public void setName(String name) {
		me.setName(name);
		this.name = name;
	}

}
