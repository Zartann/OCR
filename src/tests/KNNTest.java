package tests;

import helper.TestingImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import recognition.ImagePoint;
import recognition.classification.KNNClassifier;
import recognition.training.KNNTraining;


public class KNNTest{

	private static final String resultsPath = "resources/results/";
	private static PrintStream[][] results, resultsCSV;
	// resultD1K1, resultD1K3, resultD1K5, resultD2K1, resultD2K3,
	// resultD2K5, resultDInfK1, resultDInfK3, resultDInfK5;
	//
	// private static PrintStream resultD1K1CSV, resultD1K3CSV, resultD1K5CSV, resultD2K1CSV,
	// resultD2K3CSV, resultD2K5CSV, resultDInfK1CSV, resultDInfK3CSV, resultDInfK5CSV;

	private static int[][] nbErreurs;
	private static int numTest;

	public static void main(String[] args){

		long debut = System.currentTimeMillis();

		try{
			init();
		}
		catch (FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		KNNTraining.train();
		System.out.println( "Apprentissage effectué\n" );

		int nbTests = 1000;
		for(numTest = 1; numTest <= nbTests; numTest++){
			System.out.println( "Test n°" + numTest + " :" );

			ImagePoint imgp = TestingImageReader.readNextImage();

			for(int d = 0; d < 3; d++)
				for(int k = 0; k < 3; k++)
					recognize( imgp, d, k );

		}

		System.out.println( nbTests + " tests effectués : " );
		for(int d = 0; d < 3; d++){
			for(int k = 0; k < 3; k++){
				System.out.println( "Distance " + ( d == 0 ? "Inf" : ( "" + d ) ) + " - k = "
						+ ( 2 * k + 1 ) + " : " + nbErreurs[d][k] + " erreurs" );

				results[d][k].println( nbTests + " tests effectués : " + nbErreurs[d][k]
						+ " erreurs" );
				results[d][k].println();
				results[d][k].flush();
			}
			System.out.println();
		}
		System.out.println( "Temps écoulé : " + ( ( System.currentTimeMillis() - debut ) / 60000 )
				+ " minutes" );
		System.exit( 1 );
	}

	private static void init() throws FileNotFoundException{
		results = new PrintStream[3][3];
		resultsCSV = new PrintStream[3][3];
		nbErreurs = new int[3][3];
		numTest = 1;

		results[0][0] = new PrintStream( new File( resultsPath + "dInfk1.txt" ) );
		results[0][1] = new PrintStream( new File( resultsPath + "dInfk3.txt" ) );
		results[0][2] = new PrintStream( new File( resultsPath + "dInfk5.txt" ) );

		results[1][0] = new PrintStream( new File( resultsPath + "d1k1.txt" ) );
		results[1][1] = new PrintStream( new File( resultsPath + "d1k3.txt" ) );
		results[1][2] = new PrintStream( new File( resultsPath + "d1k5.txt" ) );

		results[2][0] = new PrintStream( new File( resultsPath + "d2k1.txt" ) );
		results[2][1] = new PrintStream( new File( resultsPath + "d2k3.txt" ) );
		results[2][2] = new PrintStream( new File( resultsPath + "d2k5.txt" ) );

		resultsCSV[0][0] = new PrintStream( new File( resultsPath + "dInfk1.csv" ) );
		resultsCSV[0][1] = new PrintStream( new File( resultsPath + "dInfk3.csv" ) );
		resultsCSV[0][2] = new PrintStream( new File( resultsPath + "dInfk5.csv" ) );

		resultsCSV[1][0] = new PrintStream( new File( resultsPath + "d1k1.csv" ) );
		resultsCSV[1][1] = new PrintStream( new File( resultsPath + "d1k3.csv" ) );
		resultsCSV[1][2] = new PrintStream( new File( resultsPath + "d1k5.csv" ) );

		resultsCSV[2][0] = new PrintStream( new File( resultsPath + "d2k1.csv" ) );
		resultsCSV[2][1] = new PrintStream( new File( resultsPath + "d2k3.csv" ) );
		resultsCSV[2][2] = new PrintStream( new File( resultsPath + "d2k5.csv" ) );
	}

	private static void recognize(ImagePoint imgp, int distAModif, int kAModif){

		int dist = distAModif == 0 ? -1 : distAModif, k = 2 * kAModif + 1;

		System.out.println( "Distance " + ( dist == -1 ? "Inf" : ( "" + dist ) ) + " - k = " + k );

		int lbl = imgp.getLabel();

		int recogLbl = KNNClassifier.recognize( imgp, dist, k );

		results[distAModif][kAModif].println( "Test n°" + numTest
				+ ( recogLbl != lbl ? "ERREUR" : "" ) );
		results[distAModif][kAModif].println( "Vrai label : " + lbl + " - Label évalué : "
				+ recogLbl );
		results[distAModif][kAModif].println();
		results[distAModif][kAModif].flush();

		resultsCSV[distAModif][kAModif].println( numTest + ";" + lbl + ";" + recogLbl );
		resultsCSV[distAModif][kAModif].flush();

		if (recogLbl != lbl){
			nbErreurs[distAModif][kAModif]++;

			try{
				BufferedImage img = imgp.getImage();
				ImageIO.write( img, "jpg", new File( "resources/errors/errorD"
						+ ( dist == -1 ? "Inf" : ( "" + dist ) ) + "K" + k + "/test" + numTest
						+ "V" + lbl + "R" + recogLbl + ".jpg" ) );
			}
			catch (IOException e){
				e.printStackTrace();
			}

			System.err.println( "ERROR Test n°" + numTest + " Distance "
					+ ( dist == -1 ? "Inf" : ( "" + dist ) ) + " - k = " + k + " : Vrai = " + lbl
					+ " - Trouvé = " + recogLbl );
		}
		System.out.println( "Vrai label : " + lbl );
		System.out.println( "Label évalué : " + recogLbl );
		System.out.println();
	}

}
