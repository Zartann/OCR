package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import recognition.ImagePoint;


public class TrainingImageReader{

	public static String trainingImages = "resources/train-images.idx3-ubyte",
			trainingLabels = "resources/train-labels.idx1-ubyte";

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

			for(int i = 0; i < 8; i++)
				readNextImage();
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

	public static ImagePoint readNextImage(){

		int label = bufLabels[labelOffset];
		// System.out.println(label);
		labelOffset++;

		int[] img = new int[width * height];

		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++){
				img[j + i * height] = 255 - bufImages[imagesOffset];
				imagesOffset++;
			}

		return new ImagePoint( img, width, height, label );

	}

}
