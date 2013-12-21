

/**
##Wir haben hier nur zwei methode, die relevant für unsere Aufgabe sind.
die erste ist extractVector(double[][] arr) liefert eine arraylist zurück. dieser ArrayList enthält sechs Arraylists, die die koordinate von Feature vektor in verschiedenen varianten enthalten.

####die zweite ist extractWedge(double[][] arr) , liefert auch ein Arraylist zurück.dieser ArrayList enthält drei Arraylists, die die Koordinate von feature vektor als kuchenstück , in den Varianten mittelwert,variance und standardabweichungen enthalten.

### Main Methode ist nicht brauchbar, kann beim Bedarf auskommentiert werden

#### Die Bandbreit (BANDWIDTH), könnten wir zu 16 oder 32 setzen, damit wir ein kürzere feature vector bekommen, und villeicht dadurch, die fehlerrate verbessern. 

**/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FeatureVector {

	ArrayList<ArrayList<Double>> Allfeatures;
	final int BANDWIDTH = 32; // deep of a Ring must be constant and a power two
	static double gefunden,s;//nur für debug
	
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
		ArrayList<Double> ringVariance = new ArrayList<Double>();
		ArrayList<Double> variance= new ArrayList<Double>();
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
			
			if (ringCounter != 0)  { // vermeiden NAN in
				ringFeatureVector.add(ringSum / ringCounter);
				ringVariance.add((ringSumQuadrat-(ringCounter*Math.pow(ringSum / ringCounter,2)))/(ringCounter-1));
				ringStdAbweichung.add(Math.sqrt((ringSumQuadrat-(ringCounter*Math.pow(ringSum / ringCounter,2)))/(ringCounter-1)));
			}
			tempSum += ringSum;
			tempSumQuadrat+= ringSumQuadrat;
			tempCounter += ringCounter;
			
			if (tempCounter != 0) { // vermeiden NAN in
				featureVector.add(tempSum / tempCounter);
				variance.add((tempSumQuadrat-(tempCounter*Math.pow(tempSum /tempCounter,2)))/(tempCounter-1));
				standAbweichung.add( Math.sqrt((tempSumQuadrat-(tempCounter*Math.pow(tempSum /tempCounter,2)))/(tempCounter-1)));
			}
		}
		gefunden=tempSum; //nur für debug
		
		Allfeatures.add(ringFeatureVector);
		Allfeatures.add(ringVariance);
		Allfeatures.add(ringStdAbweichung);
		Allfeatures.add(featureVector);
		Allfeatures.add(variance);
		Allfeatures.add(standAbweichung);
 // zeile 101 bis 107 nur ausgabe für veranschaulichen 
		System.out.println("\n	@@ RingMittelwert[0]...@@RingVariance[1]...@@RingStdAb[2]...@@FullMittelwert[3]...@@FullVariance[4]...@@@FullstdAb[5] \n");
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
		ArrayList<Double> variance= new ArrayList<Double>();
		double sumQuadrat = 0;
		double sumQuadrat1 = 0;
		double sumQuadrat2 = 0;
		double sumQuadrat3 = 0;
		double sumQuadratrightdiag=0;
		double sumQuadratleftdiag=0;
		int diagr=0; int diagl=0;
	
		for (int i = 0; i < radius; i++) {
			
			sumQuadratleftdiag=sumQuadratleftdiag+Math.pow(pic[i][i],2)+Math.pow(pic[size - 1 - i][size - 1 - i],2);
			leftDiagonale += pic[i][i] + pic[size - 1 - i][size - 1 - i]; 
			diagl+=2;
			
			sumQuadratrightdiag=sumQuadratrightdiag+Math.pow(pic[size - 1 - i][i],2)+Math.pow(pic[i][size - 1 - i],2);
			rightDiagonale += pic[size - 1 - i][i] + pic[i][size - 1 - i];
			diagr+=2;
		}

		variance.add((sumQuadratleftdiag-(size*Math.pow(leftDiagonale / size,2)))/(size-1));
		variance.add((sumQuadratrightdiag-(size*Math.pow(rightDiagonale / size,2)))/(size-1));
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
				
				sumQuadrat1=sumQuadrat2+Math.pow(pic[radius - line - 1][size+radius-col-1],2)+Math.pow(pic[radius + line][col-radius],2);
				sum1 = sum1 + pic[radius - line - 1][size+radius-col-1]+ pic[radius + line][col-radius];
				counter1 += 2;
				
				sumQuadrat2= sumQuadrat2+Math.pow(pic[radius - line - 1][size - col],2)+Math.pow(pic[radius + line][size+radius-col-1],2);
				sum2 = sum2 + pic[radius - line - 1][col-radius]+ pic[radius + line][size+radius-col-1];
				counter2 += 2; 
				
				 sumQuadrat3= sumQuadrat3+Math.pow(pic[line][size - col-1],2)+Math.pow(pic[size- line-1][col],2);
				sum3 = sum3 + pic[line][size - col-1]+ pic[size- line-1][col];
				counter3 += 2;
				
				
			}
		}
		s=sum+sum1+sum2+sum3+rightDiagonale+leftDiagonale ;//nur für debug
		
		variance.add((sumQuadrat-(counter*Math.pow(sum / counter,2)))/(counter -1));
		variance.add((sumQuadrat1-(counter1*Math.pow(sum1 / counter1,2)))/(counter1 -1));
		variance.add((sumQuadrat2-(counter2*Math.pow(sum2 / counter2,2)))/(counter2 -1));
		variance.add((sumQuadrat3-(counter3*Math.pow(sum3 / counter3,2)))/(counter3 -1));
		
		StandardAbweichung.add(Math.sqrt((sumQuadrat-(counter*Math.pow(sum / counter,2)))/(counter -1)));
		StandardAbweichung.add(Math.sqrt((sumQuadrat1-(counter1*Math.pow(sum1 / counter1,2)))/(counter1 -1)));
		StandardAbweichung.add(Math.sqrt((sumQuadrat2-(counter2*Math.pow(sum2 / counter2,2)))/(counter2 -1))); 
		StandardAbweichung.add(Math.sqrt((sumQuadrat3-(counter3*Math.pow(sum3 / counter3,2)))/(counter3 -1)));
		
		featureVector.add(sum / counter);
		featureVector.add(sum1 / counter1);
		featureVector.add(sum2 / counter2);
		featureVector.add(sum3 / counter3);
		int i = 0;
		 // zeile 197 bis 203 nur ausgabe für veranschaulichen
		System.out.println("	@@@@  WedgeMittelwert				 @@@@ wedgeStdAb					@@@@ WedgeVariance\n");
		while (!featureVector.isEmpty() && i < featureVector.size()) {
			System.out.println("Mittelwert[" + i + "]..." + featureVector.get(i)+ "		StandardAbweichung["+i+"]..."+StandardAbweichung.get(i)+"		variance["+i+"]..."+variance.get(i));
			i++;
		}
		System.out.println();
		
		Allfeatures.add(featureVector);
		Allfeatures.add(variance);
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
// Main methode Nicht relevant für die Aufgabe nur für debug
	public static void main(String[] args) {
		FeatureVector fv = new FeatureVector();
		double arr[][]=new double[256][256]; 
		double total=0; 
		for (int i=0;i<256;i++){
			for (int j=0;j<256;j++){
			arr[i][j]= (2*i*j+5*j*j/4)/(i+j+15);	// Math.random();
			total+=arr[i][j];
		//	System.out.print(arr[i][j]+"");
		}
		}

		long start1 = System.currentTimeMillis();
		fv.extractVector(arr);
		long stop1 = System.currentTimeMillis();
		long time1 = stop1 - start1;
		System.out.println("	Extract process time = ..........." + time1+" ms\n" +
		 "	sum of values found =..." + gefunden + "\n	sum of values stored=..." +total 
		+"\n	difference..."+(total-gefunden )+ "\n----------------------------------------------------------------------------------\n");
		long start = System.currentTimeMillis();
		fv.extractWedge(arr);
		long stop = System.currentTimeMillis();
		long time = stop - start;
		System.out.println( "	Wedge process time = ..........." + time +"	ms");
		System.out.println("	summe of values found =..." + s+"\n	sum of values stored=..."+total+"\n"
			+ "	difference...	"+(total-s)+"\n------------------------------------------------------------------------------------\n"  );
	}

}

  
