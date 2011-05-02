/**
 * 
 */
package tamago.csp.stringbuilder;

import java.util.ArrayList;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamago.csp.stringbuilder.automaton.SAnyLetter;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.stringbuilder.automaton.SAutoFusion;
import tamago.csp.stringbuilder.automaton.SPrintAutomaton;
import tamago.csp.stringbuilder.automaton.SState;
import tamago.csp.var.Intvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class InferAutoFD implements InferAuto {

	private FDvar fdvar;
	private transient SAuto auto;
	private CSPConstraint constraint;
	
	/**
	 * 
	 */
	public InferAutoFD(FDvar fdvar) {
		this.fdvar = fdvar;
		fdvar.install(this);
		this.auto = null;
		constraint = null;
		
		// on deconnecte la variable aux CSP pour qu'elles
		// soient instancier aux bons moments
		fdvar.setMustInstantiate(false);
	}

	/**
	 * @see tamago.csp.stringbuilder.InferAuto#infer()
	 */
	public SAuto infer() {
		if(auto == null)
			updateAuto();
		return auto;
	}


	/**
	 * @see tamago.csp.stringbuilder.InferAuto#isFixed()
	 */
	public boolean isFixed() {
		return fdvar.isFixed();
	}

	/**
	 * @see tamago.csp.stringbuilder.InferAuto#size()
	 */
	public FDvar length() {
		return fdvar;
	}

	public void fix() throws TamagoCSPException {
		fdvar.forward();
	}
	
	private SAuto max() {
		
		//if(fdvar.getMax().intValue() > 1000) {
		//	TamagoCCLogger.print(1, "*ERROR* AutoFD too high, not yet implemented, alert me plz!!!! 0x5627747");
		//	return SAuto.motifAny();
		//}
		
		if(!fdvar.isFixed())
			return SAuto.motifAny();
		else		
		{

			SAuto auto = SAuto.motifChar(new SAnyLetter());
			SAuto renvoie2 = SAuto.motifEmpty();// SAuto.motifChar(new SAnyLetter());
			for(int i = 0; i < fdvar.getMax().intValue(); i++) {
				renvoie2 = SAuto.motifSequence(auto, renvoie2);
			}
			for(SState state : renvoie2.getStates()) {
				renvoie2.addFinalState(state);
			}
			return renvoie2;
		}
	}
	
	private SAuto min() {
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		SAuto renvoie = SAuto.motifEmpty();//SAuto.motifChar(new SAnyLetter());
		for(int i = 0; i < fdvar.getMin().intValue(); i++) {
			renvoie = SAuto.motifSequence(auto, renvoie);
		}
		auto = SAuto.motifRepeat(auto);
		renvoie = SAuto.motifSequence(renvoie,auto);
		
		return renvoie;
	}
	/**
	 * @see tamago.csp.stringbuilder.InferAuto#updateAuto()
	 */
	public void updateAuto() {
		if(fdvar.getMin().intValue() <= 0) {
			this.auto = max();
		}
		else {
			SAuto min = min();
			SAuto max = max();
			try {
				this.auto = SAutoFusion.fusion(min, max);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	public void filter() throws TamagoCSPException {
		updateAuto();
		if(constraint != null)
			constraint.filter();
	}

	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> tmp = new ArrayList<CSPvar>(1);
		tmp.add(fdvar);
		return tmp;
	}

	public int size() {
		return 1;
	}

	public void install(CSPConstraint constraint) {
		this.constraint = constraint;		
	}
	
	public String toString() {
		return fdvar.toString();
	}

	public static void main(String args[]) {
		Intvar ent = new Intvar("toto",new Backtracking(),0,4);
		InferAutoFD iafd = new InferAutoFD(ent);
		
		iafd.updateAuto();
		
		try {
			new SPrintAutomaton(iafd.min(),"min");
			new SPrintAutomaton(iafd.max(),"max");
			new SPrintAutomaton(iafd.infer(),"infer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin");
	}
	
}
