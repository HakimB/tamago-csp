/**
 * 
 */
package tamago.csp.binarytree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComponent;


public class BTreeDrawNodePanel extends JComponent {

	private ArrayList<BTreeDrawNode> nodes;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7860073289065041692L;
	
	public BTreeDrawNodePanel() {
		nodes = new ArrayList<BTreeDrawNode>();
	}
	
	public void add(BTreeDrawNode node) {
		nodes.add(node);
	}
	
	@Override
	public void paint(Graphics g) {
		for (BTreeDrawNode node : nodes) {
			node.paint(g);
		}
	}
	
}


