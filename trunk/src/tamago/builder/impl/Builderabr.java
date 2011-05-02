/**
 * 
 */
package tamago.builder.impl;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.Backtracking;
import tamago.csp.binarytree.abr.ABRNode;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoBuilderException;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPvar;
import tamagocc.generic.api.GCall;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GInLabel;
import tamagocc.generic.api.GType;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIType;

/**
 * @author Hakim Belhaouari
 *
 */
public class Builderabr extends TamagoBuilder {

	private ABRNode root;
	
	/**
	 * @param env
	 * @param name
	 * @param type
	 * @param b
	 */
	public Builderabr(TamagoEnvironment env, String name, GType type,
			Backtracking b) {
		super(env, name, type, b);
		root = new ABRNode(name, type, b);
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getCSPvar(tamago.csp.convert.TamagoCSPInferConstraint, tamagocc.generic.api.GExpression)
	 */
	@Override
	public CSPvar getCSPvar(TamagoCSPInferConstraint owner, GExpression e)
			throws TamagoBuilderException {
		if(e == null)
			return root;
		switch(e.getCategory()) {
		case VARIABLE:
		case READ:
		case RETURN:
			return root;
		case INLABEL:
			ABRNode node = root;
			GExpression sub = ((GInLabel)e).getSubExpression();
			while(sub.getCategory() == GExprType.INLABEL) {
				GExpression newscope = ((GInLabel)sub).getTarget();
				if(newscope.getCategory() != GExprType.CALL)
					break;
				GCall call = (GCall) newscope;
				if(call.getName().equals("left"))
					try {
						node = (ABRNode) node.left(false);
					} catch (TamagoCSPException e1) {
						e1.printStackTrace();
					}
				else if(call.getName().equals("right"))
					try {
						node = (ABRNode) node.right(false);
					} catch (TamagoCSPException e1) {
						e1.printStackTrace();
					}
				else
					break;
				sub = ((GInLabel)e).getSubExpression();
			}
			switch(sub.getCategory()) {
			case CALL:
				GCall call = (GCall) sub;
				if(call.getName().equals("left"))
					try {
						return node.left(false);
					} catch (TamagoCSPException e2) {
						e2.printStackTrace();
					}
				else if(call.getName().equals("right"))
					try {
						return node.right(false);
					} catch (TamagoCSPException e1) {
						e1.printStackTrace();
					}
				else if(call.getName().equals("size"))
					return node.size();
				else if(call.getName().equals("depth"))
					return node.depth();
				else if(call.getName().equals("key"))
					return node.getKey();
			case INLABEL:
				
			}
			
		}
		throw new TamagoBuilderException("unsupported element");
	}

	/**
	 * @see tamago.builder.TamagoBuilder#getType()
	 */
	@Override
	public GType getType() {
		return GIType.generateType("BTree");
	}

	/**
	 * @see tamago.builder.TamagoBuilder#instantiate()
	 */
	@Override
	public Object instantiate() throws TamagoBuilderException {
		throw new TamagoBuilderException("not yet implemented");
	}

	/**
	 * @see tamago.builder.TamagoBuilder#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		root.setName(name);
	}

}
