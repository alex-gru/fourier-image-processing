
import java.util.ArrayList;

public class FeatureVector {
	
	public static ArrayList<ArrayList<Double>> getSquareVector(Double[][] pic, int bandwith) {
		int picSize = pic[0].length;
		int squareSize = 0;

		ArrayList<Double> averageVector = new ArrayList<Double>();
		ArrayList<Double> stdDevationVector = new ArrayList<Double>();
		ArrayList<Double> varianceVector = new ArrayList<Double>();

		ArrayList<ArrayList<Double>> squareVector = new ArrayList<ArrayList<Double>>();
		for (int i = picSize / 2; i >= 0; squareSize += 2 * bandwith, i = i - bandwith) {
			int heigth = i;
			int width = i;
			int radius = squareSize / 2;
			int separator = bandwith;
			Double ringSum = 0.0;
			Double ringSumQuadrat=0.0;
			int ringCounter = 0;

			for (int col = heigth; col < i + radius; col++) {
				if (col < i + bandwith) {
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
				averageVector.add(ringSum / ringCounter);
				varianceVector.add((ringSumQuadrat-(ringCounter*Math.pow(ringSum / ringCounter,2)))/(ringCounter-1));
				stdDevationVector.add(Math.sqrt((ringSumQuadrat-(ringCounter*Math.pow(ringSum / ringCounter,2)))/(ringCounter-1)));
			}
		}
		
		squareVector.add(averageVector);
		squareVector.add(varianceVector);
		squareVector.add(stdDevationVector);
		return squareVector;
	}
	
	public static ArrayList<ArrayList<Double>> getFullSquareVector(Double[][] pic, int bandwith) {
		int picSize = pic[0].length;
		int squareSize = 0;
		Double tempSum = 0.0;
		Double tempSumQuadrat = 0.0;
		int tempCounter = 0;
		ArrayList<Double> averageVector = new ArrayList<Double>();
		ArrayList<Double> standAbweichung = new ArrayList<Double>();
		ArrayList<Double> varianceVector= new ArrayList<Double>();
		ArrayList<ArrayList<Double>> fullSquareVector = new ArrayList<ArrayList<Double>>();
		for (int i = picSize / 2; i >= 0; squareSize += 2 * bandwith, i = i - bandwith) {
			int heigth = i;
			int width = i;
			int radius = squareSize / 2;
			int separator = bandwith;
			Double ringSum = 0.0;
			Double ringSumQuadrat=0.0;
			int ringCounter = 0;

			for (int col = heigth; col < i + radius; col++) {
				if (col < i + bandwith) {
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
			
			tempSum += ringSum;
			tempSumQuadrat+= ringSumQuadrat;
			tempCounter += ringCounter;
			
			if (tempCounter != 0) { // vermeiden NAN in
				averageVector.add(tempSum / tempCounter);
				varianceVector.add((tempSumQuadrat-(tempCounter*Math.pow(tempSum /tempCounter,2)))/(tempCounter-1));
				standAbweichung.add( Math.sqrt((tempSumQuadrat-(tempCounter*Math.pow(tempSum /tempCounter,2)))/(tempCounter-1)));
			}
		}

		fullSquareVector.add(averageVector);
		fullSquareVector.add(varianceVector);
		fullSquareVector.add(standAbweichung);
		
		return fullSquareVector;
	}
	
	public static ArrayList<ArrayList<Double>> getWedgesVector(Double[][] pic) {

		int size = pic[0].length;
		int radius = size / 2;
		Double sum = 0.0;
		Double sum1 = 0.0;
		Double sum2 = 0.0;
		Double sum3 = 0.0;
		int counter = 0;
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		Double leftDiagonale = 0.0;
		Double rightDiagonale = 0.0;
		ArrayList<ArrayList<Double>> wedgesVector = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> averageVector = new ArrayList<Double>();
		ArrayList<Double> stdDevationVector = new ArrayList<Double>();
		ArrayList<Double> varianceVector= new ArrayList<Double>();
		Double sumQuadrat = 0.0;
		Double sumQuadrat1 = 0.0;
		Double sumQuadrat2 = 0.0;
		Double sumQuadrat3 = 0.0;
		Double sumQuadratrightdiag=0.0;
		Double sumQuadratleftdiag=0.0;

	
		for (int i = 0; i < radius; i++) {			
			sumQuadratleftdiag=sumQuadratleftdiag+Math.pow(pic[i][i],2)+Math.pow(pic[size - 1 - i][size - 1 - i],2);
			leftDiagonale += pic[i][i] + pic[size - 1 - i][size - 1 - i]; 
		
			sumQuadratrightdiag=sumQuadratrightdiag+Math.pow(pic[size - 1 - i][i],2)+Math.pow(pic[i][size - 1 - i],2);
			rightDiagonale += pic[size - 1 - i][i] + pic[i][size - 1 - i];
		}

		varianceVector.add((sumQuadratleftdiag-(size*Math.pow(leftDiagonale / size,2)))/(size-1));
		varianceVector.add((sumQuadratrightdiag-(size*Math.pow(rightDiagonale / size,2)))/(size-1));
		stdDevationVector.add(Math.sqrt((sumQuadratleftdiag-(size*Math.pow(leftDiagonale / size,2)))/(size-1)));
		stdDevationVector.add(Math.sqrt((sumQuadratrightdiag-(size*Math.pow(rightDiagonale / size,2)))/(size-1)));
		averageVector.add(leftDiagonale / size);
		averageVector.add(rightDiagonale / size);
		
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
		
		varianceVector.add((sumQuadrat-(counter*Math.pow(sum / counter,2)))/(counter -1));
		varianceVector.add((sumQuadrat1-(counter1*Math.pow(sum1 / counter1,2)))/(counter1 -1));
		varianceVector.add((sumQuadrat2-(counter2*Math.pow(sum2 / counter2,2)))/(counter2 -1));
		varianceVector.add((sumQuadrat3-(counter3*Math.pow(sum3 / counter3,2)))/(counter3 -1));
		
		stdDevationVector.add(Math.sqrt((sumQuadrat-(counter*Math.pow(sum / counter,2)))/(counter -1)));
		stdDevationVector.add(Math.sqrt((sumQuadrat1-(counter1*Math.pow(sum1 / counter1,2)))/(counter1 -1)));
		stdDevationVector.add(Math.sqrt((sumQuadrat2-(counter2*Math.pow(sum2 / counter2,2)))/(counter2 -1))); 
		stdDevationVector.add(Math.sqrt((sumQuadrat3-(counter3*Math.pow(sum3 / counter3,2)))/(counter3 -1)));
		
		averageVector.add(sum / counter);
		averageVector.add(sum1 / counter1);
		averageVector.add(sum2 / counter2);
		averageVector.add(sum3 / counter3);
		
		wedgesVector.add(averageVector);
		wedgesVector.add(varianceVector);
		wedgesVector.add(stdDevationVector);
		return wedgesVector;
	}
	
	public static ArrayList<ArrayList<Double>> getWedgeVector(Double[][] pic){
		return Wedges.extract(pic);
	}
}

  
