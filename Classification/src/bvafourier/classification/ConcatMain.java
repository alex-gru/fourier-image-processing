
import java.util.ArrayList;

public class ConcatMain {

	public static void main(String[] args) throws Exception {
		/* GET IMAGES */
		long start = System.currentTimeMillis();
		//get images from database
		//createAllImages(magOrPhase, colorChannel)
		//magnitude = 0, phase = 1
		//colorChannel: y = 0; u = 1; v = 2; r = 3; g = 4; b = 5;
		ArrayList<Image> images1 = ImageCreator.createAllImages(0, 3);
		ArrayList<Image> images2 = ImageCreator.createAllImages(0, 1);
		ArrayList<ArrayList<Image>> colorChannels = new ArrayList<ArrayList<Image>>();
		colorChannels.add(images1);
		colorChannels.add(images2);
		System.out.println("Duration Image Creator: " + Math.round((System.currentTimeMillis() - start) / 1000) + "sec");
		
		/* CLASSIFY */
		start = System.currentTimeMillis();
		ConcatClassifier c = new ConcatClassifier(colorChannels);
		//different values for knn
		int[] k = { 1, 2, 4, 8, 16, 32};
		c.setK(k);
		/*the length of the following arrays needs to be equal
		to the number of colorChannels!!!!!*/	
		//features for the different feature vectors
		int[] features = {0,0};
		c.setFeatures(features);
		//bandwidths of the features. needs to be 0 if feature is wedges.
		int[] bandwidths = {8,2};
		c.setBandwidths(bandwidths);
		//statistic mode of feature. 0 = average; 1 = variance;
		int[] statModes ={0,0};
		c.setStatModes(statModes);
		//filters for different feature vectors. needs to be 0 if feature is wedges.
		int[] lowFilters = {8,2};
		c.setLowFilters(lowFilters);
		int[] highFilters = {2,16};
		c.setHighFilters(highFilters);
		
		c.classify();
		System.out.println("Duration Classifier: " + Math.round((System.currentTimeMillis() - start) / 1000) + "sec");
	}
}
