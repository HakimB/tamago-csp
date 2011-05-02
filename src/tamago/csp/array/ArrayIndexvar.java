/**
 * 
 */
package tamago.csp.array;

import tamago.csp.Backtracking;
import tamago.csp.constraint.XltY;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.var.Intvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class ArrayIndexvar extends Intvar implements CSPrepercussion {

	private static int prob = 0;
	private static String genName() {
		return "idx"+(prob++);
	}
	
	private Intvar borne;
	/**
	 * @param name
	 * @param b
	 */
	public ArrayIndexvar(Intvar borne,Backtracking b) {
		super(genName(), b,0, Intvar.MAXINT);
		this.borne = borne;
		
		borne.addRepercussion(this);
		setMustInstantiate(true);
		
		new XltY(this, borne);
	}
	
	public ArrayIndexvar(Intvar borne, int value,Backtracking b) {
		super(genName(), b,value);
		this.borne = borne;
		
		borne.addRepercussion(this);
		setMustInstantiate(true);
	}
	
	public ArrayIndexvar(String name,Intvar borne,Backtracking b) {
		super(name, b,0, Intvar.MAXINT);
		this.borne = borne;
		
		borne.addRepercussion(this);
		setMustInstantiate(true);
	}
	
	
	// un bug lorsque la longueur est deja filtre avant l'existence 
	// de cette variable dans ce cas, y'a pas de updateDomain dans la repercussion
	// c'est pour ca qu'on ajoute une contrainte franche
	public void updateDomain(CSPvar v) throws TamagoCSPException {
		if(v != borne) return;
		
		this.setMaxEx(((Intvar)v).getMax());
	}
}
