/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

/**
 * @author Hakim Belhaouari
 *
 */
public interface SLetter {
	boolean accept(char c);
	SLetter intersect(SLetter target) throws LetterNotCompatible;
	char select();
	
	SLetterType getType();
}
