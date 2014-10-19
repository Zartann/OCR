package recognition.classification;

import helper.ImageDisplayFrame;
import helper.TestingImageReader;

import java.awt.image.BufferedImage;
import java.util.TreeMap;

import recognition.ImagePoint;
import recognition.training.KNNTraining;


public class KNNClassifier{

	private static final int k = 3;

	/**
	 * Reconna�t le caract�re envoy� en image Suppose que l'entra�nement est d�j� fait
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
	 * Pr�traitement de l'image
	 * 
	 * @param imgp
	 */
	public static void pretreat(ImagePoint imgp){

	}

	public static void main(String[] args){
		KNNTraining.train();
		System.out.println( "Apprentissage effectu�" );

		int nbTests = 1000;
		int nbErreurs = 0;
		for(int i = 1; i <= nbTests; i++){
			System.out.println( "Test n�" + i + " :" );

			ImagePoint imgp = TestingImageReader.readNextImage();

			// ImageDisplayFrame disp = new ImageDisplayFrame( imgp.image );
			// disp.changeImage( imgp.image );

			int recogLbl = recognize( imgp ), lbl = imgp.getLabel();
			BufferedImage img = imgp.getImage();

			if (recogLbl != lbl){
				nbErreurs++;
				ImageDisplayFrame disp = new ImageDisplayFrame( img );
				disp.changeImage( img );
				System.err.println( "ERROR Test n�" + i + " : Vrai = " + lbl + " - Trouv� = "
						+ recogLbl );
			}
			System.out.println( "Vrai label : " + lbl );
			System.out.println( "Label �valu� : " + recogLbl );
			System.out.println();
		}

		System.out.println( nbTests + " tests effectu�s : " + nbErreurs + " erreurs" );
		System.exit( 1 );
	}
}