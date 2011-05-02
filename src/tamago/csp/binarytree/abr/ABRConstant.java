/**
 * 
 */
package tamago.csp.binarytree.abr;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import tamago.csp.binarytree.BTreeConstant;
import tamago.csp.binarytree.BTreeDrawNode;
import tamago.csp.binarytree.BTreeDrawNodePanel;
import tamagocc.ast.api.AExpression;
import tamagocc.ast.api.AInstruction;
import tamagocc.ast.impl.AIIdent;
import tamagocc.ast.impl.AIInitialisation;
import tamagocc.ast.impl.AINew;
import tamagocc.ast.impl.AINil;
import tamagocc.ast.impl.AISequence;
import tamagocc.ast.impl.AIType;
import tamagocc.ast.impl.AIVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoFreshVar;

/**
 * @author Hakim Belhaouari
 *
 */
public class ABRConstant extends BTreeConstant {

	protected ABRNode node;
	
	/**
	 * @param owner
	 * @param b
	 */
	public ABRConstant(ABRNode owner, BTreeConstant b) {
		super(owner, b);
		this.node = owner;
	}

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
	
	@Override
	public AExpression toAExpression() {
		if(isnull)
			return  new AINil();
		else {
			AINew init = new AINew(AIType.generateType(owner.getType().getType()));
			init.addArguments(this.node.getKey().getValue().toAExpression());
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

				int width = 500 + leafs*0;
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
		ABRDrawNode bt = new ABRDrawNode(name, x-10, y-10, 20, 20,
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
}
