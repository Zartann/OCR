package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import recognition.ImagePoint;


public class TestingImageReader{

	public static String trainingImages = "resources/t10k-images.idx3-ubyte",
			trainingLabels = "resources/t10k-labels.idx1-ubyte";

	public static byte[] bufImages = loadTrainingImages(), bufLabels = loadTrainingLabels();

	private static byte[] loadTrainingImages(){
		try{
			return Files.readAllBytes( Paths.get( trainingImages ) );
		}
		catch (IOException e){
			e.printStackTrace();
			System.exit( 1 );
		}
		return null;
	}

	private static byte[] loadTrainingLabels(){
		try{
			return Files.readAllBytes( Paths.get( trainingLabels ) );
		}
		catch (IOException e){
			e.printStackTrace();
			System.exit( 1 );
		}
		return null;
	}

	public static void main(String[] args){
		read();
	}

	/**
	 * Fonction de test lisant des images
	 */
	private static void read(){
		try{

//			for(int i = 0; i < 9; i++)
//				readNextImage();
			System.out.println( bufLabels[labelOffset] );
			BufferedImage image = readNextImage().getImage();

			ImageDisplayFrame disp = new ImageDisplayFrame( image, "Label : "
					+ bufLabels[labelOffset - 1] );

			ImageIO.write( image, "jpg", new File( "test.jpg" ) );
			// for(int i = 0; i < 1000; i++)
			// System.out.println( bufLabels[i] );

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public static int imagesOffset = 16, labelOffset = 8;

	public static int width = 28, height = 28;

	/**
	 * Lit la prochaine image des donn�es
	 * 
	 * @return
	 */
	public static ImagePoint readNextImage(){

		int label = bufLabels[labelOffset];
		// System.out.println( label );
		labelOffset++;

		float[] img = new float[width * height];

		for(int j = 0; j < height; j++)
			for(int i = 0; i < width; i++){
				//Conversion du byte en entier non sign� et passage en niveau de gris correct
				img[i + j * width] = 255 - (bufImages[imagesOffset] & 0xFF);
				imagesOffset++;
			}

		return new ImagePoint( img, width, height, label );
	}

}
