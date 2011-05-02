/**
 * 
 */
package tamago.csp.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import tamago.csp.constant.CSPinteger;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;

/**
 * @author Hakim Belhaouari
 *
 */
public class IntDomain implements CSPAbstractDomain {

	CSPinteger min;
	CSPinteger max;
	ArrayList<CSPinteger> removed;
	/**
	 * 
	 */
	public IntDomain(int min, int max, Collection<CSPinteger> removed) {
		this.min = new CSPinteger(min);
		this.max = new CSPinteger(max);
		this.removed = new ArrayList<CSPinteger>();
		for (CSPconst csPconst : removed) {
			this.removed.add(new CSPinteger(csPconst.intValue()));
		}
	}

	public CSPinteger getMin() {
		return min;
	}
	
	public CSPinteger getMax() {
		return max;
	}
	
	public Collection<CSPinteger> getRemoved() {
		return removed;
	}
	
	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#intersect(tamago.csp.domain.CSPAbstractDomain)
	 */
	public CSPAbstractDomain intersect(CSPAbstractDomain domain)
			throws TamagoCSPException {
		
		return null;
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		CSPinteger val = new CSPinteger(o.intValue());
		return min.intValue() <= o.intValue() &&  
			o.intValue() <= max.intValue() &&
			!removed.contains(val);
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#sameDomain(tamago.csp.domain.CSPAbstractDomain)
	 */
	public boolean sameDomain(CSPAbstractDomain domain) {
		if(domain instanceof IntDomain) {
			IntDomain dom = (IntDomain)domain;
			return (dom.min.equals(min) && max.equals(dom.max) 
					&& removed.containsAll(dom.removed)
					&& dom.removed.containsAll(removed));
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(min.intValue() == max.intValue()) {
			sb.append(" FIXED ");
			sb.append(min.intValue());
		}
		else {
			sb.append("[ ");
			sb.append(min.intValue());
			sb.append("; ");
			sb.append(max.intValue());
			sb.append("] \\ {");
			Iterator<CSPinteger> ints = removed.iterator();
			while(ints.hasNext()) {
				sb.append(ints.next().toString());
				if(ints.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
