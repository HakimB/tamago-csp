/**
 * 
 */
package tamago.csp.binarytree;

import java.util.ArrayList;

import tamago.csp.constant.CSPinteger;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;
import tamago.csp.var.Intvar;

/**
 * @author Hakim Belhaouari
 *
 */
public class BTreeMaxDepthConstraint implements CSPConstraint {

	private BTreeNode node;
	private ArrayList<CSPvar> var;
	private boolean filtering;
	
	/**
	 * 
	 */
	public BTreeMaxDepthConstraint(BTreeNode node) {
		this.node = node;
		filtering = false;
		node.install(this);
		node.depth().install(this);
		node.depthG().install(this);
		node.depthD().install(this);
		node.size().install(this);
		node.sizeG().install(this);
		node.sizeD().install(this);
		node.me().install(this);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	@Override
	public void filter() throws TamagoCSPException {
		if(filtering)
			return;
		try {
			filtering = true;
			
			// On vérifie si 'me' en coherent avec la taille et/ou depth
			if(node.size().getMin().intValue() > 0)
				node.me().fix(new CSPinteger(1));
			else if(node.size().isFixed()) {
				node.me().fix(new CSPinteger(0));
			}
			
			if(node.depth().getMin().intValue() > 0)
				node.me().fix(new CSPinteger(1));
			else if(node.depth().isFixed()) {
					node.me().fix(new CSPinteger(0));
			}
			
			// Si on est un noeud concret on verie les bornes 
			// Sinon on fixe les valeurs de coherences (zero)
			if(node.me().isInDomain(new CSPinteger(1))) {
				int max = node.depth().getMax().intValue() - 1;
				node.depthG().setMax(new CSPinteger(max));
				node.depthD().setMax(new CSPinteger(max));

				int maxG = node.depthG().getMax().intValue() + 1;
				int maxD = node.depthD().getMax().intValue() + 1;
				node.depth().setMax(new CSPinteger(Math.max(maxG, maxD)));
			}
			else {
				node.depth().fix(new CSPinteger(0));
				node.depthG().fix(new CSPinteger(0));
				node.depthD().fix(new CSPinteger(0));
				node.size().fix(new CSPinteger(0));
			}
			
			// on contraint lorsque on trouve des valeurs fixees
			// utile pour remplacer la fonction max pour la hauteur
			if(node.depth().isFixed() && node.depth().getValue().intValue() > 0) {
				if(node.random.nextBoolean()) {
					if(node.depthG().isFixed() && 
							(node.depthG().getValue().intValue() + 1 != node.depth().getValue().intValue()))
						node.depthD().fix(new CSPinteger(node.depth().getValue().intValue() - 1));

					if(node.depthD().isFixed() && 
							(node.depthD().getValue().intValue() + 1 != node.depth().getValue().intValue()))
						node.depthG().fix(new CSPinteger(node.depth().getValue().intValue() - 1));
				}
				else {
					if(node.depthD().isFixed() && 
							(node.depthD().getValue().intValue() + 1 != node.depth().getValue().intValue()))
						node.depthG().fix(new CSPinteger(node.depth().getValue().intValue() - 1));			
					if(node.depthG().isFixed() && 
							(node.depthG().getValue().intValue() + 1 != node.depth().getValue().intValue()))
						node.depthD().fix(new CSPinteger(node.depth().getValue().intValue() - 1));
				}
			}
			
			// Verification que la profondeur est coherente avec la longueur
			// depth <= size <= 2^depth - 1 (problème d'arrondit)
			/*if(node.depth().isFixed()) {
				node.size().setMin(node.depth().getMin());
				int maxsize = (int) ((long)Math.pow(2, node.depth().getValue().intValue()) - 1L);
				node.size().setMax(new CSPinteger(maxsize));
			}
			
			if(node.size().isFixed()) {
				node.depth().setMax(node.size().getMax());
				int maxdepth = (int) ((long) Math.round(Math.log(node.size().getValue().intValue() + 1) / Math.log(2)));
				node.depth().setMin(new CSPinteger(maxdepth));
			}*/
			
			tryFix(node.depth(), node.size());
			
			// G
			if(node.random.nextBoolean()) {
				tryFix(node.depthG(), node.sizeG());
				tryFix(node.depthD(), node.sizeD());
			}
			else {
				tryFix(node.depthD(), node.sizeD());
				tryFix(node.depthG(), node.sizeG());
			}
		}
		finally {
			filtering = false;
		}
		
		

	}
	
	private void tryFix(Intvar depth, Intvar size) throws TamagoCSPException {
		if(depth.isFixed()) {
			size.setMin(depth.getMin());
			int maxsize = (int) ((long)Math.pow(2, depth.getValue().intValue()) - 1L);
			size.setMax(new CSPinteger(maxsize));
		}
		
		if(size.isFixed()) {
			depth.setMax(size.getMax());
			int maxdepth = (int) ((long) Math.round(Math.log(size.getValue().intValue() + 1) / Math.log(2)));
			depth.setMin(new CSPinteger(maxdepth));
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	@Override
	public Iterable<CSPvar> getVariables() {
		if(var == null)
			rawVars();
		return var;
	}

	
	
	
	private void rawVars() {
		var = new ArrayList<CSPvar>(1);
		var.add(node);
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	@Override
	public int size() {
		return 1;
	}

}
