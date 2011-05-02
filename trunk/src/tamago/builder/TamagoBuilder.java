/**
 * 
 */
package tamago.builder;

import tamago.csp.Backtracking;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.generic.CSPvar;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GType;

/**
 * @author Hakim Belhaouari
 *
 */
public abstract class TamagoBuilder {
	public abstract GType getType();
	public abstract CSPvar getCSPvar(TamagoCSPInferConstraint owner, GExpression e) throws TamagoBuilderException;
	
	// ----
	
	protected String name;
	protected GType type;
	protected TamagoEnvironment env;
	protected Backtracking b;
	
	
	protected TamagoBuilder(TamagoEnvironment env, String name,GType type,Backtracking b) {
		this.name = name;
		this.type = type;
		this.env = env;
		this.b = b;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return !((name == null) || (name.length() == 0));
	}
	
	public TamagoEnvironment getEnvironment() {
		return env;
	}
	
	public abstract void setName(String name);
	
	public abstract Object instantiate() throws TamagoBuilderException;
	
	public void setBacktrack(Backtracking b) {
		this.b = b;
		getCSPvar().setBacktrack(b);
	}
	public CSPvar getCSPvar() {
		try {
			return getCSPvar(null, null);
		} catch (TamagoBuilderException e) {
			return null;
		}
	}
	public void setEnvironment(TamagoEnvironment ctx) {
		env = ctx;		
	}
	
}
