package de.dfki.trecs.groundtruth.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import net.sourceforge.jiu.data.MemoryShortChannelImage;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;

import de.dfki.trecs.groundtruth.data.GTCell;
import de.dfki.trecs.groundtruth.data.GTCol;
import de.dfki.trecs.groundtruth.data.GTRow;
import de.dfki.trecs.groundtruth.data.GTTable;

public class GTCanvas extends Canvas implements MouseMotionListener, MouseListener, KeyListener {

	private Image image;
	private int width;
	private int height;
	private int scaledWidth;
	private int scaledHeight;
	private GTGui mainGui;
	private double zoomFactorX = 1.0;
	private double zoomFactorY = 1.0;
	private boolean zoomToFit;
	private ScrollPane myScrollPane;
	private Object interpolation;
	private CanvasState state;
	private int previousX;
	private int previousY;

	private int x0 = -1; // start position for mouse drawing
	private int y0 = -1;// start position for mouse drawing
	private int x2 = -1;// end position for mouse drawing
	private int y2 = -1;// end position for mouse drawing
	private int imageX = 0;

	/**
	 * @return the imageX
	 */
	public int getImageX() {
		return imageX;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		mainGui.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the imageY
	 */
	public int getImageY() {
		return imageY;
	}

	public void clear() {
		image = null;
		clearInteractiveDrawingLoc();
		repaint();
	}

	/**
	 * @param imageX the imageX to set
	 */
	public void setImageX(int imageX) {
		this.imageX = imageX;
	}

	/**
	 * @param imageY the imageY to set
	 */
	public void setImageY(int imageY) {
		this.imageY = imageY;
	}

	private int imageY = 0;

	public GTCanvas(ScrollPane scrollPane, CanvasState state) {
		myScrollPane = scrollPane;
		interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		this.state = state;
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void drawLine(int x0, int y0, int x1, int y1) {
		getGraphics().setColor(Color.black);
		getGraphics().drawLine(x0, y0, x1, y1);

	}

	public void drawLine(int x0, int y0, int x1, int y1, Color color, int thickness, Graphics g) {
		g.setColor(color);
		g.drawLine(x0, y0, x1, y1);
		g.fillRect(x0 - thickness / 2, y0 - thickness / 2, (x1 - x0) + thickness, (y1 - y0) + thickness);
	}


	public void computeZoomToFitSize() {
		if (!zoomToFit || myScrollPane == null) {
			return;
		}
		Dimension scrollPaneSize = myScrollPane.getSize();
		int maxWidth = scrollPaneSize.width;
		int maxHeight = scrollPaneSize.height;
		double paneRatio = (double) maxWidth / (double) maxHeight;
		double imageRatio = (double) width / (double) height;
		if (paneRatio < imageRatio) {
			scaledWidth = maxWidth;
			scaledHeight = (int) (scaledWidth * imageRatio);
		} else {
			scaledHeight = maxHeight;
			scaledWidth = (int) (scaledHeight * imageRatio);
		}
		scaledHeight--;
		scaledWidth--;
		zoomFactorX = (double) scaledWidth / (double) width;
		zoomFactorY = zoomFactorX;
	}

	public int getZoomPercentageX() {
		return (int) (zoomFactorX * 100.0);
	}

	public int getZoomPercentageY() {
		return (int) (zoomFactorY * 100.0);
	}

	public Dimension getPreferredSize() {
		return new Dimension(scaledWidth, scaledHeight);
	}

	/**
	 * Draws image to upper left corner.
	 */
	public void paint(Graphics g) {
		if (image == null) {
			super.paint(g);
		} else {
			Rectangle rect = getBounds();
			int canvasWidth = rect.width;
			int canvasHeight = rect.height;
			int x1 = 0;
			int y1 = 0;
			if (canvasWidth > scaledWidth) {
				x1 = (canvasWidth - scaledWidth) / 2;

			}
			if (canvasHeight > scaledHeight) {
				y1 = (canvasHeight - scaledHeight) / 2;

			}
			if (canvasHeight > canvasWidth || canvasHeight > scaledHeight) {
				super.paint(g);
			}
			/* commented because Graphics2D requires Java 1.2+ */
			if (g instanceof Graphics2D) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
			}

			imageX = x1;
			imageY = y1;
			g.drawImage(image, x1, y1, scaledWidth, scaledHeight, this);
			// System.out.println("imageX="+imageX+", imageY="+imageY);
			g.setColor(Color.blue);

			g.drawRect(x1, y1, scaledWidth, scaledHeight);

			drawGTStateElements(g);

			if (state.isDrawing()) {
				switch (state.getMarkType()) {
				case CanvasState.MARK_TABLE:
					g.setColor(state.getCurrentElement().getForegroundColor());

				// System.out.println("x2="+x2+" y2="+y2);

				{
//					int tmpX =(int)((x0-x1)*state.getZoomFactorX());
//					int tmpY = (int) ((y0-y1)*state.getZoomFactorY());
//					int tmpX2 = (int) ((x2-x1)*state.getZoomFactorX());
//					int tmpY2 = (int) ((y2-y1)*state.getZoomFactorY());
//					g.fillRect(tmpX,tmpY,tmpX2-tmpX,tmpY2-tmpY);
					paintRectangleBoundary(Math.min(x0, x2), Math.min(y0, y2), Math.max(x0, x2), Math.max(y0, y2), state.getCurrentElement().getForegroundColor(), g);
					// g.fillRect(x0, y0, x2-x0, y2-y0);
				}
					break;
				case CanvasState.MARK_ROW_COL_SPAN:
					GTTable table = state.getCurrentTable();
					if (table == null)
						break;
					Point p1 = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
					Point p2 = translateToScreenPoint(new Point(table.getX1(), table.getY1()));
					if (p1 == null || p2 == null)
						break;
					if (x2 > p2.x)
						x2 = p2.x;
					if (y2 > p2.y)
						y2 = p2.y;

					g.setColor(Color.BLACK);
					this.drawLine(Math.min(x0, x2), Math.min(y0, y2), Math.max(x0, x2), Math.max(y0, y2), Color.BLACK, 2, g);
					break;
				}

			}

		}
	}

	public void drawGTStateElements(Graphics g) {
		ArrayList<GTTable> tableList = state.getList();
		for (GTTable table : tableList) {
			if(state.getMarkType() == CanvasState.MARK_ORIENTATION) {
				Point p = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
				Point p2 = translateToScreenPoint(new Point(table.getX1(), table.getY1()));
				Color color = table.getForegroundColor();
				Color tmpColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 70);
				g.setColor(tmpColor);
				g.fillRect(p.x, p.y, p2.x - p.x, p2.y - p.y);
				continue;
			}
			else {
				drawTable(table, g);
			}

			if (table.getGtCells() == null) {
				ArrayList<GTRow> rows = table.getGtRows();
				for (GTRow row : rows) {
					g.setColor(row.getForegroundColor());
					Point p = translateToScreenPoint(new Point(row.getX0(), row.getY0()));
					Point p2 = translateToScreenPoint(new Point(row.getX1(), row.getY1()));
					this.drawLine(p.x, p.y, p2.x, p2.y, row.getForegroundColor(), 3, g);

				}
				ArrayList<GTCol> cols = table.getGtCols();
				for (GTCol col : cols) {
					g.setColor(col.getForegroundColor());
					Point p = translateToScreenPoint(new Point(col.getX0(), col.getY0()));
					Point p2 = translateToScreenPoint(new Point(col.getX1(), col.getY1()));
					this.drawLine(p.x, p.y, p2.x, p2.y, col.getForegroundColor(), 3, g);
				}
			}
			GTCell cells[][] = table.getGtCells();
			if (cells != null) {
				Color color = new Color(255, 0, 0, 60);
				Color multiColor = new Color(0, 128, 128, 60);

				Color rowSpanColor = new Color(0, 255, 0, 100);
				Color colSpanColor = new Color(0, 0, 255, 100);
				for (GTCell cell : table.getCells()) {
					if (!cell.isDontCare()) {
						Point p = translateToScreenPoint(new Point(cell.getX0(), cell.getY0()));
						Point p2 = translateToScreenPoint(new Point(cell.getX1(), cell.getY1()));
						g.setColor(Color.BLACK);
						g.drawRect(p.x, p.y, p2.x - p.x, p2.y - p.y);
						if (cell.getEndCol() > cell.getStartCol() && cell.getEndRow() > cell.getStartRow())
							g.setColor(multiColor);
						else if (cell.getEndRow() > cell.getStartRow())
							g.setColor(rowSpanColor);
						else if (cell.getEndCol() > cell.getStartCol())
							g.setColor(colSpanColor);
						else
							g.setColor(color);
//						int i = cell.getStartRow();
//						int j = cell.getStartCol();
//						if ((i+j)%2 == 0)
//							g.setColor(color);
//						else
//							g.setColor(color2);
						// g.setColor(cell.getForegroundColor());
						g.fillRect(p.x, p.y, p2.x - p.x, p2.y - p.y);
					}
				}
//				for (int i=0;i<cells.length;i++)
//					for (int j=0;j<cells[i].length;j++){
//						GTCell cell = cells[i][j];
//						Point p = translateToScreenPoint(new Point(cell.getX0(),cell.getY0()));
//						Point p2 = translateToScreenPoint(new Point(cell.getX1(),cell.getY1()));
//						if ((i+j)%2 == 0)
//							g.setColor(color);
//						else
//							g.setColor(color2);
//						//g.setColor(cell.getForegroundColor());
//						g.fillRect(p.x, p.y, p2.x-p.x, p2.y-p.y);
//					}
			}
		}
		if (state.getMarkType() != CanvasState.MARK_ORIENTATION && state.getCurrentTable() != null) {
			GTTable table = state.getCurrentTable();
			Point p = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
			Point p2 = translateToScreenPoint(new Point(table.getX1(), table.getY1()));
			Color color = table.getForegroundColor();
			Color tmpColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 70);
			g.setColor(tmpColor);
			g.fillRect(p.x, p.y, p2.x - p.x, p2.y - p.y);
			if(state.getMarkType() == CanvasState.MARK_TABLE) {
				color = Color.yellow;
				tmpColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 130);
				g.setColor(tmpColor);
				int thickness = (int) (GTTable.CORNER_THICKNESS * zoomFactorX);
				g.fillRect(p.x, p.y, thickness, thickness);
				g.fillRect(p.x, p2.y - thickness, thickness, thickness);
				g.fillRect(p2.x - thickness, p.y, thickness, thickness);
				g.fillRect(p2.x - thickness, p2.y - thickness, thickness, thickness);
			}
		}

	}

	private void drawTable(GTTable table, Graphics g) {
		g.setColor(table.getForegroundColor());
		Point p1 = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
		Point p2 = translateToScreenPoint(new Point(table.getX1(), table.getY1()));
		paintRectangleBoundary(p1.x, p1.y, p2.x, p2.y, table.getForegroundColor(), g);
	}

	private void drawRow(GTRow row, Graphics g) {

	}

	private void drawCol(GTCol col, Graphics g) {

	}

	/**
	 * Specifies a new Image object to be displayed in this canvas.
	 * 
	 * @param newImage the new Image object, potentially null
	 */
	public void setImage(Image newImage) {
		image = newImage;
		width = image.getWidth(this);
		height = image.getHeight(this);
		scaledWidth = (int) (width * zoomFactorX);
		scaledHeight = (int) (height * zoomFactorY);
		/*
		 * zoomFactorX = 1.0; zoomFactorY = 1.0;
		 */
		setSize(scaledWidth, scaledHeight);
		validate();
	}

	/**
	 * Sets both zoom factors to <code>1.0</code>.
	 */
	public void setOriginalSize() {
		setZoomFactor(1.0);
	}

	public double getZoomFactorX() {
		return zoomFactorX;
	}

	public double getZoomFactorY() {
		return zoomFactorY;
	}

	/**
	 * Sets the interpolation type used for drawing to the argument (must be one of
	 * the INTERPOLATION_xyz constants of EditorState), but does not do a redraw.
	 */
	public void setInterpolation(int newType) {
		switch (newType) {
		case (CanvasState.INTERPOLATION_BICUBIC): {
			interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			break;
		}
		case (CanvasState.INTERPOLATION_BILINEAR): {
			interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			break;
		}
		case (CanvasState.INTERPOLATION_NEAREST_NEIGHBOR): {
			interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			break;
		}
		}
	}

	public void setZoomFactor(double newZoomFactor) {
		setZoomFactors(newZoomFactor, newZoomFactor);
	}

	public void setZoomFactors(double newZoomFactorX, double newZoomFactorY) {
		if (newZoomFactorX <= 0.0 || newZoomFactorY <= 0.0) {
			throw new IllegalArgumentException("Zoom factors must be larger than 0.0.");
		}
		zoomFactorX = newZoomFactorX;
		zoomFactorY = newZoomFactorY;
		scaledWidth = (int) (width * zoomFactorX);
		scaledHeight = (int) (height * zoomFactorY);

		setSize(scaledWidth, scaledHeight);
		myScrollPane.validate();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	public static void reportMousePosition(MouseEvent e) {
		System.out.println("Mouse at: (" + e.getX() + "," + e.getY() + ")");
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		Graphics g = getGraphics();

		switch (state.getMarkType()) {
		case CanvasState.MARK_TABLE:

			if(state.getCurrentTable() == null || state.getCurrentTable().getSelectedCorner() != -1) {
				x2 = e.getX();
				y2 = e.getY();
				drawInteractiveRectangle();
			}
			break;
		case CanvasState.MARK_ROW_COL:
			x2 = e.getX();
			y2 = y0;
			break;
		case CanvasState.MARK_ROW_COL_SPAN:
			if (state.getPressedButton() == MouseEvent.BUTTON1) {
				y2 = e.getY();
			} else if (state.getPressedButton() == MouseEvent.BUTTON3) {
				x2 = e.getX();
			}

			drawInteractiveLine(false);
			break;
		case CanvasState.MARK_NONE:
			return;
		}

	}

	private void drawInteractiveLine(boolean row) {
		if (previousX != x2 || previousY != y2) {
			repaint(Math.min(x0, previousX), y0, Math.abs(previousX - x0) + 4, 1);
			repaint(x0, Math.min(y0, previousY), 1, Math.abs(previousY - y0) + 4);

			previousX = x2;
			previousY = y2;
			repaint(Math.min(x2, x0), y0, Math.abs(x2 - x0), 1);
			repaint(x0, Math.min(y2, y0), 1, Math.abs(y2 - y0));
		}
//			}
	}
//	

	private void drawInteractiveRectangle() {

		if (previousX != x2 || previousY != y2) {
			int x0 = Math.min(this.x0, previousX);
			int x2 = Math.max(this.x0, previousX);

			int y0 = Math.min(this.y0, previousY);
			int y2 = Math.max(this.y0, previousY);
			
//			repaint(x0-50, y0-50, (x2-x0)+100, (y2-y0)+100);
			
			int thickness=5;
			repaint(x0 - thickness/2, y0- thickness/2, thickness, y2 - y0);
			repaint(x0- thickness/2, y0- thickness/2, x2 - x0, thickness);
			repaint(x2- thickness/2, y0- thickness/2, thickness, Math.abs(y2 - y0));
			repaint(x2- thickness/2, y2- thickness/2, Math.abs(x2 - x0), thickness);
//
			previousX = this.x2;
			previousY = this.y2;
			
			x0 = Math.min(this.x0, previousX);
			x2 = Math.max(this.x0, previousX);

			y0 = Math.min(this.y0, previousY);
			y2 = Math.max(this.y0, previousY);

			repaint(x0 - thickness/2, y0- thickness/2, thickness, y2 - y0);
			repaint(x0- thickness/2, y0- thickness/2, x2 - x0, thickness);
			repaint(x2- thickness/2, y0- thickness/2, thickness, Math.abs(y2 - y0));
			repaint(x2- thickness/2, y2- thickness/2, Math.abs(x2 - x0), thickness);
		}
	}
	
	private void paintRectangleBoundary(int x0, int y0, int x2, int y2, Color color, Graphics g) {
		if (x0 > imageX && y0 > imageY && x2 > x0 && y2 > y0) {
			if (x2 > imageX + scaledWidth)
				x2 = imageX + scaledWidth;
			if (y2 > imageY + scaledHeight)
				y2 = imageY + scaledHeight;
			this.drawLine(x0, y0, x0, y2, color, 3, g);
			this.drawLine(x0, y0, x2, y0, color, 3, g);
			this.drawLine(x2, y0, x2, y2, color, 3, g);
			this.drawLine(x0, y2, x2, y2, color, 3, g);

		}
	}

	public Point translateToScreenPoint(Point p) {
		Point pScreen = new Point();
		pScreen.x = (int) ((p.x * zoomFactorX) + imageX);
		pScreen.y = (int) ((p.y * zoomFactorY) + imageY);
		return pScreen;
	}

	public Point translateToImagePoint(Point p) {
		Point pImage = new Point();
		// if (zoomFactorX < 1.0 && zoomFactorY <1.0)
		{
			pImage.x = (int) ((p.x - imageX) / zoomFactorX);
			pImage.y = (int) ((p.y - imageY) / zoomFactorY);
		}

//		else
//		{
//			pImage.x = (int) (( p.x  - imageX ) * zoomFactorX);
//			pImage.y = (int) (( p.y - imageY ) * zoomFactorY );
//		}
//		

		return pImage;

	}

	public void mouseEntered(MouseEvent e) {
	};

	@Override
	public void mouseMoved(MouseEvent e) {
		int a = e.getX();
		int b = e.getY();
		if (isImagePoint(a, b)) {
			// System.out.println(translateToImagePoint(new Point(a,b)));
			Point p = translateToImagePoint(new Point(a, b));
			String info = "";
			if (state.getImage() != null && state.getImage() instanceof RGB48Image) {
				MemoryShortChannelImage img = (MemoryShortChannelImage) state.getImage();
				int R = img.getShortSample(0, p.x, p.y);
				int G = img.getShortSample(1, p.x, p.y);
				int B = img.getShortSample(2, p.x, p.y);
				int MSBMASK = 0x0000ff00;
				int LSBMASK = 0x000000ff;
				info = "R(MSB,LSB)G(MSB,LSB)B(MSB,LSB): ((" + ((R & MSBMASK) >> 8) + "," + (R & LSBMASK) + "),(("
						+ ((G & MSBMASK) >> 8) + "," + (G & LSBMASK) + "),((" + ((B & MSBMASK) >> 8) + ","
						+ (B & LSBMASK) + "))";

			}
			mainGui.updateInfoBar(p.x, p.y, info);
		}
		if (state.getMarkType() == CanvasState.MARK_ROW_COL) {

		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == e.BUTTON1 || e.getButton() == e.BUTTON3) {

			switch (state.getMarkType()) {
			case CanvasState.MARK_TABLE:
				if (state.getCurrentTable() == null) {
					Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
					GTTable table = state.getTable(p.x, p.y);
					if (table != null) {
						state.setCurrentTable(table);
						repaint();
					}
					else if (isImagePoint(e.getX(), e.getY())) {
						x0 = e.getX();
						y0 = e.getY();
						state.setDrawing(true);
					}
				} else {
					Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
					GTTable table = state.getTable(p.x, p.y);
					if (table == state.getCurrentTable()) {
						int corner = table.getCorner(p.x, p.y);
						if(corner != -1) {
							table.setSelectedCorner(corner);
							switch(corner) {
							case GTTable.CORNER_BOTTOM_LEFT:
								p = translateToScreenPoint(new Point(table.getX1(), table.getY0()));
								break;
							case GTTable.CORNER_BOTTOM_RIGHT:
								p = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
								break;
							case GTTable.CORNER_TOP_LEFT:
								p = translateToScreenPoint(new Point(table.getX1(), table.getY1()));
								break;
							case GTTable.CORNER_TOP_RIGHT:
								p = translateToScreenPoint(new Point(table.getX0(), table.getY1()));
								break;
							}
							x0 = p.x;
							y0 = p.y;
							state.setDrawing(true);
						}
					} else if (table != null) {
						state.setCurrentTable(table);
					}
					else {
						state.setCurrentTable(null);
					}
					repaint();
				}

				// reportMousePosition(e);
				break;
			case CanvasState.MARK_ROW_COL:
				if (state.getCurrentTable() == null) {
					Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
					GTTable table = state.getTable(p.x, p.y);
					if (table != null) {
						state.setCurrentTable(table);
						repaint();
					}
				} else {
					// System.out.println(e.getButton());
					Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
					GTTable table = state.getTable(p.x, p.y);
					if (table == state.getCurrentTable()) {

						if (e.getButton() == e.BUTTON1) {
							GTRow row = new GTRow(table.getX0(), p.y, table.getX1());
							state.addGTRow(row);
						} else if (e.getButton() == e.BUTTON3) {
							// System.out.println("Mouse right click");
							GTCol col = new GTCol(p.x, table.getY0(), table.getY1());
							state.addGTCol(col);
						}
						repaint();
					} else if (table != null) {
						state.setCurrentTable(table);
						repaint();
					}

				}
				break;
			case CanvasState.MARK_ROW_COL_SPAN:
				if (state.getCurrentTable() == null) {
					Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
					GTTable table = state.getTable(p.x, p.y);
					if (table != null) {
						state.setCurrentTable(table);
						repaint();
					}
				} else {
					GTTable table = state.getCurrentTable();
					Point p1 = translateToScreenPoint(new Point(table.getX0(), table.getY0()));
					Point p2 = translateToScreenPoint(new Point(table.getX1(), table.getY1()));

					if (e.getX() >= p1.x && e.getX() < p2.x && e.getY() >= p1.y && e.getY() < p2.y) {

						x0 = e.getX();
						y0 = e.getY();

						x2 = 0;
						y2 = 0;
						state.setDrawing(true);
						if (e.getButton() == e.BUTTON1) {
							x2 = x0;
							state.setPressedButton(e.BUTTON1);
						} else if (e.getButton() == e.BUTTON3) {
							// g.setColor(Color.pink);
							y2 = y0;
							state.setPressedButton(e.BUTTON3);
						}
					} else {
						Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
						GTTable temp = state.getTable(p.x, p.y);
						if (temp != null) {
							state.setCurrentTable(temp);
						} else {
							state.setCurrentTable(null);
						}
						repaint();
					}

				}
				break;
			case CanvasState.MARK_ORIENTATION:
				Point p = translateToImagePoint(new Point(e.getX(), e.getY()));
				GTTable table = state.getTable(p.x, p.y);
				if (table != null) {
					if (e.getButton() == e.BUTTON1) {
						table.setOrientation(table.HORIZONTAL);
					} else if (e.getButton() == e.BUTTON3) {
						table.setOrientation(table.VERTICAL);
					}
					repaint();
				}
				break;
			}

		}
//		if (mainGui.isPreviewMode())
//			mainGui.requestFocus();

	}

	private boolean isImagePoint(int a, int b) {
		return (a >= imageX && a <= imageX + scaledWidth) && (b >= imageY && b <= imageY + scaledHeight);
	}

	private void clearInteractiveDrawingLoc() {
		x0 = -1;
		y0 = -1;
		x2 = -1;
		y2 = -1;
		previousX = -1;
		previousY = -1;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if ((e.getButton() == e.BUTTON1 || e.getButton() == e.BUTTON3) && state.isDrawing()) {

			switch (state.getMarkType()) {
			case CanvasState.MARK_TABLE:
				state.setDrawing(false);
				System.out.println(state.getCurrentTable());
				x2 = e.getX();
				y2 = e.getY();
				if (x2 > imageX + scaledWidth)
					x2 = imageX + scaledWidth;
				if (y2 > imageY + scaledHeight)
					y2 = imageY + scaledHeight;
				
				if(state.getCurrentTable() == null){
					if (x2 != x0 && y2 != y0) {
						Point p1 = translateToImagePoint(new Point(Math.min(x0, x2), Math.min(y0, y2)));
						Point p2 = translateToImagePoint(new Point(Math.max(x0, x2), Math.max(y0, y2)));
						clearInteractiveDrawingLoc();
						GTTable table = new GTTable(p1.x, p1.y, p2.x, p2.y);
						state.addGTTable(table);
					}
				}
				else {
					GTTable table = state.getCurrentTable();
					if(table.getSelectedCorner() != -1 && x2 != x0 && y2 != y0) {
						Point p1 = translateToImagePoint(new Point(Math.min(x0, x2), Math.min(y0, y2)));
						Point p2 = translateToImagePoint(new Point(Math.max(x0, x2), Math.max(y0, y2)));
				
						table.moveTo(p1, p2);
					}
					this.drawGTStateElements(this.getGraphics());
					repaint();
				}
				break;
			case CanvasState.MARK_ROW_COL_SPAN:

				state.setDrawing(false);
//				x2 = e.getX();
//				y2 = e.getY();
				if (state.getCurrentTable() == null)
					return;
				Point p1 = translateToImagePoint(new Point(Math.min(x0, x2), Math.min(y0, y2)));
				Point p2 = translateToImagePoint(new Point(Math.max(x0, x2), Math.max(y0, y2)));
				if (p1 == null || p2 == null)
					return;
				if (state.getPressedButton() == MouseEvent.BUTTON1)
					state.getCurrentTable().addColSpan(p1, p2);
				else if (state.getPressedButton() == MouseEvent.BUTTON3)
					state.getCurrentTable().addRowSpan(p1, p2);

				Point p = translateToScreenPoint(
						new Point(state.getCurrentTable().getX0(), state.getCurrentTable().getY0()));
				repaint(p.x, p.y, state.getCurrentTable().getWidth(), state.getCurrentTable().getHeight());
				break;

			}

			// reportMousePosition(e);

		}
		previousX = 0;
		previousY = 0;
	}

	public void setZoomToFit(boolean newValue) {
		zoomToFit = newValue;
		validate();
	}

	/**
	 * Simply calls {@link #paint(Graphics)} with the argument.
	 * 
	 * @param g Graphics context
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * @return the scaledWidth
	 */
	public int getScaledWidth() {
		return scaledWidth;
	}

	/**
	 * @param scaledWidth the scaledWidth to set
	 */
	public void setScaledWidth(int scaledWidth) {
		this.scaledWidth = scaledWidth;
	}

	/**
	 * @return the scaledHeight
	 */
	public int getScaledHeight() {
		return scaledHeight;
	}

	/**
	 * @param scaledHeight the scaledHeight to set
	 */
	public void setScaledHeight(int scaledHeight) {
		this.scaledHeight = scaledHeight;
	}

	/**
	 * @return the zoomToFit
	 */
	public boolean isZoomToFit() {
		return zoomToFit;
	}

	/**
	 * @param zoomFactorX the zoomFactorX to set
	 */
	public void setZoomFactorX(double zoomFactorX) {
		this.zoomFactorX = zoomFactorX;
	}

	/**
	 * @param zoomFactorY the zoomFactorY to set
	 */
	public void setZoomFactorY(double zoomFactorY) {
		this.zoomFactorY = zoomFactorY;
	}

	/**
	 * @return the mainGui
	 */
	public GTGui getMainGui() {
		return mainGui;
	}

	/**
	 * @param mainGui the mainGui to set
	 */
	public void setMainGui(GTGui mainGui) {
		this.mainGui = mainGui;
	}

}
