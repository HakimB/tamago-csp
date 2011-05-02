/**
 * 
 */
package tamago.csp.generic;

import tamago.csp.exception.TamagoCSPException;

/**
 * @author Hakim Belhaouari
 *
 */
public interface FDvar extends CSPSizedvar {
	CSPconst getMin();
	CSPconst getMax();
	
	void setMin(CSPconst min) throws TamagoCSPException;
	void setMax(CSPconst max) throws TamagoCSPException;
	void setMinEx(CSPconst min) throws TamagoCSPException;
	void setMaxEx(CSPconst max) throws TamagoCSPException;
	
	void intersect(FDvar v) throws TamagoCSPException;
			
	void remove(CSPconst value) throws TamagoCSPException;
	
	void add(CSPconst value) throws TamagoCSPException;
	
	/**
	 * Return all the set of removed value.
	 * @return
	 */
	Iterable<? extends CSPconst> getRemoved();
}
