/**
 * 
 */
package tamago.csp.var;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class MaxIntvar extends Intvar implements CSPrepercussion {

	private Intvar l;
	private Intvar r;
	
	/**
	 * @param name
	 * @param b
	 */
	public MaxIntvar(String name, Backtracking b, Intvar l, Intvar r) {
		super(name, b);
		this.l = l;
		this.r = r;
		l.addRepercussion(this);
		r.addRepercussion(this);
		addRepercussion(this);
	}

	@Override
	public void updateDomain(CSPvar v) throws TamagoCSPException {
		if(v == this) {
			l.setMax(getMax());
			r.setMax(getMax());
			
			//l.setMin(getMin());
			//r.setMin(getMin());
		}
		else {
			CSPconst lm = l.getMax();
			CSPconst rm = r.getMax();

			if(lm.intValue() > rm.intValue())
				setMax(lm);
			else
				setMax(rm);

			lm = l.getMin();
			rm = r.getMin();
			if(lm.intValue() > rm.intValue())
				setMin(lm);
			else
				setMin(rm);
		}
	}

	

}
