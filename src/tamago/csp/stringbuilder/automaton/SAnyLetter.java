/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Random;

/**
 * @author Hakim Belhaouari
 *
 */
public class SAnyLetter implements SLetter {

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#accept(char)
	 */
	public boolean accept(char c) {
		return true;
	}

	public boolean equals(Object o) {
		if (o instanceof SAnyLetter) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return ".";
	}

	
	public SLetter intersect(SLetter target) throws LetterNotCompatible {
		if(target instanceof SAnyLetter) 
			return this;
		else
			return target;
	}

	public char select() {
		Random r= new Random();
		return tab[r.nextInt(tab.length)];
	}

	private static final char tab[] = {
		'A','B','C','D','E','F','G','H','J',
		'K','L','M','N','O','P','Q','R','S',
		'T','U','V','W','X','Y','Z','a','b',
		'c','d','e','f','g','h','j','k','l',
		'm','n','o','p','q','r','s','t','u',
		'v','w','x','y','z','0','1','2','3',
		'4','5','6','7','8','9'
	};

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#getType()
	 */
	public SLetterType getType() {
		return SLetterType.ANY;
	}
}
