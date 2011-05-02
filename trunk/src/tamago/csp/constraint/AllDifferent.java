package tamago.csp.constraint;

import java.util.ArrayList;
import java.util.Iterator;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class AllDifferent implements NaryConstraint {

	private ArrayList<CSPvar> vars;
	private boolean filtrage;
	
	/**
	 * 
	 */
	public AllDifferent() {
		vars = new ArrayList<CSPvar>();
		filtrage = false;
	}

	public void add(CSPvar var) {
		vars.add(var);
		var.install(this);
	}
	
	public void remove(CSPvar var) {
		vars.remove(var);
		var.uninstall(this);
	}
	
	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtrage)
			return;
		
		try {
			filtrage = true;
			for (CSPvar var : vars) {
				if(var.isFixed()) {
					for (CSPvar var2 : vars) {
						if(var != var2)
							var2.remove(var.getValue());
					}
				}
			}
		}
		finally {
			filtrage = false;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		return vars;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return vars.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AllDifferent(");
		Iterator<CSPvar> ite = vars.iterator();
		while(ite.hasNext()) {
			sb.append(ite.next().getName());
			if(ite.hasNext())
				sb.append(", ");
		}
		sb.append(")");
		return sb.toString();
	}
}
