/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.dfki.trecs.groundtruth.data.GTCol;
import de.dfki.trecs.groundtruth.data.GTRow;
import de.dfki.trecs.groundtruth.data.GTTable;
import de.dfki.trecs.groundtruth.util.ImageInfo;

import net.sourceforge.jiu.data.PixelImage;

import net.sourceforge.jiu.gui.awt.ImageCreator;
import net.sourceforge.jiu.gui.awt.RGBA;

/**
 * The frame class for the Ground truth GUI responsible for showing the main
 * application.
 * 
 * @author Shahab
 *
 */
public class GTGui extends Frame implements ActionListener, ComponentListener, KeyListener {

	private GTMenuWrapper menuWrapper;
	private GTCanvas canvas;
	private ScrollPane scrollPane;
	private CanvasState state;
	private GTOperationProcessor processor;
	public static String APP_NAME = "Trecs Ground Truth GUI";

	private long startTime = 0;
	private long endTime = 0;

	public static final String LOG_USER = "de.dfki.trecs.groundtruth.gui.loguser";
	public static final String LOG_FILE = "de.dfki.trecs.groundtruth.gui.logfile";
	private PrintWriter logWriter = null;
	private boolean logUser = false;
	private JLabel statusBar;
	private JLabel helpBar;
	private JLabel infoBar;
	public JList<String> imgList; 
	
	private boolean previewMode = false;

	private String workDir = null;
	private int currentFileIndex = -1;
	private String files[] = null;

	public void startTimeLog() {
		if (isLogUser()) {

			startTime = System.currentTimeMillis();
			logWriter.println("Start groundtruth for: " + state.getFileName() + ":: Time: " + new Date(startTime));
		}
	}

	public void endTimeLog() {
		if (isLogUser()) {
			endTime = System.currentTimeMillis();
			logWriter.println("End groundtruth for: " + state.getFileName() + ":: Time: " + new Date(endTime));
			logWriter.println("Time Elapsed(ms): " + (endTime - startTime));
			logWriter.flush();
		}
	}

	public void finishLog() {
		if (isLogUser()) {
			logWriter.close();
		}
	}

	public void previewMode() {
		if (workDir != null) {
			File imgDir = new File(workDir);
			if (imgDir.isDirectory()) {
				files = imgDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith("png"))
							return true;
						else
							return false;
					}
				});
				setPreviewMode(true);

			}

		}

	}

	/**
	 * Builds up the gui and displays it.
	 */
	public GTGui(CanvasState state) {
		super(APP_NAME);

		setLayout(new BorderLayout());
		menuWrapper = new GTMenuWrapper(this);
		addComponentListener(this);
		addKeyListener(this);

		scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.getVAdjustable().setBlockIncrement(10);
		scrollPane.getVAdjustable().setUnitIncrement(4);

		this.state = state;
		canvas = new GTCanvas(scrollPane, state);
		canvas.setMainGui(this);
		processor = new GTOperationProcessor(this, state);
		statusBar = new JLabel("Loaded ");
		infoBar = new JLabel("");
		helpBar = new JLabel("");
		infoBar.setFont(new Font("Arial", Font.PLAIN, 15));
		statusBar.setFont(new Font("Arial", Font.BOLD, 20));
		helpBar.setFont(new Font("Arial", Font.PLAIN, 12));
		
        imgList = new JList<String>(state.getImageList());
        imgList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                	imgListCallback();
                }
            }
        });
        imgList.setVisibleRowCount(7);
        imgList.setPrototypeCellValue("2010PRODUCTS_03292019_2_Page_08");
        JScrollPane listPane = new JScrollPane(imgList);
        
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				processor.exit();
			}
		});

		setMenuBar(menuWrapper.getMenuBar());

		// add(canvas,BorderLayout.CENTER);
		JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());
		p.add(statusBar, BorderLayout.NORTH);
		p.add(helpBar, BorderLayout.WEST);
		add(listPane, BorderLayout.WEST);
		add(p, BorderLayout.NORTH);
		add(infoBar, BorderLayout.SOUTH);

		logUser = Boolean.parseBoolean(System.getProperty(GTGui.LOG_USER, "false"));
		if (isLogUser()) {
			String logfile = System.getProperty(GTGui.LOG_FILE, null);
			if (logfile == null)
				setLogUser(false);
			else
				try {
					logWriter = new PrintWriter(new FileWriter(new File(logfile)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		}

//		maximi
//		
//		
//		repaint();
//		setVisible(true);

	}

	private void imgListCallback() {
		if(state.getImageList().size() == 0 || this.imgList.getSelectedValue() == null)
			return;
    	closeImage();
		this.state.clear();
    	this.state.setFileName(this.imgList.getSelectedValue().toString());
    	loadImage();		
	}

	/**
	 * @return the logWriter
	 */
	public PrintWriter getLogWriter() {
		return logWriter;
	}

	/**
	 * @param logWriter the logWriter to set
	 */
	public void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	/**
	 * @return the logUser
	 */
	public boolean isLogUser() {
		return logUser;
	}

	/**
	 * @param logUser the logUser to set
	 */
	public void setLogUser(boolean logUser) {
		this.logUser = logUser;
	}

	public void markRowColSpan() {
		state.markRowColSpan();
		canvas.repaint();
		updateStatusBar();
	}
	
	public void markOrientation() {
		if(state.getTables().size()==0) {
			this.showWarningBox("Please mark TABLES before marking ORIENTATION.");
			this.markTable();
			return;
		}
		state.markOrientation();
		canvas.repaint();
		updateStatusBar();
	}

	private int showGroundTruthConfirmationDialog() {
		return JOptionPane.showConfirmDialog(this, "Ground Truth has been modified, do you want to save it?",
				"Ground Truth Save Confirmation Dialog", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	public void showWarningBox(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public void updateInfoBar(int x, int y, String info) {
		StringBuffer sb = new StringBuffer();
		
		PixelImage image = state.getImage();
		if (image == null) {
			sb.append("");
		} else {
			sb.append(ImageInfo.getImageInfo(image));
		}
		
		sb.append("(");
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(")");
		sb.append(info);
		infoBar.setText(sb.toString());
	}

	/**
	 * Maximize the frame on the desktop. There is no such function in the 1.1 AWT
	 * (was added in 1.4), so this class determines the screen size and sets the
	 * frame to be a little smaller than that (to make up for task bars etc.). So
	 * this is just a heuristical approach.
	 */
	public void maximize() {
		/*
		 * The following line:
		 * 
		 * does a nice maximization, but works only with Java 1.4+Z
		 */
		// setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		// canvas.setSize(width, height)
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (toolkit == null) {
			return;
		}
		Dimension screenSize = toolkit.getScreenSize();
		if (screenSize == null) {
			return;
		}
		int w = screenSize.width;
		int h = screenSize.height;
		int x = 20;
		int y = 80;
		setLocation(x / 2, y / 2);
		setSize((w - x) / 2, h - y); // modified for extended desktop

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		int index = menuWrapper.findIndex(o);
		if (index != -1) {
			processor.process(index);
		}

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * If there is an image loaded, forces a canvas redraw by calling repaint.
	 */
	public void updateCanvas() {
		if (canvas != null) {
			canvas.setInterpolation(state.getInterpolation());
			// canvas.revalidate();
			canvas.repaint();
		}
	}

	/**
	 * Removes the current canvas from the frame (if there is an image loaded) and
	 * creates a new canvas for the current image.
	 */
	public void updateImage() {
		PixelImage image = state.getImage();
		if (scrollPane != null) {
			remove(scrollPane);
		}
		if (image != null) {
			// state.zoomSetOriginalSize();
			Image awtImage = ImageCreator.convertToAwtImage(image, RGBA.DEFAULT_ALPHA);
			scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);

			scrollPane.getVAdjustable().setBlockIncrement(image.getHeight()/150);
			scrollPane.getVAdjustable().setUnitIncrement(image.getHeight()/100);

			canvas = new GTCanvas(scrollPane, state);
			canvas.setMainGui(this);
			canvas.setInterpolation(state.getInterpolation());
			canvas.setZoomToFit(state.getZoomToFit());
			canvas.setImage(awtImage);
			canvas.setZoomFactors(state.getZoomFactorX(), state.getZoomFactorY());

			canvas.computeZoomToFitSize();
			scrollPane.add(canvas);
			add(scrollPane);
		}
		updateStatusBar();
		updateTitle();
		validate();
		infoBar.setText("");
		// updateCanvas();
		menuWrapper.updateEnabled(processor);

	}

	public void close() {
		if (state.isModified()) {
			int answer = showGroundTruthConfirmationDialog();
			switch (answer) {
			case JOptionPane.YES_OPTION:
				processor.saveGroundTruthFile();
				break;
			case JOptionPane.NO_OPTION:

				break;
			}

		}
		state.clear();
		canvas.clear();
		updateImage();
	}

	/**
	 * Creates a description string for the current image and sets the status bar to
	 * that text.
	 */
	public void updateStatusBar() {
		String statusBarText = "";
		String helpBarText = "";
		String tab = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		switch (state.getMarkType()) {
		case CanvasState.MARK_TABLE:
			statusBarText = "Marking Tables";
			helpBarText = "<html>"
					+ "-Click and Drag mouse to mark table<br>"
					+ "-Click on a table to select it.<br>"
					+ tab + "-Drag any of the yellow-highlighted corners to resize the table.<br>"
					+ tab + "-Press DELETE key to remove the table.<br>"
					+ "<br>"
					+ "RIGHT ARROW KEY - Save & Move to next image.<br>"
					+ "LEFT ARROW KEY - Save & Move to previous image.<br>"
					+ "</html";
			break;
		case CanvasState.MARK_ROW_COL:
			statusBarText = "Marking Rows/Columns";
			helpBarText = "<html>"
					+ "-Click on a table to select it for marking Rows/Columns<br>"
					+ tab + "-Press LEFT Mouse Button inside the table to mark a ROW.<br>"
					+ tab + "-Press RIGHT Mouse Button inside the table to mark a COLUMN.<br>"
					+ tab + "-Click on an existing Row/Column to select it (Selected element will be highlighted yellow).<br>"
					+ tab + tab + "-Press DELETE key to remove the Row/Column.<br>"
					+ "<br>"
					+ "RIGHT ARROW KEY - Save & Move to next image.<br>"
					+ "LEFT ARROW KEY - Save & Move to previous image.<br>"
					+ "</html";
			break;
		case CanvasState.MARK_ROW_COL_SPAN:
			statusBarText = "Marking Rows/Columns Span";
			helpBarText = "<html>"
					+ "-Click on a table to select it for marking Row/Column spans<br>"
					+ tab + "-Press LEFT Mouse Button and drag mouse to mark a ROW Span (merge horizontally adjacent cells).<br>"
					+ tab + "-Press RIGHT Mouse Button and drag mouse to mark a COLUMN Span (merge vertically adjacent cells).<br>"
					+ tab + "-Click on an existing Row/Column Span to select it (Selected element will be highlighted yellow).<br>"
					+ tab + tab + "-Press DELETE key to remove the Row/Column Span (split up the cells).<br>"
					+ "<br>"
					+ "RIGHT ARROW KEY - Save & Move to next image.<br>"
					+ "LEFT ARROW KEY - Save & Move to previous image.<br>"
					+ "</html";
			break;
		case CanvasState.MARK_ORIENTATION:
			statusBarText = "Marking Orientation of Tables";
			helpBarText = "<html>"
					+ "-LEFT Click on a table to mark it as HORIZONTAL (be highlighted as RED)<br>"
					+ "-RIGHT Click on a table to mark it as VERTICAL (highlighted as GREEN)<br>"
					+ "-Unmarked tables will be highlighted as BLUE<br>"
					+ "<br>"
					+ "RIGHT ARROW KEY - Save & Move to next image.<br>"
					+ "LEFT ARROW KEY - Save & Move to previous image.<br>"
					+ "</html";
			break;

		}
		if (isPreviewMode())
			statusBarText = statusBarText + "PREVIEW MODE (Use 'n' for next, 'p' for previous image)";
		else if (state.isAutoLoadGT())
			statusBarText = statusBarText + "Loading ground truth automatically";
		setStatusBar(statusBarText);
		this.helpBar.setText(helpBarText);
	}

	public void setStatusBar(String text) {
		statusBar.setText(text);
	}

	/**
	 * Sets the frame's title bar to the application name, plus the file name of the
	 * currently loaded image file, plus the current zoom factor, plus an optional
	 * asterisk in case the image was modified but not yet saved.
	 */
	public void updateTitle() {
		StringBuffer sb = new StringBuffer(APP_NAME);
		String fileName = state.getFileName();
		if (fileName != null && fileName.length() > 0) {
			sb.append(" [");
			sb.append(fileName);
			if (state.getModified()) {
				sb.append('*');
			}
			sb.append(']');
		}
		if (state.getImage() != null) {
			double zoom = state.getZoomFactorX();
			int percent = (int) (zoom * 100.0);
			sb.append(' ');
			sb.append(Integer.toString(percent));
			sb.append('%');
		}
		if (state.getGroundTruthFile() != null) {
			sb.append(" [");
			sb.append(state.getGroundTruthFile());
			sb.append("]");
		}
		if (isPreviewMode() && files != null) {
			sb.append(" [");
			sb.append("Showing " + (currentFileIndex + 1) + " of " + files.length + " images");
			sb.append(" ]");
		}
		setTitle(sb.toString());
	}

	/**
	 * If an image is currently displayed, zoom in one level.
	 */
	public void zoomIn() {
		if (canvas != null && !state.isMaximumZoom()) {

			state.zoomIn();
			canvas.setZoomFactors(state.getZoomFactorX(), state.getZoomFactorY());
			updateTitle();
			menuWrapper.updateEnabled(processor);
		}
	}

	/**
	 * If an image is currently displayed, zoom out one level.
	 */
	public void zoomOut() {
		if (canvas != null && !state.isMinimumZoom()) {
			state.zoomOut();
			canvas.setZoomFactors(state.getZoomFactorX(), state.getZoomFactorY());
			updateTitle();
			menuWrapper.updateEnabled(processor);
		}
	}

	public void zoomOriginal() {
		if (canvas != null) {
			state.zoomSetOriginalSize();
			canvas.setZoomFactors(state.getZoomFactorX(), state.getZoomFactorY());
			updateTitle();
			menuWrapper.updateEnabled(processor);
		}
	}

	public void markTable() {
		state.markTable();
		setCrosshairCursor();
		updateStatusBar();
		canvas.repaint();

	}

	@Override
	public void keyPressed(KeyEvent e) {
//		if (previewMode) {
//			switch (e.getKeyChar()) {
//			case 'n':
//			case 'N':
//				nextImagePreview();
//				break;
//			case 'p':
//			case 'P':
//				previousImagePreview();
//				break;
//			}
//		}
//		else {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
			GTTable table = state.getCurrentTable();
			if (table!=null) {
				if(state.getMarkType() == CanvasState.MARK_TABLE) {
					state.removeGTTable(table);
					state.setCurrentTable(null);
					canvas.repaint();						
				}
				else if(state.getCurrentElement()!=null){
					if (state.getMarkType() == CanvasState.MARK_ROW_COL) {
						table.remove(state.getCurrentElement());
						state.setCurrentElement(null);
					}
					else if (state.getMarkType() == CanvasState.MARK_ROW_COL_SPAN) {
						table.removeSpan(state.getCurrentElement());
						state.setCurrentElement(null);
					}
					canvas.repaint();
				}
			}
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
//			this.closeImage();
			String fn = this.changeImage(e.getKeyCode());
			if(fn != null)
				this.imgList.setSelectedIndex(state.getImageList().indexOf(fn));
//				this.loadImage();
			break;
//			}
		}
	}

	public void closeImage() {
		if(state.getImageDirectory() == null) {
			this.showWarningBox("Select image directory.");
			processor.openImageDirectory(false);
		}
		if(state.getXmlDirectory() == null) {
			this.showWarningBox("Select ground truth directory.");
			processor.openGroundTruthDirectory();
		}
		if(state.getFileName() != null && new File(state.getImageDirectory(), state.getFileName()).exists()) {
			this.processor.saveGroundTruthFile();
		}		
	}
	
	public void loadImage() {
		if(state.getFileName() == null)
			return;
		
		this.processor.fileOpen(new File(state.getImageDirectory(), state.getFileName()).getAbsolutePath());
		this.processor.openGroundTruthFile(
				new File(
				state.getXmlDirectory(), 
				state.getFileName().substring(0,state.getFileName().lastIndexOf(".")) + ".xml"
				).getAbsolutePath()
				);

		this.markTable();
		this.canvas.requestFocusInWindow();
	}
	
	public String changeImage(int key) {
		String fn = state.getFileName();
		String fn2 = null;
		if(key == KeyEvent.VK_LEFT)
			fn2 = state.previousImage();
		else if(key == KeyEvent.VK_RIGHT)
			fn2 = state.nextImage();
		
		if(fn2 == null)
			return null;

		if(fn2.equals(fn)){
			if(key == KeyEvent.VK_LEFT)
				this.showWarningBox("First Image");
			else if(key == KeyEvent.VK_RIGHT)
				this.showWarningBox("Last Image");
			return null;
		}
		return fn2;
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * @return the previewMode
	 */
	public boolean isPreviewMode() {
		return previewMode;
	}

	/**
	 * @param previewMode the previewMode to set
	 */
	public void setPreviewMode(boolean previewMode) {
		this.previewMode = previewMode;
	}

	/**
	 * @return the workDir
	 */
	public String getWorkDir() {
		return workDir;
	}

	/**
	 * @param workDir the workDir to set
	 */
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public void markRowColumns() {
		state.markRowColumns();
		setCrosshairCursor();
		updateStatusBar();
		canvas.repaint();
	}

	private void setDefaultCursor() {
		Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(cursor);
	}

	private void setCrosshairCursor() {
		Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		setCursor(cursor);
	}

	public static void main(String args[]) {
		CanvasState state = new CanvasState();
		GTGui gui = new GTGui(state);
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("auto"))
				state.setAutoLoadGT(true);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("preview")) {
				gui.setWorkDir(args[1]);
				state.setImageDirectory(args[1]);
				state.setAutoLoadGT(true);
				gui.previewMode();
//				gui.nextImagePreview();

			}
		}
		gui.maximize();
		gui.repaint();
		gui.setVisible(true);
	}

	/**
	 * @return the canvas
	 */
	public GTCanvas getCanvas() {
		return canvas;
	}

	/**
	 * @param canvas the canvas to set
	 */
	public void setCanvas(GTCanvas canvas) {
		this.canvas = canvas;
	}
}
