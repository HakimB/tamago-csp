/**
 * 
 */
package tamago.csp.generic;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPException;

/**
 * @author Hakim Belhaouari
 *
 */
public abstract class DefaultCSPvar implements CSPvar {

	protected ArrayList<CSPrepercussion> repers;
	protected String name;
	protected boolean mustinstantiate;
	protected Backtracking b;
	protected ArrayList<CSPConstraint> constraints;
	
	/**
	 * 
	 */
	public DefaultCSPvar(String name,Backtracking b) {
		this.name = name;
		repers = new ArrayList<CSPrepercussion>();
		constraints = new ArrayList<CSPConstraint>();
		this.b = b;
		mustinstantiate = true;
	}

	protected void updated() throws TamagoCSPException{
		ArrayList<CSPrepercussion> clone = new ArrayList<CSPrepercussion>(repers);
		for (CSPrepercussion rep : clone) {
			rep.updateDomain(this);
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getConstraint()
	 */
	public Iterable<CSPConstraint> getConstraint() {
		return constraints;
	}
	
	/**
	 * @see tamago.csp.generic.CSPvar#count()
	 */
	public int count() {
		return constraints.size();
	}
	
	public void setName(String name) {
		this.name = name;		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#install(tamago.csp.generic.CSPConstraint)
	 */
	public void install(CSPConstraint c) {
		constraints.add(c);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isPrimitive()
	 */
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#mustInstantiate()
	 */
	public boolean mustInstantiate() {
		return mustinstantiate;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#setBacktrack(tamago.csp.Backtracking)
	 */
	public void setBacktrack(Backtracking b) {
		this.b = b;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#setMustInstantiate(boolean)
	 */
	public void setMustInstantiate(boolean b) {
		//mustinstantiate = b;
		mustinstantiate = true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#uninstall(tamago.csp.generic.CSPConstraint)
	 */
	public void uninstall(CSPConstraint c) {
		constraints.remove(c);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#uninstallAllConstraints()
	 */
	public void uninstallAllConstraints() {
		constraints.clear();
	}
	
	public void addRepercussion(CSPrepercussion rep) {
		repers.add(rep);
	}

	public void removeRepercussion(CSPrepercussion rep) {
		repers.remove(rep);
	}
	
	public void removeAllRepercussiions() {
		repers.clear();
	}
	public Iterable<CSPrepercussion> getRepercussions() {
		return repers;
	}
}
