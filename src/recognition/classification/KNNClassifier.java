package recognition.classification;

import helper.ImageDisplayFrame;
import helper.TestingImageReader;

import java.util.Map;
import java.util.TreeMap;

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
			labels[map.pollFirstEntry().getValue().label]++;

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

		int nbTests = 1000;
		int nbErreurs = 0;
		for(int i = 1; i <= nbTests; i++){
			System.out.println("Test n°" + i + " :");
			
			ImagePoint imgp = TestingImageReader.readNextImage();

//			ImageDisplayFrame disp = new ImageDisplayFrame( imgp.image );
//			disp.changeImage( imgp.image );

			int lbl = recognize( imgp );
			
			if(lbl != imgp.label){
				nbErreurs++;
				ImageDisplayFrame disp = new ImageDisplayFrame( imgp.image );
				disp.changeImage( imgp.image );
				System.err.println("ERROR Test n°" + i + " : Vrai = " + imgp.label  + " - Trouvé = " + lbl);
			}
			System.out.println( "Vrai label : " + imgp.label );
			System.out.println( "Label évalué : " + lbl );
			System.out.println();
		}
		
		System.out.println(nbTests + " tests effectués : " + nbErreurs + " erreurs");
		System.exit( 1 );
	}
}
