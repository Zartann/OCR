package recognition.classification;

import helper.ImageDisplayFrame;
import helper.TestingImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import recognition.ImagePoint;
import recognition.training.KNNTraining;


public class KNNClassifier{

	private static final int k = 3;

	/**
	 * Reconnaît le caractère envoyé en image Suppose que l'entraînement est déjà fait
	 * 
	 * @param imgp
	 */
	public static int recognize(ImagePoint imgp){
		pretreat( imgp );
		TreeMap<Double, ImagePoint> map = new TreeMap<Double, ImagePoint>();

		for(ImagePoint imgp2 : KNNTraining.imagesList){
			map.put( imgp.distanceEuclidienne( imgp2 ), imgp2 );
		}

		int[] labels = new int[10];
		for(int i = 0; i < k; i++)
			labels[map.pollFirstEntry().getValue().getLabel()]++;

		int lbl = 0;
		for(int i = 0; i < labels.length; i++)
			if (labels[i] > labels[lbl])
				lbl = i;

		return lbl;
	}

	/**
	 * Prétraitement de l'image
	 * 
	 * @param imgp
	 */
	public static void pretreat(ImagePoint imgp){

	}

	public static void main(String[] args){
		KNNTraining.train();
		System.out.println( "Apprentissage effectué" );

//		ImageDisplayFrame disp = new ImageDisplayFrame( new BufferedImage( 28, 28,
//				BufferedImage.TYPE_INT_RGB ), "Current image" );

		int nbTests = 10000;
		int nbErreurs = 0;
		for(int i = 1; i <= nbTests; i++){
			System.out.println( "Test n°" + i + " :" );

			ImagePoint imgp = TestingImageReader.readNextImage();
			
//			disp.changeImage( img );

			// ImageDisplayFrame disp = new ImageDisplayFrame( imgp.image );
			// disp.changeImage( imgp.image );

			int lbl = imgp.getLabel(), recogLbl = recognize( imgp );

			if (recogLbl != lbl){
				nbErreurs++;
				// ImageDisplayFrame disp = new ImageDisplayFrame( img, "ERROR Test n°" + i
				// + " : Vrai = " + lbl + " - Trouvé = " + recogLbl );

				try{
					BufferedImage img = imgp.getImage();
					ImageIO.write( img, "jpg", new File( "resources/errors/errorNew-test" + i + "v"
							+ lbl + "r" + recogLbl + ".jpg" ) );
				}
				catch (IOException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.err.println( "ERROR Test n°" + i + " : Vrai = " + lbl + " - Trouvé = "
						+ recogLbl );
			}
			System.out.println( "Vrai label : " + lbl );
			System.out.println( "Label évalué : " + recogLbl );
			System.out.println();
		}

		System.out.println( nbTests + " tests effectués : " + nbErreurs + " erreurs" );
		System.exit( 1 );
	}
}
