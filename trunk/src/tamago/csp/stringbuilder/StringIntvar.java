/**
 * 
 */
package tamago.csp.stringbuilder;

import tamago.csp.Backtracking;
import tamago.csp.constant.CSPinteger;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.stringbuilder.automaton.SAnyLetter;
import tamago.csp.stringbuilder.automaton.SAuto;
import tamago.csp.var.Intvar;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public class StringIntvar extends Intvar {

	private Stringvar str;
	
	/**
	 * @param name
	 * @param b
	 */
	public StringIntvar(Stringvar var, Backtracking b) {
		super(var.getName()+"#size",b);
		this.min = 0;
		this.str = var;
		this.setMustInstantiate(false);
	}
	
	public void setMin(CSPconst min) throws TamagoCSPException {
		int oldmin = this.min;
		super.setMin(min);
		if(!isInDomain(min) || (this.min == min.intValue()))
			return;
		TamagoCCLogger.println(4,"setMin:"+min.intValue());
		SAuto renvoie = SAuto.motifRepeatChar(new SAnyLetter(), min.intValue()); 
		/*
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		SAuto renvoie = SAuto.motifEmpty();//SAuto.motifChar(new SAnyLetter());
		for(int i = 0; i < min.intValue(); i++) {
			renvoie = SAuto.motifSequence(auto, renvoie);
		}
		auto = SAuto.motifRepeat(auto);
		renvoie = SAuto.motifSequence(renvoie,auto);
		*/
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		auto = SAuto.motifRepeat(auto);
		renvoie = SAuto.motifSequence(renvoie,auto);
		
		if(!str.canfusion(renvoie))
			throw new TamagoCSPException("impossible size");
		if(oldmin == this.min)
			str.fusion(renvoie);
		TamagoCCLogger.println(4,"End setMin:"+min.intValue());
	}
	
	void setMinCore(int min) throws TamagoCSPException {
		super.setMin(new CSPinteger(min));
	}
	
	void setMaxCore(int max) throws TamagoCSPException {
		super.setMax(new CSPinteger(max));
	}
	
	public void setMax(CSPconst max) throws TamagoCSPException {
		int oldmax = this.max;
		super.setMax(max);
		if(!isInDomain(max) || (this.max == max.intValue()))
			return;
		TamagoCCLogger.println(4,"setMax:"+max.intValue());
		SAuto renvoie = SAuto.motifRepeatChar(new SAnyLetter(), max.intValue()); 

		/*
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		SAuto renvoie = SAuto.motifEmpty();// SAuto.motifChar(new SAnyLetter());
		
		for(int i = 0; i < max.intValue(); i++) {
			renvoie = SAuto.motifSequence(auto, renvoie);
		}
		for(SState state : renvoie.getStates()) {
			renvoie.addFinalState(state);
		}
		*/
		if(!str.canfusion(renvoie))
			throw new TamagoCSPException("impossible size");

		if(oldmax == this.max)
			str.fusion(renvoie);
		TamagoCCLogger.println(4,"End setMax:"+max.intValue());
	}
	
	public void fix(CSPconst n) throws TamagoCSPException {
		if(this.isFixed()) {
			super.fix(n);
			return;
		}
		TamagoCCLogger.println(4,"Fix:"+n);			
		SAuto renvoie = SAuto.motifRepeatChar(new SAnyLetter(), n.intValue()); 
		/*
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		SAuto renvoie = SAuto.motifChar(new SAnyLetter());
		for(int i = 1; i < n.intValue(); i++) {
			renvoie = SAuto.motifSequence(auto, renvoie);
		}
		*/
		if(!str.canfusion(renvoie))
			throw new TamagoCSPException("impossible size");
		super.fix(n);
		
		// TODO: attention il ne faut fusionner que si c necessaire pour garantir
		// une stabilite de la pile au bout d'un certain temps.
		str.fusion(renvoie);
		TamagoCCLogger.println(4,"End Fix:"+n);			
	}
	
	public void forward() throws TamagoCSPException {
		 
		 TamagoCCLogger.println(4,"Forward:");
		 super.forward();
		 SAuto renvoie = SAuto.motifRepeatChar(new SAnyLetter(), getValue().intValue()); 
		/*
		SAuto auto = SAuto.motifChar(new SAnyLetter());
		SAuto renvoie = SAuto.motifChar(new SAnyLetter());
		for(int i = 1; i < value; i++) {
			renvoie = SAuto.motifSequence(auto, renvoie);
		}
		*/
		if(!str.canfusion(renvoie))
			throw new TamagoCSPException("impossible size");
		
		str.fusion(renvoie);
		TamagoCCLogger.println(4,"End Forward:");
	}

}
