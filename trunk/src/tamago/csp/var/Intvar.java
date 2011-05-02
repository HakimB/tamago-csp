/**
 * 
 */
package tamago.csp.var;

 import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPinteger;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.domain.IntDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.DefaultCSPvar;
import tamago.csp.generic.FDvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class Intvar extends DefaultCSPvar implements FDvar {

	protected ArrayList<CSPinteger> removed;
	protected int min;
	protected int max;
	protected boolean fixed;
	protected int value;
	private boolean bounded;
	
	public static int MAXINT = 10000;
	public static int MININT = -10000;
	
	/**
	 * 
	 */
	public Intvar(String name, Backtracking b) {
		super(name,b);
		min = MININT;
		max = MAXINT;
		removed = new ArrayList<CSPinteger>();
		fixed = false;
		bounded = false;
	}
	
	public boolean getBounded() { return bounded; }
	public void setBounded(boolean value) { bounded = value; }
	
	public Intvar(String name, Backtracking b, boolean bounded) {
		this(name,b);
		this.bounded = bounded;
	}
	
	public Intvar(String name, Backtracking b,int min, int max) {
		super(name,b);
		this.min = min;
		this.max = max;
		removed = new ArrayList<CSPinteger>();
		fixed = false;
		bounded = false;
	}
	public Intvar(String name,Backtracking b,int min, int max,boolean bounded) {
		this(name,b,min,max);
		this.bounded = bounded;
	}

	public Intvar(String name, Backtracking b,int value) {
		super(name,b);
		this.min = value;
		this.max = value;
		removed = new ArrayList<CSPinteger>();
		fixed = true;
		bounded = false;
	}
	public Intvar(String name, Backtracking b,int value,boolean bounded) {
		this(name,b,value);
		this.bounded = bounded;
	}
	
	/**
	 * @see tamago.csp.generic.FDvar#add(java.lang.Object)
	 */
	public void add(CSPconst value) throws TamagoCSPException {
		if(removed.contains(value))
			removed.remove(value);
	}

	/**
	 * @see tamago.csp.generic.FDvar#getMax()
	 */
	public CSPconst getMax() {
		return new CSPinteger(max);
	}

	/**
	 * @see tamago.csp.generic.FDvar#getMin()
	 */
	public CSPconst getMin() {
		return new CSPinteger(min);
	}

	/**
	 * @see tamago.csp.generic.FDvar#remove(java.lang.Object)
	 */
	public void remove(CSPconst genvalue) throws TamagoCSPException {
		CSPinteger value = new CSPinteger(genvalue.intValue()); // cf. Realvar.remove
		
		if(value.intValue() == min)
			setMin(new CSPinteger(min+1));
		else if(value.intValue() == max)
			setMax(new CSPinteger(max-1));
		else if(isInDomain(value)) {
			removed.add(new CSPinteger(value.intValue()));
			IntTriplet triplet = new IntTriplet(this,FDTripletType.REMOVE,value.intValue());
			b.push(triplet);
			filter();
		}
	}

	/**
	 * @see tamago.csp.generic.FDvar#setMax(java.lang.Object)
	 */
	public void setMax(CSPconst max) throws TamagoCSPException {
		int nmax = max.intValue();
		if(nmax < min)
			throw new TamagoCSPException("MAX is too small");
		
		if(this.max > nmax) {
			IntTriplet triplet = new IntTriplet(this,FDTripletType.SETMAX,this.max);

			if(nmax < this.min)
				throw new TamagoCSPException("Max lower than min");
			while(!isInDomain(new CSPinteger(nmax))) {
				nmax--;
				if(nmax < this.min)
					throw new TamagoCSPException("Max lower than min");
			}
			this.max = nmax;
			b.push(triplet);
			filter();
		}
	}

	/**
	 * @see tamago.csp.generic.FDvar#setMin(java.lang.Object)
	 */
	public void setMin(CSPconst min) throws TamagoCSPException {
		int nmin = min.intValue();
		if(nmin > max)
			throw new TamagoCSPException(getName()+": MIN is too high ("+nmin+")");
		
		if(nmin > this.min) {
			IntTriplet triplet = new IntTriplet(this,FDTripletType.SETMIN,this.min);

			if(nmin > this.max)
				throw new TamagoCSPException(getName()+": Min greater than max");
			while(!isInDomain(new CSPinteger(nmin))) {
				nmin++;
				if(nmin > this.max)
					throw new TamagoCSPException(getName()+": Min greater than max");
			}
			this.min = nmin;
			b.push(triplet);
			filter();
		}
	}

	/**
	 * @see tamago.csp.generic.FDvar#size()
	 */
	public long size() {
		long size = ((long)max) - ((long)min) + 1;
		for (CSPinteger rev : removed) {
			if(min <= rev.intValue() && rev.intValue() <= max)
				size--;
		}
		return size;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		Iterable<CSPConstraint> cons = new ArrayList<CSPConstraint>(constraints);
		for (CSPConstraint constraint : cons) {
			constraint.filter();
		}
		updated();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(java.lang.Object)
	 */
	public boolean isInDomain(CSPconst o) {
		if(o instanceof CSPconst) {
			CSPconst i = (CSPconst)o;
			int v = i.intValue();
			if((min <= v) && (v <= max)) {
				return !removed.contains(o);
			}
		}
		return false;
	}

	public boolean isPrimitive() {
		return true;
	}

	public boolean isFixed() {
		return size()==1L;
	}

	public void retrieve(Triplet triplet) throws TamagoCSPException {
		switch (((IntTriplet)triplet).type()) {
		case FORWARD:
			//try {
				remove(triplet.getValue());
			//} catch (TamagoCSPException e) {
			//	TamagoCCLogger.println(3, "*Warning* convergency of variable "+getName()+" may be slowly");
			//}
			break;
		case FIX: {
			IntFixTriplet intfix = (IntFixTriplet) triplet;
			min = intfix.getMin();
			max = intfix.getMax();
			if(min != max)
				fixed = false;
			break;
		}
		case REMOVE: {
			IntTriplet t = (IntTriplet) triplet;
			CSPinteger i = new CSPinteger(t.value());
			if(removed.contains(i))
				removed.remove(i);
			break;
		}
		case SETMAX: {
			IntTriplet t = (IntTriplet) triplet;
			max = t.value();
			break;
		}
		case SETMIN:{
			IntTriplet t = (IntTriplet) triplet;
			min = t.value();
			break;
		}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" : ");
		if(isFixed()) {
			sb.append(" FIXED ");
			sb.append(getValue().toString());
		}
		else {
			sb.append("[ ");
			sb.append(min);
			sb.append(", ");
			sb.append(max);
			sb.append("] \\ {");
			Iterator<CSPinteger> ints = removed.iterator();
			while(ints.hasNext()) {
				sb.append(ints.next().toString());
				if(ints.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}

	public void forward() throws TamagoCSPException {
		if(isFixed())
			return;
		// choose a value
		// currently i choose a random value
		if(max != min) {
			Random random = new Random();
			int cur = min + random.nextInt(max-min);
			if(bounded) {
				if(random.nextBoolean()) {
					cur = min;
				}
				else {
					cur = max;
				}
			}
			else {
				while(!isInDomain(new CSPinteger(cur))) {
					cur = min + random.nextInt(max-min);
				}
			}
			IntTriplet forward = new IntTriplet(this,FDTripletType.FORWARD,cur);
			b.push(forward);
			IntFixTriplet backup = new IntFixTriplet(this,min,max);
			b.push(backup);
			value = cur;
			min = value;
			max = value;
			fixed = true;
		}
		filter();
	}

	public void fix(CSPconst n) throws TamagoCSPException {
		if(isInDomain(n)) {
			if(fixed && (value != n.intValue()))
				throw new TamagoCSPException(getName() + ": FIX "+n.intValue()+" but already set for "+value);
			else if(fixed && (value == n.intValue()))
				return; // nothing to do
			else {
				IntFixTriplet triplet = new IntFixTriplet(this,this.min,this.max);
				value = n.intValue();
				min = value;
				max = value;
				b.push(triplet);
				fixed = true;
				filter();
			}
		}
		else
			throw new TamagoCSPException(toString()+": FIX value not possible : "+n.intValue()); 
	}

	public void intersect(FDvar v) throws TamagoCSPException {
		int tmp_min = v.getMin().intValue();
		int tmp_max = v.getMax().intValue();

		if(tmp_min > this.min) {
			setMin(v.getMin());
		}

		if(tmp_max < this.max) {
			setMax(v.getMax());
		}
		// -----------------------------

		for (CSPconst num : v.getRemoved()) {
			if(isInDomain(num)) {
				remove(num);
			}
		}

	}

	public Iterable<? extends CSPconst> getRemoved() {
		return removed;
	}

	public void setMaxEx(CSPconst max) throws TamagoCSPException {
		int nmax = max.intValue();
		setMax(new CSPinteger(nmax-1));
	}

	public void setMinEx(CSPconst min) throws TamagoCSPException {
		int nmin = min.intValue();
		setMin(new CSPinteger(nmin+1));
	}

	public CSPconst getValue() {
		if(!isFixed())
			throw new TamagoCSPRuntime("variable "+toString()+" not fixed");
		return new CSPinteger(min);
	}

	public CSPAbstractDomain getAbstractDomain() {
		return new IntDomain(min, max, removed);
	}

	public boolean load(CSPAbstractDomain domain) {
		if(domain instanceof IntDomain) {
			IntDomain idom = (IntDomain) domain;
			min = idom.getMin().intValue();
			max = idom.getMax().intValue();
			removed.clear();
			removed.addAll(idom.getRemoved());
			return true;
		}
		return false;
	}
	
	
}
