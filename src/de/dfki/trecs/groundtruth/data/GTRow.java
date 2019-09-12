package de.dfki.trecs.groundtruth.data;

import java.awt.Color;

import de.dfki.tablerecognizer.block.BoundingBox;

/**
 * This class represents the GTRow element. It is a bounding box with the
 * restriction that y0=y1.
 * 
 * @author Shahab
 *
 */
public class GTRow extends GTElement implements Comparable {

	static {
		THICKNESS = 20;
	}
	
	private GTTable table;

	public GTRow(int x0, int y0, int x1) {
		setX0(x0);
		setX1(x1);
		setY0(y0);
		setY1(y0);
	}
	
	public GTRow() {
	}

	@Override
	public Color getForegroundColor() {
		return Color.green;
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
