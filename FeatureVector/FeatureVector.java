
/**
	
@@@@@@@@@@@@@@@@@

**/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FeatureVector {

	private static Picture picture = new Picture();
	ArrayList<ArrayList<Double>> Allfeatures;
	final int BANDWIDTH = 16; // deep of a Ring must be constant and a power two

	
	private ArrayList<ArrayList<Double>> extractVector(double[][] pic) {

		int picSize = pic[0].length;
		int squareSize = 0;
		double tempSum = 0;
		double tempSumQuadrat = 0;
		int tempCounter = 0;
		ArrayList<Double> featureVector = new ArrayList<Double>();
		ArrayList<Double> ringFeatureVector = new ArrayList<Double>();
		ArrayList<Double> standAbweichung = new ArrayList<Double>();
		ArrayList<Double> ringStdAbweichung = new ArrayList<Double>();
		Allfeatures = new ArrayList<ArrayList<Double>>();
		for (int i = picSize / 2; i >= 0; squareSize += 2 * BANDWIDTH, i = i
				- BANDWIDTH) {
			int heigth = i;
			int width = i;
			int radius = squareSize / 2;
			int separator = BANDWIDTH;
			double ringSum = 0; double ringSumQuadrat=0;
			int ringCounter = 0;

			for (int col = heigth; col < i + radius; col++) {
				if (col < i + BANDWIDTH) {
					for (int line = width; line < width + radius; line++) {
						ringSum += pic[col][line]
								+ pic[col][picSize - 1 - line]
								+ pic[picSize - 1 - col][line]
								+ pic[picSize - 1 - col][picSize - 1 - line];
								
							ringSumQuadrat += Math.pow(pic[col][line],2)
								+ Math.pow(pic[col][picSize - 1 - line],2)
								+ Math.pow(pic[picSize - 1 - col][line],2)
								+ Math.pow(pic[picSize - 1 - col][picSize - 1 - line],2) ;	

						ringCounter += 4;
						
					}
				} else {
					for (int line = width; line < width + separator; line++) {
						ringSum += pic[col][line]
								+ pic[col][picSize - 1 - line]
								+ pic[picSize - 1 - col][line]
								+ pic[picSize - 1 - col][picSize - 1 - line];
								
						ringSumQuadrat += Math.pow(pic[col][line],2)
								+ Math.pow(pic[col][picSize - 1 - line],2)
								+ Math.pow(pic[picSize - 1 - col][line],2)
								+ Math.pow(pic[picSize - 1 - col][picSize - 1 - line],2);
						
						ringCounter += 4;
					}
				}
			}
			System.out.println("square size....." + picSize + "pixel \n"
					+ "ringSquare	" + ringCounter + "	pixel	"
					+ "	valueOfRingSquare	" + ringSum + "	RingMiddleValue		"
					+ ringSum / ringCounter+ "	sumQuadrat	"+ringSumQuadrat);
			if (ringCounter != 0)  { // vermeiden NAN in
				ringFeatureVector.add(ringSum / ringCounter);
				ringStdAbweichung.add(Math.sqrt((ringSumQuadrat-(ringCounter*Math.pow(ringSum / ringCounter,2)))/(ringCounter-1)));
			}
			tempSum += ringSum;
			tempSumQuadrat+= ringSumQuadrat;
			tempCounter += ringCounter;
			if (tempCounter != 0) { // vermeiden NAN in
				featureVector.add(tempSum / tempCounter);
				standAbweichung.add( Math.sqrt((tempSumQuadrat-(tempCounter*Math.pow(tempSum /tempCounter,2)))/(tempCounter-1)));
			}
			System.out.println("fullSquare	" + tempCounter + "	pixel	"
					+ "	ValueOfSquare		" + tempSum + "	 FullMiddleValue	"
					+ tempSum / tempCounter + "	sumQuadrat	"+tempSumQuadrat+"\n");
		}
		Allfeatures.add(featureVector);
		Allfeatures.add(ringFeatureVector);
		Allfeatures.add(standAbweichung);
		Allfeatures.add(ringStdAbweichung);
		System.out.println("			@@ RingMittelwert[0]...FullMittelwert[1]...RingStdAb[2]...FullstdAb[3] @@@\n");
		int i = 0;
		while (!Allfeatures.isEmpty() && i < Allfeatures.size()) {
			System.out.println("feature[" + i + "]..." + Allfeatures.get(i) );
			i++;
		}
		return Allfeatures;
	}


	private ArrayList<ArrayList<Double>> extractWedge(double[][] pic) {

		int size = pic[0].length;
		int radius = size / 2;
		double sum = 0;
		double sum1 = 0;
		double sum2 = 0;
		double sum3 = 0;
		int counter = 0;
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		double leftDiagonale = 0.0;
		double rightDiagonale = 0.0;
		Allfeatures = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> featureVector = new ArrayList<Double>();
		ArrayList<Double> StandardAbweichung = new ArrayList<Double>();
		double sumQuadrat = 0;
		double sumQuadrat1 = 0;
		double sumQuadrat2 = 0;
		double sumQuadrat3 = 0;
		double sumQuadratrightdiag=0;
		double sumQuadratleftdiag=0;
		int diagr=0; int diagl=0;
		// standardaweichug : S=wurzel()
		for (int i = 0; i < radius; i++) {
			
			sumQuadratleftdiag=sumQuadratleftdiag+Math.pow(pic[i][i],2)+Math.pow(pic[size - 1 - i][size - 1 - i],2);
			leftDiagonale += pic[i][i] + pic[size - 1 - i][size - 1 - i]; 
			diagl+=2;
			
			sumQuadratrightdiag=sumQuadratrightdiag+Math.pow(pic[size - 1 - i][i],2)+Math.pow(pic[i][size - 1 - i],2);
			rightDiagonale += pic[size - 1 - i][i] + pic[i][size - 1 - i];
			diagr+=2;
		}
		StandardAbweichung.add(Math.sqrt((sumQuadratleftdiag-(size*Math.pow(leftDiagonale / size,2)))/(size-1)));
		StandardAbweichung.add(Math.sqrt((sumQuadratrightdiag-(size*Math.pow(rightDiagonale / size,2)))/(size-1)));
		featureVector.add(leftDiagonale / size);
		featureVector.add(rightDiagonale / size);
		
		// for(int phase=45; phase<90 ; phase+=45){
		for (int line = radius - 1; line > 0; line--) {
			for (int col = size - line; col < size; col++) {
				
				sumQuadrat=sumQuadrat+Math.pow(pic[line][col],2)+Math.pow( pic[size - line - 1][size - col - 1],2);
				sum = sum + pic[line][col]+ pic[size - line - 1][size - col - 1]; // 45...225
				counter += 2;
				
				sumQuadrat1=sumQuadrat1+ Math.pow(pic[line][size - col],2)+Math.pow(pic[size - line - 1][col],2);
				sum1 = sum1 + pic[line][size - col] + pic[size - line - 1][col];
				counter1 += 2;
				
				sumQuadrat2=sumQuadrat2+Math.pow(pic[radius - line - 1][col - 1],2)+Math.pow(pic[radius + line][size - col],2);
				sum2 = sum2 + pic[radius - line - 1][col - 1]+ pic[radius + line][size - col];
				counter2 += 2;
				
				 sumQuadrat3= sumQuadrat3+Math.pow(pic[radius - line - 1][size - col],2)+Math.pow(pic[radius + line][col - 1],2);
				sum3 = sum3 + pic[radius - line - 1][size - col]+ pic[radius + line][col - 1];
				counter3 += 2;
			}
		}
		System.out.println("sum45grad...	"+sum+"	pixel45grad...	"+counter+ "	quqdratSum...	"+ sumQuadrat+"\n"+
				"sum90grad...	"+sum3+"	pixel90grad...	"+counter3+ "	quadratSum3...	"+ sumQuadrat3+ "\n"+
				"sum135grad...	"+sum2+"	pixel135grad...	"+counter2+ "	quadratSum2...	"+ sumQuadrat2+"\n"+
				"sum180grad...	"+sum1+"	pixel180grad...	"+counter1+ "	quadratSum1...	"+ sumQuadrat1+"\n"+
				"leftdiag...	"+leftDiagonale+"		pixelldiag...	"+diagl+ "	quqdratSumLeftdiag...	"+ sumQuadratleftdiag+"\n"+
				"righttdiag...	"+ rightDiagonale+"		pixelrdiag...	"+diagr+ "	quqdratSumRightdiag...	"+ sumQuadratrightdiag);
		System.out.println();
		
		StandardAbweichung.add(Math.sqrt((sumQuadrat-(counter*Math.pow(sum / counter,2)))/(counter -1)));
		StandardAbweichung.add(Math.sqrt((sumQuadrat1-(counter1*Math.pow(sum1 / counter1,2)))/(counter1 -1)));
		StandardAbweichung.add(Math.sqrt((sumQuadrat2-(counter2*Math.pow(sum2 / counter2,2)))/(counter2 -1))); 
		StandardAbweichung.add(Math.sqrt((sumQuadrat3-(counter3*Math.pow(sum3 / counter3,2)))/(counter3 -1)));
		
		featureVector.add(sum / counter);
		featureVector.add(sum1 / counter1);
		featureVector.add(sum2 / counter2);
		featureVector.add(sum3 / counter3);
		int i = 0;
		
		System.out.println("				@@@@  WedgeMittelwert.............wedgeStdAb @@@@\n");
		while (!featureVector.isEmpty() && i < featureVector.size()) {
			System.out.println("feature[" + i + "]..." + featureVector.get(i)+ "		StandardAbweichung["+i+"]..."+StandardAbweichung.get(i));
			i++;
		}
		Allfeatures.add(featureVector);
		Allfeatures.add(StandardAbweichung);
		return Allfeatures ;
	}

	private void printPictureFeature(ArrayList<Double> feature) {
		int i = 0;
		while (!feature.isEmpty() && i < feature.size()) {
			System.out.println("feature [" + i + "]	" + feature.get(i));
			i++;
		}
		System.out.println();
	}

	public static void main(String[] args) {
		FeatureVector fv = new FeatureVector();
		double arr[][]=new double[256][256]; 
		for (int i=0;i<256;i++){
			for (int j=0;j<256;j++){
			arr[i][j]=j;
		//	System.out.print(arr[i][j]+"	");
		}
		System.out.println();
		}

		long start1 = System.currentTimeMillis();
		fv.extractVector(arr);
		//fv.featurevector(arr);
		long stop1 = System.currentTimeMillis();
		long time1 = stop1 - start1;
		System.out.println("Extract process time = ..........." + time1+"	ms" +
		"\n----------------------------------------------------------------------------------\n");
			long start = System.currentTimeMillis();
		//fv.extractVector(arr);
		fv.extractWedge(arr);
		long stop = System.currentTimeMillis();
		long time = stop - start;
		System.out.println("Wedge process time = ..........." + time +"	ms");
	}

}
