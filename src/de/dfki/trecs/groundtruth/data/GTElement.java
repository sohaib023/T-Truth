package de.dfki.trecs.groundtruth.data;

import java.awt.Color;

public interface GTElement {

	/**
	 * All the ground truth datatypes should implement this function which will
	 * update the x1,y1 co-ordinates for the given type of ground truth component.
	 * 
	 * @param x
	 * @param y
	 */
	public void updatePosition(int x, int y);

	public void initializePosition(int x, int y);

	public Color getForegroundColor();
}
