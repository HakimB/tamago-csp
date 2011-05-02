/**
 * 
 */
package tamago.csp.var;

import tamago.builder.SimpleTriplet;
import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.DefaultCSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class Transparencyvar extends DefaultCSPvar {

	private CSPconst value;
	
	
	/**
	 * @param name
	 * @param b
	 */
	public Transparencyvar(String name, Backtracking b) {
		super(name, b);
		value = null;
		this.setMustInstantiate(false);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		for (CSPConstraint cons : this.getConstraint()) {
			cons.filter();
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	public void fix(CSPconst n) throws TamagoCSPException {
		if(value == null) {
			SimpleTriplet st = new SimpleTriplet(this,n,false);
			value = n;
			b.add(st);
			updated();
		}
		else  if(!value.equals(n)) {
			throw new TamagoCSPException("Transparency variable "+getName()+" fixed with two differents values:"+value.toString()+ " / "+n.toString());
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getValue()
	 */
	public CSPconst getValue() {
		return value;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return (value != null);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		return true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#remove(tamago.csp.generic.CSPconst)
	 */
	public void remove(CSPconst value) throws TamagoCSPException {
		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	public void retrieve(Triplet triplet) throws TamagoCSPException {
		value = null;
	}

	public CSPAbstractDomain getAbstractDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}

}
