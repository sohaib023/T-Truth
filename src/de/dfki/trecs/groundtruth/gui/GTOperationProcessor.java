/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

/**
 * The main Ground Truth Operation processor class which performs different operations in GUI.
 * @author Shahab
 *
 */
public class GTOperationProcessor extends OperationProcessor{

	private GTGui frame;
	
	
	
	public GTOperationProcessor(GTGui frame,CanvasState state) {
		super(state);
		this.frame = frame;
	}
	@Override
	public void fileClose() {
		frame.close();
		
	}
	@Override
	public void assignColors() {
		state.evaluateTableCells();
		frame.getCanvas().repaint();
		
	}
	@Override
	public void saveGroundTruthFile() {
		File file = null;
		FileDialog fd = new FileDialog(frame,"Save Ground Truth File",FileDialog.SAVE);
		String dir = state.getCurrentDirectory();
		if (dir!= null){
			fd.setDirectory(dir);
		}
		String name = state.getFileName();
		if (name != null) {
//			fd.setName("name");
			fd.setFile(name.substring(0, name.lastIndexOf('.')) + ".xml");
			
			fd.setVisible(true);
		}
		
//	
//		String fn = state.getFileName();
//		fn = fn.substring(fn.lastIndexOf(File.separator)+1, fn.lastIndexOf('.'));
//		fn = fn+".xml";
//		fd.setFile(fn);
		fd.setVisible(true);
		
		
		String dn = fd.getDirectory();
		String fn = fd.getFile();
		
		if (fn == null || dn == null)
		{
			return;
		}
		file = new File(dn, fn);
		state.saveGroundTruthFile(file);
		frame.endTimeLog();
	}
	
	@Override
	public void fileOpen(String uri) {
		
		frame.close();
		File file = null;
		if (uri==null){
			//show the file open dialog
			FileDialog fd = new FileDialog(frame,"Open Image File",FileDialog.LOAD);
			
			String dir = state.getCurrentDirectory();
			if (dir != null)
			{
				fd.setDirectory(dir);
			}
			//fd.setFilenameFilter(ImageLoader.createFilenameFilter());
			fd.setVisible(true);
			fd.setMode(FileDialog.LOAD);
			String fn = fd.getFile();
			String dn = fd.getDirectory();
			
			if (fn == null || dn == null)
			{
				return;
			}
			state.setCurrentDirectory(dn);
			file = new File(dn, fn);
			state.setFileName(file.getAbsolutePath());
			
			
		//	state.setZoomToFit(true);
			
		}
		else 
			file = new File(uri);
		
		/**
		 * code for checking if autoload is set 
		 * 
		 */
		if (state.isAutoLoadGT()){
			String fn = file.getName();
			String dn = file.getParent();
			String gtFileName = fn.substring(0,fn.indexOf("."));
			gtFileName = gtFileName + ".xml";
			File gtFile = new File(dn,gtFileName);
			if (gtFile.exists()){
				openGroundTruthFile(gtFile.getAbsolutePath());
			}
			
		}
		frame.startTimeLog();
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(file.getAbsolutePath());
		state.setImage(image,false);
		
		frame.updateImage();
		
		//System.out.println("Call done");
	}
	@Override
	public void openGroundTruthFile(String uri) {
		// TODO Auto-generated method stub
	//	String gtfilename = state.getFileName();
//		gtfilename = gtfilename.substring(0,gtfilename.lastIndexOf('.'))+".xml";
		
		File gtfile = null;
		if (uri == null){
		if (state.getFileName()==null){
			frame.showWarningBox("No Image loaded, load image first");
			return;
		}
			
		FileDialog fd = new FileDialog(frame,"Open Ground Truth File",FileDialog.LOAD);
		
		String dir = state.getCurrentDirectory();
		if (dir != null)
		{
			fd.setDirectory(dir);
		}
		//fd.setFilenameFilter(ImageLoader.createFilenameFilter());
		fd.setVisible(true);
		fd.setMode(FileDialog.LOAD);
		String fn = fd.getFile();
		String dn = fd.getDirectory();
		
		if (fn == null || dn == null)
		{
			return;
		}
		
		
		gtfile = new File(dn, fn);
		}
		else
			gtfile = new File(uri);
		
	//	gtfile.getAbsolutePath().substring(beginIndex, endIndex)
		if (gtfile.exists() && gtfile.getAbsolutePath().endsWith("xml")){
			state.loadGroundTruthFile(gtfile);
			state.setGroundTruthFile(gtfile.getAbsolutePath());
			state.setInitialCellsMarked(true);
			//System.out.println("laoded groundtruthfile");
			frame.getCanvas().repaint();
			frame.updateTitle();
		}	
		else
			frame.showWarningBox("Can't load the ground truth file: "+gtfile.getAbsolutePath());
	}
	@Override
	public void exit() {
		fileClose();
		frame.finishLog();
		System.exit(0);
		
	}
	@Override
	public void redo() {
		// TODO Auto-generated method stub
		state.redo();
		frame.getCanvas().repaint();
	}
	@Override
	public void undo() {
		// TODO Auto-generated method stub
		state.undo();
		frame.getCanvas().repaint();
	}
	@Override
	public void fileSaveAs() {
		if (state.getRgb48Image()==null){
			return;
			
		}
		else 
		{
			File file = null;
			FileDialog fd = new FileDialog(frame,"Save Ground Truth Image File",FileDialog.SAVE);
			String dir = state.getCurrentDirectory();
			if (dir!= null){
				fd.setDirectory(dir);
			}
		
//			String fn = state.getFileName();
//			fn = fn.substring(fn.lastIndexOf(File.separator)+1, fn.indexOf('.'));
//			fn = fn+".png";
//			fd.setFile(fn);
			fd.setVisible(true);
			
			
			String dn = fd.getDirectory();
			String fn = fd.getFile();
			
			if (fn == null || dn == null)
			{
				return;
			}
			file = new File(dn, fn);
			state.saveGTImage(file);
		}
	}
	
	@Override
	public void markRowColSpan() {
		// TODO Auto-generated method stub
		frame.markRowColSpan();
	}
	@Override
	public void zoomIn() {
		
		frame.zoomIn();
		
	}
	@Override
	public void zoomOut() {
		
		frame.zoomOut();
		
	}
	@Override
	public void zoomToFit() {
		
		frame.zoomOriginal();
	}
	
	@Override
	public void markTable() {
		frame.markTable();
		
	}
	@Override
	public void markRowColumns(){
		frame.markRowColumns();
	}
	@Override
	public void markOrientation() {
		frame.markOrientation();
	}
}
