/**
 * 
 */
package de.dfki.trecs.groundtruth.image;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.color.promotion.PromotionRGB48;
import net.sourceforge.jiu.data.MemoryRGB48Image;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB48Image;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;
import net.sourceforge.jiu.ops.ImageToImageOperation;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.dfki.tablerecognizer.block.BoundingBox;
import de.dfki.tablerecognizer.util.XMLManager;
import de.dfki.trecs.groundtruth.color.ColorEnumerator;
import de.dfki.trecs.groundtruth.color.ColorModel16Bit;
import de.dfki.trecs.groundtruth.data.GTCell;
import de.dfki.trecs.groundtruth.data.GTTable;

/**
 * @author Shahab
 *
 */
public class GroundTruthImage {

	public static int FOREGROUND_CHANNEL_BBOX = 0x000f;
	private static Logger log() {return Logger.getLogger(GroundTruthImage.class.getName());}
	public static void main (String args[]){
		produceGroundTruthImage(args[0],args[1],args[2],args[3]);
		System.out.println("Successfully painted the image : output in "+args[1]);
	}
	
	public static void produceGroundTruthImage(String src,String target,String gtXml, String wordXmlfile){
		
		XMLManager manager = new XMLManager();
		Document xmlDoc = manager.parse(gtXml);
		
		PixelImage image = ToolkitLoader.loadViaToolkitOrCodecs(src);
		MemoryRGB48Image rgb48Image = null;
		
		log().info("promoting image to RGB48");
		ImageToImageOperation imgOperation = new PromotionRGB48();
		imgOperation.setInputImage(image);
		try{
		imgOperation.process();
		}
		catch(Exception ex){
			System.out.println("Error transforming image to 48bit");
		}
		rgb48Image = (MemoryRGB48Image)imgOperation.getOutputImage();
		
		MemoryRGB48Image rgb48WordImage = null;
		rgb48WordImage = (MemoryRGB48Image)rgb48Image.createCopy();
		if (rgb48Image != null){
		
		log().info("Image promoted, Reading words from xml file and painting them on image.");
		// read words and paint all words in the given color
		ArrayList<BoundingBox> words = readWordsFromXMLFile(wordXmlfile);
		Color wordColor = new Color(FOREGROUND_CHANNEL_BBOX,FOREGROUND_CHANNEL_BBOX,FOREGROUND_CHANNEL_BBOX);
		for (BoundingBox word:words){
			colorImageForeground(rgb48WordImage, word, wordColor);
		}
		
			log().info("Image painted with word boxes, Now painting tables.");
			NodeList tableList  = manager.getElementsByTagName(xmlDoc, "Table");
			
			for (int i=0;i<tableList.getLength();i++){
				Node tableNode = tableList.item(i);
				NodeList cellsList = tableNode.getChildNodes();
				for (int j=0;j<cellsList.getLength();j++){
					Node cellNode = cellsList.item(j);
					if (cellNode.getNodeName().equals("Cell")){
						int x0 = Integer.parseInt(cellNode.getAttributes().getNamedItem("x0").getTextContent());
						int y0 = Integer.parseInt(cellNode.getAttributes().getNamedItem("y0").getTextContent());
						int x1 = Integer.parseInt(cellNode.getAttributes().getNamedItem("x1").getTextContent());
						int y1 = Integer.parseInt(cellNode.getAttributes().getNamedItem("y1").getTextContent());
						boolean dontCare = false;
						try {
						 dontCare = Boolean.parseBoolean(cellNode.getAttributes().getNamedItem("dontCare").getTextContent());
						}
						catch (Exception ex){
							
						}
						if (!dontCare){
						String colorString = cellNode.getTextContent();
						ColorModel16Bit color = ColorModel16Bit.parseColor(colorString.trim());
						GTCell cell = new GTCell(x0,y0,x1,y1);
						cell.setColor(color);
						colorImageForeground(rgb48Image,rgb48WordImage,cell);
						}
						
					}
				 
				}
			}
		}
		
		/**
		 * Enumerate Colors and paint a tmp image here
		 * 
		 */
		//RGB48Image recolImage = recolorImage(rgb48Image);
		
		PNGCodec codec = new PNGCodec();
		try
		{
			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(target))));
			codec.setImage(rgb48Image);
			codec.process();
			
//			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File("/tmp/wordboxes-48.png"))));
//			codec.setImage(rgb48WordImage);
//			codec.process();
			
//			codec.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File("/tmp/cell-recolor.png"))));
//			codec.setImage(recolImage);
//			codec.process();
//			
			codec.close();
		}
		catch (Exception e)
		{
			System.out.println("Error saving the ground truth image file");
			return;
		}
		 
	}

	private static ArrayList<BoundingBox> readWordsFromXMLFile(String xmlfile){
		
		ArrayList<BoundingBox> words = new ArrayList<BoundingBox>();
		XMLManager manager = new XMLManager(xmlfile,true);
		int docWidth = 0;
		int docHeight = 0;
		int docDPI = 0;
		NodeList nodelist = manager.getElementsByTagName(manager.getDocument(), "docinfo");
		if (nodelist.getLength() == 1) {
			Node node = nodelist.item(0);
			NamedNodeMap nnm = node.getAttributes();
			docWidth = Integer.parseInt( nnm.getNamedItem("width").getNodeValue() );
			docHeight = Integer.parseInt( nnm.getNamedItem("height").getNodeValue() );
			docDPI = Integer.parseInt( nnm.getNamedItem("dpix").getNodeValue() );
		}
		
		nodelist =  manager.getElementsByTagName(manager.getDocument(), "word");
		for ( int i=0; i<nodelist.getLength(); i++ ) {
			int x0, y0, x1, y1;
			String content = null;
			Node node = nodelist.item(i);
			NamedNodeMap nnm = node.getAttributes();
			
			/*Read word from the XML File */
			
			x0 = Integer.parseInt( nnm.getNamedItem("left").getNodeValue() );
			y0 = Integer.parseInt( nnm.getNamedItem("bottom").getNodeValue() );
			
			x1 = Integer.parseInt( nnm.getNamedItem("right").getNodeValue() );
			y1 = Integer.parseInt( nnm.getNamedItem("top").getNodeValue() );
			
			int temp = y1;
			y1 = Math.abs(docHeight - y0);
			y0 = Math.abs(docHeight - temp);
			
			BoundingBox box = new BoundingBox(x0,y0,x1,y1);
			words.add(box);
			
		}
		return words;
	}
	
	private static void colorImageForeground(MemoryRGB48Image image,  BoundingBox cell, Color color){
		
	//	System.out.println(cell);
		int R[] = new int[(cell.getWidth()*cell.getHeight())];
		int G[] =  new int[(cell.getWidth()*cell.getHeight())];
		int B[] =  new int[(cell.getWidth()*cell.getHeight())];
		image.getSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
		image.getSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
		image.getSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
		for (int r=0;r<R.length;r++){
			if (R[r]!=0xffff)
				R[r] = 0x0000;//(color.getRed()<<8 | color.getRed());
			if (G[r]!=0xffff)
				
				G[r] = 0xffff;//(color.getGreen()<<8 | color.getGreen());
			if (B[r]!=0xffff)
				B[r] = 0x0000;//(color.getBlue()<<8 | color.getBlue());
		}
		
		image.putSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
		image.putSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
		image.putSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
	
	}
	
	public static RGB48Image recolorImage(RGB48Image img){
		RGB48Image out_img = (RGB48Image)img.createCopy();
		ColorEnumerator enumerator = (ColorEnumerator.enumerateColorsInImage(out_img,ColorEnumerator.CELL_ENUMERATOR));
		short imglabels[] = enumerator.getNormalizedImage();
		for (int j=0;j<out_img.getHeight();j++)
			for (int i=0;i<out_img.getWidth();i++){
				int index = j*out_img.getWidth()+i;
				int label = imglabels[index];
				if (label <=2){
					continue;
				}
				else
				{
					ColorModel16Bit color = interestingColors(label);
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
private static void colorImageForeground(MemoryRGB48Image image,MemoryRGB48Image rgb48WordImage, GTCell cell){
		
		
				
				int R[] = new int[(cell.getWidth()*cell.getHeight())];
				int G[] =  new int[(cell.getWidth()*cell.getHeight())];
				int B[] =  new int[(cell.getWidth()*cell.getHeight())];
				
				image.getSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
				image.getSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
				image.getSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
				
				int Rw[] = new int[(cell.getWidth()*cell.getHeight())];
				int Gw[] =  new int[(cell.getWidth()*cell.getHeight())];
				int Bw[] =  new int[(cell.getWidth()*cell.getHeight())];
				
				rgb48WordImage.getSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), Rw,0);
				rgb48WordImage.getSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), Gw,0);
				rgb48WordImage.getSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), Bw,0);
				for (int r=0;r<R.length;r++){
					if (Gw[r] == 0xffff){
					if (R[r]!=0xffff  )
						R[r] =cell.getColor().getR();
					if (G[r]!=0xffff )
						G[r] = cell.getColor().getG();
					if (B[r]!=0xffff )
						B[r] = cell.getColor().getB();
					}
				}
				
				image.putSamples(0, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), R,0);
				image.putSamples(1, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), G,0);
				image.putSamples(2, cell.getX0(), cell.getY0(), cell.getWidth(), cell.getHeight(), B,0);
			
			}
			
		
		
	
}
