/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Random;

/**
 * @author Hakim Belhaouari
 *
 */
public class SGreaterLetter implements SLetter {

	private char min;

	/**
	 * 
	 */
	public SGreaterLetter(char min) {
		this.min = min;
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#accept(char)
	 */
	public boolean accept(char c) {
		return (c >= min);
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#getType()
	 */
	public SLetterType getType() {
		return SLetterType.DOWN;
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
			return new SBoundedLetter(min,((SLessLetter)target).max());
		case DOWN:
			return new SGreaterLetter((char)Math.max(min, ((SGreaterLetter)target).min));
		case BOUNDED:
			((SBoundedLetter)target).setMin(min);
			return target;
		case OTHER:
			return target.intersect(this);
		}
		throw new LetterNotCompatible();
	}

	public boolean equals(Object o) {
		if (o instanceof SGreaterLetter) {
			SGreaterLetter no = (SGreaterLetter) o;
			return min == no.min;
		}
		return false;
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
		Random r = new Random();
		int countdown = 0;
		char m = (char) r.nextInt();
		
		do {
			m = (char) (min + r.nextInt(Character.MAX_VALUE - min));
			countdown++;
		} while((countdown < 1000) && (m < min) && (!isLetter(m)));
		
		System.out.println("MIN : "+min + " val: "+m);
		if(m < min || !isLetter(m)) {
			m = min;
		}
		return m;
	}

	/**
	 * @return
	 */
	public char min() {
		return min;
	}

	public String toString() {
		return ">"+min;
	}
}
