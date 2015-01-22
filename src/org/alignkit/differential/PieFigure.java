/**
 * 
 */
package org.alignkit.differential;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeremy Gwinnup
 *
 */
public class PieFigure extends RectangleFigure {
	
	int count;
	int mask;
	int step; //for arc business
	
	//static color objects?
	static Color[] palette = {
		ColorConstants.red,
		ColorConstants.orange,
		ColorConstants.yellow,
		ColorConstants.green,
		ColorConstants.blue,
		new Color(Display.getCurrent(), 128, 0, 128),
		ColorConstants.cyan,
		ColorConstants.gray
	};
	
	public PieFigure(int count, int mask){
	
		super();
		
		this.count = count;
		this.mask = mask;
		this.step = 360/count; //int or float?
		this.setSize(30, 30);
		//this.setBackgroundColor(ColorConstants.gray);
	}
	

	protected void fillShape(Graphics graphics) {
		
	}
	
	protected void outlineShape(Graphics graphics) {
		
		Rectangle bounds= getBounds().getCopy();
		//graphics.setLineWidth(5);
		//Rectangle rect = Rectangle.SINGLETON.setBounds(getBounds());
		//Rectangle rect = this.getClientArea();
		//adjust...
		bounds.height--;
		bounds.width--;
		
		//centers
		int cx = 15;//rect.x + 15;
		int cy = 15;//rect.y + 15;
		
		//graphics.drawOval(rect);
		graphics.setForegroundColor(ColorConstants.black);
		
		//iterate over arcs
		for(int i=0; i < count; i++){
		//	//graphics.drawArc(rect, 0, 0);
			if((mask & (int) Math.pow(2, i)) == (int) Math.pow(2, i) ) { //duh
				//graphics.setForegroundColor(ColorConstants.black);//palette[i]);
				graphics.setBackgroundColor(palette[i]);
				graphics.fillArc(bounds.x,bounds.y,bounds.width ,bounds.height, i*step, step);
		
			}
		}
		graphics.drawOval(bounds);
		
	}
	
}
