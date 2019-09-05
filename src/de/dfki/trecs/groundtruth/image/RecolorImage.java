/**
 * 
 */
package de.dfki.trecs.groundtruth.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;
import de.dfki.trecs.groundtruth.color.ColorEnumerator;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;

/**
 * @author shahab
 *
 */
public class RecolorImage {

	public static void main(String args[]){
		recolorImage(args[0],args[1],Integer.parseInt(args[2]));
	}
	public static void recolorImage(String input_img,String out_img,int enumerator){
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(input_img);
		PixelImage imageOut = recolorImage((RGB48Image)image,enumerator);
		PNGCodec codec = new PNGCodec();
		try
		{
			
			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(out_img))));
			codec.setImage(imageOut);
			codec.process();
			
			codec.close();
			System.out.println("Saved Image "+out_img);
		}
		catch (Exception e)
		{
			System.out.println("Error saving the ground truth image file");
			return;
		}
	}
	public static RGB48Image recolorImage(RGB48Image img,int enumeratorType){
		RGB48Image out_img = (RGB48Image)img.createCopy();
		ColorEnumerator enumerator = (ColorEnumerator.enumerateColorsInImage(out_img,enumeratorType));
		short imglabels[] = enumerator.getNormalizedImage();
		for (int j=0;j<out_img.getHeight();j++)
			for (int i=0;i<out_img.getWidth();i++){
				int index = j*out_img.getWidth()+i;
				int label = imglabels[index];
				if (label <=1){
					continue;
				}
				else if (label == 2){
					out_img.putSample(0, i, j, 0);
					out_img.putSample(1, i,j, 0);
					out_img.putSample(2,i,j,0);
				}
				else
				{
					ColorModel16Bit color = interestingColors(1+label%enumerator.getNumberOfLabels());
					out_img.putSample(0, i, j, color.getR());
					out_img.putSample(1, i,j, color.getG());
					out_img.putSample(2,i,j,color.getB());
				}
			}
		return out_img;
	}
	private static ColorModel16Bit interestingColors(int x){
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i=0;i<8;i++){
			r = (r<<1)|(x&1);x>>=1;
			g = (g<<1) |(x&1);x>>=1;
			b = (b<<1) | (x&1);x>>=1;
		}
		return ColorModel16Bit.ColorModel8Bit(r, g, b);
	}
}
