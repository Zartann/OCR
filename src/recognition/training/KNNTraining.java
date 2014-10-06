package recognition.training;

import helper.TrainingImageReader;

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
		
		for(int i = 0; i < 60000; i++)
			imagesList.add(TrainingImageReader.readNextImage());
	}
	
	public static void main(String[] args){
		train();
		
		int i = 0;
		for(ImagePoint imgp : imagesList){
			try{
				ImageIO.write( imgp.image, "jpg", new File("resources/train-images/image" + i + "-" + imgp.label + ".jpg") );
			}
			catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

}
