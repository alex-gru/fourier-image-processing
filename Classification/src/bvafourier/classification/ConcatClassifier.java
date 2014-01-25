import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConcatClassifier {	
	//Arrays to save allErrors of different knn's
	//example: if k = {2,4,8} then p0Errors[0] = (all p0Errors with KNN 2)
	private int[] allErrors, p0Errors, p1Errors, p2Errors;
	//Quantities of different patterns
	private final int p0Quantity = 198;
	private final int p1Quantity = 420;
	private final int p2Quantity = 98;
	//List contains images for different color channels
	private ArrayList<ArrayList<Image>> colorChannels;
	//Array for different features
	//example: feature[0] = (feature for feature vector 1)
	private int [] features = {0};
	//Array for different k values
	private int [] k = {1,2,4,8,16,32};
	//Array for different low filters
	//example: lowFilters[0] = (low filter for feature 1)
	private int [] lowFilters = {0};
	//Array for different high filters
	//example: highFilters[0] = (high filter for feature 1)
	private int [] highFilters = {0};	
	//Array for different bandwidths (not for wedges)
	//example: bandwidth[0] = (bandwidth for feature 1)
	private int [] bandwidths = {2};
	//Array for different statistic modes
	private int [] statModes = {0};
	//Image List to sort by distance
	private ArrayList<Image> sortedImages;
	//string which is written to the results file
	private StringBuilder results;
	
	public ConcatClassifier(ArrayList<ArrayList<Image>> colorChannels) {
		this.colorChannels = colorChannels;
	}

	public void classify() {

		results = new StringBuilder();
		allErrors = new int[k.length];
		p0Errors = new int[k.length];
		p1Errors = new int[k.length];
		p2Errors = new int[k.length];
		Arrays.fill(allErrors, 0);
		Arrays.fill(p0Errors, 0);
		Arrays.fill(p1Errors, 0);
		Arrays.fill(p2Errors, 0);
		
		setFeatureVectors();
		filterFeatureVectors();
		
		//classify all images
		for (int i = 0; i < 716; i++) {
			setDistances(i);
			
			//get shallow copy of image list and sort it by distance
			sortedImages = new ArrayList<Image>(colorChannels.get(0));
			Collections.sort(sortedImages);
			
			//classify all given knn and set errors
			for (int j = 0; j < k.length; j++) {
				classifyPattern(i, k[j]);
				isError(i, j);
			}
		}	
		createFile();	
	}
	
	private void createFile() {
		
		// set result string
		results.append("features;bandwidths;magOrPhase;statistic modes;filters low;filters high;knn;allError;p0Error;p1Error;p2Error\n");
		for (int i = 0; i < k.length; i++) {
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(features[j]);
				else 
					results.append(" + "+features[j]);
			}
			results.append(";");
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(bandwidths[j]);
				else 
					results.append(" + "+bandwidths[j]);
			}
			results.append(";");
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(colorChannels.get(j).get(0).getMagOrPhase());
				else 
					results.append(" + "+colorChannels.get(j).get(0).getMagOrPhase());
			}
			results.append(";");
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(statModes[j]);
				else 
					results.append(" + "+statModes[j]);
			}
			results.append(";");
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(lowFilters[j]);
				else 
					results.append(" + "+lowFilters[j]);
			}
			results.append(";");
			for (int j = 0; j < colorChannels.size(); j++){
				if (j == 0)
					results.append(highFilters[j]);
				else 
					results.append(" + "+highFilters[j]);
			}
			results.append(";");
			double error = (double) allErrors[i] / 716;
			results.append(k[i] + ";");
			results.append(Math.round(error * 100) + "%;");
			error = (double) p0Errors[i] / p0Quantity;
			results.append(Math.round(error * 100) + "%;");
			error = (double) p1Errors[i] / p1Quantity;
			results.append(Math.round(error * 100) + "%;");
			error = (double) p2Errors[i] / p2Quantity;
			results.append(Math.round(error * 100) + "%;");
			results.append("\n");
		}
		
		// write classification file
		try {
			FileWriter writer = null;
			writer = new FileWriter("concat.csv", true);
			BufferedWriter out = new BufferedWriter(writer);
			out.append(results.toString());
			out.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void filterFeatureVectors() {
		//loop goes through all color channels
		for (int i = 0; i < colorChannels.size(); i++){
			//loop goes through all images in color channel
			for (Image img : colorChannels.get(i)){
				//use filter for given coloChannel
				for (int j = 0; j < lowFilters[i]; j++) {
					img.getFeatureVectors().get(0).remove(0);
					img.getFeatureVectors().get(1).remove(0);
				}
				for (int j = 0; j < highFilters[i]; j++){
					img.getFeatureVectors().get(0).remove(img.getFeatureVectors().get(0).size()-1);
					img.getFeatureVectors().get(1).remove(img.getFeatureVectors().get(1).size()-1);
				}
			}
		}
		
	}

	private void setFeatureVectors() {
		//loop goes through all color channels
		for (int i = 0; i < colorChannels.size(); i++){
			//loop goes through all images in color channel
			for (Image img : colorChannels.get(i)){
				//chooses feature for given color channel
				switch (features [i]){
				case (0):
					img.setFeatureVectors(FeatureVector.getSquareVector(img.getImage(),bandwidths[i]));
					break;
				case (1):
					img.setFeatureVectors(FeatureVector.getFullSquareVector(img.getImage(),bandwidths[i]));
					break;
				case (2):
					img.setFeatureVectors(FeatureVector.getWedgesVector(img.getImage()));
					break;
				case (3):
					img.setFeatureVectors(FeatureVector.getWedgeVector(img.getImage()));
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * Sets the distances in the traingsImages by calculating the euclidian
	 * distance between the testImage and the trainigsImages.
	 */
	private void setDistances(int imageIndex) {
		ArrayList<Double> testVector = new ArrayList<Double>();
		for (int i = 0; i < colorChannels.size(); i++){
			testVector.addAll(colorChannels.get(i).get(imageIndex).getFeatureVectors().get(statModes[i]));
		}		
		for (int i = 0; i < 716; i++) {
			ArrayList<Double> trainingsVector = new ArrayList<Double>();
			Double distance = 0.0;
			if (colorChannels.get(0).get(i).getPatientID() == colorChannels.get(0).get(imageIndex).getPatientID()) {
				colorChannels.get(0).get(i).setDistance(Double.MAX_VALUE);
				continue;
			}
			for (int j = 0; j < colorChannels.size(); j++){
				trainingsVector.addAll(colorChannels.get(j).get(i).getFeatureVectors().get(statModes[j]));
			}
			
			for (int j = 0; j < testVector.size(); j++) {
				distance += Math.pow((testVector.get(j) - trainingsVector.get(j)), 2);
			}
			distance = Math.sqrt(distance);
			colorChannels.get(0).get(i).setDistance(distance);
		}
	}

	/**
	 * sets the determined pattern class of the image by getting most common
	 * pattern classes of KNN.
	 * 
	 * @param imageIndex: index of image to be classified
	 * @param k: k nearest neighbors
	 */
	private void classifyPattern(int imageIndex, int k) {
		int[] classCount = new int[3];
		int max = 0;
		for (int i = 0; i < k; i++) {
			switch (sortedImages.get(i).getPatternClass()) {
			case 0:
				classCount[0]++;
				break;
			case 1:
				classCount[1]++;
				break;
			case 2:
				classCount[2]++;
				break;
			}
		}
		for (int i = 0; i < classCount.length; i++) {
			if (classCount[i] >= max) {
				max = classCount[i];
				colorChannels.get(0).get(imageIndex).setDeterminedClass(i);
			}
		}
	}

	/**
	 * checks if image is classified correctly and counts allErrors.
	 * 
	 * @param imageIndex: index of image to be checked
	 * @param knnIndex: errorIndex (KNN)
	 */
	private void isError(int imageIndex, int knnIndex) {
		if (colorChannels.get(0).get(imageIndex).getDeterminedClass() != colorChannels.get(0).get(imageIndex).getPatternClass()) {
			allErrors[knnIndex]++;
			switch (colorChannels.get(0).get(imageIndex).getPatternClass()) {
			case (0):
				p0Errors[knnIndex]++;
				break;
			case (1):
				p1Errors[knnIndex]++;
				break;
			case (2):
				p2Errors[knnIndex]++;
				break;
			}
		}
	}
	
	public int [] getK() {
		return k;
	}

	public void setK(int [] k) {
		this.k = k;
	}

	public int [] getLowFilters() {
		return lowFilters;
	}

	public void setLowFilters(int [] lowFilters) {
		this.lowFilters = lowFilters;
	}

	public int [] getHighFilters() {
		return highFilters;
	}

	public void setHighFilters(int [] highFilters) {
		this.highFilters = highFilters;
	}

	public int [] getFeatures() {
		return features;
	}

	public void setFeatures(int [] features) {
		this.features = features;
	}
	
	public int [] getBandwidths() {
		return bandwidths;
	}

	public void setBandwidths(int [] bandwidths) {
		this.bandwidths = bandwidths;
	}
	public int [] getStatModes() {
		return statModes;
	}

	public void setStatModes(int [] statModes) {
		this.statModes = statModes;
	}
}
