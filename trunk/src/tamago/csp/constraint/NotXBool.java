/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPbool;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class NotXBool  implements UnaryConstraint,CSPvar  {

	private CSPvar var;
	private boolean filtering;
	
	private ArrayList<CSPConstraint> constraints;
	private ArrayList<CSPrepercussion> repercussions;
	
	/**
	 * 
	 */
	public NotXBool(CSPvar var) {
		this.var = var;
		var.install(this);
		constraints = new ArrayList<CSPConstraint>(1);
		repercussions = new ArrayList<CSPrepercussion>(1);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		filtering = true;
		try {
			CSPbool b = new CSPbool(true);
			var.remove(b);
		}
		finally {
			for (CSPrepercussion rep : repercussions) {
				rep.updateDomain(this);
			}
			filtering = false;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> var = new ArrayList<CSPvar>();
		var.add(this.var);
		return var;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 1;
	}

	public String toString() {
		return "not "+var.getName();
	}

	public int count() {
		return constraints.size();
	}

	public void fix(CSPconst n) throws TamagoCSPException {
		CSPbool b = new CSPbool(!n.boolValue());
		var.fix(b);
	}

	public Iterable<CSPConstraint> getConstraint() {
		return new ArrayList<CSPConstraint>(0);
	}

	public String getName() {
		return "!"+var.getName();
	}

	public CSPconst getValue() {
		CSPbool b = new CSPbool(!var.getValue().boolValue());
		return b;
	}

	public void install(CSPConstraint c) {
		constraints.add(c);
	}

	public void forward() throws TamagoCSPException {
		var.forward();		
	}

	public boolean isFixed() {
		return var.isFixed();
	}

	public boolean isInDomain(CSPconst o) {
		boolean res = var.isInDomain(o);
		return !res;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean mustInstantiate() {
		return var.mustInstantiate();
	}

	public void remove(CSPconst value) throws TamagoCSPException {
		CSPbool b = new CSPbool(!value.boolValue());
		var.remove(b);
	}

	public void retrieve(Triplet triplet) {
		throw new TamagoCSPRuntime("NotXBool: not yet implemented");
		
		/*if(((BoolTriplet)triplet).isForward()) {
			// nothing to do, made by the CSP
		}
		else {
			BoolTriplet b = new BoolTriplet(FDTripletType.REMOVE,var,((Boolvar)var).getDomain());
			var.retrieve(b);
		}*/
	}

	public void setBacktrack(Backtracking b) {
		var.setBacktrack(b);		
	}

	public void setMustInstantiate(boolean b) {
		var.setMustInstantiate(b);
	}

	public void uninstallAllConstraints() {
		var.uninstallAllConstraints();
		constraints.clear();
	}

	public void uninstall(CSPConstraint c) {
		constraints.remove(c);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#addRepercussion(tamago.csp.generic.CSPrepercussion)
	 */
	public void addRepercussion(CSPrepercussion rep) {
		repercussions.add(rep);		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#removeAllRepercussiions()
	 */
	public void removeAllRepercussiions() {
		repercussions.clear();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#removeRepercussion(tamago.csp.generic.CSPrepercussion)
	 */
	public void removeRepercussion(CSPrepercussion rep) {
		repercussions.remove(rep);		
	}

	public Iterable<CSPrepercussion> getRepercussions() {
		return repercussions;
	}

	public CSPAbstractDomain getAbstractDomain() {
		return null;
	}

	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}
}
