/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import javax.swing.JFileChooser;

import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

/**index
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
	public void saveGroundTruthFile() {	
		String dn = state.getXmlDirectory();
		String fn = state.getFileName().substring(0, state.getFileName().lastIndexOf('.')) + ".xml";
		
		if (fn == null || dn == null || state.isModified() == false)
		{
			return;
		}
		File file = new File(dn, fn);
		state.saveGroundTruthFile(file);
		frame.endTimeLog();
	}
	
	@Override
	public void openImageDirectory(boolean moveNext) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open Image Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File imgDir = chooser.getSelectedFile();
			if(imgDir.isDirectory())
				state.setImageDirectory(imgDir.getAbsolutePath());
		}				
		if(moveNext) {
			this.frame.changeImage(KeyEvent.VK_RIGHT);
			this.frame.loadImage();
		}
	}
	
	@Override
	public void fileOpen(String uri) {
		
		frame.close();
		File file = null;
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

	//	gtfile.getAbsolutePath().substring(beginIndex, endIndex)
		if (file.exists()){
			frame.startTimeLog();
			PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(file.getAbsolutePath());
			state.setImage(image,false);
			
			frame.updateImage();
		}	
		else
			frame.showWarningBox("Can't load the image file: " + file.getAbsolutePath());
		
		//System.out.println("Call done");
	}
	
	@Override
	public void openGroundTruthDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open Ground Truth Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File xmlDir = chooser.getSelectedFile();
			if(xmlDir.isDirectory()){
				state.setXmlDirectory(xmlDir.getAbsolutePath());
				if(state.getImageDirectory() != null &&
						state.getFileName() != null &&
						new File(state.getImageDirectory(), state.getFileName()).exists())
					this.openGroundTruthFile(
							new File(
							state.getXmlDirectory(), 
							state.getFileName().substring(0,state.getFileName().lastIndexOf(".")) + ".xml"
							).getAbsolutePath()
							);
			}
		}
	}
	
	@Override
	public void openGroundTruthFile(String uri) {
		File gtfile = null;
		gtfile = new File(uri);
		
		if (gtfile.exists() && gtfile.getAbsolutePath().endsWith("xml")){
			state.loadGroundTruthFile(gtfile);
			state.setGroundTruthFile(gtfile.getAbsolutePath());
			//System.out.println("laoded groundtruthfile");
			frame.getCanvas().repaint();
			frame.updateTitle();
		}	
		else
			frame.showWarningBox("Can't load the ground truth file: "+gtfile.getAbsolutePath());
		state.setModified(false);
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
			String dir = state.getImageDirectory();
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
