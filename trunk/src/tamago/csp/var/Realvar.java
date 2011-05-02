/**
 * 
 */
package tamago.csp.var;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tamago.csp.Backtracking;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPreal;
import tamago.csp.domain.CSPAbstractDomain;
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
public class Realvar extends DefaultCSPvar implements FDvar {

	private ArrayList<CSPreal> removed;
	private double min;
	private double max;
	private boolean fixed;

	public static final double DELTA = 0.0000000000001;
	public static final double MAX_REAL = 10000.0;
	public static final double MIN_REAL = -10000.0;
	
	public static boolean q(double a,double b) {
		return Math.abs(a-b) < DELTA;
	}
	
	/**
	 * 
	 */
	public Realvar(String name,Backtracking b) {
		super(name, b);
		removed = new ArrayList<CSPreal>();
		min = MIN_REAL;
		max = MAX_REAL;
		fixed = false;
	}
	public Realvar(String name,Backtracking b,double nmin,double nmax) {
		this(name,b);
		min = nmin;
		max = nmax;
	}

	/**
	 * @see tamago.csp.generic.FDvar#add(tamago.csp.generic.CSPconst)
	 */
	public void add(CSPconst value) throws TamagoCSPException {
		if(removed.contains(value))
			removed.remove(value);
	}

	/**
	 * @see tamago.csp.generic.FDvar#getMax()
	 */
	public CSPconst getMax() {
		return new CSPreal(max);
	}

	/**
	 * @see tamago.csp.generic.FDvar#getMin()
	 */
	public CSPconst getMin() {
		return new CSPreal(min);
	}

	/**
	 * @see tamago.csp.generic.FDvar#getRemoved()
	 */
	public Iterable<? extends CSPconst> getRemoved() {
		return removed;
	}

	/**
	 * @see tamago.csp.generic.FDvar#intersect(tamago.csp.generic.FDvar)
	 */
	public void intersect(FDvar v) throws TamagoCSPException {
		double tmp_min = v.getMin().realValue();
		double tmp_max = v.getMax().realValue();

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

	/**
	 * @see tamago.csp.generic.FDvar#remove(tamago.csp.generic.CSPconst)
	 */
	public void remove(CSPconst genvalue) throws TamagoCSPException {
		CSPreal value = new CSPreal(genvalue.realValue()); // fonction qui permet de convertir le type
		// afin que la methode pour le test d'egalite soit correct
		if(q(value.realValue(),min))
			setMin(new CSPreal(min+DELTA));
		else if(value.realValue() == max)
			setMax(new CSPreal(max-DELTA));
		else if(!removed.contains(value)) {
			removed.add(new CSPreal(value.realValue()));
			RealTriplet triplet = new RealTriplet(this,FDTripletType.REMOVE,value.realValue());
			b.push(triplet);
			filter();
		}
	}

	/**
	 * @see tamago.csp.generic.FDvar#setMax(tamago.csp.generic.CSPconst)
	 */
	public void setMax(CSPconst max) throws TamagoCSPException {
		double nmax = max.realValue();
		if(nmax < min)
			throw new TamagoCSPException("MAX is too small");
		if(isInDomain(max) && (!q(this.max,nmax))) {
			RealTriplet triplet = new RealTriplet(this,FDTripletType.SETMAX,this.max);

			if(nmax < this.min)
				throw new TamagoCSPException("Max lower than min");
			while(!isInDomain(new CSPreal(nmax))) {
				nmax -= DELTA;
				if(nmax < this.min)
					throw new TamagoCSPException("Max lower than min");
			}
			this.max = nmax;
			b.push(triplet);
			filter();
		}
	}

	/**
	 * @see tamago.csp.generic.FDvar#setMaxEx(tamago.csp.generic.CSPconst)
	 */
	public void setMaxEx(CSPconst max) throws TamagoCSPException {
		setMax(new CSPreal(max.realValue()-DELTA));
	}

	/**
	 * @see tamago.csp.generic.FDvar#setMin(tamago.csp.generic.CSPconst)
	 */
	public void setMin(CSPconst min) throws TamagoCSPException {
		double nmin = min.realValue();
		if(nmin > max)
			throw new TamagoCSPException("MIN is too high");
		if(isInDomain(min) && (!q(nmin,this.min))) {
			RealTriplet triplet = new RealTriplet(this,FDTripletType.SETMIN,this.min);

			if(nmin > this.max)
				throw new TamagoCSPException("Min greater than max");
			while(!isInDomain(new CSPreal(nmin))) {
				nmin+= DELTA;
				if(nmin > this.max)
					throw new TamagoCSPException("Min greater than max");
			}
			this.min = nmin;
			b.push(triplet);
			filter();
		}

	}

	/**
	 * @see tamago.csp.generic.FDvar#setMinEx(tamago.csp.generic.CSPconst)
	 */
	public void setMinEx(CSPconst min) throws TamagoCSPException {
		setMin(new CSPreal(min.realValue()+DELTA));
	}

	/**
	 * @see tamago.csp.generic.FDvar#size()
	 */
	public long size() {
		return Long.MAX_VALUE; 
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	public void filter() throws TamagoCSPException {
		for (CSPConstraint constraint : constraints) {
			constraint.filter();
		}
		updated();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	public void fix(CSPconst n) throws TamagoCSPException {
		if(isInDomain(n)) {
			if(fixed && (!q(min,n.realValue())))
				throw new TamagoCSPException("FIX value already done for another value");
			else if(fixed && q(min,n.realValue()))
				return; // nothing to do
			else {
				RealFixTriplet triplet = new RealFixTriplet(this,this.min,this.max);
				min = n.realValue();
				max = min;
				b.push(triplet);
				fixed = true;
				filter();
			}
		}
		else
			throw new TamagoCSPException(toString()+": FIX value ("+n.realValue()+") not possible for "+toString()); 

	}

	/**
	 * @see tamago.csp.generic.CSPvar#getValue()
	 */
	public CSPconst getValue() {
		if(!isFixed())
			throw new TamagoCSPRuntime("Echec variable not fixed:"+toString());
		return new CSPreal(min);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	public void forward() throws TamagoCSPException {
		if(isFixed())
			return;
		
		// choose a value
		// currently i choose a random value
		if(!q(max,min)) {
			Random random = new Random();
			double cur = min + random.nextDouble()*(max-min);
			while(!isInDomain(new CSPreal(cur))) {
				cur = min + random.nextDouble()*(max-min);
			}
			RealTriplet triplet = new RealTriplet(this,FDTripletType.FORWARD,cur);
			min = cur;
			max = min;
			b.push(triplet);
			fixed = true;
			filter();
			
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	public boolean isFixed() {
		return q(min,max);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(tamago.csp.generic.CSPconst)
	 */
	public boolean isInDomain(CSPconst o) {
		double a = o.realValue();
		if(min <= a && a<= max) {
			CSPreal real = new CSPreal(a);
			return !removed.contains(real);
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
		switch(((RealTriplet)triplet).getType()) {
		case FORWARD:
			remove(triplet.getValue());
			
			break;
		case FIX: {
			RealFixTriplet intfix = (RealFixTriplet) triplet;
			min = intfix.getMin();
			max = intfix.getMax();
			fixed = q(min,max);
			break;
		}
		case REMOVE: {
			RealTriplet t = (RealTriplet) triplet;
			CSPreal i = new CSPreal(t.value());
			if(removed.contains(i))
				removed.remove(i);
			break;
		}
		case SETMAX: {
			RealTriplet t = (RealTriplet) triplet;
			max = t.value();
			break;
		}
		case SETMIN:{
			RealTriplet t = (RealTriplet) triplet;
			min = t.value();
			break;
		}
		}

	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" : [");
		sb.append(min);
		sb.append(", ");
		sb.append(max);
		sb.append("] \\ {");
		Iterator<CSPreal> ints = removed.iterator();
		while(ints.hasNext()) {
			sb.append(ints.next().toString());
			if(ints.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public CSPAbstractDomain getAbstractDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}
}
