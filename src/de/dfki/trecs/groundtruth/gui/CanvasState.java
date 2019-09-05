/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

import java.awt.Cursor;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.dfki.tablerecognizer.block.BoundingBox;
import de.dfki.tablerecognizer.util.XMLManager;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;
import de.dfki.trecs.groundtruth.data.GTCell;
import de.dfki.trecs.groundtruth.data.GTCol;
import de.dfki.trecs.groundtruth.data.GTElement;
import de.dfki.trecs.groundtruth.data.GTRow;
import de.dfki.trecs.groundtruth.data.GTTable;

import net.sourceforge.jiu.codecs.BMPCodec;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.color.promotion.PromotionRGB48;
import net.sourceforge.jiu.data.MemoryRGB48Image;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.ops.ImageToImageOperation;

/**
 * @author Shahab
 * 
 */
public class CanvasState implements MenuIndexConstants {

	public static final int MARK_TABLE = 0;

	public static final int MARK_ROW_COL_SPAN = 1;

	public static final int MARK_ROW_COL = 2;

	public static final int MARK_NONE = -1;
	/**
	 * The default number of undo steps possible.
	 */
	public static final int DEFAULT_MAX_UNDO_IMAGES = 2;

	/**
	 * The default number of redo steps possible.
	 */
	public static final int DEFAULT_MAX_REDO_IMAGES = DEFAULT_MAX_UNDO_IMAGES;

	/**
	 * All allowed zoom levels, as percentage values in ascending order.
	 */
	public static final int[] ZOOM_LEVELS = { 5, 7, 10, 15, 20, 30, 50, 70,
			100, 150, 200, 300, 500, 700, 1000, 2000, 3000, 5000 };

	/**
	 * The index into the {@link #ZOOM_LEVELS} array that holds the original
	 * size zoom level (100 percent). So, ZOOM_LEVELS[ORIGINAL_SIZE_ZOOM_INDEX]
	 * must be equal to 100.
	 */
	public static final int ORIGINAL_SIZE_ZOOM_INDEX = 8;

	/**
	 * Integer constant for <em>nearest neighbor interpolation</em>. A fast but
	 * ugly method.
	 */
	public static final int INTERPOLATION_NEAREST_NEIGHBOR = 0;

	/**
	 * Integer constant for <em>bilinear neighbor interpolation</em>. A slow but
	 * nice method.
	 */
	public static final int INTERPOLATION_BILINEAR = 1;

	/**
	 * Integer constant for <em>bicubic interpolation</em>. A very slow method,
	 * but with the nicest output of the three supported interpolation types.
	 */
	public static final int INTERPOLATION_BICUBIC = 2;

	/**
	 * The default interpolation type, one of the three INTERPOLATION_xyz
	 * constants.
	 */
	public static final int DEFAULT_INTERPOLATION = INTERPOLATION_NEAREST_NEIGHBOR;
	private String currentDirectory;
	private String fileName;
	private PixelImage currentImage;
	private MemoryRGB48Image rgb48Image = null;
	private int interpolation;
	private Locale locale;
	private int maxRedoImages;
	private int maxUndoImages;
	private boolean modified;
	private boolean drawing;
	private GTTable currentTable = null;
	private boolean initialCellsMarked = false;
	
	private int markType = MARK_NONE;

	private Stack<GTElement> undoStack = new Stack<GTElement>();

	private Stack<GTElement> redoStack = new Stack<GTElement>();

	private ArrayList<GTTable> list = new ArrayList<GTTable>();
	private GTElement currentElement = null;
	// private Vector progressListeners;
	// private Vector redoImages;
	// private Vector redoModified;
	// private String startupImageName;
	// private Strings strings;
	// private Vector undoImages;
	// private Vector undoModified;
	private int zoomIndex = ORIGINAL_SIZE_ZOOM_INDEX;
	private double zoomFactorX;
	private double zoomFactorY;
	private boolean zoomToFit;
	
	private boolean autoLoadGT = false;
	/**
	 * @param zoomToFit the zoomToFit to set
	 */
	public void setZoomToFit(boolean zoomToFit) {
		this.zoomToFit = zoomToFit;
	}

	private String groundTruthFile;
	
	/**
	 * Create new EditorState object and initialize its private fields to
	 * default values.
	 */
	public CanvasState() {
		locale = Locale.getDefault();
		// setStrings(null);
		// progressListeners = new Vector();
		maxRedoImages = DEFAULT_MAX_REDO_IMAGES;
		maxUndoImages = DEFAULT_MAX_UNDO_IMAGES;
		// redoImages = new Vector(maxRedoImages);
		// redoModified = new Vector(maxRedoImages);
		// undoImages = new Vector(maxUndoImages);
		// undoModified = new Vector(maxUndoImages);
		zoomFactorX = 1.0;
		zoomFactorY = 1.0;
		zoomToFit = false;
		interpolation = 0;
	}
	public void clear(){
		if (currentImage!=null){
			currentImage = null;
		}
		clearList();
		clearData();
		
	}
	private void clearList(){
		undoStack.clear();
		redoStack.clear();
		list.clear();
		currentTable = null;
		currentElement = null;
		setGroundTruthFile(null);
		modified = false;
		
	}
	private void clearData(){
		
		zoomToFit = false;
		setZoomFactors(1.0, 1.0);
		interpolation = 0;
	
		//currentDirectory = null;
		fileName = null;
		drawing = false;
		
		rgb48Image = null;
		markType = CanvasState.MARK_NONE;
		initialCellsMarked = false;
		
	}
	private void addGroundTruthCoordinates(XMLManager xmlManager, Node node,
			BoundingBox box) {
		xmlManager.addAttribue((Element) node, "x0", "" + box.getX0());
		xmlManager.addAttribue((Element) node, "y0", "" + box.getY0());
		xmlManager.addAttribue((Element) node, "x1", "" + box.getX1());
		xmlManager.addAttribue((Element) node, "y1", "" + box.getY1());

	}
	public void updateModifiedFlag(){
		if (undoStack.size()>0){
			modified = true;
		}
		else if (undoStack.size() == 0)
			modified = false;
	}
	public void addGTCol(GTCol col) {
		currentTable.addCols(col);
		col.setTable(currentTable);
		//System.out.println("Column added" + col);
		addUndoElement(col);
	}

	public void addGTRow(GTRow row) {
		currentTable.addRow(row);
		row.setTable(currentTable);
		addUndoElement(row);
	}

	public void addGTTable(GTTable table) {
		list.add(table);
		addUndoElement(table);
	}

	public void addRedoElement(GTElement e) {
		redoStack.add(e);
		updateModifiedFlag();
	}

	public void addUndoElement(GTElement e) {
		undoStack.push(e);
		updateModifiedFlag();

	}

	/**
	 * Returns the current directory. This directory will be used when file
	 * dialogs are opened.
	 */
	public String getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * @return the currentElement
	 */
	public GTElement getCurrentElement() {
		return currentElement;
	}

	/**
	 * @return the currentTable
	 */
	public GTTable getCurrentTable() {
		return currentTable;
	}

	/**
	 * Returns the name of the file from which the current image was loaded.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the image object currently loaded.
	 */
	public PixelImage getImage() {
		return currentImage;
	}

	/**
	 * Returns the current interpolation type, one of the INTERPOLATION_xyz
	 * constants.
	 */
	public int getInterpolation() {
		return interpolation;
	}

	/**
	 * @return the list
	 */
	public ArrayList<GTTable> getList() {
		return list;
	}

	/**
	 * Returns the Locale object currently used.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return the markType
	 */
	public int getMarkType() {
		return markType;
	}

	/**
	 * Returns the current modified state (true if image was modified and not
	 * saved after modification, false otherwise).
	 */
	public boolean getModified() {
		return modified;
	}

	/**
	 * @return the redoStack
	 */
	public Stack<GTElement> getRedoStack() {
		return redoStack;
	}

	public GTTable getTable(int x0, int y0) {
		for (GTTable table : list) {
			if (x0 >= table.getX0() && y0 >= table.getY0()
					&& x0 <= table.getX1() && y0 <= table.getY1())
				return table;
		}
		return null;
	}

	/**
	 * @return the undoStack
	 */
	public Stack<GTElement> getUndoStack() {
		return undoStack;
	}

	/**
	 * Returns the current zoom factor in horizontal direction. The value 1.0
	 * means that the image is displayed at its original size. Anything smaller
	 * means that the image is scaled down, anything larger means that the image
	 * is scaled up. The value must not be smaller than or equal to 0.0.
	 * 
	 * @return zoom factor in horizontal direction
	 * @see #getZoomFactorY
	 */
	public double getZoomFactorX() {
		return zoomFactorX;
	}

	/**
	 * Returns the current zoom factor in vertical direction. The value 1.0
	 * means that the image is displayed at its original size. Anything smaller
	 * means that the image is scaled down, anything larger means that the image
	 * is scaled up. The value must not be smaller than or equal to 0.0.
	 * 
	 * @return zoom factor in vertical direction
	 * @see #getZoomFactorX
	 */
	public double getZoomFactorY() {
		return zoomFactorY;
	}

	/**
	 * Returns if image display is currently set to &quot;zoom to fit&quot; Zoom
	 * to fit means that the image is always zoomed to fit exactly into the
	 * window.
	 */
	public boolean getZoomToFit() {
		return zoomToFit;
	}

	/**
	 * Returns if this state encapsulates an image object.
	 */
	public boolean hasImage() {
		return (currentImage != null);
	}

	/**
	 * @return the drawing
	 */
	public boolean isDrawing() {
		return drawing;
	}

	/**
	 * Returns if the image is displayed at maximum zoom level.
	 */
	public boolean isMaximumZoom() {
		return zoomIndex == ZOOM_LEVELS.length - 1;
	}

	/**
	 * @return the autoLoadGT
	 */
	public boolean isAutoLoadGT() {
		return autoLoadGT;
	}
	/**
	 * @param autoLoadGT the autoLoadGT to set
	 */
	public void setAutoLoadGT(boolean autoLoadGT) {
		this.autoLoadGT = autoLoadGT;
	}
	/**
	 * @return the groundTruthFile
	 */
	public String getGroundTruthFile() {
		return groundTruthFile;
	}
	/**
	 * @param groundTruthFile the groundTruthFile to set
	 */
	public void setGroundTruthFile(String groundTruthFile) {
		this.groundTruthFile = groundTruthFile;
	}
	/**
	 * Returns if the image is displayed at minimum zoom level.
	 */
	public boolean isMinimumZoom() {
		return zoomIndex == 0;
	}

	/**
	 * Returns if the current zoom level is set to original size (each image
	 * pixel is displayed as one pixel).
	 */
	public boolean isZoomOriginalSize() {
		return zoomIndex == ORIGINAL_SIZE_ZOOM_INDEX;
	}

	
	public void promoteImage(){
		if (currentImage!=null)
		{
			if (rgb48Image==null){
				ImageToImageOperation imgOperation = new PromotionRGB48();
				imgOperation.setInputImage(currentImage);
				try{
				imgOperation.process();
				}
				catch(Exception ex){
					System.out.println("Error transforming image to 48bit");
				}
				rgb48Image = (MemoryRGB48Image)imgOperation.getOutputImage();
			}
		}
	}
	public void evaluateTableCells(){
		int index = 0;
		//promoteImage();
		for (GTTable table:list){
			table.setIndex(index);
			table.evaluateInitialCells();
			setInitialCellsMarked(true);
		//	colorImageForeground(rgb48Image, table);
			index ++;
			//table.assignColors();
		}
		/**
		 * clear the stack no undo redo after table cell evaluation
		 */
		undoStack.clear();
		redoStack.clear();
	}
	private void colorImageForeground(MemoryRGB48Image image, GTTable table){
		
		GTCell cells[][] = table.getGtCells();
		for (int i=0;i<cells.length;i++){
			for (int j=0;j<cells[i].length;j++){
				GTCell cell = cells [i][j];
				int R[] = new int[(cell.getWidth()*cell.getHeight())];
				int G[] =  new int[(cell.getWidth()*cell.getHeight())];
				int B[] =  new int[(cell.getWidth()*cell.getHeight())];
				image.getSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
				image.getSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
				image.getSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
				for (int r=0;r<R.length;r++){
					if (R[r]!=0xffff)
						R[r] =cell.getColor().getR();
					if (G[r]!=0xffff)
						G[r] = cell.getColor().getG();
					if (B[r]!=0xffff)
						B[r] = cell.getColor().getB();
				}
				
				image.putSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
				image.putSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
				image.putSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
			
			}
			
		}
		
	}
	public void loadGroundTruthFile(File f) {
//		list.clear();
//		modified = false;
//		
//		currentTable = null;
		clearList();
		XMLManager xmlManager = new XMLManager(f.getAbsolutePath(), true);
		NodeList tablesList = xmlManager.getElementsByTagName(xmlManager
				.getDocument(), "Table");
		for (int i = 0; i < tablesList.getLength(); i++) {
			Node tableNode = tablesList.item(i);
			GTTable table = new GTTable();
			loadGTElementFromNode(table, tableNode);

			NodeList children = tableNode.getChildNodes();

			for (int j = 0; j < children.getLength(); j++) {
				Node childNode = children.item(j);
				if (childNode.getNodeName().equals("Row")) {
					GTRow row = new GTRow();
					loadGTElementFromNode(row, childNode);
					table.addRow(row);
				} else if (childNode.getNodeName().equals("Column")) {
					GTCol col = new GTCol();
					loadGTElementFromNode(col, childNode);
					table.addCols(col);
				}
				else if (childNode.getNodeName().equals("Cell")){
					GTCell cell = new GTCell();
					loadGTElementFromNode(cell,childNode);
					cell.setColor(ColorModel16Bit.parseColor(childNode.getTextContent().trim()));
					NamedNodeMap nmmap = childNode.getAttributes();
					cell.setStartRow(Integer.parseInt(nmmap.getNamedItem("startRow").getTextContent()));
					cell.setEndRow(Integer.parseInt(nmmap.getNamedItem("endRow").getTextContent()));
					cell.setStartCol(Integer.parseInt(nmmap.getNamedItem("startCol").getTextContent()));
					cell.setEndCol(Integer.parseInt(nmmap.getNamedItem("endCol").getTextContent()));
					cell.setDontCare(Boolean.parseBoolean(nmmap.getNamedItem("dontCare").getTextContent()));
					table.getCells().add(cell);
					
				}
			}
			if(table.getCells().size() > 0)
				table.populateCellMatrix();
			list.add(table);

		}
	}

	private void loadGTElementFromNode(BoundingBox box, Node node) {
		NamedNodeMap nmmap = node.getAttributes();
		box.setX0(Integer.parseInt(nmmap.getNamedItem("x0").getTextContent()
				.trim()));
		box.setY0(Integer.parseInt(nmmap.getNamedItem("y0").getTextContent()
				.trim()));
		box.setX1(Integer.parseInt(nmmap.getNamedItem("x1").getTextContent()
				.trim()));
		box.setY1(Integer.parseInt(nmmap.getNamedItem("y1").getTextContent()
				.trim()));

	}

	public void markRowColSpan() {
		if (isInitialCellsMarked()){
			markType = MARK_ROW_COL_SPAN;
			currentTable = null;
		}
		
	}

	public void markRowColumns() {
		markType = MARK_ROW_COL;
		currentElement = new GTRow();
		currentTable = null;
	}

	public void markTable() {
		markType = MARK_TABLE;
		currentTable = null;
		currentElement = new GTTable();

	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			GTElement e = redoStack.pop();
			if (e instanceof GTTable) {
				list.add((GTTable) e);

			} else if (e instanceof GTRow) {
				GTTable table = ((GTRow) e).getTable();
				table.addRow((GTRow) e);
			} else if (e instanceof GTCol) {
				GTTable table = ((GTCol) e).getTable();
				table.addCols((GTCol) e);
			}
			addUndoElement(e);
		}
	}

	public void resetZoomFactors() {
		setZoomFactors(1.0, 1.0);
	}

	/**
	 * saves the ground truth file
	 * 
	 * @param f
	 */
	public void saveGroundTruthFile(File f) {
		XMLManager xmlManager = new XMLManager("GroundTruth", false);
		xmlManager.getDocument().getDocumentElement().setAttribute("InputFile",fileName);
		Node tablesNode = xmlManager.createElement(xmlManager.getDocument(),
				"Tables");
		xmlManager.getDocument().getDocumentElement().appendChild(tablesNode);
		for (GTTable table : list) {
			Node tableNode = xmlManager.createElement(xmlManager.getDocument(),
					"Table");
			addGroundTruthCoordinates(xmlManager, tableNode, table);
			tablesNode.appendChild(tableNode);
			// Node rowsNode =
			// xmlManager.createElement(xmlManager.getDocument(), "Rows");
			// tableNode.appendChild(rowsNode);
			ArrayList<GTRow> rows = table.getGtRows();
			for (GTRow row : rows) {
				Node rowNode = xmlManager.createElement(xmlManager
						.getDocument(), "Row");
				addGroundTruthCoordinates(xmlManager, rowNode, row);
				tableNode.appendChild(rowNode);
			}
			// Node colsNode =
			// xmlManager.createElement(xmlManager.getDocument(), "Columns");
			// tableNode.appendChild(colsNode);
			ArrayList<GTCol> cols = table.getGtCols();
			for (GTCol col : cols) {
				Node colNode = xmlManager.createElement(xmlManager
						.getDocument(), "Column");
				addGroundTruthCoordinates(xmlManager, colNode, col);
				tableNode.appendChild(colNode);
			}
			
			GTCell cells[][] = table.getGtCells();
			if (cells!= null){
				for (int i=0;i<cells.length;i++){
					for (int j=0;j<cells[i].length;j++){
						GTCell cell = cells[i][j];
						Node cellNode = xmlManager.createElement(xmlManager.getDocument(), "Cell");
						addGroundTruthCoordinates(xmlManager, cellNode, cell);
						xmlManager.addAttribue((Element)cellNode, "startRow", ""+cell.getStartRow());
						xmlManager.addAttribue((Element)cellNode, "endRow", ""+cell.getEndRow());
						xmlManager.addAttribue((Element)cellNode, "startCol", ""+cell.getStartCol());
						xmlManager.addAttribue((Element)cellNode, "endCol", ""+cell.getEndCol());
						xmlManager.addAttribue((Element)cellNode, "dontCare", ""+((cell.isDontCare())?"true":"false"));
						
						cellNode.setTextContent(cell.getColor().toString());
						tableNode.appendChild(cellNode);
					}
				}
			}

		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(f), "UTF-8")));
			xmlManager.serializeXmlDom(xmlManager.getDocument(), out);
			out.close();
			
			undoStack.clear();
			redoStack.clear();
			updateModifiedFlag();
		} catch (Exception ex) {
			System.out.println("Error saving groundtruth");
		}
	}

	/**
	 * Sets a new current directory.
	 * 
	 * @param newCurrentDirectory
	 *            the directory to be used as current directory from now on
	 */
	public void setCurrentDirectory(String newCurrentDirectory) {
		currentDirectory = newCurrentDirectory;
	}

	/**
	 * @param currentElement
	 *            the currentElement to set
	 */
	public void setCurrentElement(GTElement currentElement) {
		this.currentElement = currentElement;
	}

	/**
	 * @param currentTable
	 *            the currentTable to set
	 */
	public void setCurrentTable(GTTable currentTable) {
		this.currentTable = currentTable;
	}

	/**
	 * @param drawing
	 *            the drawing to set
	 */
	public void setDrawing(boolean drawing) {
		this.drawing = drawing;
	}

	/**
	 * Sets a new file name. This is used mostly after a new image was loaded
	 * from a file or if the current image is closed (then a null value would be
	 * given to this method).
	 * 
	 * @param newFileName
	 *            new name of the current file
	 */
	public void setFileName(String newFileName) {
		fileName = newFileName;
	}

	public void setImage(PixelImage image, boolean flag) {
		currentImage = image;
		modified = flag;
	}

	/**
	 * Sets a new interpolation type to be used for display.
	 * 
	 * @param newInterpolation
	 *            an int for the interpolation type, must be one of the
	 *            INTERPOLATION_xyz constants
	 */
	public void setInterpolation(int newInterpolation) {
		if (newInterpolation == INTERPOLATION_NEAREST_NEIGHBOR
				|| newInterpolation == INTERPOLATION_BILINEAR
				|| newInterpolation == INTERPOLATION_BICUBIC) {
			interpolation = newInterpolation;
		}
	}

	public void close(){
		if ( isModified()){
		
		}
	}

	public boolean isModified(){
		return modified;
	}
	
	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(ArrayList<GTTable> list) {
		this.list = list;
	}

	/**
	 * @param markType
	 *            the markType to set
	 */
	public void setMarkType(int markType) {
		this.markType = markType;
	}

	/**
	 * @param redoStack
	 *            the redoStack to set
	 */
	public void setRedoStack(Stack<GTElement> redoStack) {
		this.redoStack = redoStack;
	}

	/**
	 * @param undoStack
	 *            the undoStack to set
	 */
	public void setUndoStack(Stack<GTElement> undoStack) {
		this.undoStack = undoStack;
	}

	/**
	 * Sets the zoom factors to the argument values.
	 */
	public void setZoomFactors(double zoomX, double zoomY) {
		zoomFactorX = zoomX;
		zoomFactorY = zoomY;
	}

	public void undo() {

		if (!undoStack.isEmpty()) {
			GTElement e = undoStack.pop();
			if (e instanceof GTTable) {
				list.remove(e);
				if ((GTTable) e == currentTable) {
					currentTable = null;
				}

			} else if (e instanceof GTRow) {
				GTTable table = ((GTRow) e).getTable();
				table.remove((GTRow) e);
			} else if (e instanceof GTCol) {
				GTTable table = ((GTCol) e).getTable();
				table.remove((GTCol) e);
			}

			addRedoElement(e);
		}
	}

	public void saveGTImage(File file){
		
		PNGCodec codec = new PNGCodec();
		try
		{
			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			codec.setImage(rgb48Image);
			codec.process();
			codec.close();
		}
		catch (Exception e)
		{
			System.out.println("Error saving the ground truth image file");
			return;
		}
		
	}
	/**
	 * Increase the zoom level by one.
	 * 
	 * @see #zoomOut
	 * @see #zoomSetOriginalSize
	 */
	public void zoomIn() {
		if (zoomIndex + 1 == ZOOM_LEVELS.length) {
			return;
		}
		zoomIndex++;
		zoomFactorX = 1.0 * ZOOM_LEVELS[zoomIndex] / 100;
		zoomFactorY = zoomFactorX;
	}

	/**
	 * Decrease the zoom level by one.
	 * 
	 * @see #zoomIn
	 * @see #zoomSetOriginalSize
	 */
	public void zoomOut() {
		if (zoomIndex == 0) {
			return;
		}
		zoomIndex--;
		zoomFactorX = 1.0 * ZOOM_LEVELS[zoomIndex] / 100;
		zoomFactorY = zoomFactorX;
	}

	/**
	 * Set the zoom level to 100 percent (1:1). Each image pixel will be
	 * displayed as one pixel
	 * 
	 * @see #zoomIn
	 * @see #zoomOut
	 */
	public void zoomSetOriginalSize() {
		zoomIndex = ORIGINAL_SIZE_ZOOM_INDEX;
		zoomFactorX = 1.0;
		zoomFactorY = 1.0;
	}
	public MemoryRGB48Image getRgb48Image() {
		return rgb48Image;
	}
	public void setRgb48Image(MemoryRGB48Image rgb48Image) {
		this.rgb48Image = rgb48Image;
	}
	/**
	 * @return the initialCellsMarked
	 */
	public boolean isInitialCellsMarked() {
		return initialCellsMarked;
	}
	/**
	 * @param initialCellsMarked the initialCellsMarked to set
	 */
	public void setInitialCellsMarked(boolean initialCellsMarked) {
		this.initialCellsMarked = initialCellsMarked;
	}
}
