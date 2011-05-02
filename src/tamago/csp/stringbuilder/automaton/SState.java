/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.ArrayList;

/**
 * @author Hakim Belhaouari
 *
 */
public class SState implements Cloneable, Comparable<SState> {

	private int n;
	private ArrayList<SAbsTransition> outgoings;
	
	/**
	 * 
	 */
	public SState(int id) {
		n = id;
		outgoings = new ArrayList<SAbsTransition>();
	}
	
	
	public int id() { return n; }

	public void register(SAbsTransition trans) {
		outgoings.add(trans);
	}
	
	public Iterable<SAbsTransition> getTransitions() {
		return outgoings;
	}
	
	public int hashCode() {
		return n;
	}
	
	public boolean equals(Object o) {
		if (o instanceof SState) {
			SState no = (SState) o;
			return no.n == this.n;
		}
		return false;
	}


	public int compareTo(SState o) {
		return o.n - n;
	}

	public String toString() {
		return String.valueOf(n);
	}
}
