package tamago.csp.binarytree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * @author Hakim Belhaouari
 *
 */
public class BTreeDrawNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4556597117536042689L;
	protected BTreeConstant constant;
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	protected int lx;
	protected int ly;
	protected int rx;
	protected int ry;
	protected String name;
	
	/**
	 * 
	 */
	public BTreeDrawNode(String name,int x, int y, int w, int h, int lx, int ly, int rx, int ry, BTreeConstant constant) {
		this.constant = constant;
		this.name = name;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.lx = lx;
		this.ly = ly;
		this.rx = rx;
		this.ry = ry;
	}

	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		if(constant.hasLeft()) {
			g.drawLine(x, y+h, lx, ly);
		}
		
		if(constant.hasRight()) {
			g.drawLine(x+w, y+h, rx, ry);
		}
		
		g.setColor(Color.CYAN);
		g.fillRect(x, y, w, h);
		g.setColor(Color.BLACK);
		g.setFont( new Font("Helvetica", Font.BOLD, 14)  );
		g.drawString(name, x, y);
		if(constant.isNull()) {
			g.setColor(Color.PINK);
			g.drawString("D:"+constant.owner().depth().getValue().intValue(), x, y+ h/2);
			g.drawString("S:"+constant.owner().size().getValue().intValue(), x, y+ h);
			g.drawString("SG:"+constant.owner().sizeG().getValue().intValue(), x, (int) (y+ 1.5*h));
			g.drawString("SD:"+constant.owner().sizeD().getValue().intValue(), x, y+ 2*h);
			g.drawString("DG:"+constant.owner().depthG().getValue().intValue(), x, (int) (y+ 2.5*h));
			g.drawString("DD:"+constant.owner().depthD().getValue().intValue(), x, y+ 3*h);
		}
		else {
			g.setColor(Color.RED);
			g.drawString("D:"+constant.owner().depth().getValue().intValue(), x, y+ h/2);
			g.drawString("S:"+constant.owner().size().getValue().intValue(), x, y+ h);
			g.drawString("SG:"+constant.owner().sizeG().getValue().intValue(), x, (int) (y+ 1.5*h));
			g.drawString("SD:"+constant.owner().sizeD().getValue().intValue(), x, y+ 2*h);
			g.drawString("DG:"+constant.owner().depthG().getValue().intValue(), x, (int) (y+ 2.5*h));
			g.drawString("DD:"+constant.owner().depthD().getValue().intValue(), x, y+ 3*h);
		}
		
	}
}