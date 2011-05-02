/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Random;

/**
 * @author Hakim Belhaouari
 *
 */
public class SLessLetter implements SLetter {

	private char max;
	
	/**
	 * 
	 */
	public SLessLetter(char max) {
		this.max = max;
	}
	
	public char max() {
		return max;
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#accept(char)
	 */
	public boolean accept(char c) {
		return (c <= max);
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#intersect(tamago.csp.stringbuilder.automaton.SLetter)
	 */
	public SLetter intersect(SLetter target) throws LetterNotCompatible {
		switch(target.getType()) {
		case ANY:
			return this;
		case GENERIC:
			return target.intersect(this);
		case TOP:
			return new SLessLetter((char) Math.min(this.max, ((SLessLetter)target).max));
		case DOWN:
			return new SBoundedLetter(((SGreaterLetter)target).min(),max);
		case BOUNDED:
			((SBoundedLetter)target).setMax(max);
			return target;
		case OTHER:
			return target.intersect(this);
		}
		throw new LetterNotCompatible();
	}
	
	private static boolean isLetter(char m) {
		return ('A' <= m && m<='Z')
		|| ('a' <= m && m <= 'z')
		|| ('0' <= m && m <= '9');
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#select()
	 */
	public char select() {
		// TODO Select
		char c;
		int countdown = 0;
		Random r = new Random();
		do {
			c = (char)r.nextInt(max+1);
			countdown++;
		} while((!isLetter(c)) && (countdown < 1000));
		if(!isLetter(c)) {
			return max;
		}	
		
		return c;
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#getType()
	 */
	public SLetterType getType() {
		return SLetterType.TOP;
	}
	public String toString() {
		return "<"+max;
	}
	
	public boolean equals(Object p) {
		if (p instanceof SLessLetter) {
			SLessLetter no = (SLessLetter) p;
			return no.max == max;
		}
		return false;
	}
}
