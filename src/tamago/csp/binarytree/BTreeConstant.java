/**
 * 
 */
package tamago.csp.binarytree;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import tamago.csp.generic.CSPconst;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AIIdent;
import tamagocc.ast.impl.AIInitialisation;
import tamagocc.ast.impl.AINew;
import tamagocc.ast.impl.AINil;
import tamagocc.ast.impl.AINoInstruction;
import tamagocc.ast.impl.AISequence;
import tamagocc.ast.impl.AIType;
import tamagocc.ast.impl.AIVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoFreshVar;

/**
 * @author Hakim Belhaouari
 *
 */
public class BTreeConstant implements CSPconst {

	protected BTreeConstant left;
	protected BTreeConstant right;
	protected String nameleft;
	protected String nameright;
	protected BTreeConstant parent;
	protected boolean isnull;
	protected BTreeNode owner;
	protected JFrame frame;
	protected String fenetreName;
	
	/**
	 * 
	 */
	public BTreeConstant(boolean isnull, BTreeNode owner,BTreeConstant left, BTreeConstant right) {
		parent = null;
		this.owner = owner;
		this.left = left;
		this.right = right;
		if(left != null)
			this.left.setParent(this);
		if(right != null)
			this.right.setParent(this);
		this.isnull = isnull;
		nameleft = TamagoFreshVar.Default.getName("__tree_left");
		nameright = TamagoFreshVar.Default.getName("__tree_right");
	}
	
	protected BTreeConstant(BTreeNode owner, BTreeConstant b) {
		this.parent = b.parent;
		this.owner = owner;
		this.left = b.left;
		this.right = b.right;
		this.nameleft = b.nameleft;
		this.nameright = b.nameright;
		this.isnull = b.isnull;
	}

	protected void setParent(BTreeConstant bTreeConstant) {
		this.parent = bTreeConstant;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#boolValue()
	 */
	@Override
	public boolean boolValue() {
		return false;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#intValue()
	 */
	@Override
	public int intValue() {
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#objectValue()
	 */
	@Override
	public Object objectValue() {
		return this;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#realValue()
	 */
	@Override
	public double realValue() {
		return 0;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#stringValue()
	 */
	@Override
	public String stringValue() {
		return null;
	}

	/**
	 * @see tamago.csp.generic.CSPconst#toAExpression()
	 */
	@Override
	public AExpression toAExpression() {
		if(isnull)
			return  new AINil();
		else {
			AINew init = new AINew(AIType.generateType(owner.getType().getType()));
			if(left != null)
				init.addArguments(new AIVariable(new AIIdent(nameleft)));
			else
				init.addArguments(new AINil());
			if(right != null)
				init.addArguments(new AIVariable(new AIIdent(nameright)));
			else
				init.addArguments(new AINil());
			return init;
		}
	}

	/**
	 * @see tamago.csp.generic.CSPconst#toPostExpression()
	 */
	@Override
	public AInstruction toPostExpression() {
		return new AINoInstruction();
	}

	/**
	 * @see tamago.csp.generic.CSPconst#toPreExpression()
	 */
	@Override
	public AInstruction toPreExpression() {
		AISequence seq = new AISequence();
		if(left != null) {
			seq.addInstruction(left.toPreExpression());
			seq.addInstruction(new  AIInitialisation(new AIIdent(nameleft), AIType.generateType(owner.getType().getType()), left.toAExpression()));
		}
		if(right != null) {
			seq.addInstruction(right.toPreExpression());
			seq.addInstruction(new  AIInitialisation(new AIIdent(nameright), AIType.generateType(owner.getType().getType()), right.toAExpression()));
		}
		
		return seq;
	}

	public String toString() {
		if(TamagoCCLogger.getLevel() <= 4)
			return owner.toString();
		else {
			if(frame == null) {
				fenetreName = TamagoFreshVar.Default.getName("AB_fen");
				frame = new JFrame(fenetreName);
				BTreeDrawNodePanel panel = new BTreeDrawNodePanel();
				JScrollPane view = new JScrollPane(panel); 

				frame.getContentPane().add(view);

				int depth = owner.depth().getValue().intValue();
				int maxnodes = (int)Math.pow(2, depth) - 1; 
				int leafs = (int)Math.pow(2,depth);

				int width = 200 + leafs*0;
				int height = 60 + depth*100;
				panel.setSize( width, height);
				panel.setPreferredSize(new Dimension(width, height));
				frame.setSize(new Dimension(1024, 768));

				drawNode(panel, "root", width/2, 30,width,100);

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
			frame.setVisible(true);
			
			return fenetreName;
		}
	}

	public void drawNode(BTreeDrawNodePanel panel, String name, int x, int y, int width, int height) {
		BTreeDrawNode bt = new BTreeDrawNode(name, x-10, y-10, 20, 20,
								x - (width/4), y + height,
								x + (width/4), y + height,   this);
		panel.add(bt);
		if(left != null) {
			left.drawNode(panel, nameleft, x - (width/4), y + height,width/2,height);
			
		}
		
		if(right != null) {
			right.drawNode(panel, nameright,x + (width/4), y + height,width/2,height);
		}
	}

	public boolean isNull() {
		return isnull;
	}

	public boolean hasLeft() {
		return left != null;
	}
	
	public boolean hasRight() {
		return right != null;
	}

	public BTreeNode owner() {
		return owner;
	}
}
