/**
 * 
 */
package de.dfki.trecs.groundtruth.color;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.jiu.color.promotion.PromotionRGB48;
import net.sourceforge.jiu.data.MemoryRGB24Image;
import net.sourceforge.jiu.data.MemoryShortChannelImage;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

/**
 * This class represents a ColorEnumerator, that holds one entry for each of the unique color.
 * This class also assigns a label and store the label correesponding to a color in labelMap.
 * @author shahab
 *
 */
public class ColorEnumerator {

	public static final int CELL_ENUMERATOR = 0;
	public static final int ROW_ENUMERATOR = 1;
	public static final int COL_ENUMERATOR = 2;
	public static final int TABLE_ENUMERATOR = 3;
	public static final int ROW_SPAN_ENUMERATOR = 4;
	public static final int COL_SPAN_ENUMERATOR = 5;
	public static final int ROW_COL_SPAN_ENUMERATOR = 6;
	
	
	HashMap<ColorModel16Bit,Integer> colorMap = null;
	HashMap<ColorModel16Bit,Integer> labelMap = null;
	private int type = CELL_ENUMERATOR;
	
	short normalizedImage [] = null;
	private int labelIndex = 0;
	
	public static final ColorModel16Bit BLACK = ColorModel16Bit.getColorModel(0, 0, 0);
	public static final ColorModel16Bit WHITE = ColorModel16Bit.getColorModel(65535,65535,65535);
	public static final ColorModel16Bit DONTCARE = ColorModel16Bit.getColorModel(255,255,255);
	
	public ColorEnumerator() {
		colorMap = new HashMap<ColorModel16Bit,Integer>();
		labelMap = new HashMap<ColorModel16Bit,Integer>();
		/**
		 * initiallize labelmaps with black non-marked foreground and background pixel labels.
		 */
		labelMap.put(WHITE, 0);
		labelMap.put(BLACK,1);
		labelMap.put(DONTCARE, 2);
		labelIndex = 3;
	}
	public ColorEnumerator(int width,int height){
		this();
		normalizedImage = new short[width*height];
		Arrays.fill(normalizedImage, (short)0);
	}
//	public ColorEnumerator(int width,int height){
//		this();
//		normalizedImage = new short[width*height];
//		Arrays.fill(normalizedImage, (short) 0);
//	}
	
	public void addColor(ColorModel16Bit color){
		
		if (colorMap.containsKey(color)){
			int freq = colorMap.get(color).intValue();
			freq++;
			colorMap.put(color, freq);
		}
		else{
			colorMap.put(color, 1);
			if (!(color.equals(BLACK) | color.equals(WHITE)|color.equals(DONTCARE)))
				labelMap.put(color, labelIndex++);
		}
		
	}
	public int getColorFrequency(ColorModel16Bit color){
		if (colorMap.containsKey(color)){
			return colorMap.get(color).intValue(); 
		}
		else 
			return 0;
	}
	/**}
		
	 * Returns the number of unique colors in colorMap.
	 * @return
	 */
	public int getNumberOfColors(){
		return colorMap.size();
	}
	
	public int getColorLabel(ColorModel16Bit color){
		if (labelMap.containsKey(color))
			return labelMap.get(color);
		else
			return -1;
	}
	
	
	/**
	 * returns the color frequency with its label
	 * @param label
	 * @return
	 */
	public int getColorFrequency(int label){
		Iterator<ColorModel16Bit> iterator = labelMap.keySet().iterator();
		while (iterator.hasNext()){
			ColorModel16Bit color = iterator.next();
			if (labelMap.get(color) == label)
				return getColorFrequency(color);
		}
		return 0;
	}
	public void setNormalizedImage(int x,ColorModel16Bit color){
		normalizedImage[x] = (short)getColorLabel(color);
	}
	
	public static ColorEnumerator enumerateColorsInImage(RGB48Image image,int type){
		ColorEnumerator enumerator = new ColorEnumerator(image.getWidth(),image.getHeight());
		enumerator.setType(type);
		
	
			for (int j=0;j<image.getHeight();j++)
				for (int i=0;i<image.getWidth();i++){
			{
				int R = image.getSample(0, i, j);
				int G = image.getSample(1,i,j);
				int B = image.getSample(2,i,j);
				ColorModel16Bit color = ColorModel16Bit.getColorModel(R, G, B);
				switch(enumerator.getType()){
				case CELL_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_NONE);
					break;
				case ROW_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_G);
					int g = color.getG();
					int gLSB = g&0x000000ff;
					int gMSB = (g>>8)&0x000000ff;
					if (gLSB!=gMSB)
						color = DONTCARE;
					break;
				case COL_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_B);
					int b = color.getB();
					int bLSB = b&0x000000ff;
					int bMSB = (b>>8)&0x000000ff;
					if (bLSB!=bMSB)
						color = DONTCARE;
					break;
				case TABLE_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_R);
					break;
				case ROW_SPAN_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_GG);
					 g = color.getG();
					 gLSB = g&0x000000ff;
					 gMSB = (g>>8)&0x000000ff;
					if (gLSB==gMSB)
						color = DONTCARE;
					break;
				case COL_SPAN_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_BB);
					b = color.getB();
					bLSB = b&0x000000ff;
					bMSB = (b>>8)&0x000000ff;
					if (bLSB==bMSB)
						color = DONTCARE;
					break;
				case ROW_COL_SPAN_ENUMERATOR:
					color.setDominantChannel(ColorModel16Bit.CHANNEL_GB);
					 g = color.getG();
					 gLSB = g&0x000000ff;
					 gMSB = (g>>8)&0x000000ff;
					if (gLSB==gMSB)
						color = DONTCARE;
					
					b = color.getB();
					bLSB = b&0x000000ff;
					bMSB = (b>>8)&0x000000ff;
					if (bLSB==bMSB)
						color = DONTCARE;
					break;
				}
				
				enumerator.addColor(color);
				enumerator.setNormalizedImage(j*image.getWidth()+i, color);
		
				
			}
		}
		
		return enumerator;
		
		
	}
	public int getNumberOfLabels(){
		
		return labelMap.size();
	}
	public Iterator<ColorModel16Bit> getColors(){
		return colorMap.keySet().iterator();
	}
	/**
	 * @return the normalizedImage
	 */
	public short[] getNormalizedImage() {
		return normalizedImage;
	}
	/**
	 * @param normalizedImage the normalizedImage to set
	 */
	public void setNormalizedImage(short[] normalizedImage) {
		this.normalizedImage = normalizedImage;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public static void main(String args[]) throws Exception{
		
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs("/home/shahab/tmp/0725_026-seg.png");
		PixelImage test = null;
		if (image instanceof MemoryRGB24Image){
			PromotionRGB48 promotion = new PromotionRGB48();
			promotion.setInputImage(image);
			promotion.process();
			image = promotion.getOutputImage();
			System.out.println("Promoted");
		}
		
			
		System.out.println (image.getImageType());
		if (image instanceof MemoryShortChannelImage){
			ColorEnumerator enumerator = ColorEnumerator.enumerateColorsInImage((RGB48Image)image,ColorEnumerator.CELL_ENUMERATOR);
			Iterator<ColorModel16Bit> iterator = enumerator.getColors();
			System.out.println("Total Number of Colors: "+enumerator.getNumberOfColors());
			while (iterator.hasNext()){
				ColorModel16Bit color = iterator.next();
				System.out.println(color.toString()+": "+enumerator.getColorFrequency(color)+": label: "+enumerator.getColorLabel(color));
			}
			enumerator = ColorEnumerator.enumerateColorsInImage((RGB48Image)image,ColorEnumerator.ROW_ENUMERATOR);
			 iterator = enumerator.getColors();
			System.out.println("Total Number of Colors: "+enumerator.getNumberOfColors());
			while (iterator.hasNext()){
				ColorModel16Bit color = iterator.next();
				System.out.println(color.toString()+": "+enumerator.getColorFrequency(color)+": label: "+enumerator.getColorLabel(color));
			}
			enumerator = ColorEnumerator.enumerateColorsInImage((RGB48Image)image,ColorEnumerator.COL_ENUMERATOR);
			 iterator = enumerator.getColors();
			System.out.println("Total Number of Colors: "+enumerator.getNumberOfColors());
			while (iterator.hasNext()){
				ColorModel16Bit color = iterator.next();
				System.out.println(color.toString()+": "+enumerator.getColorFrequency(color)+": label: "+enumerator.getColorLabel(color));
			}
			
		}
		
	}
	
}
