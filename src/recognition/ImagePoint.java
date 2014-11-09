package recognition;

import java.awt.image.BufferedImage;


public class ImagePoint{

	// public final BufferedImage image;

	/**
	 * Image stockée sous forme de doubles correspondant à des pixels de niveaux de gris (i, j) -> i
	 * + j * width
	 */
	private double[] image;
	private double[][] tangents;
	private int width, height;
	private final int label;
	
	private static final int BACKGROUND = 0;

	public ImagePoint(double[] img, int w, int h, int lbl){
		image = img;
		width = w;
		height = h;
		label = lbl;
	}
	
	public void computeTangents(){
		tangents = TangentDistance.calculateTangents( image, height, width, BACKGROUND );
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
		// System.out.println(value);
		if (value > 255)
			value = 255;
		if (value < 0)
			value = 0;
		return value;
	}

	/**
	 * Change la valeur en (x, y) par v
	 * 
	 * @param x
	 * @param y
	 * @param v
	 */
	public void setValue(int x, int y, double v){
		image[x + y * width] = v;
	}

	/**
	 * Change la valeur en (x, y) par v
	 * 
	 * @param x
	 * @param y
	 * @param v
	 */
	public void setValue(int x, int y, int v){
		image[x + y * width] = v;
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
	 * Retourne l'ensemble des vecteurs tangents à l'image, en les calculant si besoin
	 * @return
	 */
	public double[][] getTangents(){
		if(tangents == null)
			computeTangents();
		return tangents;
	}
	
	/**
	 * Retourne l'image sous forme de tableau
	 * @return
	 */
	public double[] getImageArray(){
		return image;
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

	public double distance(ImagePoint img2, int l){

		if (getWidth() != img2.getWidth() || getHeight() != img2.getHeight())
			throw new RuntimeException( "Les dimensions ne correspondent pas !" );

		switch (l){
			case 1:
				return distanceL1( img2 );
			case 2:
				return distanceEuclidienne( img2 );
			case -1:
				return distanceLInf( img2 );
			case 0:
				return tangentDistance( img2 );
			default:
				throw new IllegalArgumentException( "Unknown distance" );
		}

	}

	/**
	 * Retoune la distance L1 entre les deux images
	 * 
	 * @param img2
	 * @return
	 */
	private double distanceL1(ImagePoint img2){

		double dist = 0;

		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getHeight(); j++)
				dist += Math.abs( ( getValue( i, j ) - img2.getValue( i, j ) ) );

		return dist;

	}

	/**
	 * Retoune la distance euclidienne entre les deux images
	 * 
	 * @param img2
	 * @return
	 */
	private double distanceEuclidienne(ImagePoint img2){

		double dist = 0;

		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getHeight(); j++)
				dist += Math.pow( ( getValue( i, j ) - img2.getValue( i, j ) ), 2 );

		return Math.sqrt( dist );

	}

	/**
	 * Retoune la distance L_infinie entre les deux images
	 * 
	 * @param img2
	 * @return
	 */
	private double distanceLInf(ImagePoint img2){

		double dist = 0;

		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getHeight(); j++)
				dist = Math.max( dist, Math.abs( ( getValue( i, j ) - img2.getValue( i, j ) ) ) );

		return dist;

	}
	
	private double tangentDistance(ImagePoint img2){
		
		return TangentDistance.calculateTangentDistance(this, img2);
		
	}
}
