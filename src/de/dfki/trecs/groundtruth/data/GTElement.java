package de.dfki.trecs.groundtruth.data;

import java.awt.Color;
import de.dfki.tablerecognizer.block.BoundingBox;

public abstract class GTElement extends BoundingBox {

	/**
	 * All the ground truth datatypes should implement this function which will
	 * update the x1,y1 co-ordinates for the given type of ground truth component.
	 * 
	 * @param x
	 * @param y
	 */
	public static int THICKNESS = 0;
	
	public GTElement() {}
	
	public GTElement(int x0, int y0, int x1, int y1) {
		super(x0, y0, x1, y1);
	}
	
//	public void updatePosition(int x, int y);

//	public void initializePosition(int x, int y);

	public abstract Color getForegroundColor();

	public boolean contains(int x, int y) {
		if(x > this.getX0() - THICKNESS/2 && x < this.getX1() + THICKNESS/2)
			if(y > this.getY0() - THICKNESS/2 && y < this.getY1() + THICKNESS/2)
				return true;
		return false;
	}
}
