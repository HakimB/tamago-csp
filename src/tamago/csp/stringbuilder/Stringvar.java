/**
 * 
 */
package tamago.csp.stringbuilder;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPVarEqInterface;
import tamago.csp.generic.Obvar;
import tamago.csp.stringbuilder.automaton.SAuto;

/**
 * @author Hakim Belhaouari
 *
 */
public interface Stringvar extends Obvar, CSPVarEqInterface {
	public StringIntvar getIntvar();
	
	void fusion(SAuto auto) throws TamagoCSPException;
	SAuto getFusion(SAuto auto) throws TamagoCSPException;
	
	boolean canfusion(SAuto auto);
	
	SAuto getRegExp();	
	void setRegExp(SAuto auto);
	
	void updateSubString(boolean affect) throws TamagoCSPException;
	
	void addDepends(Stringvar sv);
	
	void clearDepends();
	
}
