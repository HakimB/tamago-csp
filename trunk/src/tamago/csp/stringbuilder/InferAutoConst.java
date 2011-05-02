/**
 * 
 */
package tamago.csp.stringbuilder;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.stringbuilder.automaton.SAnyLetter;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.var.Intvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class InferAutoConst implements InferAuto {

	private CSPconst t;
	
	private SAuto auto;
	private FDvar fd;
	
	/**
	 * 
	 */
	public InferAutoConst(CSPconst cons,Backtracking b) {
		t = cons;
		fd = new Intvar("inferauto-TMP-CST",b,cons.intValue(),cons.intValue());
		fd.setMustInstantiate(false);
		genAuto();
	}

	private void genAuto() {
		SAuto tmp = SAuto.motifChar(new SAnyLetter());
		auto = SAuto.motifEmpty();
		for(int i=0; i < t.intValue(); i++) {
			auto = SAuto.motifSequence(auto, tmp);
		}
	}
	
	/**
	 * @see tamago.csp.stringbuilder.InferAuto#infer()
	 */
	public SAuto infer() {
		return auto;
	}

	/**
	 * @see tamago.csp.stringbuilder.InferAuto#updateAuto()
	 */
	public void updateAuto() {
		// rien a faire c une constante
	}

	public boolean isFixed() {
		return true;
	}

	public FDvar length() {
		return fd;
	}

	public void fix() throws TamagoCSPException {
		// rien a faire 		
	}
	
	public void filter() throws TamagoCSPException {
		fd.filter();
		updateAuto();
	}

	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> tmp = new ArrayList<CSPvar>(1);
		tmp.add(fd);
		return tmp;
	}

	public int size() {
		return 1;
	}

	public void install(CSPConstraint constraint) {
		// a priori rien a faire
		// la convergence est optimale
	}

}
