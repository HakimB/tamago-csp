/**
 * 
 */
package tamago.builder.impl;

import java.util.Hashtable;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.Backtracking;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.generic.CSPvar;
import tamago.csp.var.Transparencyvar;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GType;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class Buildertransparency extends TamagoBuilder {

	private Transparencyvar main;
	private Hashtable<String, Transparencyvar> fields;
	
	/**
	 * @param env
	 * @param name
	 * @param type
	 * @param b
	 */
	public Buildertransparency(TamagoEnvironment env, String name, GType type,
			Backtracking b) {
		super(env, name, type, b);
		main = new Transparencyvar(name,b);
		fields = new Hashtable<String, Transparencyvar>();
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getCSPvar(tamago.csp.convert.TamagoCSPInferConstraint, tamagocc.generic.api.GExpression)
	 */
	public CSPvar getCSPvar(TamagoCSPInferConstraint owner, GExpression e)
			throws TamagoBuilderException {
		if(e == null)
			return main;
		
		String key = TamagoCCMakeReadableGExpression.toString(e);
		if(key.equals(main.getName()))
			return main;
		
		if(fields.containsKey(key))
			return fields.get(key);
		else {
			Transparencyvar var = new Transparencyvar(key,b);
			fields.put(key, var);
			return var;
		}
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getType()
	 */
	public GType getType() {
		return type;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	public Object instantiate() throws TamagoBuilderException {
		return null;
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setName(java.lang.String)
	 */
	public void setName(String name) {
		main.setName(name);
	}

}
