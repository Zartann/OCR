package tests;

import helper.TestingImageReader;
import helper.TrainingImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import recognition.ImagePoint;
import recognition.classification.KNNClassifier;
import recognition.training.KNNTraining;


public class KNNCrossValidation{

	private static final int nombreTrainingImages = 10000, nombreTestingImages = 1000;
	private static final int tailleSets = 1000;

	private static final int numeroLancement = (int) ( Math.random() * 10000 );

	private static LinkedList<LinkedList<ImagePoint>> sets = new LinkedList<LinkedList<ImagePoint>>();

	private static void computeSets() throws FileNotFoundException{
		LinkedList<ImagePoint> list = new LinkedList<ImagePoint>();

		// Récupération des images de training
		int imagesOffset = TrainingImageReader.imagesOffset, labelOffset = TrainingImageReader.labelOffset;
		int imgN = TrainingImageReader.imgNum;

		TrainingImageReader.imagesOffset = 16;
		TrainingImageReader.labelOffset = 8;
		TrainingImageReader.imgNum = 0;

		ImagePoint imgp;
		for(int i = 0; i < nombreTrainingImages; i++){
			imgp = TrainingImageReader.readNextImage();
			KNNTraining.pretreat( imgp );
			list.add( imgp );
		}

		TrainingImageReader.imagesOffset = imagesOffset;
		TrainingImageReader.labelOffset = labelOffset;
		TrainingImageReader.imgNum = imgN;

		// Récupération des images de tests
		imagesOffset = TestingImageReader.imagesOffset;
		labelOffset = TestingImageReader.labelOffset;
		imgN = TestingImageReader.imgNum;

		TestingImageReader.imagesOffset = 16;
		TestingImageReader.labelOffset = 8;
		TestingImageReader.imgNum = 0;

		for(int i = 0; i < nombreTestingImages; i++){
			imgp = TestingImageReader.readNextImage();
			KNNTraining.pretreat( imgp );
			list.add( imgp );
		}

		TestingImageReader.imagesOffset = imagesOffset;
		TestingImageReader.labelOffset = labelOffset;
		TestingImageReader.imgNum = imgN;

		File dossierRacine = new File( "resources/crossValidation/" + numeroLancement );
		if (!dossierRacine.exists())
			dossierRacine.mkdir();

		PrintStream out = new PrintStream( new File( "resources/crossValidation/" + numeroLancement
				+ "/sets.txt" ) );

		// Création des sets
		Collections.shuffle( list );
		LinkedList<ImagePoint> l = new LinkedList<ImagePoint>();
		for(int i = 0; i < nombreTestingImages + nombreTrainingImages; i++){
			if (i % tailleSets == 0){
				if (!sets.isEmpty())
					out.println();
				out.flush();
				l = new LinkedList<ImagePoint>();
				sets.add( l );
			}
			ImagePoint img = list.poll();
			out.print( img.getName() + ";" );
			l.add( img );
		}

		out.flush();
		out.close();
	}

	private static int numSet, numTest;
	private static int[] nbErreurs;
	private static PrintStream out, outCSV;

	private static void validate(int dist, int k) throws FileNotFoundException{
		long debut = System.currentTimeMillis();

		File dossierErrors = new File( "resources/crossValidation/" + numeroLancement + "/errors" );
		if (!dossierErrors.exists())
			dossierErrors.mkdir();

		File sousDossierErrors = new File( "resources/crossValidation/" + numeroLancement
				+ "/errors/errorsd" + ( dist == -1 ? "Inf" : ( dist == 0 ? "Tgt" : dist ) ) + "k"
				+ k );
		if (!sousDossierErrors.exists())
			sousDossierErrors.mkdir();

		File dossierResults = new File( "resources/crossValidation/" + numeroLancement + "/results" );
		if (!dossierResults.exists())
			dossierResults.mkdir();

		out = new PrintStream( new File( "resources/crossValidation/" + numeroLancement
				+ "/results/resultd" + ( dist == -1 ? "Inf" : ( dist == 0 ? "Tgt" : dist ) ) + "k"
				+ k + "-" + numeroLancement + ".txt" ) );
		outCSV = new PrintStream( new File( "resources/crossValidation/" + numeroLancement
				+ "/results/resultd" + ( dist == -1 ? "Inf" : ( dist == 0 ? "Tgt" : dist ) ) + "k"
				+ k + "-" + numeroLancement + ".csv" ) );

		nbErreurs = new int[1 + ( nombreTestingImages + nombreTrainingImages ) / tailleSets];
		for(LinkedList<ImagePoint> testingSet : sets){
			numSet++;
			numTest = 0;

			LinkedList<ImagePoint> trainingSet = new LinkedList<ImagePoint>();
			for(LinkedList<ImagePoint> set : sets)
				if (set.equals( testingSet ))
					System.out.println( "Found" );
				else
					trainingSet.addAll( set );

			for(ImagePoint imgTest : testingSet){
				numTest++;
				recognize( imgTest, k, dist, trainingSet );
			}
		}

		System.out.println( "Tests effectués : Testing sets de taille " + tailleSets );
		out.println( "Tests effectués : Testing sets de taille " + tailleSets );
		for(int i = 1; i <= numSet; i++){
			System.out.println( "Set n°" + i + " : " + nbErreurs[i] + " erreurs" );
			out.println( "Set n°" + i + " : " + nbErreurs[i] + " erreurs" );
		}
		System.out.println( "\nTemps écoulé : " + ( ( System.currentTimeMillis() - debut ) / 60000 )
				+ " minutes\n" );
		out.println( "\nTemps écoulé : " + ( ( System.currentTimeMillis() - debut ) / 60000 )
				+ " minutes" );

		out.flush();
		out.close();
		outCSV.close();

	}

	private static void recognize(ImagePoint imgp, int k, int dist,
			LinkedList<ImagePoint> trainingSet){

		System.out.println( "Set n° " + numSet + " - Test n° " + numTest );

		int lbl = imgp.getLabel();

		int recogLbl = KNNClassifier.recognize( imgp, dist, k, trainingSet );

		out.println( "Set n°" + numSet + "Test n°" + numTest + " - Image " + imgp.getName()
				+ ( recogLbl != lbl ? " -> ERREUR" : "" ) );
		out.println( "Vrai label : " + lbl + " - Label évalué : " + recogLbl );
		out.println();
		out.flush();

		outCSV.println( numSet + ";" + numTest + ";" + imgp.getName() + ";" + lbl + ";" + recogLbl );
		outCSV.flush();

		if (recogLbl != lbl){
			nbErreurs[numSet - 1]++;

			try{
				BufferedImage img = imgp.getImage();
				ImageIO.write( img, "jpg", new File( "resources/crossValidation/" + numeroLancement
						+ "/errors/errorsd" + ( dist == -1 ? "Inf" : ( dist == 0 ? "Tgt" : dist ) )
						+ "k" + k + "/set" + numSet + "test" + numTest + imgp.getName() + "V" + lbl
						+ "R" + recogLbl + ".jpg" ) );
			}
			catch (IOException e){
				e.printStackTrace();
			}

			System.err.println( "ERROR Set n°" + numSet + " Test n°" + numTest + " Distance "
					+ ( dist == -1 ? "Inf" : ( dist == 0 ? "Tgt" : dist ) ) + " - k = " + k
					+ " : Vrai = " + lbl + " - Trouvé = " + recogLbl );
		}
		System.out.println( "Vrai label : " + lbl );
		System.out.println( "Label évalué : " + recogLbl );
		System.out.println();
	}

	public static void main(String[] args){
		try{
			computeSets();
			validate( 2, 1 );
			validate( 2, 3 );
			validate( 2, 5 );
			
			validate( 1, 1 );
			validate( 1, 3 );
			validate( 1, 5 );

			validate( -1, 1 );
			validate( -1, 3 );
			validate( -1, 5 );
		}
		catch (FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
