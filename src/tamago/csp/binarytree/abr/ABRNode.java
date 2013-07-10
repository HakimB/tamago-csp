/**
 * 
 */
package tamago.csp.binarytree.abr;

import tamago.csp.Backtracking;
import tamago.csp.TamagoCSP;
import tamago.csp.binarytree.BTreeNode;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constraint.XEqC;
import tamago.csp.constraint.XGtC;
import tamago.csp.constraint.XLeqY;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.FDvar;
import tamago.csp.var.Intvar;
import tamagocc.generic.api.GType;
import tamagocc.generic.impl.GIType;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
enum ABRNodeChild {
	LEFT,
	RIGHT
}

public class ABRNode extends BTreeNode {

	private Intvar key;
	private Intvar minkey;
	private Intvar maxkey;
	
	
	/**
	 * @param name
	 * @param type
	 * @param b
	 */
	public ABRNode(String name, GType type, Backtracking b) {
		super(name, type, b);
		key = new Intvar(name+"|key", b);
		minkey = null;
		maxkey = null;
	}

	/**
	 * @param name
	 * @param psize
	 * @param pdepth
	 * @param type
	 * @param b
	 */
	public ABRNode(String name, Intvar min, Intvar max, ABRNode parent, ABRNodeChild child, Backtracking b) {
		super(name, (child==ABRNodeChild.LEFT)? parent.sizeG : parent.sizeD,
				(child== ABRNodeChild.LEFT)? parent.depthG : parent.depthD, 
				parent.getType(), b);
		key = new Intvar(name+"|key", b);
		if(min != null)
			new XLeqY(min, key);
		if(max != null)
			new XLeqY(key, max);
		minkey = min;
		maxkey = max;
	}
	
	public FDvar getKey() {
		return key;
	}

	@Override
	public void filter() throws TamagoCSPException {
		key.filter();
		super.filter();
	}
	
	@Override
	protected void creatChilds(boolean f) throws TamagoCSPException {
		left = new ABRNode(name+".fg", minkey, key, this, ABRNodeChild.LEFT , b);
		right = new ABRNode(name+".fd", key, maxkey, this, ABRNodeChild.RIGHT, b);
		super.creatChilds(f);
		if(f) {
			key.filter();
			((ABRNode)left).getKey().filter();
			((ABRNode)right).getKey().filter();
		}
		/*XLeqY linfr = new XLeqY(((ABRNode)left).getKey(), getKey());
		XLeqY rinfr = new XLeqY(getKey(), ((ABRNode)right).getKey());
		
		if(f) {
			linfr.filter();
			rinfr.filter();
		}*/
	}
	
	@Override
	public void forward() throws TamagoCSPException {
		key.forward();
		if(left != null)
			((ABRNode)left).getKey().forward();
		if(right != null)
			((ABRNode)right).getKey().forward();
		super.forward();
		this.value = new ABRConstant(this, this.value);
	}
	
	public static void run() {
		TamagoCSP csp = new TamagoCSP();
		
		ABRNode node = new ABRNode("root", GIType.generateType("Abr"), csp.getBacktrack());
		csp.addVariable(node);
		
		XEqC size2 = new XEqC(node.size(), new CSPinteger(3));
		csp.addConstraint(size2);
		
		CSPConstraint depth2 = new XEqC(node.depth(), new CSPinteger(3));
		csp.addConstraint(depth2);
		
		CSPConstraint fg = null;
		try {
			fg = new XGtC(node.left(false).depth(), new CSPinteger(0));
		} catch (TamagoCSPException e1) {
			e1.printStackTrace();
		}
		csp.addConstraint(fg);
		
		System.err.println(csp.toString());
		try {
			csp.solve();
		} catch (TamagoCSPException e) {
			e.printStackTrace();
		}
		System.err.println(csp.toString());
		
	}
	
	public static void main(String[] args) {
		TamagoCCLogger.setLevel(6);
		for(int i = 0; i < 5; i++) {
			run();
		}
	}
}
