/**
 * 
 */
package tamago.csp.var;

import java.util.Random;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPbool;
import tamago.csp.domain.BoolDomain;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPSizedvar;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.DefaultCSPvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class Boolvar extends DefaultCSPvar implements CSPSizedvar {

	private boolean domain[];

	/**
	 * 
	 */
	public Boolvar(String name,Backtracking b) {
		super(name,b);
		domain = new boolean[2];
		domain[0] = true;
		domain[1] = true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		updated();
		for (CSPConstraint constraint : constraints) {
			constraint.filter();
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		if(isFixed())
			return;
		Random random = new Random();
		fix(new CSPbool(random.nextBoolean()));
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return domain[0]^domain[1];
	}

	public CSPconst getValue() {
		if(!isFixed())
			throw new TamagoCSPRuntime("variable not fixed");
		if(domain[0])
			return new CSPbool(false);
		else
			return new CSPbool(true);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(java.lang.Object)
	 */
	public boolean isInDomain(CSPconst o) {
		if(o instanceof CSPconst) {
			boolean b = ((CSPconst)o).boolValue();
			if(b)
				return domain[1];
			else
				return domain[0];
		}
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isPrimitive()
	 */
	public boolean isPrimitive() {
		return true;
	}

	/**
	 * @throws TamagoCSPException 
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	public void retrieve(Triplet triplet) throws TamagoCSPException {
		BoolTriplet booltriplet = (BoolTriplet)triplet;
		switch((booltriplet).getType()) {
		case FORWARD:
			remove(triplet.getValue());
			break;
		case REMOVE:
			domain[0] = booltriplet.domaine(0);
			domain[1] = booltriplet.domaine(1);
			break;
		case FIX:
			domain[0] = booltriplet.domaine(0);
			domain[1] = booltriplet.domaine(1);
			break;
		default:
			throw new TamagoCSPRuntime("unknown boolean triplet");
		}

	}

	public void remove(CSPconst genvalue) throws TamagoCSPException {
		CSPbool value = new CSPbool(genvalue.boolValue());
		boolean b = ((CSPconst)value).boolValue();
		
		BoolTriplet bt = new BoolTriplet(FDTripletType.REMOVE,this,domain);
		if(b) {
			if(!domain[0])
				throw new TamagoCSPException("Boolvar "+getName()+" remove last possible value (true)");
			if(domain[1]) {
				this.b.push(bt);
				domain[1] = false;
			}
		}
		else {
			if(!domain[1])
				throw new TamagoCSPException("Boolvar "+getName()+" remove last possible value (false)");
			if(domain[0]) {
				this.b.push(bt);
				domain[0] = false;
			}
		}
/*
		if(b && domain[1]) {
			if(!domain[0])
				throw new TamagoCSPException("Boolvar "+getName()+" remove last bool (TRUE)");
			BoolTriplet bt = new BoolTriplet(FDTripletType.REMOVE,this,domain);
			this.b.push(bt);
			domain[1] = false;
		}
		else if((!b) && domain[0]) {
			if(!domain[1])
				throw new TamagoCSPException("Boolvar "+getName()+" remove last bool (TRUE)");
			BoolTriplet bt = new BoolTriplet(FDTripletType.REMOVE,this,domain);
			this.b.push(bt);
			domain[0] = false;
		}
*/
	}

	public void fix(CSPconst n) throws TamagoCSPException {
		if(n.boolValue()) {
			if(domain[1]) {
				if(domain[0]) {
					BoolTriplet bt = new BoolTriplet(FDTripletType.FIX,this,domain);
					this.b.push(bt);
					domain[0] = false;
				}
			}
			else {
				throw new TamagoCSPException(getName()+"FIX: true not possible value");
			}
		}
		else {
			if(domain[0]) {
				if(domain[1]) {
					BoolTriplet bt = new BoolTriplet(FDTripletType.FIX,this,domain);
					this.b.push(bt);
					domain[1] = false;
				}
			}
			else {
				throw new TamagoCSPException(toString() +": FIX false not possible value "+n.toString());
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" : { ");
		if(domain[0])
			sb.append("false ");
		if(domain[1])
			sb.append("true ");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @return
	 */
	public boolean[] getDomain() {
		return domain;
	}

	public long size() {
		return (domain[0]? 1 : 0) + (domain[1]? 1 : 0);
	}

	public CSPAbstractDomain getAbstractDomain() {
		return new BoolDomain(domain[0], domain[1]);
	}

	public boolean load(CSPAbstractDomain dom) {
		if(dom instanceof BoolDomain) {
			BoolDomain bd = (BoolDomain)dom;
			domain[0] = bd.a();
			domain[1] = bd.b();
			return true;
		}
		return false;
	}

}
