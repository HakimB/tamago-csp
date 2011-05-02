/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Random;

/**
 * @author Hakim Belhaouari
 *
 */
public class SGenericLetter implements SLetter {

	String pattern;


	public SGenericLetter(String authorized) {
		pattern = authorized;
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#accept(char)
	 */
	public boolean accept(char c) {
		return (pattern.indexOf((int)c)>= 0);
	}

	public String getPattern()  {
		return pattern;
	}

	public boolean equals(Object o) {
		if (o instanceof SGenericLetter) {
			SGenericLetter no = (SGenericLetter) o;
			for(int k=0; k < pattern.length();k++) {
				if(!no.accept(pattern.charAt(k))) return false;
			}
			for(int k=0; k < no.pattern.length();k++) {
				if(!accept(no.pattern.charAt(k))) return false;
			}
			return true;
		}
		return false;
	}


	public static void main(String args[]) {
		String a = "abcde";
		String b = "bedca";
		String c = "ab";

		SGenericLetter[] ts = new SGenericLetter[] {
				new SGenericLetter(a),
				new SGenericLetter(b),
				new SGenericLetter(c)
		};

		System.out.println("1- "+ts[0].equals(ts[1]));
		System.out.println("2- "+ts[1].equals(ts[2]));
		System.out.println("3- "+ts[2].equals(ts[0]));
	}

	public String toString() {
		return "["+pattern+"]";
	}


	public SLetter intersect(SLetter target) throws LetterNotCompatible {
		switch(target.getType()) {
		case ANY:
			return this;
		default:{
			String npattern = "";
			for(int i=0;i < pattern.length();i++) {
				char c = pattern.charAt(i);
				if(target.accept(c))
					npattern = npattern+ c;
			}
			if(npattern.length() == 0)
				throw new LetterNotCompatible("incompatible pattern with this letter: "+pattern);
			return new SGenericLetter(npattern);
		}
		}
	}

	public char select() {
		Random r = new Random();
		return pattern.charAt(r.nextInt(pattern.length()));
	}

	/**
	 * @see tamago.csp.stringbuilder.automaton.SLetter#getType()
	 */
	public SLetterType getType() {
		return SLetterType.GENERIC;
	}
}
