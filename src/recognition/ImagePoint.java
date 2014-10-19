package recognition;

import java.awt.image.BufferedImage;


public class ImagePoint{

	// public final BufferedImage image;

	/**
	 * Image stockée sous forme de doubles correspondant à des pixels de niveaux de gris (i, j) -> i
	 * + j * width
	 */
	private double[] image;
	private int width, height;
	private final int label;

	public ImagePoint(double[] img, int w, int h, int lbl){
		image = img;
		width = w;
		height = h;
		label = lbl;
	}

	/**
	 * Renvoie la valeur du pixel en (x,y) : 0 noir, 255 blanc
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getValue(int x, int y){
		// return image.getRGB( x, y ) & 0xFF;
		int value = (int) image[x + y * width];
		if (value > 255)
			value = 255;
		if (value < 0)
			value = 0;
		return value;
	}

	/**
	 * Retourne la largeur de l'image
	 * 
	 * @return
	 */
	public int getWidth(){
		return width;
	}

	/**
	 * Retourne la hauteur de l'image
	 * 
	 * @return
	 */
	public int getHeight(){
		return height;
	}

	/**
	 * Retourne l'image sous un format de BufferedImage
	 * 
	 * @return
	 */
	public BufferedImage getImage(){
		BufferedImage img = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );

		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getHeight(); j++)
				img.setRGB( i, j, getValue( i, j ) * 0x00010101 );

		return img;
	}

	/**
	 * Retourne le label de l'image
	 * 
	 * @return
	 */
	public int getLabel(){
		return label;
	}

	/**
	 * Retoune la distance euclidienne entre les deux images
	 * 
	 * @param img2
	 * @return
	 */
	public double distanceEuclidienne(ImagePoint img2){

		if (getWidth() != img2.getWidth() || getHeight() != img2.getHeight())
			throw new RuntimeException( "Les dimensions ne ocrrespondent pas" );

		double dist = 0;

		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getHeight(); j++)
				dist += Math.pow( ( getValue( i, j ) - img2.getValue( i, j ) ), 2 );

		return Math.sqrt( dist );

	}

}
