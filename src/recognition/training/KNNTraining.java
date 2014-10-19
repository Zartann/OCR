package recognition.training;

import helper.TrainingImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import recognition.ImagePoint;


public class KNNTraining{

	public static LinkedList<ImagePoint> imagesList = new LinkedList<ImagePoint>();

	/**
	 * Entraînement sur le set d'images via la méthode k-NN
	 */
	public static void train(){
		int imagesOffset = TrainingImageReader.imagesOffset, labelOffset = TrainingImageReader.labelOffset;

		TrainingImageReader.imagesOffset = 16;
		TrainingImageReader.labelOffset = 8;

		ImagePoint imgp;
		for(int i = 0; i < 60000; i++){
			imgp = TrainingImageReader.readNextImage();
			pretreat( imgp );
			imagesList.add( imgp );
		}

		TrainingImageReader.imagesOffset = imagesOffset;
		TrainingImageReader.labelOffset = labelOffset;
	}

	public static void pretreat(ImagePoint imgp){

	}

	public static void main(String[] args){
		train();

		int i = 0;
		for(ImagePoint imgp : imagesList){
			try{
				BufferedImage img = imgp.getImage();
				int lbl = imgp.getLabel();
				ImageIO.write( img, "jpg", new File( "resources/train-images/image" + i + "-" + lbl
						+ ".jpg" ) );
			}
			catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

}
