/**
 * 
 */
package tamago.csp.heuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPHeuristicException;
import tamago.csp.generic.CSPSizedvar;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author hakim
 *
 */
public class FFHeuristic implements TamagoCSPHeuristic {

	private ArrayList<CSPvar> vars;
	private int lastpos;
	@SuppressWarnings("unused")
	private Backtracking b;
	
	/**
	 * 
	 */
	public FFHeuristic(ArrayList<CSPvar> vars,Backtracking b) {
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
					TamagoCCLogger.println(4, "\tFFHeuristic: first selection of "+tmp.getName());
				}
				else {
					if((res instanceof CSPSizedvar) && (tmp instanceof CSPSizedvar)) {
						if(((CSPSizedvar)tmp).size() < ((CSPSizedvar)res).size()) {
							TamagoCCLogger.println(4, "\tFFHeuristic: change selection "+res.getName()+" for smaller domain "+tmp.getName());
							res = tmp;
						}
					}
					if(!(res instanceof CSPSizedvar) && (tmp instanceof CSPSizedvar)) {
						TamagoCCLogger.println(4, "\tFFHeuristic: change selection "+res.getName()+" for finite domain "+tmp.getName());
						res = tmp;
					}
					if(varDependOf(res,tmp)) {
						TamagoCCLogger.println(4, "\tFFHeuristic: change selection "+res.getName()+" for depending "+tmp.getName());
						res = tmp;
					}
				}
			pos = (pos+1)%vars.size();
					
		} while(pos != lastpos);
		TamagoCCLogger.println(4, "\tFFHeuristic: return "+res.getName());
		return res;
	}

	private boolean varDependOf(CSPvar res, CSPvar tmp) {
		ArrayList<CSPvar> visited = new ArrayList<CSPvar>();
		Stack<CSPvar> pile = new Stack<CSPvar>();
		pile.push(res);
		visited.add(res);
		
		while(pile.size() > 0) { 
			CSPvar v = pile.pop();
			if(!visited.contains(v)) {
				visited.add(v);
				Iterable<CSPrepercussion> morepriorities = v.getRepercussions();
				for (CSPrepercussion rep : morepriorities) {
					if(rep == tmp)
						return true;
					else if(rep instanceof CSPvar)
						pile.push((CSPvar)rep);
				}
			}
		}
		return false;
	}

	/**
	 * @see tamago.csp.heuristic.TamagoCSPHeuristic#undo()
	 */
	public void undo() {
		

	}

}
