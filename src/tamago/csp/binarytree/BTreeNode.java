package tamago.csp.binarytree;

import java.util.ArrayList;
import java.util.Random;

import tamago.csp.Backtracking;
import tamago.csp.TamagoCSP;
import tamago.csp.Triplet;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constraint.EqLinear;
import tamago.csp.constraint.IneqLinear;
import tamago.csp.constraint.IneqOperator;
import tamago.csp.constraint.XEqC;
import tamago.csp.constraint.XGeqY;
import tamago.csp.constraint.XGtC;
import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPconst;
import tamago.csp.generic.CSPrepercussion;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.DefaultCSPvar;
import tamago.csp.generic.Obvar;
import tamago.csp.var.Intvar;
import tamagocc.exception.TamagoCCException;
import tamagocc.generic.api.GType;
import tamagocc.generic.impl.GIType;
import tamagocc.logger.TamagoCCLogger;

/**
 * 
 */

/**
 * @author Hakim Belhaouari
 *
 */
public class BTreeNode extends DefaultCSPvar implements Obvar,CSPrepercussion {

	protected Intvar depth;
	protected Intvar depthG;
	protected Intvar depthD;
	protected Intvar size;
	protected Intvar sizeG;
	protected Intvar sizeD;
	protected Intvar me;
	protected Random random;
	
	protected BTreeNode left;
	protected BTreeNode right;
	protected BTreeConstant value;
	protected GType type;
	protected BTreeMaxDepthConstraint btmdc;
	
	
	public BTreeNode(String name, GType type, Backtracking b) {
		this(name, new Intvar(name+".size", b, 0,Intvar.MAXINT), new Intvar(name+".depth", b,0,31), type, b);
	}
	
	/**
	 * @param name
	 * @param b
	 */
	public BTreeNode(String name, Intvar psize, Intvar pdepth, GType type, Backtracking b) {
		super(name, b);
		this.type = type;
		depth = pdepth;
		depthG = new Intvar(name+".depthG", b,0,31);
		depthD = new Intvar(name+".depthD", b,0,31);
		size = psize;
		sizeG = new Intvar(name+".sizeG", b, 0,Intvar.MAXINT);
		sizeD = new Intvar(name+".sizeD", b, 0,Intvar.MAXINT);
		me = new Intvar(name+"|me", b, 0, 1);
		
		XGeqY c1 = new XGeqY(size, depth);
		XGeqY c2 = new XGeqY(sizeG, depthG);
		XGeqY c3 = new XGeqY(sizeD, depthD);
		
		XGeqY c4 = new XGeqY(depth, me); // accelere la convergence
		XGeqY c5 = new XGeqY(size, me); // accelere la convergence
		
		btmdc = new BTreeMaxDepthConstraint(this);
				
		
		depth.addRepercussion(this);
		depthG.addRepercussion(this);
		depthD.addRepercussion(this);
		size.addRepercussion(this);
		sizeG.addRepercussion(this);
		sizeD.addRepercussion(this);
		
				
		EqLinear eqls = new EqLinear();
		eqls.put(new CSPinteger(-1), sizeD);
		eqls.put(new CSPinteger(-1), sizeG);
		eqls.put(new CSPinteger(1), size);
		eqls.put(new CSPinteger(-1), me);
		eqls.setConstant(new CSPinteger(0));
		
		IneqLinear ieqls;
		
		/*ieqls = new IneqLinear(IneqOperator.EQ);
		ieqls.put(new CSPinteger(-1), sizeD);
		ieqls.put(new CSPinteger(-1), sizeG);
		ieqls.put(new CSPinteger(1), size);
		ieqls.put(new CSPinteger(-1), me);
		ieqls.setConstant(new CSPinteger(0));*/
		
		ieqls = new IneqLinear(IneqOperator.GE);
		ieqls.put(new CSPinteger(-1), depthD);
		ieqls.put(new CSPinteger(-1), depthG);
		ieqls.put(new CSPinteger(1), size);
		ieqls.put(new CSPinteger(-1), me);
		ieqls.setConstant(new CSPinteger(0));
		
		random = new Random();
		
		value = null;
	}
	
	public GType getType() {
		return type;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" - { "+me+" ; "+depth+" ; "+size);
		if(left != null) {
			sb.append(" ; ");
			sb.append(left.toString());
		}
		if(right != null) {
			sb.append(" ; ");
			sb.append(right.toString());
		}
		sb.append(" }");
		return sb.toString();
	}

	protected void creatChilds(boolean f) throws TamagoCSPException {
		if(left == null)
			left = new BTreeNode(name+".fg", sizeG, depthG, type, b);
		if(right == null)
			right = new BTreeNode(name+".fd", sizeD, depthD, type, b);
	
		XGeqY c5 = new XGeqY(size, left.size);
		XGeqY c6 = new XGeqY(size, right.size);
		
		XGeqY c7 = new XGeqY(depth, left.depth);
		XGeqY c8 = new XGeqY(depth, right.depth);
		
		if(f) {		
			/*c1.filter();
			c2.filter();
			c3.filter();
			c4.filter();*/
			c5.filter();
			c6.filter();
			c7.filter();
			c8.filter();
		}		
	}
	

	/**
	 * @see tamago.csp.generic.Obvar#canBeNull()
	 */
	@Override
	public boolean canBeNull() {
		return me.size() == 2 || me.getValue().intValue() == 0;
	}

	/**
	 * @see tamago.csp.generic.Obvar#forceNotNull()
	 */
	@Override
	public void forceNotNull() throws TamagoCCException {
		try {
			me.fix(new CSPinteger(1));
		} catch (TamagoCSPException e) {
			throw new TamagoCCException(e);
		}
	}

	/**
	 * @see tamago.csp.generic.Obvar#forceNull()
	 */
	@Override
	public void forceNull() throws TamagoCCException {
		if(left != null)
			left.forceNull();
		if(right != null)
			right.forceNull();
		try {
			me.fix(new CSPinteger(0));
			size.fix(new CSPinteger(0));
			depth.fix(new CSPinteger(0));
		} catch (TamagoCSPException e) {
			new TamagoCCException(e);
		}
		
	}

	/**
	 * @see tamago.csp.generic.CSPvar#filter()
	 */
	@Override
	public void filter() throws TamagoCSPException {
		Iterable<CSPConstraint> cons = new ArrayList<CSPConstraint>(constraints);
		for (CSPConstraint constraint : cons) {
			constraint.filter();
		}
		me.filter();
		size.filter();
		sizeG.filter();
		sizeD.filter();
		depth.filter();
		depthG.filter();
		depthD.filter();
		if(left != null)
			left.filter();
		if(right != null)
			right.filter();
		updated();
	}

	/**
	 * @see tamago.csp.generic.CSPvar#fix(tamago.csp.generic.CSPconst)
	 */
	@Override
	public void fix(CSPconst n) throws TamagoCSPException {
		TamagoCCLogger.println(4,"BTreeNode operator FIX not yet implemented");
		throw new TamagoCSPException("BTreeNode: not yet implemented");
	}

	/**
	 * @see tamago.csp.generic.CSPvar#forward()
	 */
	@Override
	public void forward() throws TamagoCSPException {
		if(!me.isFixed())
			me.forward();
		
		if(!size.isFixed())
			size.forward();

		if(random.nextBoolean()) {
			if(!sizeG.isFixed())
				sizeG.forward();
			if(!sizeD.isFixed())
				sizeD.forward();
		}
		else {
			if(!sizeD.isFixed())
				sizeD.forward();
			if(!sizeG.isFixed())
				sizeG.forward();
		}

		
		if(!depth.isFixed())
			depth.forward();
		
		if(random.nextBoolean()) {		
			if(!depthG.isFixed())
				depthG.forward();
			if(!depthD.isFixed())
				depthD.forward();
		}
		else {
			if(!depthD.isFixed())
				depthD.forward();
			if(!depthG.isFixed())
				depthG.forward();
		}
		
		if(size.getValue().intValue() > 0) {
			me.fix(new CSPinteger(1));
			left(true);
			right(true);
			if(left != null && !left.isFixed())
				left.forward();
			
			if(right != null && !right.isFixed())
				right.forward();
		}

		if(me.getValue().intValue() == 0)
			value = new BTreeConstant(true, this, null, null);
		else {
			BTreeConstant cleft = null;
			BTreeConstant cright = null;
			if(left != null) {
				cleft = (BTreeConstant) left.getValue();
			}
			if(right != null) {
				cright = (BTreeConstant) right.getValue();
			}
			value = new BTreeConstant(false, this, cleft, cright);
		}
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getAbstractDomain()
	 */
	@Override
	public CSPAbstractDomain getAbstractDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#getValue()
	 */
	@Override
	public CSPconst getValue() {
		return value;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isFixed()
	 */
	@Override
	public boolean isFixed() {
		return (value != null);
	}

	/**
	 * @see tamago.csp.generic.CSPvar#isInDomain(tamago.csp.generic.CSPconst)
	 */
	@Override
	public boolean isInDomain(CSPconst o) {
		return true;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#load(tamago.csp.domain.CSPAbstractDomain)
	 */
	@Override
	public boolean load(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPvar#remove(tamago.csp.generic.CSPconst)
	 */
	@Override
	public void remove(CSPconst value) throws TamagoCSPException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see tamago.csp.generic.CSPvar#retrieve(tamago.csp.Triplet)
	 */
	@Override
	public void retrieve(Triplet triplet) throws TamagoCSPException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBacktrack(Backtracking b) {
		super.setBacktrack(b);
		if(left != null)
			left.setBacktrack(b);
		if(right != null)
			right.setBacktrack(b);
		me.setBacktrack(b);
		depth.setBacktrack(b);
		size.setBacktrack(b);
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		if(left != null) 
			left.setName(name+".fg");
		if(right != null)
			right.setName(name+".fd");
		depth.setName(name+".depth");
		size.setName(name+".nbnodes");
		me.setName(name+"|me");
	}

	public BTreeNode left(boolean f) throws TamagoCSPException {
		if(left == null)
			creatChilds(f);
		return left;
	}
	
	public BTreeNode right(boolean f) throws TamagoCSPException {
		if(right == null)
			creatChilds(f);
		return right;
	}
	
	public Intvar size() {
		return size;
	}
	
	public Intvar depth() {
		return depth;
	}
	
	Intvar depthG() {
		return depthG;
	}
	
	Intvar depthD() {
		return depthD;
	}
	
	Intvar me() {
		return me;
	}
	
	@Override
	public void updateDomain(CSPvar v) throws TamagoCSPException {
		if(this.size().isFixed()) {
			if(this.size().getValue().intValue() > 0)
				this.me().fix(new CSPinteger(1));
			else
				this.me().fix(new CSPinteger(0));
		}
		
		if(this.depth().isFixed()) {
			if(this.depth().getValue().intValue() > 0)
				this.me().fix(new CSPinteger(1));
			else
				this.me().fix(new CSPinteger(0));
		}
		
		btmdc.filter();
	}

	Intvar sizeG() {
		return sizeG;
	}
	Intvar sizeD() {
		return sizeD;
	}
	
	
	public static void run() {
		TamagoCSP csp = new TamagoCSP();
		
		BTreeNode node = new BTreeNode("root", GIType.generateType("BTree"), csp.getBacktrack());
		csp.addVariable(node);
		
		XEqC size2 = new XEqC(node.size(), new CSPinteger(3));
		csp.addConstraint(size2);
		
		CSPConstraint depth2 = new XEqC(node.depth(), new CSPinteger(3));
		csp.addConstraint(depth2);
		
		CSPConstraint fg = null;
		try {
			fg = new XGtC(node.left(false).depth(), new CSPinteger(0));
		} catch (TamagoCSPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		csp.addConstraint(fg);
		
		System.err.println(csp.toString());
		try {
			csp.solve();
		} catch (TamagoCSPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(csp.toString());
		
	}
	public static void main(String[] args) {
		TamagoCCLogger.setLevel(6);
		for(int i = 0; i < 20; i++) {
			run();
		}
	}
}
