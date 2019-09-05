/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

/**
 * @author Shahab
 *
 */
public abstract class OperationProcessor implements MenuIndexConstants{
	
	protected CanvasState state;
	
	/**
	 * Creates an object with the Canvas state for later use. 
	 * @param state
	 */
	public OperationProcessor(CanvasState state){
		this.state = state;
	}
	
	/**
	 * loads an image in the application
	 */
	public abstract void fileOpen(String uri);
	
	/**
	 * close the file currently open in the applicaiton.
	 */
	public abstract void fileClose();
	
	public abstract void zoomIn();
	
	public abstract void zoomOut();
	
	public abstract void zoomToFit();
	
	public abstract void markTable();
	/**
	 * save file as currently open in the application.
	 */
	public abstract void fileSaveAs();
	
	public abstract void saveGroundTruthFile();
	
	public abstract void openGroundTruthFile(String uri);
	public abstract void undo();
	public abstract void redo();
	public abstract void assignColors();
	public abstract void exit();
	public abstract void markRowColSpan();
	
	public abstract void markRowColumns();
	public void process(int index){
		switch (index){
		case MenuIndexConstants.FILE_OPEN:
			fileOpen(null);
			break;
		case MenuIndexConstants.OPEN_GT_FILE:
			openGroundTruthFile(null);
			break;
		case MenuIndexConstants.FILE_CLOSE:
			fileClose();
			break;
//		case MenuIndexConstants.FILE_SAVE_AS:
//			fileSaveAs();
//			break;
		case MenuIndexConstants.ZOOM_IN:
			zoomIn();
			break;
		case MenuIndexConstants.ZOOM_OUT:
			zoomOut();
			break;
		case MenuIndexConstants.ZOOM_TO_FIT:
			zoomToFit();
			break;
		case MenuIndexConstants.MARK_TABLE:
			markTable();
			break;
		case MenuIndexConstants.UNDO:
			undo();
			break;
		case MenuIndexConstants.REDO:
			redo();
			break;
		case MenuIndexConstants.MARK_ROW_COL:
			markRowColumns();
			break;
		case MenuIndexConstants.SAVE_GT_FILE:
			saveGroundTruthFile();
			break;
		case MenuIndexConstants.ASSIGN_COLORS:
			assignColors();
			break;
		case MenuIndexConstants.MARK_ROW_COL_SPAN:
			markRowColSpan();
			break;
		case MenuIndexConstants.EXIT:
			exit();
			break;
		
			
		}
	}
	

}
