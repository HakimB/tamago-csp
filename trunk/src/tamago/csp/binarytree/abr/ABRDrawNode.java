/**
 * 
 */
package tamago.csp.binarytree.abr;

import java.awt.Color;
import java.awt.Graphics;

import tamago.csp.binarytree.BTreeDrawNode;

/**
 * @author Hakim Belhaouari
 *
 */
public class ABRDrawNode extends BTreeDrawNode {

	
	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param lx
	 * @param ly
	 * @param rx
	 * @param ry
	 * @param constant
	 */
	public ABRDrawNode(String name, int x, int y, int w, int h, int lx, int ly,
			int rx, int ry, ABRConstant constant) {
		super(name, x, y, w, h, lx, ly, rx, ry, constant);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(constant.isNull())
			g.setColor(Color.PINK);
		else
			g.setColor(Color.RED);
		g.drawString("K:"+((ABRNode)constant.owner()).getKey().getValue().intValue(), x, y+ (int)(3.5 * h));
	}

}
