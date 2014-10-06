package recognition;

import java.awt.image.BufferedImage;


public class ImagePoint{

	public final BufferedImage image;
	public final int label;
	
	public ImagePoint(BufferedImage img, int lbl){
		image = img;
		label = lbl;
	}
	
	/**
	 * Renvoie la valeur du pixel en (x,y) : 0 noir, 255 blanc
	 * @param x
	 * @param y
	 * @return
	 */
	public int getValue(int x, int y){
		return image.getRGB( x, y ) & 0xFF;
	}
	
	/**
	 * Retoune la distance euclidienne entre les deux images
	 * @param img2
	 * @return
	 */
	public double distanceEuclidienne(ImagePoint img2){
		
		if( image.getWidth() != img2.image.getWidth() || image.getHeight() != img2.image.getHeight())
			throw new RuntimeException("Pas mêmes dimensions");
		
		double dist = 0;
		
		for(int i = 0; i < image.getWidth(); i++)
			for(int j = 0; j < image.getHeight(); j++)
				dist += Math.pow( (getValue(i, j) - img2.getValue( i, j )), 2);
		
		return Math.sqrt(dist);
		
	}
	
}
