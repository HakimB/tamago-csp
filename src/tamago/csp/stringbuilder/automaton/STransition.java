/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

/**
 * @author Hakim Belhaouari
 *
 */
public class STransition implements SAbsTransition,Cloneable {

	private SState beg;
	private SState end;
	
	private SLetter letter;
	private long redundancy;
	private long current;
	
	/**
	 * 
	 */
	public STransition(SState beg,SState end,SLetter letter,long redundancy) {
		this.beg = beg;
		beg.register(this);
		//end.register(this);
		this.end = end;
		this.letter = letter;
		this.redundancy = redundancy;
		current = 0;
	}

	public STransition(SState beg,SState end,SLetter letter) {
		this(beg,end,letter,-1);
	}
	
	/**
	 * @return the letter
	 */
	public SLetter getLetter() {
		return letter;
	}

	/**
	 * @return the beg
	 */
	public SState getBeg() {
		return beg;
	}
	
	public boolean accept(char c) {
		return letter.accept(c);
	}

	/**
	 * @return the end
	 */
	public SState getEnd() {
		return end;
	}

	/**
	 * @return the redundancy
	 */
	public long getRedundancy() {
		return redundancy;
	}
	
	public long getThrowTime() {
		return current;
	}
	
	public void reset() {
		current = 0;
	}
	
	public void fetch() throws STransitionException {
		if(redundancy <= 0) {
			current++;
		}
		else if(redundancy == current) {
			throw new STransitionException("Transition reach the maximal redundancy");
		}
		else {
			current++;
		}
	}

	public boolean isEpsilon() {
		return false;
	}

	public SAbsTransition clone(SState beg, SState end) {
		STransition trans = new STransition(beg,end,this.letter,this.redundancy);
		return trans;
	}

	public boolean equals(Object o) {
		if (o instanceof STransition) {
			STransition no = (STransition) o;
			return (no.redundancy == redundancy) && (letter.equals(no.letter)) 
				&& beg.equals(no.beg) && end.equals(no.end);
		}
		return false;
	}
	
	public boolean equalsWithoutState(Object o) {
		if (o instanceof STransition) {
			STransition no = (STransition) o;
			return (no.redundancy == redundancy) && (letter.equals(no.letter));
		}
		return false;
	}
	
	public String toString() {
		return letter.toString();
	}
}
