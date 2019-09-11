package de.dfki.trecs.groundtruth.data;

import java.awt.Color;

import de.dfki.tablerecognizer.block.BoundingBox;

/**
 * This class represent a ground truth column. This is an extension to the
 * BoundingBox class with the restriction that x0=x1 i.e. it represents a
 * vertical line.
 * 
 * @author Shahab
 *
 */

public class GTCol extends BoundingBox implements GTElement, Comparable {

	private GTTable table;

	public GTCol() {
		super();
	}

	public GTCol(int x, int y0, int y1) {
		setX0(x);
		setX1(x);
		setY0(y0);
		setY1(y1);
	}

	@Override
	public Color getForegroundColor() {
		// TODO Auto-generated method stub
		return Color.red;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof GTCol) {
			GTCol other = (GTCol) o;
			if (getX0() < other.getX0())
				return -1;
			else if (getX0() == other.getX0())
				return 0;
			else
				return +1;
		} else
			return super.compare(this, (BoundingBox) o);
	}

	/**
	 * @return the table
	 */
	public GTTable getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(GTTable table) {
		this.table = table;
	}
}
