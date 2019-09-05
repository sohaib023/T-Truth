/**
 * 
 */
package de.dfki.trecs.evaluation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import de.dfki.trecs.groundtruth.color.ColorEnumerator;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;

import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

/**
 * This class performs the pixel based segment evaluation and publish 
 * results in SegmentEvaluationResult class.
 * @author shahab
 *
 */
public class SegmentEvaluation {

	private int [][] colorMatrix = null;
	private String modelImageName = null;
	private String resultImageName = null;
	private String rootOutDir = null;
	
	private static final String outDir  = "de.dfki.trecs.evaluation.segmentevaluation.outdir";
	private static final String outFile = "de.dfki.trecs.evaluation.segmentevaluation.outfile";
	
	
	private RGB48Image modelImage = null;
	private RGB48Image resultImage = null;
	
	
	
	private ColorEnumerator modelEnumerator = null;
	private ColorEnumerator resultEnumerator = null;
	
	private SegmentEvaluationResult evalResult = null;
	
	private static ColorModel16Bit fineColor = ColorModel16Bit.ColorModel8Bit(0, 255, 0);
	private static ColorModel16Bit missColor = ColorModel16Bit.ColorModel8Bit(255, 0, 0);
	private static ColorModel16Bit falarmColor = ColorModel16Bit.ColorModel8Bit(0, 0, 255);
	private static ColorModel16Bit undersegColor = ColorModel16Bit.ColorModel8Bit(0x7d, 0x05, 0x3f);
	private static ColorModel16Bit oversegColor =  ColorModel16Bit.ColorModel8Bit(0x8d, 0x38, 0xc9);
	
	public static final double RELATIVE_THRESHOLD = 0.1;
	public static final double ABSOLUTE_THRESHOLD = 100;
	
	private static Logger log() {return Logger.getLogger(SegmentEvaluation.class.getName());}
	public SegmentEvaluation(String modelImageName,String resultImageName){
		setModelImageName(modelImageName);
		setResultImageName(resultImageName);
		init();
	}
	
	private void init(){
		modelImage = (RGB48Image)ToolkitLoader.loadViaToolkitOrCodecs(modelImageName);
		resultImage = (RGB48Image)ToolkitLoader.loadViaToolkitOrCodecs(resultImageName);
		rootOutDir = System.getProperty(outDir,null);
		
		if (modelImage == null || resultImage == null){
			log().severe("Unable to load images");
			exit(-1);
		}
		
		// images loaded successfully, lets check for image dimensions.
		
		if ((modelImage.getWidth() != resultImage.getWidth()) &&
				(modelImage.getHeight() != resultImage.getHeight())){
			log().severe("Dimensions of images donot match");
			exit(-2);
		}
		
		evaluate(ColorEnumerator.TABLE_ENUMERATOR);
		evaluate(ColorEnumerator.ROW_ENUMERATOR);
		evaluate(ColorEnumerator.COL_ENUMERATOR);
		evaluate(ColorEnumerator.CELL_ENUMERATOR);
		
		evaluate(ColorEnumerator.ROW_SPAN_ENUMERATOR);
		evaluate(ColorEnumerator.COL_SPAN_ENUMERATOR);
		evaluate(ColorEnumerator.ROW_COL_SPAN_ENUMERATOR);
//		
		
		//enumerate colors in model and result image.
//		modelEnumerator = ColorEnumerator.enumerateColorsInImage(modelImage,ColorEnumerator.CELL_ENUMERATOR);
//		resultEnumerator = ColorEnumerator.enumerateColorsInImage(resultImage,ColorEnumerator.CELL_ENUMERATOR);
//		
//	
//		// create other datastructures;
//		
//		evalResult = new SegmentEvaluationResult();
//		
//		colorMatrix = new int[modelEnumerator.getNumberOfLabels()][resultEnumerator.getNumberOfLabels()];
//		
//		for (int i=0;i<colorMatrix.length;i++)
//			Arrays.fill(colorMatrix[i], 0);
//		
//		evaluateColorMatrix();
//		performEvaluation();
//	
//		System.out.println("Cell Evaluation Result");
//		evalResult.print();
//		
//		saveSegmentationErrors(evalResult, "/tmp/model_cell_out.png", "/tmp/result_cell_out.png");
//		
//		modelEnumerator = ColorEnumerator.enumerateColorsInImage(modelImage, ColorEnumerator.ROW_ENUMERATOR);
//		resultEnumerator = ColorEnumerator.enumerateColorsInImage(resultImage, ColorEnumerator.ROW_ENUMERATOR);
//		
//		evalResult = new SegmentEvaluationResult();
//		colorMatrix = new int[modelEnumerator.getNumberOfLabels()][resultEnumerator.getNumberOfLabels()];
//		
//		for (int i=0;i<colorMatrix.length;i++)
//			Arrays.fill(colorMatrix[i], 0);
//		
//		evaluateColorMatrix();
//		performEvaluation();
//		saveSegmentationErrors(evalResult, "/tmp/model_row_out.png","/tmp/result_row_out.png");
//	
//		System.out.println("Row Evaluation Result");
//		evalResult.print();
//		
//
//		modelEnumerator = ColorEnumerator.enumerateColorsInImage(modelImage, ColorEnumerator.COL_ENUMERATOR);
//		resultEnumerator = ColorEnumerator.enumerateColorsInImage(resultImage, ColorEnumerator.COL_ENUMERATOR);
//		
//		evalResult = new SegmentEvaluationResult();
//		colorMatrix = new int[modelEnumerator.getNumberOfLabels()][resultEnumerator.getNumberOfLabels()];
//		
//		for (int i=0;i<colorMatrix.length;i++)
//			Arrays.fill(colorMatrix[i], 0);
//		
//		evaluateColorMatrix();
//		performEvaluation();
//	
//		System.out.println("Col Evaluation Result");
//		evalResult.print();
//		saveSegmentationErrors(evalResult, "/tmp/model_col_out.png","/tmp/result_col_out.png");
	}
	
	public void saveImage(PixelImage image,String imageName) throws Exception{
		PNGCodec codec = new PNGCodec();
		codec.setImage(image);
		codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(imageName))));
		codec.process();
		codec.close();
	}
	private void evaluate(int enumerator){
		modelEnumerator = ColorEnumerator.enumerateColorsInImage(modelImage, enumerator);
		resultEnumerator = ColorEnumerator.enumerateColorsInImage(resultImage, enumerator);
		
		evalResult = new SegmentEvaluationResult();
		colorMatrix = new int[modelEnumerator.getNumberOfLabels()][resultEnumerator.getNumberOfLabels()];
		
		for (int i=0;i<colorMatrix.length;i++)
			Arrays.fill(colorMatrix[i], 0);
		
		evaluateColorMatrix();
		performEvaluation();
		
		String tmp = new File(modelImageName).getName();
		StringBuffer imgName = new StringBuffer(tmp.substring(0, tmp.indexOf('.')));
		StringBuffer resultName = new StringBuffer(imgName.toString());
		String outfile = System.getProperty(outFile,"null");
		StringBuffer evalName = new StringBuffer();
		if (outFile!=null)
			evalName = new StringBuffer(outfile);
		switch(enumerator){
		case ColorEnumerator.TABLE_ENUMERATOR:
			imgName.append("_table_model.png");
			resultName.append("_table_result.png");
			evalName.append("_table.out");
			break;
		case ColorEnumerator.CELL_ENUMERATOR:
			imgName.append("_cell_model.png");
			resultName.append("_cell_result.png");
			evalName.append("_cell.out");
			break;
		case ColorEnumerator.ROW_ENUMERATOR:
			imgName.append("_row_model.png");
			resultName.append("_row_result.png");
			evalName.append("_row.out");
			break;
		case ColorEnumerator.ROW_SPAN_ENUMERATOR:
			imgName.append("_rowspan_model.png");
			resultName.append("_rowspan_result.png");
			evalName.append("_rowspan.out");
			break;
		case ColorEnumerator.COL_SPAN_ENUMERATOR:
			imgName.append("_colspan_model.png");
			resultName.append("_colspan_result.png");
			evalName.append("_colspan.out");
			break;
		case ColorEnumerator.ROW_COL_SPAN_ENUMERATOR:
			imgName.append("_rowcolspan_model.png");
			resultName.append("_rowcolspan_result.png");
			evalName.append("_rowcolspan.out");
			break;
		case ColorEnumerator.COL_ENUMERATOR:
			imgName.append("_col_model.png");
			resultName.append("_col_result.png");
			evalName.append("_col.out");
			break;
		}
		if (rootOutDir!= null){
			saveSegmentationErrors(evalResult,new File(rootOutDir,imgName.toString()).getAbsolutePath(),new File(rootOutDir,resultName.toString()).getAbsolutePath());
			saveEvaluationResult(evalResult,evalName.toString());
		}
	}
	
	public void saveEvaluationResult(SegmentEvaluationResult eval,String out_file){
		File f = new File(rootOutDir,out_file);
		StringBuffer resultLine = new StringBuffer();
		resultLine.append(new File(modelImageName).getName());
		resultLine.append(";"+new File(resultImageName).getName());
		resultLine.append(";"+eval.getFileString());
		resultLine.append(";");
		try {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f,true)));
		writer.println(resultLine.toString());
		writer.close();
		}
		catch (Exception ex){
			System.out.println("Cant write to aan outfile");
		}
	}
	public void saveSegmentationErrors(SegmentEvaluationResult eval,String model_out,String result_out){
		
		int num_over = eval.getiOverSeg().size();
		int num_under = eval.getiUnderSeg().size();
		int num_miss = eval.getiMissed().size();
		int num_falarm = eval.getiFAlarm().size();
		RGB48Image modelImageCopy = (RGB48Image)modelImage.createCopy();
		RGB48Image resultImageCopy = (RGB48Image)resultImage.createCopy();
		int width = modelImageCopy.getWidth();
		int height = modelImageCopy.getHeight();
		if (modelImageCopy!=null && resultImageCopy!=null && modelEnumerator!=null && resultEnumerator!=null){
			for (int i=0;i<height;i++)
				for (int j=0;j<width;j++){
					int value = modelEnumerator.getNormalizedImage()[i*width+j];
					for (int x=0;x<num_over;x++){
						if (value == eval.getiOverSeg().get(x)){
							modelImageCopy.putSample(2, j, i,oversegColor.getB());
							modelImageCopy.putSample(1, j, i, oversegColor.getG());
							modelImageCopy.putSample(0, j, i, oversegColor.getR());
							break;
						}
					}
					for (int y=0;y<num_miss;y++){
						if (value == eval.getiMissed().get(y)){
							modelImageCopy.putSample(1, j, i, missColor.getG());
							modelImageCopy.putSample(2, j, i, missColor.getB());
							modelImageCopy.putSample(0, j, i, missColor.getR());
							break;
						}
					}
					if (value >2 && !eval.getiOverSeg().contains(value) && !eval.getiMissed().contains(value)){
						modelImageCopy.putSample(1, j, i, fineColor.getG());
						modelImageCopy.putSample(2, j, i, fineColor.getB());
						modelImageCopy.putSample(0, j, i, fineColor.getR());
						
					}
				}
			
			for (int i=0;i<height;i++)
				for (int j=0;j<width;j++){
					int value = resultEnumerator.getNormalizedImage()[i*width+j];
					for (int x=0;x<num_under;x++){
						if (value == eval.getiUnderSeg().get(x)){
							resultImageCopy.putSample(0, j,i,undersegColor.getR());
							resultImageCopy.putSample(1,j,i,undersegColor.getG());
							resultImageCopy.putSample(2, j,i,undersegColor.getB());
							break;
						}
					}
					for (int y=0;y<num_falarm;y++){
						if (value == eval.getiFAlarm().get(y)){
							resultImageCopy.putSample(0, j,i,falarmColor.getR());
							resultImageCopy.putSample(1,j,i,falarmColor.getG());
							resultImageCopy.putSample(2, j,i,falarmColor.getB());
							break;
						}
					}
					if (value >2 &&  !eval.getiUnderSeg().contains(value) && !eval.getiFAlarm().contains(value)){
						resultImageCopy.putSample(1, j, i, fineColor.getG());
						resultImageCopy.putSample(2, j, i, fineColor.getB());
						resultImageCopy.putSample(0, j, i, fineColor.getR());
						
					}
				}
		}
		try{
			saveImage(modelImageCopy, model_out);
			saveImage(resultImageCopy,result_out);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	private void calculateCorrect() {
		int startIndex = 3;
		int nrows = colorMatrix.length;
		int ncols = colorMatrix[nrows-1].length;
		
		int rowsum[] = new int[nrows];
		int colsum[] = new int[ncols];
		Arrays.fill(rowsum, 0);
		Arrays.fill(colsum, 0);
		
	//	printColorMatrix();
		for (int i=0;i<ncols;i++){
			for (int j=0;j<nrows;j++){
				colsum[i]+= colorMatrix[j][i];
			}
		}
		for (int j=0;j<nrows;j++){
			for (int i=0;i<ncols;i++){
				rowsum[j]+= colorMatrix[j][i];
			}
		}
		
		for (int i=startIndex;i<ncols;i++){
			for (int j=startIndex;j<nrows;j++){
				if (colorMatrix[j][i] == 0 )
					continue;
				
				if (
						(colorMatrix[j][i] >= (int)((1-RELATIVE_THRESHOLD)*(colsum[i]))) 
						&& (colorMatrix[j][i] >= (int)((1-RELATIVE_THRESHOLD)*(rowsum[j]))) 
						//&& colorMatrix[i][j] >= ABSOLUTE_THRESHOLD 
				   ){
					
						evalResult.incrementCorrect();
				}
				else{ 
					if (
//						((colorMatrix[j][i] < (int)((1-RELATIVE_THRESHOLD)*(colsum[i]))) 
//					|| (colorMatrix[j][i] < (int)((1-RELATIVE_THRESHOLD)*(rowsum[j]))))
					 (colorMatrix[j][i] > (int)((RELATIVE_THRESHOLD)*(colsum[i]))) 
					&& (colorMatrix[j][i] > (int)((RELATIVE_THRESHOLD)*(rowsum[j])))
						)
				{
					boolean partialEdges = false;
					for (int n=startIndex;n<ncols;n++){
						if (n==i)
							continue;
						if (((double)colorMatrix[j][n])/colsum[n]>=RELATIVE_THRESHOLD)
							partialEdges = true;
					}
					for (int n=startIndex;n<nrows;n++){
						if (n==j)
							continue;
						if (((double)colorMatrix[n][i])/rowsum[n]>=RELATIVE_THRESHOLD)
							partialEdges = true;
					}
					if (!partialEdges)
						evalResult.incrementPartialMatches();
				}
				}
			}
		}
	}
	private void performEvaluation(){
		
		int startIndex = 3;
		for (int i=startIndex;i<colorMatrix.length;i++){
			int total = 0;
			for (int j=startIndex;j<colorMatrix[i].length;j++){
				total += colorMatrix[i][j];
				
			}
			if (total == 0){
				evalResult.incrementMCount();
				evalResult.incrementMOver();
				evalResult.incrementMissed();
				evalResult.pushIMissed(i);
				continue;
			}
			
			evalResult.incrementMCount();
			int splits = 0;
			for (int j=startIndex;j<colorMatrix[i].length;j++){
				if (colorMatrix[i][j] == 0) continue;
				double frac = colorMatrix[i][j] * 1.0 / total;
				if (frac>RELATIVE_THRESHOLD){
					evalResult.incrementMOver();
					splits++;
				}
			}
			if (splits>1){
			
				evalResult.pushIOverSeg(i);
			}
		}
		/**
		 * From the Image side now
		 */
		for (int j=startIndex;j<colorMatrix[0].length;j++){
			int total = 0;
			for (int i=startIndex;i<colorMatrix.length;i++ ){
				total += colorMatrix[i][j];
			}
			
			if (total == 0){
				evalResult.incrementICount();
				evalResult.incrementiOver();
				evalResult.incrementFalseAlarms();
				evalResult.pushIFAlarm(j);
				continue;
			}
			
			evalResult.incrementICount();
			int splits = 0;
			for (int i=startIndex;i<colorMatrix.length;i++){
				if (colorMatrix[i][j] == 0) continue;
				double frac = colorMatrix[i][j]*1.0/total;
				if (frac>RELATIVE_THRESHOLD){
					evalResult.incrementiOver();
					splits++;
				}
			}
			if (splits>1){
			
				evalResult.pushIUnderSeg(j);
			}
		}
		calculateCorrect();
	}
	
	private void evaluateColorMatrix(){
		
		short model1d[] = modelEnumerator.getNormalizedImage();
		short result1d[] = resultEnumerator.getNormalizedImage();
		
		if (model1d.length != result1d.length){
			log().severe("Model and Result Normalized Images don't match in size");
			exit(-1);
		}
		
		int two_count = 0;
		int thr_count =0;
		for (int i=0;i<model1d.length;i++){
			short mval = model1d[i];
			short rval = result1d[i];
//			if (mval == 2) two_count++;
//			if (mval == 3) thr_count++;
			colorMatrix[mval][rval]++;
		}
		
//		System.out.println("Two: "+two_count+"; Three: "+thr_count);
	}

	private void printColorMatrix(){
		for (int i=3;i<colorMatrix.length;i++){
			for (int j=3;j<colorMatrix[i].length;j++){
				System.out.print(colorMatrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
	public void exit(int status){
		if (status == 0){
			log().info("Done Segment Evaluation");
		}
		else
		{
			log().severe("Segment Evaluation Failed. Exit with status code: "+status);
		}
		System.exit(status);
	}
	/**
	 * @return the colorMatrix
	 */
	public int[][] getColorMatrix() {
		return colorMatrix;
	}

	/**
	 * @param colorMatrix the colorMatrix to set
	 */
	public void setColorMatrix(int[][] colorMatrix) {
		this.colorMatrix = colorMatrix;
	}

	/**
	 * @return the modelImageName
	 */
	public String getModelImageName() {
		return modelImageName;
	}

	/**
	 * @param modelImageName the modelImageName to set
	 */
	public void setModelImageName(String modelImageName) {
		this.modelImageName = modelImageName;
	}

	/**
	 * @return the resultImageName
	 */
	public String getResultImageName() {
		return resultImageName;
	}

	/**
	 * @param resultImageName the resultImageName to set
	 */
	public void setResultImageName(String resultImageName) {
		this.resultImageName = resultImageName;
	}

	/**
	 * @return the modelImage
	 */
	public RGB48Image getModelImage() {
		return modelImage;
	}

	/**
	 * @param modelImage the modelImage to set
	 */
	public void setModelImage(RGB48Image modelImage) {
		this.modelImage = modelImage;
	}

	/**
	 * @return the resultImage
	 */
	public RGB48Image getResultImage() {
		return resultImage;
	}

	/**
	 * @param resultImage the resultImage to set
	 */
	public void setResultImage(RGB48Image resultImage) {
		this.resultImage = resultImage;
	}

	/**
	 * @return the evalResult
	 */
	public SegmentEvaluationResult getEvalResult() {
		return evalResult;
	}

	/**
	 * @param evalResult the evalResult to set
	 */
	public void setEvalResult(SegmentEvaluationResult evalResult) {
		this.evalResult = evalResult;
	}
	
	
	public static void main(String args[]){
		System.out.println("Evaluation starts: "+new Date());
		SegmentEvaluation evaluator = new SegmentEvaluation(args[0],args[1]);
		
//		evaluator.printColorMatrix();
//		evaluator.getEvalResult().print();
		System.out.println("Evaluation done: "+new Date());
	}
	
	
	
}
