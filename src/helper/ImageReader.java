package helper;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;

import sun.misc.IOUtils;


public class ImageReader{

	public static String trainingImages = "resources/train-images.idx3-ubyte",
			trainingLabels = "resources/train-labels.idx1-ubyte";
	
	public static byte[] bufImages = loadTrainingImages(),
	 bufLabels = loadTrainingLabels();
	
	private static byte[] loadTrainingImages(){
		try{	return Files.readAllBytes( Paths.get( trainingImages ) );	}
		catch (IOException e){
			e.printStackTrace();
			System.exit( 1 );
		}
		return null;
	}
	
	private static byte[] loadTrainingLabels(){
		try{	return Files.readAllBytes( Paths.get( trainingLabels ) );	}
		catch (IOException e){
			e.printStackTrace();
			System.exit( 1 );
		}
		return null;
	}

	public static void main(String[] args){
		read();
	}

	public static void read(){
		try{
			
			for(int i = 0; i < 8; i++)
				readNextImage();
			BufferedImage image = readNextImage();
			
			ImageDisplayFrame disp = new ImageDisplayFrame( image );
			disp.changeImage( image );
			ImageIO.write( image, "jpg", new File("test.jpg") );
			
//			for(int i = 0; i < 1000; i++)
//				System.out.println( bufLabels[i] );

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private static int imagesOffset = 16, labelOffset = 8;
	
	public static int width = 28, height = 28;
	
	public static BufferedImage readNextImage(){
		
//		int height = 0;
//		for(int i = 0; i < 4; i++){
//			height <<= 4;
//			height += bufImages[imagesOffset];
//			imagesOffset++;
//		}
//		System.out.println(height);
//		
//		int width = 0;
//		for(int i = 0; i < 4; i++){
//			width <<= 4;
//			width += bufImages[imagesOffset];
//			imagesOffset++;
//		}
//		System.out.println(width);
		
		System.out.println(bufLabels[labelOffset]);
		
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++){
//				System.out.println(i + " " + j + " " + bufImages[imagesOffset]);
				image.setRGB( j, i, (255 - bufImages[imagesOffset]) * 0x00010101 );
				imagesOffset++;
			}
		
		labelOffset++;
		return image;
	}

}