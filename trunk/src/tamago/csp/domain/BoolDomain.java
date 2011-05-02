/**
 * 
 */
package tamago.csp.domain;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;

/**
 * @author Hakim Belhaouari
 *
 */
public class BoolDomain implements CSPAbstractDomain {

	boolean a,b;
	/**
	 * 
	 */
	public BoolDomain(boolean a,boolean b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#intersect(tamago.csp.domain.CSPAbstractDomain)
	 */
	public CSPAbstractDomain intersect(CSPAbstractDomain domain)
			throws TamagoCSPException {
		if(domain instanceof BoolDomain) {
			BoolDomain bd = (BoolDomain)domain;
			return new BoolDomain(a && bd.a, b && bd.b); 
		}
		throw new TamagoCSPException("Unkown domain");
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		boolean b = o.boolValue();
		if(b)
			return a;
		else
			return this.b;
	}

	private static boolean equiv(boolean a,boolean b) {
		return (!a || b) && (!b || a);
	}
	
	/*private static void log(boolean a,boolean b) {
		System.out.println(""+a+" <=> "+b+" donne "+equiv(a,b));
	}
	public static void main(String[] args) {
		log(false,false);
		log(false,true);
		log(true,false);
		log(true,true);
	}*/
	
	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#sameDomain(tamago.csp.domain.CSPAbstractDomain)
	 */
	public boolean sameDomain(CSPAbstractDomain domain) {
		if(domain instanceof BoolDomain) {
			BoolDomain bd = (BoolDomain)domain;
			return equiv(a,bd.a) && equiv(b,bd.b) ; 
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		if(a) sb.append("false ");
		if(b) sb.append("true ");
		sb.append("}");
		return sb.toString();
	}

	public boolean a() {
		return a;
	}
	public boolean b() {
		return b;
	}
}
