/**
 * 
 */
package de.dfki.trecs.evaluation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Stack;

import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;

/**
 * 
 * This class represent the type of information returned by the segment evaluation.
 * This is a separate class, so later it can be directly serialized. 
 * @author shahab
 *
 */
public class SegmentEvaluationResult {

	private int missed = 0;
	private int falseAlarms = 0;
	private int overSegmentation = 0;
	private int underSegmentation = 0;
	private int correct = 0;
	private int partialMatches=0;
	
	
//	private RGB48Image modelImage = null;
//	private RGB48Image resultImage = null;
	
	/**
	 * @return the partialMatches
	 */
	public int getPartialMatches() {
		return partialMatches;
	}
	/**
	 * @param partialMatches the partialMatches to set
	 */
	public void setPartialMatches(int partialMatches) {
		this.partialMatches = partialMatches;
	}
	/**
	 * @return the correct
	 */
	public int getCorrect() {
		return correct;
	}
	/**
	 * @param correct the correct to set
	 */
	public void setCorrect(int correct) {
		this.correct = correct;
	}
	private Stack<Integer> iOverSeg = new Stack<Integer>();
	/**
	 * @return the iOverSeg
	 */
	public Stack<Integer> getiOverSeg() {
		return iOverSeg;
	}
	/**
	 * @param iOverSeg the iOverSeg to set
	 */
	public void setiOverSeg(Stack<Integer> iOverSeg) {
		this.iOverSeg = iOverSeg;
	}
	/**
	 * @return the iUnderSeg
	 */
	public Stack<Integer> getiUnderSeg() {
		return iUnderSeg;
	}
	/**
	 * @param iUnderSeg the iUnderSeg to set
	 */
	public void setiUnderSeg(Stack<Integer> iUnderSeg) {
		this.iUnderSeg = iUnderSeg;
	}
	/**
	 * @return the iMissed
	 */
	public Stack<Integer> getiMissed() {
		return iMissed;
	}
	/**
	 * @param iMissed the iMissed to set
	 */
	public void setiMissed(Stack<Integer> iMissed) {
		this.iMissed = iMissed;
	}
	/**
	 * @return the iFAlarm
	 */
	public Stack<Integer> getiFAlarm() {
		return iFAlarm;
	}
	/**
	 * @param iFAlarm the iFAlarm to set
	 */
	public void setiFAlarm(Stack<Integer> iFAlarm) {
		this.iFAlarm = iFAlarm;
	}
	private Stack<Integer> iUnderSeg = new Stack<Integer>();
	private Stack<Integer> iMissed = new Stack<Integer>();
	private Stack<Integer> iFAlarm = new Stack<Integer>();
	
//	public SegmentEvaluationResult(RGB48Image modelImage,RGB48Image resultImage){
//		setModelImage(modelImage);
//		setResultImage(resultImage);
//	}
	public void pushIOverSeg(int x){
		iOverSeg.push(x);
	}
	public void pushIUnderSeg(int x){
		iUnderSeg.push(x);
	}
	public void pushIMissed(int x){
		iMissed.push(x);
	}
	public void pushIFAlarm(int x){
		iFAlarm.push(x);
	}
	private int mOver = 0;
	private int iOver = 0;
	 
	private int mCount = 0;
	private int iCount = 0;
	
	public void incrementCorrect(){
		correct++;
	}
	public void incrementPartialMatches(){
		partialMatches++;
	}
	public void incrementMOver(){
		mOver++;
	}
	public void incrementiOver(){
		iOver++;
	}
	public void incrementMCount(){
		mCount++;
	}
	public void incrementICount(){
		iCount++;
	}
	public void incrementMissed(){
		missed++;
	}
	public void incrementFalseAlarms(){
		falseAlarms++;
	}
	public void incrementOverSegmentation(){
		overSegmentation++;
	}
	public void incrementUnderSegmentation(){
		underSegmentation++;
	}
	public int getMissed() {
		return missed;
	}
	public void setMissed(int missed) {
		this.missed = missed;
	}
	public int getFalseAlarms() {
		return falseAlarms;
	}
	public void setFalseAlarms(int falseAlarms) {
		this.falseAlarms = falseAlarms;
	}
	public int getOverSegmentation() {
		return overSegmentation;
	}
	public void setOverSegmentation(int overSegmentation) {
		this.overSegmentation = overSegmentation;
	}
	public int getUnderSegmentation() {
		return underSegmentation;
	}
	public void setUnderSegmentation(int underSegmentation) {
		this.underSegmentation = underSegmentation;
	}
	/**
	 * @return the mCount
	 */
	public int getmCount() {
		return mCount;
	}
	/**
	 * @param mCount the mCount to set
	 */
	public void setmCount(int mCount) {
		this.mCount = mCount;
	}
	/**
	 * @return the iCount
	 */
	public int getiCount() {
		return iCount;
	}
	/**
	 * @param iCount the iCount to set
	 */
	public void setiCount(int iCount) {
		this.iCount = iCount;
	}
	/**
	 * @return the mOver
	 */
	public int getmOver() {
		return mOver;
	}
	/**
	 * @param mOver the mOver to set
	 */
	public void setmOver(int mOver) {
		this.mOver = mOver;
	}
	/**
	 * @return the iOver
	 */
	public int getiOver() {
		return iOver;
	}
	/**
	 * @param iOver the iOver to set
	 */
	public void setiOver(int iOver) {
		this.iOver = iOver;
	}
	
	
//	/**
//	 * @return the modelImage
//	 */
//	public RGB48Image getModelImage() {
//		return modelImage;
//	}
//	/**
//	 * @param modelImage the modelImage to set
//	 */
//	public void setModelImage(RGB48Image modelImage) {
//		this.modelImage = modelImage;
//	}
//	/**
//	 * @return the resultImage
//	 */
//	public RGB48Image getResultImage() {
//		return resultImage;
//	}
//	/**
//	 * @param resultImage the resultImage to set
//	 */
//	public void setResultImage(RGB48Image resultImage) {
//		this.resultImage = resultImage;
//	}
//	
//	public void paintResultImage(String model,String result){
//		
//	}
//	
//	public void saveImage(PixelImage image,String imageName) throws Exception{
//		PNGCodec codec = new PNGCodec();
//		codec.setImage(image);
//		codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(imageName))));
//		codec.process();
//		codec.close();
//	}
	public void print(){
		System.out.println("GroundTruth Components: "+getmCount());
		System.out.println("Segmentation Components: "+getiCount());
		System.out.println("Number of Over Segmenation: "+(mOver-mCount));
		System.out.println("Number of Under Segmenation: "+(iOver-iCount));
		System.out.println("Number of False Alarms: "+iFAlarm.size());
		System.out.println("Oversegmented Components: "+iOverSeg.size());
		System.out.println("Undersegmented Components: "+iUnderSeg.size());
		System.out.println("falarm: "+falseAlarms);
		System.out.println("missed: "+missed);
		System.out.println("Correct: "+correct);
		System.out.println("Partial: "+partialMatches);
		
		
		
		
	}
	public String getFileString(){
		StringBuffer test = new StringBuffer();
		test.append(getmCount());
		test.append(";"+getiCount());
		test.append(";"+(mOver-mCount));
		test.append(";"+(iOver-iCount));
		test.append(";"+iFAlarm.size());
		test.append(";"+iOverSeg.size());
		test.append(";"+iUnderSeg.size());
		test.append(";"+falseAlarms);
		test.append(";"+missed);
		test.append(";"+correct);
		test.append(";"+partialMatches);
		return test.toString();
		
		
	}
	
}
