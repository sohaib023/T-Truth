/**
 * 
 */
package de.dfki.trecs.groundtruth.data;

import static java.awt.Color.green;

import java.awt.Color;

import de.dfki.tablerecognizer.block.BoundingBox;

/**
 * This class represents the GTRow element. It is a bounding box with the
 * restriction that y0=y1.
 * 
 * @author Shahab
 *
 */
public class GTRow extends BoundingBox implements GTElement, Comparable {

	public GTRow() {
		super();
	}

	@Override
	public void initializePosition(int x, int y) {
		// TODO Auto-generated method stub
		setX0(x);
		setY0(y);
		setY1(y);
	}

	private GTTable table;

	@Override
	public void updatePosition(int x, int y) {
		// TODO Auto-generated method stub
		setX1(x);
	}

	public GTRow(int x0, int y0, int x1) {
		initializePosition(x0, y0);
		updatePosition(x1, y0);
	}

	@Override
	public Color getForegroundColor() {
		// TODO Auto-generated method stub
		return Color.blue;
	}

	@Override
	public int compareTo(Object o) {

		if (o instanceof GTRow) {
			GTRow other = (GTRow) o;
			if (getY0() < other.getY0())
				return -1;
			else if (getY0() == other.getY0())
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
