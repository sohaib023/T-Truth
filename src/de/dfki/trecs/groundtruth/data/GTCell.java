/**
 * 
 */
package de.dfki.trecs.groundtruth.data;

import java.awt.Color;

import de.dfki.tablerecognizer.block.BoundingBox;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;

/**
 * 
 * This class represents a Ground Truth Cell element. Right now there is little
 * knowledge as to how this will be used. However it's added for completeness.
 * 
 * @author Shahab
 *
 */
public class GTCell extends BoundingBox implements GTElement, Comparable {

	private ColorModel16Bit color = null;
	private Color foregroundColor;

	private int startRow = -1;
	private int endRow = -1;

	private int startCol = -1;
	private int endCol = -1;

	private int tableIndex = -1;
	private boolean dontCare = false;

	@Override
	public int compareTo(Object o) {
		GTCell cell = (GTCell) o;
		if (getY0() == cell.getY0()) {
			if (getX0() < cell.getX0())
				return -1;
			else if (getX0() == cell.getX0())
				return 0;
			else
				return 1;
		} else if (getY0() < cell.getY0()) {
			return -1;
		}

		else if (getX0() < cell.getX0())
			return -1;
		else
			return 1;

	}

	public GTCell() {

	}

	@Override
	public Color getForegroundColor() {
		// TODO Auto-generated method stub
		return Color.black;
	}

	public void setForegroundColor(Color color) {
		foregroundColor = color;
	}

	public GTCell(int x0, int y0, int x1, int y1, int startRow, int startCol, int tableIndex) {
		this(x0, y0, x1, y1);
		setStartRow(startRow);
		setStartCol(startCol);
		setEndRow(startRow);
		setEndCol(startCol);
		setTableIndex(tableIndex);

		assumeColor();

	}

	public void assumeColor() {
		ColorModel16Bit color = new ColorModel16Bit(tableIndex + 1, startRow + 1, endRow + 1, startCol + 1, endCol + 1);
		setColor(color);
	}

	public GTCell(int x0, int y0, int x1, int y1, ColorModel16Bit color) {
		this(x0, y0, x1, y1);
		setColor(color);
	}

	/*
	 * Construct that creates a cell with null color.
	 */
	public GTCell(int x0, int y0, int x1, int y1) {
		super(x0, y0, x1, y1);
	}

	@Override
	public void initializePosition(int x, int y) {
		setX0(x);
		setY0(y);

	}

	@Override
	public void updatePosition(int x, int y) {
		// TODO Auto-generated method stub
		setX1(x);
		setY1(y);
	}

	/**
	 * @return the color
	 */
	public ColorModel16Bit getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(ColorModel16Bit color) {
		this.color = color;
	}

	/**
	 * @return the startRow
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the endRow
	 */
	public int getEndRow() {
		return endRow;
	}

	/**
	 * @param endRow the endRow to set
	 */
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	/**
	 * @return the startCol
	 */
	public int getStartCol() {
		return startCol;
	}

	/**
	 * @param startCol the startCol to set
	 */
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	/**
	 * @return the endCol
	 */
	public int getEndCol() {
		return endCol;
	}

	/**
	 * @param endCol the endCol to set
	 */
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	public void assumeDontCare() {
		if (startRow == -1 || startCol == -1)
			setDontCare(true);
		else
			setDontCare(false);
	}

	/**
	 * @return the dontCare
	 */
	public boolean isDontCare() {
		return dontCare;
	}

	/**
	 * @param dontCare the dontCare to set
	 */
	public void setDontCare(boolean dontCare) {
		this.dontCare = dontCare;
	}

	/**
	 * @return the tableIndex
	 */
	public int getTableIndex() {
		return tableIndex;
	}

	/**
	 * @param tableIndex the tableIndex to set
	 */
	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}

}
