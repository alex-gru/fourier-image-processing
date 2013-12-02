import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

package FeatureVector;

public class FeatureVector {

	private static Picture picture = new Picture();
	ArrayList<ArrayList<Double>> Allfeatures;
	final int BANDWIDTH = 2; // depth of a Ring (i.e. square) must be constant and a power two

	static String file;	//= "/home/aubin/Arbeitsfläche/";
	static BufferedReader br = null;
	static String line = "";
	static String comma = ",";

	public ArrayList<ArrayList<Double>> extractVector(double[][] pic) {
		int picSize = pic[0].length;
		int squareSize = 0;
		double tempSum = 0;
		int tempCounter = 0;
		ArrayList<Double> featureVector = new ArrayList<Double>();	// innerer ring + aeusserer ring
		ArrayList<Double> ringFeatureVector = new ArrayList<Double>();	// ringe einzeln als fv koordinate
		//Allfeatures = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		for (int i = picSize / 2; i >= 0; squareSize += 2 * BANDWIDTH, i = i - BANDWIDTH) {
			int heigth = i;
			int width = i;
			int radius = squareSize / 2;
			int separator = BANDWIDTH;
			double ringSum = 0;
			int ringCounter = 0;

			for (int col = heigth; col < i + radius; col++) {
				if (col < i + BANDWIDTH) {
					for (int line = width; line < width + radius; line++) {
						ringSum += pic[col][line]
								+ pic[col][picSize - 1 - line]
								+ pic[picSize - 1 - col][line]
								+ pic[picSize - 1 - col][picSize - 1 - line];
						ringCounter += 4;
					}
				} else {
					for (int line = width; line < width + separator; line++) {
						ringSum += pic[col][line]
								+ pic[col][picSize - 1 - line]
								+ pic[picSize - 1 - col][line]
								+ pic[picSize - 1 - col][picSize - 1 - line];
						ringCounter += 4;
					}
				}
			}
			 System.out.println("square size....." + picSize + "pixel \n"
						 + "ringSquare	" + ringCounter + "	pixel	"
						 + "	valueOfRingSquare	" + ringSum
						 + "	RingMiddleValue		" + ringSum / ringCounter);
			if (ringCounter != 0){ // { // vermeiden NAN in
				ringFeatureVector.add(ringSum / ringCounter);
			}
			tempSum += ringSum;
			tempCounter += ringCounter;
			if (tempCounter != 0) { // vermeiden NAN in
				featureVector.add(tempSum / tempCounter);
			}
			System.out.println("fullSquare	" + tempCounter + "	pixel	"
					 + "	ValueOfSquare		" + tempSum
					 + "	 FullMiddleValue	" + tempSum / tempCounter + "\n");
		}
		result.add(featureVector);
		result.add(ringFeatureVector);
		return result;
	}

}
