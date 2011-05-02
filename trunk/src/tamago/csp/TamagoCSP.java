/**
 * 
 */
package tamago.csp;

import java.util.ArrayList;
import java.util.EmptyStackException;

import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPHeuristicException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;
import tamago.csp.heuristic.FFHeuristic;
import tamago.csp.heuristic.TamagoCSPHeuristic;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public final class TamagoCSP {

	private ArrayList<CSPvar> vars;
	private ArrayList<CSPConstraint> constraints;
	private Backtracking backtrack;
	private TamagoCSPHeuristic heuristic;

	/**
	 * 
	 */
	public TamagoCSP() {
		vars = new ArrayList<CSPvar>();
		constraints = new ArrayList<CSPConstraint>();
		backtrack = new Backtracking();
		//heuristic = new StdHeuristic(vars,backtrack);
		heuristic = new FFHeuristic(vars,backtrack);
		//heuristic = new ComplexHeuristic(vars,backtrack);
	}

	public Backtracking getBacktrack() {
		return backtrack;
	}

	public void addVariable(CSPvar v) {
		vars.add(v);
	}

	public Iterable<CSPvar> getVariables() {
		return vars;
	}

	public Iterable<CSPConstraint> getConstraints() {
		return constraints;
	}

	public void addConstraint(CSPConstraint c) {
		constraints.add(c);
	}

	private boolean isFinish() {
		boolean isfin = true;
		for (CSPvar var : vars) {
			if(var.mustInstantiate())
				isfin = var.isFixed() && isfin;
		}
		return isfin;
	}


	private void forward() throws TamagoCSPHeuristicException, TamagoCSPException {
		CSPvar v = heuristic.select();
		TamagoCCLogger.println(4, "*** ***** **** Select variable: "+v.getName());
		v.forward();
		TamagoCCLogger.println(4, "*** ***** **** Select value: "+v.getValue().toString());
	}

	public void solve() throws TamagoCSPException {
		try {
			while(!isFinish()) {
				try {
					minimize();
					TamagoCCLogger.println(2, "Minization succeed");
					if(isFinish())
						return;
					forward();
				}
				catch(TamagoCSPException e) {
					TamagoCCLogger.info(4,e);
					boolean cont = true;
					while(cont) {
						try {
							backtrack();
							cont = false;
						}
						catch(TamagoCSPException exb) {
							
						}
					}
				}
			}	
		}
		catch(EmptyStackException ese) {
			TamagoCCLogger.println(2, "empty stack");
			throw new TamagoCSPException("No more solution");
		}
		catch(TamagoCSPHeuristicException herr) {
			TamagoCCLogger.println(1, "Plus de variable");
			throw new TamagoCSPException(herr);
		}
	}

	private void backtrack() throws TamagoCSPException {
		TamagoCCLogger.println(4, "BACKTRACK: ");
		Triplet t = null;
		boolean cont = true;
		do {
			t = backtrack.pop();
			CSPvar v = t.getVariable();
			TamagoCCLogger.println(4, "    TRIPLET TYPE "+ t.getClass().getName());
			TamagoCCLogger.println(4, "            on "+v.getName());
			TamagoCCLogger.println(4, "            flag "+t.isForward());
			TamagoCCLogger.println(4, "            val "+t.getValue());
			v.retrieve(t);
			if(t.isForward()) {
				cont = false;//!v.mustInstantiate();
			}
		} while(cont) ;
		heuristic.undo();
	}

	public void setHeuristic(TamagoCSPHeuristic heuristic) {
		this.heuristic = heuristic;
	}

	public void minimize() throws TamagoCSPException {
		int lg = backtrack.size();
		do {
			lg = backtrack.size();
			ArrayList<CSPConstraint> clone = new ArrayList<CSPConstraint>(constraints);
			for (CSPConstraint constraint : clone) {
				constraint.filter();				
			}
			ArrayList<CSPvar> clonevars = new ArrayList<CSPvar>(vars);
			for (CSPvar var : clonevars) {
				var.filter();
			}
		} while(backtrack.size() != lg);
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CSP:\n");
		sb.append("Variables:\n");
		for (CSPvar var : vars) {
			sb.append("\t");
			sb.append(var.toString());
			sb.append("\n");
		}
		sb.append("Constraints:\n");
		for (CSPConstraint cons : constraints) {
			sb.append("\t");
			sb.append(cons.toString());
			sb.append("\n");
		}
		sb.append("Instantiate:\n");
		for(CSPvar var : vars) {
			if(var.isFixed()) {
				sb.append("\t");
				sb.append(var.getName());
				sb.append(" := ");
				sb.append(var.getValue().toString());
				sb.append("\n");
			}
		}

		sb.append("Not Instantiate:\n");
		for(CSPvar var : vars) {
			if(!var.isFixed()) {
				sb.append("\t");
				sb.append(var.toString());
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}

	public void clearConstraints() {
		constraints.clear();
		for (CSPvar var : vars) {
			var.uninstallAllConstraints();
			var.removeAllRepercussiions();
		}

	}

	public void fixBacktrack() {
		for(CSPvar var : vars) {
			var.setBacktrack(this.backtrack);
		}
	}
}
