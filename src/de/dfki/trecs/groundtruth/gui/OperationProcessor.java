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
	
	public abstract void openImageDirectory(boolean moveNext);
	
	/**
	 * close the file currently open in the applicaiton.
	 */
	public abstract void fileClose();
	
	public abstract void zoomIn();
	
	public abstract void zoomOut();
	
	public abstract void zoomToFit();

	public abstract void markTable();
	
	public abstract void markOrientation();
	/**
	 * save file as currently open in the application.
	 */
	public abstract void fileSaveAs();
	
	public abstract void saveGroundTruthFile();

	public abstract void openGroundTruthDirectory();
	public abstract void openGroundTruthFile(String uri);
	public abstract void undo();
	public abstract void redo();
	public abstract void exit();
	public abstract void markRowColSpan();
	
	public abstract void markRowColumns();
	public void process(int index){
		switch (index){
		case MenuIndexConstants.FILE_OPEN_DIR:
			openImageDirectory(true);
			break;
		case MenuIndexConstants.OPEN_GT_DIR:
			openGroundTruthDirectory();
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
		case MenuIndexConstants.MARK_ORIENTATION:
			markOrientation();
			break;
		case MenuIndexConstants.SAVE_GT_FILE:
			saveGroundTruthFile();
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
