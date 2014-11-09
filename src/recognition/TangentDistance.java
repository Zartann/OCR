package recognition;

import Jama.LUDecomposition;
import Jama.Matrix;
import transformations.TransformationType;


class TangentDistance{

	static double calculateTangentDistance(ImagePoint img1, ImagePoint img2){

		double[][] tangentsE = 
				img1.getTangents();
//		 calculateTangents( img1.getImageArray(), 28, 28, 0 );
		double[][] tangentsP = 
				img2.getTangents();
//		 calculateTangents( img2.getImageArray(), 28, 28, 0 );

		Matrix LeT = new Matrix( tangentsE ), LpT = new Matrix( tangentsP );
		Matrix Le = LeT.transpose(), Lp = LpT.transpose();

		Matrix Lee = LeT.times( Le ), Lpp = LpT.times( Lp );

		Matrix LeeI = Lee.inverse(), LppI = Lpp.inverse();

		Matrix Lep = LeT.times( Lp ), Lpe = Lep.transpose();

		Matrix E = new Matrix( img1.getImageArray(), img1.getImageArray().length ), P = new Matrix(
				img2.getImageArray(), img2.getImageArray().length );

		Matrix temp1 = Lpe.times( LeeI ).times( LeT ).minus( LpT ).times( E.minus( P ) );
		LUDecomposition lu1 = Lpe.times( LeeI ).times( Lep ).minus( Lpp ).lu();

		Matrix alphaP = lu1.solve( temp1 );

		Matrix temp2 = Lep.times( LppI ).times( LpT ).minus( LeT ).times( E.minus( P ) );
		LUDecomposition lu2 = Lee.minus( Lep.times( LppI ).times( Lpe ) ).lu();

		Matrix alphaE = lu2.solve( temp2 );

		Matrix E2 = E.plus( Le.times( alphaE ) ), P2 = P.plus( Lp.times( alphaP ) );

		double dist = E2.minus( P2 ).norm2();

		// System.out.println( "Distance = " + dist );

		return dist;
	}

	private static final double TEMPLATE_FACTOR_1 = 0.1667, TEMPLATE_FACTOR_2 = 0.6667,
			TEMPLATE_FACTOR_3 = 0.08;
	private static final double ADDITIVE_BRIGHTNESS_VALUE = 0.1;

	// Two dimensional access on images saved in one dimensional array
	private static int tdIndex(int y, int x, int width){
		return y * width + x;
	}

	static double[][] calculateTangents(double[] image, int height, int width, double bg){

		double[][] tangents = new double[3][height * width];

		tangents[0] = calculateTangent( image, height, width, TransformationType.TranslationX, bg );
		tangents[1] = calculateTangent( image, height, width, TransformationType.TranslationY, bg );
		tangents[2] = calculateTangent( image, height, width, TransformationType.Rotation, bg );
//		tangents[3] = calculateTangent( image, height, width, TransformationType.Scaling, bg );
//		tangents[4] = calculateTangent( image, height, width, TransformationType.Thickening, bg );
//		tangents[5] = calculateTangent( image, height, width, TransformationType.Hyperbolic1, bg );
//		tangents[6] = calculateTangent( image, height, width, TransformationType.Hyperbolic2, bg );

		return tangents;
	}

	private static double[] calculateTangent(double[] image, int height, int width,
			TransformationType type, double background){
		int j, k, ind, maxdim;
		double tp, factorW, offsetW, factorH, factor, offsetH, halfbg;
		double[] tmp, x1, x2;

		int size = height * width;
		maxdim = ( height > width ) ? height : width;

		tmp = new double[maxdim];
		x1 = new double[size];
		x2 = new double[size];

		factorW = ( (double) width * 0.5 );
		offsetW = 0.5 - factorW;
		factorW = 1.0 / factorW;

		factorH = ( (double) height * 0.5 );
		offsetH = 0.5 - factorH;
		factorH = 1.0 / factorH;

		factor = ( factorH < factorW ) ? factorH : factorW; // min

		halfbg = 0.5 * background;

		/* x1 shift along width */
		/* first use mask 1 0 -1 */
		for(k = 0; k < height; k++){
			/* first column */
			ind = tdIndex( k, 0, width );
			x1[ind] = halfbg - image[ind + 1] * 0.5;
			/* other columns */
			for(j = 1; j < width - 1; j++){
				ind = tdIndex( k, j, width );
				x1[ind] = ( image[ind - 1] - image[ind + 1] ) * 0.5;
			}
			/* last column */
			ind = tdIndex( k, width - 1, width );
			x1[ind] = image[ind - 1] * 0.5 - halfbg;
		}
		/* now compute 3x3 template */
		/* first line */
		for(j = 0; j < width; j++){
			tmp[j] = x1[j];
			x1[j] = TEMPLATE_FACTOR_2 * x1[j] + TEMPLATE_FACTOR_1 * x1[j + width];
		}
		/* other lines */
		for(k = 1; k < height - 1; k++)
			for(j = 0; j < width; j++){
				ind = tdIndex( k, j, width );
				tp = x1[ind];
				x1[ind] = TEMPLATE_FACTOR_1 * tmp[j] + TEMPLATE_FACTOR_2 * x1[ind]
						+ TEMPLATE_FACTOR_1 * x1[ind + width];
				tmp[j] = tp;
			}
		/* last line */
		for(j = 0; j < width; j++){
			ind = tdIndex( height - 1, j, width );
			x1[ind] = TEMPLATE_FACTOR_1 * tmp[j] + TEMPLATE_FACTOR_2 * x1[ind];
		}
		/* now add the remaining parts outside the 3x3 template */
		/* first two columns */
		for(j = 0; j < 2; j++)
			for(k = 0; k < height; k++){
				ind = tdIndex( k, j, width );
				x1[ind] += TEMPLATE_FACTOR_3 * background;
			}
		/* other columns */
		for(j = 2; j < width; j++)
			for(k = 0; k < height; k++){
				ind = tdIndex( k, j, width );
				x1[ind] += TEMPLATE_FACTOR_3 * image[ind - 2];
			}
		for(j = 0; j < width - 2; j++)
			for(k = 0; k < height; k++){
				ind = tdIndex( k, j, width );
				x1[ind] -= TEMPLATE_FACTOR_3 * image[ind + 2];
			}
		/* last two columns */
		for(j = width - 2; j < width; j++)
			for(k = 0; k < height; k++){
				ind = tdIndex( k, j, width );
				x1[ind] -= TEMPLATE_FACTOR_3 * background;
			}

		/* x2 shift along height */
		/* first use mask 1 0 -1 */
		for(j = 0; j < width; j++){
			/* first line */
			x2[j] = halfbg - image[j + width] * 0.5;
			/* other lines */
			for(k = 1; k < height - 1; k++){
				ind = tdIndex( k, j, width );
				x2[ind] = ( image[ind - width] - image[ind + width] ) * 0.5;
			}
			/* last line */
			ind = tdIndex( height - 1, j, width );
			x2[ind] = image[ind - width] * 0.5 - halfbg;
		}

		/* now compute 3x3 template */
		/* first column */
		for(j = 0; j < height; j++){
			ind = tdIndex( j, 0, width );
			tmp[j] = x2[ind];
			x2[ind] = TEMPLATE_FACTOR_2 * x2[ind] + TEMPLATE_FACTOR_1 * x2[ind + 1];
		}
		/* other columns */
		for(k = 1; k < width - 1; k++)
			for(j = 0; j < height; j++){
				ind = tdIndex( j, k, width );
				tp = x2[ind];
				x2[ind] = TEMPLATE_FACTOR_1 * tmp[j] + TEMPLATE_FACTOR_2 * x2[ind]
						+ TEMPLATE_FACTOR_1 * x2[ind + 1];
				tmp[j] = tp;
			}
		/* last column */
		for(j = 0; j < height; j++){
			ind = tdIndex( j, width - 1, width );
			x2[ind] = TEMPLATE_FACTOR_1 * tmp[j] + TEMPLATE_FACTOR_2 * x2[ind];
		}

		/* now add the remaining parts outside the 3x3 template */
		for(j = 0; j < 2; j++)
			for(k = 0; k < width; k++){
				ind = tdIndex( j, k, width );
				x2[ind] += TEMPLATE_FACTOR_3 * background;
			}
		for(j = 2; j < height; j++)
			for(k = 0; k < width; k++){
				ind = tdIndex( j, k, width );
				x2[ind] += TEMPLATE_FACTOR_3 * image[ind - 2 * width];
			}
		for(j = 0; j < height - 2; j++)
			for(k = 0; k < width; k++){
				ind = tdIndex( j, k, width );
				x2[ind] -= TEMPLATE_FACTOR_3 * image[ind + 2 * width];
			}
		for(j = height - 2; j < height; j++)
			for(k = 0; k < width; k++){
				ind = tdIndex( j, k, width );
				x2[ind] -= TEMPLATE_FACTOR_3 * background;
			}

		/* now go through the tangents */

		double[] tangent = new double[size];

		switch (type){
			case TranslationX:
				for(ind = 0; ind < size; ind++)
					tangent[ind] = x1[ind];
				break;

			case TranslationY:
				for(ind = 0; ind < size; ind++)
					tangent[ind] = x2[ind];
				break;

			case Rotation:
				ind = 0;
				for(k = 0; k < height; k++)
					for(j = 0; j < width; j++){
						tangent[ind] = ( ( k + offsetH ) * x1[ind] - ( j + offsetW ) * x2[ind] )
								* factor;
						ind++;
					}
				break;

			case Scaling:
				ind = 0;
				for(k = 0; k < height; k++)
					for(j = 0; j < width; j++){
						tangent[ind] = ( ( j + offsetW ) * x1[ind] + ( k + offsetH ) * x2[ind] )
								* factor;
						ind++;
					}
				break;

			case Thickening:
				ind = 0;
				for(k = 0; k < height; k++)
					for(j = 0; j < width; j++){
						tangent[ind] = x1[ind] * x1[ind] + x2[ind] * x2[ind];
						ind++;
					}
				break;

			case Hyperbolic1:
				ind = 0;
				for(k = 0; k < height; k++)
					for(j = 0; j < width; j++){
						tangent[ind] = ( ( j + offsetW ) * x1[ind] - ( k + offsetH ) * x2[ind] )
								* factor;
						ind++;
					}
				break;

			case Hyperbolic2:
				ind = 0;
				for(k = 0; k < height; k++)
					for(j = 0; j < width; j++){
						tangent[ind] = ( ( k + offsetH ) * x1[ind] + ( j + offsetW ) * x2[ind] )
								* factor;
						ind++;
					}
				break;

			case AdditiveBrightness:
				for(ind = 0; ind < size; ind++)
					tangent[ind] = ADDITIVE_BRIGHTNESS_VALUE;
				break;

			case MultiplicativeBrightness:
				for(ind = 0; ind < size; ind++)
					tangent[ind] = image[ind];
				break;

			default:
				throw new IllegalArgumentException( "Unknown Transformation" );
		}

		return tangent;
	}

}
