/**
 * 
 */
package de.dfki.trecs.groundtruth.data;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import de.dfki.tablerecognizer.block.BoundingBox;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;

/**
 * @author Shahab
 *
 */
public class GTTable extends BoundingBox implements GTElement {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int UNKNOWN = 2;
	
	public static final int CORNER_TOP_LEFT = 0;
	
	public static final int CORNER_TOP_RIGHT = 1;
	
	public static final int CORNER_BOTTOM_LEFT = 2;
	
	public static final int CORNER_BOTTOM_RIGHT = 3;
	
	public static final int CORNER_THICKNESS = 30;
	
	public static final int ROW_COL_THICKNESS = 20;
	
	
	private ArrayList<GTRow> gtRows = new ArrayList<GTRow>();
	private ArrayList<GTCol> gtCols = new ArrayList<GTCol>();
	private ArrayList<GTCell> cells = new ArrayList<GTCell>();
	private GTCell gtCells[][] = null;
	private int index;
	private Color foregroundColor = Color.BLUE;
	private int orientation = UNKNOWN;
	private int selectedCorner = -1;
	
	public ArrayList<GTCell> getCells() {
		return cells;
	}

	public GTTable() {

	}

	public GTTable(int x0, int y0, int x1, int y1) {
		super(x0, y0, x1, y1);
	}

	public void addRow(GTRow row) {
		gtRows.add(row);
	}

	public void addCols(GTCol col) {
		gtCols.add(col);
	}

	public void remove(GTRow row) {
		gtRows.remove(row);
	}

	public void remove(GTCol col) {
		gtCols.remove(col);
	}

	public void assignColors() {
		int num_colors = (gtRows.size() + 1) * (gtCols.size() + 1);
		if (gtCells == null)
			return;
		int j = 0, k = 0;
		for (int i = 0; i < 360; i += 360 / num_colors) {

			int hue = i;
			double saturation = 90 + Math.random() * 10;
			double lightness = 50 + Math.random() * 10;

			if (k > gtCols.size()) {
				j++;
				k = 0;
			}
			gtCells[j][k++]
					.setForegroundColor(new Color(Color.HSBtoRGB((float) hue, (float) saturation, (float) lightness)));

		}
	}

	public GTCell getCellAtPoint(Point p) {

		if (gtCells == null) {
			return null;
		} else {
			for (int i = 0; i < gtCells.length; i++) {
				for (int j = 0; j < gtCells[i].length; j++) {
					GTCell cell = gtCells[i][j];
					if (p.x >= cell.getX0() && p.x <= cell.getX1() && p.y >= cell.getY0() && p.y <= cell.getY1())
						return cell;
				}
			}
		}
		return null;
	}

	public void addRowSpan(Point p1, Point p2) {

		GTCell startCell = getCellAtPoint(p1);
		GTCell endCell = getCellAtPoint(p2);
		if (startCell == null || endCell == null || startCell.getStartRow() != endCell.getStartRow()) {
			System.out.println("Cant add Row Span: for " + p1 + " ,and " + p2);
			return;
		}
		startCell.setEndCol(endCell.getEndCol());
		for (int i = startCell.getStartCol() + 1; i <= endCell.getEndCol(); i++) {
			GTCell temp = gtCells[startCell.getStartRow()][i];
//			temp.setStartCol(-1);
//			temp.setEndCol(-1);
			temp.setDontCare(true);
			startCell.setX1(temp.getX1());
			if (temp.getY1() > startCell.getY1())
				startCell.setX1(temp.getY1());

			for (int j = startCell.getStartRow() + 1; j <= startCell.getEndRow(); j++)
				gtCells[j][i].setDontCare(true);

		}
		startCell.assumeColor();
	}

	/**
	 * used by the load ground truth file
	 */
	public void populateCellMatrix() {
		int numRows = gtRows.size() + 1;
		int numCols = gtCols.size() + 1;
		gtCells = new GTCell[numRows][numCols];

		Collections.sort(cells);

		if (cells.size() != numRows * numCols) {
			System.out.println("Arrays don't match for cells");
			return;
		}
		Iterator<GTCell> iterator = cells.iterator();
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numCols; j++) {
				gtCells[i][j] = iterator.next();
			}
	}

	public void addColSpan(Point p1, Point p2) {
		GTCell startCell = getCellAtPoint(p1);
		GTCell endCell = getCellAtPoint(p2);
		if (startCell == null || endCell == null || startCell.getStartCol() != endCell.getStartCol()) {
			System.out.println("Cant add Col Span: for " + p1 + " ,and " + p2);
			return;
		}
		startCell.setEndRow(endCell.getEndRow());
		for (int i = startCell.getStartRow() + 1; i <= endCell.getEndRow(); i++) {
			GTCell temp = gtCells[i][startCell.getStartCol()];
//			temp.setStartRow(-1);
//			temp.setEndRow(-1);
			temp.setDontCare(true);
			startCell.setY1(temp.getY1());
			if (temp.getX1() > startCell.getX1())
				startCell.setX1(temp.getX1());

			/**
			 * set other spaning cells here to dont care
			 */
			for (int j = startCell.getStartCol() + 1; j <= startCell.getEndCol(); j++)
				gtCells[i][j].setDontCare(true);

		}
		startCell.assumeColor();

	}

	/**
	 * does nothing at the moment
	 */
	public void reevaluateCells() {
		for (int i = 0; i < cells.size(); i++) {

		}
	}

	public void evaluateInitialCells() {

		Collections.sort(gtRows);
		Collections.sort(gtCols);
		int l, t, r, b;
		int numRows = gtRows.size() + 1;
		int numCols = gtCols.size() + 1;

		gtCells = new GTCell[numRows][numCols];
		cells.clear();
		int rows = 0;
		int cols = 0;
		l = getX0();
		t = getY0();
		for (int i = 0; i < numRows; i++) {
			if (i < gtRows.size())
				b = gtRows.get(i).getY0();
			else
				b = getY1();
			for (int j = 0; j < numCols; j++) {
				if (j < gtCols.size())
					r = gtCols.get(j).getX0();
				else
					r = getX1();

				GTCell cell = new GTCell(l, t, r, b, i, j, index);

//				ColorModel16Bit color = new ColorModel16Bit((253+index+1),(i+1),(j+1));
//				cell.setColor(color);

				gtCells[i][j] = cell;
				cells.add(cell);

				l = r;
			}
			l = getX0();
			t = b;
		}
	}

	@Override
	public Color getForegroundColor() {
		// TODO Auto-generated method stub
		return foregroundColor;
	}

	/**
	 * @return the gtRows
	 */
	public ArrayList<GTRow> getGtRows() {
		return gtRows;
	}

	/**
	 * @param gtRows the gtRows to set
	 */
	public void setGtRows(ArrayList<GTRow> gtRows) {
		this.gtRows = gtRows;
	}

	/**
	 * @return the gtCols
	 */
	public ArrayList<GTCol> getGtCols() {
		return gtCols;
	}

	/**
	 * @param gtCols the gtCols to set
	 */
	public void setGtCols(ArrayList<GTCol> gtCols) {
		this.gtCols = gtCols;
	}

	public GTCell[][] getGtCells() {
		return gtCells;
	}

	public void setGtCells(GTCell[][] gtCells) {
		this.gtCells = gtCells;
	}

	public int getOrientation() {
		return this.orientation;
	}
	
	public void setOrientation(int orientation) {
		if (orientation == HORIZONTAL)
			this.foregroundColor = Color.RED;
		else if (orientation == VERTICAL)
			this.foregroundColor = Color.GREEN;
		else
			this.foregroundColor = Color.BLUE;
		this.orientation = orientation;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getCorner(int x0, int y0) {
		System.out.println("" + x0 + ", " + y0);
		if(x0 >= getX0() && x0 < getX0() + CORNER_THICKNESS) {
			if(y0 >= getY0() && y0 < getY0() + CORNER_THICKNESS) {
				return CORNER_TOP_LEFT;
			}
			else if(y0 > getY1() - CORNER_THICKNESS && y0 <= getY1()) {
				return CORNER_BOTTOM_LEFT;
			} 
		}
		else if(x0 >= getX1() - CORNER_THICKNESS && x0 < getX1()) {
			System.out.println("x1");
			if(y0 >= getY0() && y0 < getY0() + CORNER_THICKNESS) {
				return CORNER_TOP_RIGHT;
			}
			else if(y0 > getY1() - CORNER_THICKNESS && y0 <= getY1()) {
				return CORNER_BOTTOM_RIGHT;
			} 
		}
		return -1;
	}
	
	public void moveTo(Point p1, Point p2) {
		ArrayList<GTCol> cols = this.getGtCols();
		for(int i=0; i<cols.size(); i++) {
			GTCol col = cols.get(i);
			col.setY0(p1.y);
			col.setY1(p2.y);
			if(col.getX0() < p1.x) {
				col.setX0(p1.x);
				col.setX1(p1.x);
			}
			else if (col.getX1() > p2.x) {
				col.setX0(p2.x);
				col.setX1(p2.x);
			}
		}
		ArrayList<GTRow> rows = this.getGtRows();
		for(int i=0; i<rows.size(); i++) {
			GTRow row = rows.get(i);
			row.setX0(p1.x);
			row.setX1(p2.x);
			if(row.getY0() < p1.y) {
				row.setY0(p1.y);	
				row.setY1(p1.y);
			}
			else if (row.getY1() > p2.y) {
				row.setY0(p2.y);
				row.setY1(p2.y);
			}
		}
		
		this.setX0(p1.x);
		this.setX1(p2.x);
		
		this.setY0(p1.y);
		this.setY1(p2.y);
	}
	
	public GTElement getElementAtPosition(int x, int y) {
		ArrayList<GTCol> cols = this.getGtCols();
		for(int i=0; i<cols.size(); i++) {
			if(x > cols.get(i).getX0() - ROW_COL_THICKNESS/2 && 
					x < cols.get(i).getX0() + ROW_COL_THICKNESS/2) {
				return cols.get(i);
			}
		}
		ArrayList<GTRow> rows = this.getGtRows();
		for(int i=0; i<rows.size(); i++) {
			if(y > rows.get(i).getY0() - ROW_COL_THICKNESS/2 && 
					y < rows.get(i).getY0() + ROW_COL_THICKNESS/2) {
				return rows.get(i);
			}
		}
		return null;
	}
	
	public int getSelectedCorner() {
		return this.selectedCorner;
	}
	
	public void setSelectedCorner(int corner) {
		this.selectedCorner = corner;
		System.out.println(corner);
	}
}
