package recognition.classification;

import helper.TestingImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import recognition.ImagePoint;
import recognition.training.KNNTraining;
//import recognition.training.KNNTraining;


public class KNNClassifier{

	private static final int defaultK = 3;
	private static final int defaultDist = 0;

	/**
	 * Reconnaît le caractère envoyé en image Suppose que l'entraînement est déjà fait
	 * 
	 * @param imgp
	 */
	public static int recognize(ImagePoint imgp, int dist, int k, LinkedList<ImagePoint> list){
		pretreat( imgp );
		TreeMap<Double, ImagePoint> map = new TreeMap<Double, ImagePoint>();

		for(ImagePoint imgp2 : list){
			map.put( imgp.distance( imgp2, dist ), imgp2 );
		}
		
		double bestDist = imgp.distance(map.firstEntry().getValue(), dist );
		System.out.println("Best Distance = " + bestDist);
		
		int[] labels = new int[10];
		for(int i = 0; i < k; i++)
			labels[map.pollFirstEntry().getValue().getLabel()]++;

		int lbl = 0;
		for(int i = 0; i < labels.length; i++)
			if (labels[i] > labels[lbl])
				lbl = i;

		return lbl;
	}
	
	public static int recognize(ImagePoint imgp, int dist, int k){
		return recognize(imgp, dist, k, KNNTraining.imagesList);
	}

	/**
	 * Prétraitement de l'image
	 * 
	 * @param imgp
	 */
	public static void pretreat(ImagePoint imgp){

	}

	public static void main(String[] args){
		System.out.println("K = " + defaultK + " - Dist = " + defaultDist);
		
		KNNTraining.train();
		System.out.println( "Apprentissage effectué" );

		// ImageDisplayFrame disp = new ImageDisplayFrame( new BufferedImage( 28, 28,
		// BufferedImage.TYPE_INT_RGB ), "Current image" );

		int nbTests = 100;
		int nbErreurs = 0;
		for(int i = 1; i <= nbTests; i++){
			System.out.println( "Test n°" + i + " :" );

			ImagePoint imgp = TestingImageReader.readNextImage();

			// disp.changeImage( img );

			// ImageDisplayFrame disp = new ImageDisplayFrame( imgp.image );
			// disp.changeImage( imgp.image );

			int lbl = imgp.getLabel(), recogLbl = recognize( imgp, defaultDist, defaultK );

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
