/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Random;

/**
 * @author Hakim Belhaouari
 *
 */
public class SBoundedLetter implements SLetter {

	private char min;
	private char max;
	
	/**
	 * 
	 */
	public SBoundedLetter(char min, char max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#accept(char)
	 */
	public boolean accept(char c) {
		return ((min <= c) && (c <= max));
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#getType()
	 */
	public SLetterType getType() {
		return SLetterType.BOUNDED;
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
			if(((SLessLetter)target).max() < max)
				return new SBoundedLetter(min,  ((SLessLetter)target).max());
			else
				return this;
		case DOWN:
			if(((SGreaterLetter)target).min() < min)
				return this;
			else
				return new SBoundedLetter(((SGreaterLetter)target).min(), max);
		case BOUNDED:
			return new SBoundedLetter((char)Math.max(((SBoundedLetter)target).min,min),
					(char)Math.min(((SBoundedLetter)target).max,max));
		case OTHER:
			return target.intersect(this);
		}
		throw new LetterNotCompatible();
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#select()
	 */
	public char select() {
		Random r = new Random();
		int countdown = 0;
		char m = (char) r.nextInt();
		
		do {
			m = (char) r.nextInt();
			
		} while((countdown < 1000) && ((m < min) || (max < m)));
		if((m < min) && (m > max)) {
			m = (char) ((min+max)/2);
		}
		return m;
	}

	/**
	 * @param max
	 * @return
	 */
	public void setMax(char max) throws LetterNotCompatible {
		if((this.min <= max) && (max <= this.max)) {
			this.max = max;
		}
	}

	/**
	 * @param min
	 * @return
	 */
	public void setMin(char min) {
		if((this.min <= min) && (min <= this.max)) {
			this.min = min;
		}
	}

	public String toString() {
		return ""+min+"<x<"+max;
	}
	
	public boolean equals(Object o) {
		if (o instanceof SBoundedLetter) {
			SBoundedLetter no = (SBoundedLetter) o;
			return ((no.min == min) && (no.max == max));
		}
		return false;
	}
}
