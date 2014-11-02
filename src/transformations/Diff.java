package transformations;

import helper.ImageDisplayFrame;
import helper.TestingImageReader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Diff{

	public static void main(String[] args){

		BufferedImage image = TestingImageReader.readNextImage().getImage();

		ImageDisplayFrame disp = new ImageDisplayFrame( image, "Image départ" );

		BufferedImage img2;
		// img2 = rotate( image, Math.PI / 12 );
		// img2 = scale( image, 1, 1 );
		// img2 = shear( image, 0, 0.2 );
		img2 = translate( image, 1, 1 );
		
		ImageDisplayFrame disp2 = new ImageDisplayFrame( img2, "Image Transformée" );
		
		double[][] diff = diff( image, TransformationType.Translation );
		BufferedImage img3 = applyDiff( image, diff, -1 );
		
		ImageDisplayFrame disp3 = new ImageDisplayFrame( img3, "Image + Diff" );
		
	}

	private static double[][] diff(BufferedImage img, TransformationType transfType){
		switch (transfType){
			case Translation:
				BufferedImage img2 = translate( img, 1, 1 );
				return subDiff( img, img2, 1 );
			default:
				throw new IllegalArgumentException( "Unknown Transformation" );
		}
	}

	private static BufferedImage invert(BufferedImage img){
		BufferedImage img2 = new BufferedImage( img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_RGB );

		for(int i = 0; i < img.getWidth(); i++)
			for(int j = 0; j < img.getHeight(); j++)
				img2.setRGB( i, j, img.getRGB( i, j ) ^ 0xFFFFFF );

		return img2;
	}

	private static BufferedImage rotate(BufferedImage img, double theta){
		AffineTransform rotation = AffineTransform.getRotateInstance( theta, img.getWidth() / 2,
				img.getHeight() / 2 );

		return applyTransformation( img, rotation );
	}

	private static BufferedImage translate(BufferedImage img, double x, double y){
		AffineTransform translation = AffineTransform.getTranslateInstance( x, y );

		return applyTransformation( img, translation );
	}

	private static BufferedImage scale(BufferedImage img, double x, double y){
		AffineTransform translation = AffineTransform.getScaleInstance( x, y );

		return applyTransformation( img, translation );
	}

	private static BufferedImage shear(BufferedImage img, double x, double y){
		AffineTransform translation = AffineTransform.getShearInstance( x, y );

		return applyTransformation( img, translation );
	}

	private static BufferedImage applyTransformation(BufferedImage img, AffineTransform af){
		BufferedImage img2 = new BufferedImage( img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_RGB );

		Graphics2D g = (Graphics2D) img2.getGraphics();
		g.drawImage( invert( img ), af, null );

		return invert( img2 );
	}

	private static double[][] subDiff(BufferedImage img1, BufferedImage img2, double epsilon){
		int width = img1.getWidth(), height = img1.getHeight();
		double[][] diff = new double[width * height][3];

		Color color1, color2;
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++){
				color1 = new Color( img1.getRGB( i, j ) );
				color2 = new Color( img2.getRGB( i, j ) );
				diff[i + width * j][0] = ( color2.getRed() - color1.getRed() ) / epsilon;
				diff[i + width * j][1] = ( color2.getGreen() - color1.getGreen() ) / epsilon;
				diff[i + width * j][2] = ( color2.getBlue() - color1.getBlue() ) / epsilon;
			}

		return diff;
	}

	private static BufferedImage applyDiff(BufferedImage img, double[][] diff, double alpha){
		int width = img.getWidth(), height = img.getHeight();

		BufferedImage img2 = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++){
				double[] diff1 = diff[i + j * width];
				img2.setRGB( i, j, img.getRGB( i, j )
						+ (int) ( alpha * ( 256 * 256 * diff1[0] + 256 * diff1[1] + diff1[2] ) ) );
			}

		return img2;
	}

}
