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
public class GTTable extends BoundingBox implements GTElement{

	private ArrayList<GTRow> gtRows = new ArrayList<GTRow>();
	private ArrayList<GTCol> gtCols = new ArrayList<GTCol>();
	private ArrayList<GTCell> cells = new ArrayList<GTCell>();
	private GTCell gtCells[][] = null;
	private int index ;
	private Color foregroundColor = Color.RED;
	
	public ArrayList<GTCell> getCells(){
		return cells;
	}
	public GTTable() {
		
	}
	
	public GTTable(int x0,int y0, int x1, int y1){
		super(x0,y0,x1,y1);
	}
	
	public void addRow(GTRow row){
		gtRows.add(row);
	}
	public void addCols(GTCol col){
		gtCols.add(col);
	}
	public void remove(GTRow row){
		gtRows.remove(row);
	}
	public void remove(GTCol col){
		gtCols.remove(col);
	}
	
	public void assignColors(){
	int num_colors = (gtRows.size()+1)*(gtCols.size()+1);
	if (gtCells == null)
		return;
	int j = 0,k=0;
	for(int i = 0; i < 360; i += 360 / num_colors) {
	    
	    int hue = i;
	    double saturation = 90 + Math.random() * 10;
	    double lightness = 50 + Math.random() * 10;
	    
	    if (k>gtCols.size()){
	    	j++;
	    	k = 0;
	    }
	    gtCells[j][k++].setForegroundColor( new Color(Color.HSBtoRGB((float)hue,(float) saturation,(float) lightness)));
	    
	}
	}
	
	public GTCell getCellAtPoint(Point p){
		
		if (gtCells == null){
			return null;
		}
		else {
			for (int i=0;i<gtCells.length;i++){
				for (int j=0;j<gtCells[i].length;j++){
					GTCell cell = gtCells[i][j];
					if (p.x>=cell.getX0() && p.x<=cell.getX1() && p.y>=cell.getY0() && p.y<=cell.getY1())
						return cell;
				}
			}
		}
		return null;
	}
	
	public void addRowSpan(Point p1,Point p2){
		
		GTCell startCell = getCellAtPoint(p1);
		GTCell endCell = getCellAtPoint(p2);
		if (startCell==null || endCell==null || startCell.getStartRow() != endCell.getStartRow()){
			System.out.println("Cant add Row Span: for "+p1+" ,and "+p2);
			return;
		}
		startCell.setEndCol(endCell.getEndCol());
		for (int i=startCell.getStartCol()+1;i<=endCell.getEndCol();i++){
			GTCell temp = gtCells[startCell.getStartRow()][i];
//			temp.setStartCol(-1);
//			temp.setEndCol(-1);
			temp.setDontCare(true);
			startCell.setX1(temp.getX1());
			if (temp.getY1()>startCell.getY1())
				startCell.setX1(temp.getY1());
			
			for (int j=startCell.getStartRow()+1;j<=startCell.getEndRow();j++)
				gtCells[j][i].setDontCare(true);
			
		}
		startCell.assumeColor();
	}
	
	/**
	 * used by the load ground truth file
	 */
	public void populateCellMatrix(){
		int numRows = gtRows.size() + 1;
		int numCols = gtCols.size() + 1;
		gtCells = new GTCell[numRows][numCols];
		
		Collections.sort(cells);
		
		if (cells.size() != numRows*numCols)
		{
			System.out.println("Arrays don't match for cells");
			return;
		}
		Iterator<GTCell> iterator = cells.iterator();
		for (int i=0;i<numRows;i++)
			for (int j=0;j<numCols;j++)
			{
				gtCells[i][j] = iterator.next();
			}
	}
	public void addColSpan(Point p1, Point p2){
		GTCell startCell = getCellAtPoint(p1);
		GTCell endCell = getCellAtPoint(p2);
		if (startCell==null || endCell==null || startCell.getStartCol() != endCell.getStartCol()){
			System.out.println("Cant add Col Span: for "+p1+" ,and "+p2);
			return;
		}
		startCell.setEndRow(endCell.getEndRow());
		for (int i=startCell.getStartRow()+1;i<=endCell.getEndRow();i++){
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
			for (int j=startCell.getStartCol()+1;j<=startCell.getEndCol();j++)
				gtCells[i][j].setDontCare(true);
			
			
		}
		startCell.assumeColor();
		
	}
	/**
	 * does nothing at the moment
	 */
	public void reevaluateCells(){
		for (int i=0;i<cells.size();i++){
			
		}
	}
	public void evaluateInitialCells(){
	
		
		Collections.sort(gtRows);
		Collections.sort(gtCols);
		int l,t,r,b;
		int numRows = gtRows.size() + 1;
		int numCols = gtCols.size() + 1;
		
		gtCells = new GTCell[numRows][numCols];
		cells.clear();
		int rows = 0;
		int cols = 0;
		l = getX0();
		t = getY0();
		for (int i=0;i<numRows;i++){
			if (i<gtRows.size())
				b = gtRows.get(i).getY0();
			else
				b = getY1();
			for (int j=0;j<numCols;j++){
				if (j<gtCols.size())
					r = gtCols.get(j).getX0();
				else
					r = getX1();
				
				GTCell cell = new GTCell(l,t,r,b,i,j,index);
				
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
	public void initializePosition(int x, int y) {
		// TODO Auto-generated method stub
		setX0(x);
		setY0(y);
	}
	@Override
	public void updatePosition(int x, int y) {
		// TODO Auto-generated method stub
		setX1(x);
		setY1(y);
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
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
