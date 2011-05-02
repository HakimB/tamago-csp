/**
 * 
 */
package tamago.csp.stringbuilder;

import tamago.csp.Backtracking;
import tamago.csp.constant.CSPstring;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.stringbuilder.automaton.SAutoFusion;
import tamago.csp.stringbuilder.automaton.SPrintAutomaton;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public class SubStringvar extends MainStringvar {

	private Stringvar owner;

	private InferAuto pre;
	private InferAuto post;

//private boolean updating;

		/**
	 * 
	 */
	public SubStringvar(String name,Backtracking b, Stringvar stringvar,InferAuto pre,InferAuto post) {
		super(name,b);
		this.pre = pre;
		this.post = post;
		updating = false;
		this.owner = stringvar;
		setMustInstantiate(false);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		for (CSPConstraint constraint : constraints) {
			constraint.filter();
		}
		updateSubString(false);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return fixed && pre.isFixed() && post.isFixed();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		if(isFixed())
			return;

		pre.length().forward();
		post.length().forward();
		size.forward();
		
		if(regexp.getFinals().size() == 0)
			throw new TamagoCSPException(getName()+".substring - No final state in this regexp");
		String f = SAuto.searchRandomWord(regexp);
		// check si le mot est correct
		SAuto concat= SAuto.convertStringToAuto(f);
		SAuto res = null;
		try {
			concat = SAuto.motifSequence(pre.infer() , concat); 
			//concat = SAuto.motifSequence(concat, post.infer());
			res= SAutoFusion.fusion(concat, post.infer());
			res = SAuto.motifSequence(res, SAuto.motifAny());
			owner.canfusion(res);
		}
		catch(Exception e) {
			TamagoCCLogger.println(3, " *** Exception during verification of substring ***");
			TamagoCCLogger.info(3, e);
			throw new TamagoCSPException(e);
		}

		fix(new CSPstring(f));
		owner.fusion(res); // j'avais mis avant concat;
	}

	
	public void updateSubString(boolean affect) throws TamagoCSPException {
		if(updating)
			return;
		// on met a jour nos informations
		updating = true;
		try {
			// on repercute chez nos dependances
			pre.updateAuto();
			post.updateAuto();


			SAuto auto = SAuto.motifSequence(pre.infer(), this.regexp);
			try {
				SAuto fus = SAutoFusion.fusion(auto, post.infer());
				//new SPrintAutomaton(fus,"substringPREM");
				fus = SAuto.motifSequence(fus, SAuto.motifAny() ); // on ajoute un padding infini
				SAuto fusion = owner.getFusion(fus);
				
				if(TamagoCCLogger.getLevel() > 6) {
					new SPrintAutomaton(pre.infer(),"pre");
					new SPrintAutomaton(post.infer(),"post");
					new SPrintAutomaton(regexp,"me");
					new SPrintAutomaton(auto,"auto");
					new SPrintAutomaton(fus,"substring");
					new SPrintAutomaton(owner.getRegExp(),"owner");
					new SPrintAutomaton(fusion,"resultat");
				}
				
				if(fusion == null) {
					throw new TamagoCSPException(toString()+" - substring problem");
				}
				if(affect){
					owner.fusion(fus);
					//owner.updateSubString(affect);
				}
				if(pre.isFixed() && post.isFixed()) {
					this.regexp = SAuto.subgraph(fusion, pre.length().getValue().intValue(), post.length().getValue().intValue());
					if(owner.isFixed()) {
						String f = SAuto.searchRandomWord(regexp);
						fix(new CSPstring(f));						
					}
					else
						owner.fusion(fus);
					//owner.updateSubString(affect);
				}
			}
			catch(TamagoCSPException tcspe) {
				TamagoCCLogger.print(3, "substring error: "+tcspe.getMessage());
				throw tcspe;
			}
			catch (Exception e) {
				TamagoCCLogger.println(3, "substring not checkable");
				TamagoCCLogger.info(3, e);
			}

			// on propage la mise a jour
			for (Stringvar var : depends) {
				var.updateSubString(affect);
			}	
		}
		finally {
			updating = false;
		}
	}

	public void uninstallAllConstraints() {
		constraints.clear();
		for (Stringvar sv : depends) {
			sv.uninstallAllConstraints();
		}
		clearDepends();
	}
}
