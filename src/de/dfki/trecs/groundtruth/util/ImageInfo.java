/**
 * 
 */
package de.dfki.trecs.groundtruth.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.sourceforge.jiu.apps.StringIndexConstants;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.color.promotion.PromotionRGB48;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

/**
 * @author Shahab
 *
 */
public class ImageInfo {

	public static String getImageInfo(PixelImage image){
		StringBuffer result = new StringBuffer();
		result.append("Image Type");
		result.append(": ");
		result.append(image.getImageType().getSimpleName());
		result.append(", ");
		result.append("Pixels");
		result.append(": ");
		int width = image.getWidth();
		int height = image.getHeight();
		result.append(width);
		
		result.append(" x ");
		result.append(height);
		result.append(" (");
		result.append(width * height);
		result.append("), ");
		result.append("BitsPerPixel");
		result.append(": ");
		result.append(image.getBitsPerPixel());
		return result.toString();
	}
	
	public static void promoteImageToRGB48(String source,String result) throws Exception{
		
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(source);
		
		PromotionRGB48 promotor = new PromotionRGB48();
		promotor.setInputImage(image);
		promotor.process();
		
		PixelImage outImage = promotor.getOutputImage();
		
		PNGCodec codec = new PNGCodec();
		codec.setImage(outImage);
		codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(result))));
		codec.process();
		
		codec.close();
		System.out.println("Image promoted "+result);
		
	}
	
	public static void main(String args[]) throws Exception{
		ImageInfo.promoteImageToRGB48(args[0], args[1]);
	}
}
