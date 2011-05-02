/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

/**
 * @author Hakim Belhaouari
 *
 */
public class SEpsTransition implements SAbsTransition {

	private SState beg;
	private SState end;

	/**
	 * @param beg 
	 * @param end 
	 * 
	 */
	public SEpsTransition(SState beg, SState end) {
		this.beg = beg;
		beg.register(this);
		this.end = end;
		//end.register(this);
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SAbsTransition#isEpsilon()
	 */
	public boolean isEpsilon() {
		return true;
	}

	public boolean accept(char c) {
		return false; // we dont accept everything
	}

	public SState getBeg() {
		return beg;
	}

	public SState getEnd() {
		return end;
	}

	public SLetter getLetter() {
		return null;
	}

	public SAbsTransition clone(SState beg, SState end) {
		return new SEpsTransition(beg,end);
	}

	public String toString() {
		return "Eps";
	}
}
