/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

/**
 * @author Hakim Belhaouari
 *
 */
public interface SAbsTransition {
	boolean isEpsilon();
	
	SLetter getLetter();
	
	
	SState getBeg();
	
	boolean accept(char c);

	public SState getEnd() ;
	
	public SAbsTransition clone(SState beg,SState end);
	
	public boolean equals(Object o);
	
}
