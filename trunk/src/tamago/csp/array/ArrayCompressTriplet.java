/**
 * 
 */
package tamago.csp.array;

import java.util.ArrayList;

import tamago.builder.TamagoBuilder;
import tamago.csp.Triplet;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.FDvar;
import tamagocc.util.Pair;

/**
 * @author Hakim Belhaouari
 *
 */
public class ArrayCompressTriplet implements Triplet {
	private ArrayList<Pair<FDvar, TamagoBuilder>> clonevars;
	private Arrayvar owner;
	/**
	 * 
	 */
	public ArrayCompressTriplet(Arrayvar owner, ArrayList<Pair<FDvar, TamagoBuilder>> vars) {
		clonevars = vars;
		this.owner = owner;
	}

	/**
	 * @see tamago.csp.Triplet#getValue()
	 */
	public CSPconst getValue() {
		return null;
	}

	/**
	 * @see tamago.csp.Triplet#getVariable()
	 */
	public CSPvar getVariable() {
		return owner;
	}

	/**
	 * @see tamago.csp.Triplet#isForward()
	 */
	public boolean isForward() {
		return false;
	}
	
	public ArrayList<Pair<FDvar, TamagoBuilder>> getVars() {
		return clonevars;
	}

}
