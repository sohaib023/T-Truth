/**
 * 
 */
package de.dfki.trecs.groundtruth.color;

import java.util.Comparator;

/**
 * This class represents the 16 bit color model for coloring the ground truth images. Each channel 
 * (R,G,B) here is 16 bit. 
 * @author Shahab
 *
 */
public class ColorModel16Bit implements Comparable<ColorModel16Bit> {
	
	/* The R channel  */
	private int R;
	/* The G channel */
	private int G;
	/* The B channel */
	private int B;
	
	public static final int CHANNEL_R = 0;
	public static final int CHANNEL_G = 1;
	public static final int CHANNEL_B = 2;
	
	public static final int CHANNEL_NONE = 3;
	public static final int CHANNEL_GG = 4;
	public static final int CHANNEL_BB = 5;
	public static final int CHANNEL_GB = 6;
	private int dominantChannel = CHANNEL_NONE;
	
	
	public ColorModel16Bit(int R, int G, int B) {
		
		setR((253<<8)|(R));
		
		setG((G<<8));
		setB((B<<8));
	}
	
	public static ColorModel16Bit ColorModel8Bit(int r,int g,int b){
		ColorModel16Bit t = new ColorModel16Bit();
		t.setR(r<<8);
		t.setG(g<<8);
		t.setB(b<<8);
		return t;
	}
	public ColorModel16Bit(int table,int startRow,int endRow,int startCol,int endCol){
		setR((253<<8)|table);
		setG((startRow<<8)|endRow);
		setB((startCol<<8)|endCol);
	}
	private ColorModel16Bit() {
		
	}
	/**
	 * Factory method to produce colors without the shifting, used by 
	 * the image analysis classes to generate colormodel from the pixel colors.; 
	 * @param R
	 * @param G
	 * @param B
	 * @return
	 */
	public static ColorModel16Bit getColorModel(int R, int G, int B){
		ColorModel16Bit model = new ColorModel16Bit();
		model.setR(R);
		model.setG(G);
		model.setB(B);
		return model;
	}
	@Override
	public int compareTo(ColorModel16Bit o) {
		
		if (getG()>o.getG())
			return +1;
		else if (equals(o))
			return 0;
		else return -1;
		
	}
	/*
	 * Overridden equals method of Object class in order for ColorModel to be added
	 * to the HashTable. It also requires the hashCode function.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		
			ColorModel16Bit target = (ColorModel16Bit)obj;
			if ((getR() == 0 && target.getR()==0) || (getR()==65535 ||target.getR() == 65535) || (getR()==255 || target.getR()==255))
					return (getR() == target.getR() &&
							getG() == target.getG() &&
							getB() == target.getB());
			switch (dominantChannel){
			
			case CHANNEL_NONE:
				if (getR() == target.getR() &&
						getG() == target.getG() &&
						getB() == target.getB())
				return true;
			else
				return false;
			case CHANNEL_R:
				if (getR() == target.getR())
					return true;
				else
					return false;
			case CHANNEL_G:
			case CHANNEL_GG:
				if (getG()+getR() == target.getG()+target.getR())
					return true;
				else 
					return false;
			
				
			case CHANNEL_BB:
			case CHANNEL_B:
				if (getB()+getR() == target.getB()+target.getR())
					return true;
				else 
					return false;
			case CHANNEL_GB:
				if ((getG()+getR() == target.getG() + target.getR()) ||
					(getB()+getR() == target.getB()+target.getR()))
						return true;
				else
					return false;
			}
			
				
			return false;
	}
	/**
	 * The hashcode function which will ensure no duplicates are added.
	 * If two colors are equal the hashCode must return the same value for the two colors.
	 * It just returns the sum of the R,G, B values
	 * @see java.lang.Object#hashCode()(java.lang.Object)
	 */
	public int hashCode(){
		if (getR() == 0 || getR() == 65535||getR() == 255)
			return getR()+getG()+getB();
		switch(dominantChannel){
		case CHANNEL_NONE:
			return getR()+getG()+getB();
		case CHANNEL_R:
			return getR();
		case CHANNEL_G:
		case CHANNEL_GG:
			return getR()+getG();
		case CHANNEL_B:
		case CHANNEL_BB:
			return getR()+getB();
		case CHANNEL_GB:
			return getR()+getG()+getR()+getB();
			
		default:
			return 0;
		
		}
		
	}
	
	public static ColorModel16Bit parseColor(String color){
		ColorModel16Bit returnColor = new ColorModel16Bit(0,0,0);
		
		int commaIndex = color.indexOf(',', 0);
		String r = color.substring(color.indexOf('(')+1,commaIndex);
		String g = color.substring(commaIndex+1,color.indexOf(',', commaIndex+1));
		commaIndex = color.indexOf(',',commaIndex+1);
		String b = color.substring(commaIndex+1,color.indexOf(')'));
		
		returnColor.setR(Integer.parseInt(r));
		returnColor.setG(Integer.parseInt(g));
		returnColor.setB(Integer.parseInt(b));
		return returnColor;
	}
	public String toString(){
		return "("+getR()+","+getG()+","+getB()+")";
	}
	
	/**
	 * @return the r
	 */
	public int getR() {
		return R;
	}

	/**
	 * @param r the r to set
	 */
	public void setR(int r) {
		R = r;
	}

	/**
	 * @return the g
	 */
	public int getG() {
		return G;
	}

	/**
	 * @param g the g to set
	 */
	public void setG(int g) {
		G = g;
	}

	/**
	 * @return the b
	 */
	public int getB() {
		return B;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(int b) {
		B = b;
	}


	public int getDominantChannel() {
		return dominantChannel;
	}


	public void setDominantChannel(int dominantChannel) {
		this.dominantChannel = dominantChannel;
	}
	
}
