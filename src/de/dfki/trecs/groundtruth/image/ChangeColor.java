package de.dfki.trecs.groundtruth.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import de.dfki.trecs.groundtruth.color.ColorModel16Bit;

import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;

public class ChangeColor {

	public static void main(String args[]){
		String input_img = args[0];
		String out_img = args[1];
		String in_color = args[2];
		String out_color = args[3];
		ColorModel16Bit srcColor = ColorModel16Bit.parseColor(in_color);
		ColorModel16Bit targetColor = ColorModel16Bit.parseColor(out_color);
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(input_img);
		PixelImage outImage = image.createCopy();
		for (int i=0;i<image.getWidth();i++){
			for (int j=0;j<image.getHeight();j++){
				int r = ((RGB48Image)image).getSample(0, i, j);
				int g = ((RGB48Image)image).getSample(1, i, j);
				int b = ((RGB48Image)image).getSample(2, i, j);
				if (r == srcColor.getR() && g == srcColor.getG() && b == srcColor.getB()){
					((RGB48Image)outImage).putSample(0, i, j, targetColor.getR());
					((RGB48Image)outImage).putSample(1, i, j, targetColor.getG());
					((RGB48Image)outImage).putSample(2, i, j, targetColor.getB());
				}
			}
		}
		PNGCodec codec = new PNGCodec();
		try
		{
			
			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(out_img))));
			codec.setImage(outImage);
			codec.process();
			
			codec.close();
			System.out.println("Saved Image "+out_img);
		}
		catch (Exception e)
		{
			System.out.println("Error saving the change colored image image file");
			return;
		}
	}
}
