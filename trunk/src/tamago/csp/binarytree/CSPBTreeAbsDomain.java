/**
 * 
 */
package tamago.csp.binarytree;

import tamago.csp.domain.CSPAbstractDomain;
import tamago.csp.domain.IntDomain;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPconst;

/**
 * @author Hakim Belhaouari
 *
 */
public class CSPBTreeAbsDomain implements CSPAbstractDomain {

	protected IntDomain depth;
	protected IntDomain depthG;
	protected IntDomain depthD;
	protected IntDomain size;
	protected IntDomain sizeG;
	protected IntDomain sizeD;
	protected IntDomain me;
	protected CSPBTreeAbsDomain left;
	protected CSPBTreeAbsDomain right;
	
	
	/**
	 * 
	 */
	public CSPBTreeAbsDomain(BTreeNode node) {
		depth = (IntDomain) node.depth().getAbstractDomain();
		depthG = (IntDomain) node.depthG().getAbstractDomain();
		depthD = (IntDomain) node.depthD().getAbstractDomain();
		
		size = (IntDomain) node.size().getAbstractDomain();
		sizeG = (IntDomain) node.sizeG().getAbstractDomain();
		sizeD = (IntDomain) node.sizeD().getAbstractDomain();
		
		me = (IntDomain) node.me().getAbstractDomain();
		if(node.left != null)
			left = (CSPBTreeAbsDomain) node.left.getAbstractDomain();
		
		if(node.right != null)
			right = (CSPBTreeAbsDomain) node.right.getAbstractDomain();
		
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#intersect(tamago.csp.domain.CSPAbstractDomain)
	 */
	@Override
	public CSPAbstractDomain intersect(CSPAbstractDomain domain)
			throws TamagoCSPException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#isInDomain(tamago.csp.generic.CSPconst)
	 */
	@Override
	public boolean isInDomain(CSPconst o) {
		BTreeConstant cons = (BTreeConstant)o;
		return false;
	}

	/**
	 * @see tamago.csp.domain.CSPAbstractDomain#sameDomain(tamago.csp.domain.CSPAbstractDomain)
	 */
	@Override
	public boolean sameDomain(CSPAbstractDomain domain) {
		// TODO Auto-generated method stub
		return false;
	}

}
