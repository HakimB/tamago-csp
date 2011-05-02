/**
 * 
 */
package tamago.csp.heuristic;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPHeuristicException;
import tamago.csp.generic.CSPSizedvar;
import tamago.csp.generic.CSPvar;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author hakim
 *
 */
public class ComplexHeuristic implements TamagoCSPHeuristic {

	private ArrayList<CSPvar> vars;
	private int lastpos;
	@SuppressWarnings("unused")
	private Backtracking b;
	/**
	 * 
	 */
	public ComplexHeuristic(ArrayList<CSPvar> vars,Backtracking b) {
		this.vars = vars;
		lastpos = 0;
		this.b = b;
	}

	/**
	 * @see tamago.csp.heuristic.TamagoCSPHeuristic#select()
	 */
	public CSPvar select() throws TamagoCSPHeuristicException {
		int pos =lastpos;
		CSPvar res = null;
		CSPvar tmp = null;
		do {
			tmp = vars.get(pos);
			if(!tmp.isFixed()) 
				if(res == null) {
					res = tmp;
					TamagoCCLogger.println(4, "\tLFHeuristic: first selection of "+tmp.getName());
				}
				else {
					if((res instanceof CSPSizedvar) && (tmp instanceof CSPSizedvar)) {
						if(((CSPSizedvar)tmp).size() > ((CSPSizedvar)res).size()) {
							TamagoCCLogger.println(4, "\tLFHeuristic: change selection "+res.getName()+" for "+tmp.getName());
							res = tmp;
						}
					}
					if((res instanceof CSPSizedvar) && !(tmp instanceof CSPSizedvar)) {
						TamagoCCLogger.println(4, "\tLFHeuristic: change selection "+res.getName()+" for "+tmp.getName());
						res = tmp;
					}
				}
			pos = (pos+1)%vars.size();
					
		} while(pos != lastpos);
		TamagoCCLogger.println(4, "\tLFHeuristic: return "+res.getName());
		return res;
	}

	/**
	 * @see tamago.csp.heuristic.TamagoCSPHeuristic#undo()
	 */
	public void undo() {
	
	}

}
