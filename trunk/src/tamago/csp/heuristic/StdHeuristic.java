/**
 * 
 */
package tamago.csp.heuristic;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPHeuristicException;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class StdHeuristic implements TamagoCSPHeuristic {

	private ArrayList<CSPvar> vars;
	private int lastpos;
	@SuppressWarnings("unused")
	private Backtracking b;
	
	private long loop;
	
	private static final long MAX_LOOP = 20;
	/**
	 * 
	 */
	public StdHeuristic(ArrayList<CSPvar> vars,Backtracking b) {
		this.vars = vars;
		lastpos = 0;
		this.b = b;
	}

	/**
	 * @see tamago.csp.heuristic.TamagoCSPHeuristic#select()
	 */
	public CSPvar select() throws TamagoCSPHeuristicException {
		CSPvar v;
		loop = 0;
		do {
			v = vars.get(lastpos);
			lastpos = (lastpos+1)% vars.size();
			if(lastpos == 0) {
				if(loop > MAX_LOOP)
					throw new TamagoCSPHeuristicException("max loop reach, maybe inconsistent arc");
				else
					loop++;
			}
		} while(v.isFixed());
		return v;
	}

	public void undo() {
		
	}

}
