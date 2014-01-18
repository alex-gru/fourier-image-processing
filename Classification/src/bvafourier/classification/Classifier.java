import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Classifier {

	private int[] errors, p0Errors, p1Errors, p2Errors;
	private int p0Counter, p1Counter, p2Counter;
	private ArrayList<Image> images;
	private ArrayList<Image> sortedImages;
	private StringBuilder data;
	private boolean[][] imgErrors;

	public Classifier(ArrayList<Image> images) {
		this.images = images;
		try {
			FileWriter writer = null;
			data = new StringBuilder();
			data.append("filter low;filter high;bandwidth;statistic mode; magOrPhase;ColorChannel;KNN;all Errors;Errors P1;Errors P2;Errors P3;");
			for (Image img : images) {
				data.append(img.getImgName() + ";");
			}
			data.append("\n");
			writer = new FileWriter("square.csv", true);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(data.toString());
			out.close();
			writer = new FileWriter("fullSquare.csv", true);
			out = new BufferedWriter(writer);
			out.write(data.toString());
			out.close();
			data = new StringBuilder();
			data.append("statistic mode; magOrPhase;ColorChannel;KNN;all Errors;Errors P1;Errors P2;Errors P3;");
			for (Image img : images) {
				data.append(img.getImgName() + ";");
			}
			data.append("\n");
			writer = new FileWriter("wedge.csv", true);
			out = new BufferedWriter(writer);
			out.write(data.toString());
			out.close();
			writer = new FileWriter("wedges.csv", true);
			out = new BufferedWriter(writer);
			out.write(data.toString());
			out.close();
			writer = null;
			data = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * classifies all images with the given feature and KNN-classification
	 * 
	 * @param k
	 *            : k nearest neighbors
	 * @param feature
	 *            : 0=square 1=fullSquare 2=wedges
	 * @param bandwidth
	 *            : only for square and fullSquare 0=Wedges
	 */
	public void classify(int[] k, int feature, int bandwidth) {
		data = new StringBuilder();
		errors = new int[k.length];
		p0Errors = new int[k.length];
		p1Errors = new int[k.length];
		p2Errors = new int[k.length];
		countPatterns();
		setFeatureVectors(feature, bandwidth);

		// loop runs through all statistic modes and filter settings
		for (int statMode = 0; statMode < 2; statMode++) {
			if (feature < 2) {
				for (int low = 0; low < 9; low = low * 2) {
					for (int high = 0; high < 1; high = high * 2) {
						System.out.println("classify: feature " + feature
								+ ", StatMode:" + statMode + ", lowFilter: "
								+ low + ", highFilter: " + high
								+ ", bandwidth: " + bandwidth);
						Arrays.fill(errors, 0);
						Arrays.fill(p0Errors, 0);
						Arrays.fill(p1Errors, 0);
						Arrays.fill(p2Errors, 0);
						imgErrors = new boolean[images.size()][images.size()];
						for (int i = 0; i < images.size(); i++) {
							setDistances(images.get(i), statMode, feature, low,
									high);
							// images get sorted by distance
							sortedImages = new ArrayList<Image>(images);
							Collections.sort(sortedImages);
							for (int j = 0; j < k.length; j++) {
								classifyPattern(images.get(i), k[j]);
								isError(images.get(i), j, i);
							}
						}
						// create file data

						for (int i = 0; i < k.length; i++) {
							data.append(low + ";" + high + ";" + bandwidth
									+ ";");
							if (statMode == 0)
								data.append("AVERAGE;");
							else
								data.append("VARIANCE;");
							if (images.get(0).getMagOrPhase() == 0)
								data.append("magnitude;");
							else if (images.get(0).getMagOrPhase() == 1)
								data.append("phase;");
							switch (images.get(0).getColorChannel()) {
							case 0:
								data.append("y;");
								break;
							case 1:
								data.append("u;");
								break;
							case 2:
								data.append("v;");
								break;
							case 3:
								data.append("r;");
								break;
							case 4:
								data.append("g;");
								break;
							case 5:
								data.append("b;");
								break;
							}
							double error = (double) errors[i] / images.size();
							data.append(k[i] + ";");
							data.append(Math.round(error * 100) + "%;");
							error = (double) p0Errors[i] / p0Counter;
							data.append(Math.round(error * 100) + "%;");
							error = (double) p1Errors[i] / p1Counter;
							data.append(Math.round(error * 100) + "%;");
							error = (double) p2Errors[i] / p2Counter;
							data.append(Math.round(error * 100) + "%;");
							for (int j = 0; j < imgErrors.length; j++) {
								data.append("Error: " + imgErrors[i][j] + ";");
							}
							data.append("\n");
						}
						if (low == 0) {
							low++;
						}
						if (high == 0) {
							high++;
						}
					}
				}
			} else {
				System.out.println("classify: feature " + feature
						+ ", StatMode:" + statMode);
				Arrays.fill(errors, 0);
				Arrays.fill(p0Errors, 0);
				Arrays.fill(p1Errors, 0);
				Arrays.fill(p2Errors, 0);
				imgErrors = new boolean[images.size()][images.size()];
				for (int i = 0; i < images.size(); i++) {
					setDistances(images.get(i), statMode, feature, 0, 0);
					// images get sorted by distance
					sortedImages = new ArrayList<Image>(images);
					Collections.sort(sortedImages);
					for (int j = 0; j < k.length; j++) {
						classifyPattern(images.get(i), k[j]);
						isError(images.get(i), j, i);
					}
				}
				// create file data

				for (int i = 0; i < k.length; i++) {
					if (statMode == 0)
						data.append("AVERAGE;");
					else
						data.append("VARIANCE;");
					if (images.get(0).getMagOrPhase() == 0)
						data.append("magnitude;");
					else if (images.get(0).getMagOrPhase() == 1)
						data.append("phase;");
					switch (images.get(0).getColorChannel()) {
					case 0:
						data.append("y;");
						break;
					case 1:
						data.append("u;");
						break;
					case 2:
						data.append("v;");
						break;
					case 3:
						data.append("r;");
						break;
					case 4:
						data.append("g;");
						break;
					case 5:
						data.append("b;");
						break;
					}
					double error = (double) errors[i] / images.size();
					data.append(k[i] + ";");
					data.append(Math.round(error * 100) + "%;");
					error = (double) p0Errors[i] / p0Counter;
					data.append(Math.round(error * 100) + "%;");
					error = (double) p1Errors[i] / p1Counter;
					data.append(Math.round(error * 100) + "%;");
					error = (double) p2Errors[i] / p2Counter;
					data.append(Math.round(error * 100) + "%;");
					for (int j = 0; j < imgErrors.length; j++) {
						data.append("Error: " + imgErrors[i][j] + ";");
					}
					data.append("\n");
					

				}
			}
		}

		// write classification file
		try {
			FileWriter writer = null;
			switch (feature) {
			case (0):
				writer = new FileWriter("square.csv", true);
				break;
			case (1):
				writer = new FileWriter("fullSquare.csv", true);
				break;
			case (2):
				writer = new FileWriter("wedges.csv", true);
				break;
			case (3):
				writer = new FileWriter("wedge.csv", true);
				break;
			}
			BufferedWriter out = new BufferedWriter(writer);
			out.append(data.toString());
			out.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		errors = null;
		p0Errors = null;
		p1Errors = null;
		p2Errors = null;
		imgErrors = null;
		data = null;
		sortedImages = null;

	}

	/**
	 * counts the incidence of pattern classes
	 */
	private void countPatterns() {
		p0Counter = 0;
		p1Counter = 0;
		p2Counter = 0;
		for (Image img : images) {
			switch (img.getPatternClass()) {
			case (0):
				p0Counter++;
				break;
			case (1):
				p1Counter++;
				break;
			case (2):
				p2Counter++;
				break;
			}
		}
	}

	/**
	 * sets the feature vector in the image object
	 * 
	 * @param feature
	 *            : 0=square 1=fullSquare 2=wedges
	 * @param bandwith
	 *            : only for fullSquare and square; 0=wedges
	 */
	private void setFeatureVectors(int feature, int bandwidth) {
		for (Image i : images) {
			switch (feature) {
			case (0):
				i.setFeatureVector(FeatureVector.getSquareVector(i.getImage(),
						bandwidth));
				break;
			case (1):
				i.setFeatureVector(FeatureVector.getFullSquareVector(
						i.getImage(), bandwidth));
				break;
			case (2):
				i.setFeatureVector(FeatureVector.getWedgesVector(i.getImage()));
				break;
			case (3):
				i.setFeatureVector(FeatureVector.getWedgeVector(i.getImage()));
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	/**
	 * Sets the distances in the traingsImages by calculating the euclidian
	 * distance between the testImage and the trainigsImage.
	 * 
	 * @param testImage
	 *            : the image to be classified
	 * @param statisticMode
	 *            : 0=average 1=variance 2=stdDevation
	 */
	private void setDistances(Image testImage, int statisticMode, int feature,
			int low, int high) {
		ArrayList<Double> testVector = testImage.getFeatureVectors().get(
				statisticMode);
		ArrayList<Double> trainingsVector;

		for (int i = 0; i < images.size(); i++) {
			Double distance = 0.0;
			Image trainingsImage = images.get(i);
			if (trainingsImage.getPatientID() == testImage.getPatientID()) {
				trainingsImage.setDistance(Double.MAX_VALUE);
				continue;
			}
			trainingsVector = trainingsImage.getFeatureVectors().get(
					statisticMode);
			int j;
			int stop;
			if (feature == 2) {
				j = 0;
				stop = testVector.size();
				// Filter
			} else {
				j = low;
				stop = testVector.size() - high;
			}
			while (j < stop) {
				distance += Math.pow(
						testVector.get(j) - trainingsVector.get(j), 2);
				j++;
			}
			distance = Math.sqrt(distance);
			trainingsImage.setDistance(distance);
		}
	}

	/**
	 * sets the determined pattern class of the image by getting most common
	 * pattern classes of KNN.
	 * 
	 * @param image
	 *            : image to be classified
	 * @param k
	 *            : k nearest neighbors
	 */
	private void classifyPattern(Image image, int k) {
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
				image.setDeterminedClass(i);
			}
		}
	}

	/**
	 * checks if image is classified correctly and counts errors.
	 * 
	 * @param image
	 *            : image to be checked
	 * @param index
	 *            : errorIndex (KNN)
	 */
	private void isError(Image image, int index1, int index2) {
		if (image.getDeterminedClass() != image.getPatternClass()) {
			errors[index1]++;
			imgErrors[index1][index2] = true;
			switch (image.getPatternClass()) {
			case (0):
				p0Errors[index1]++;
				break;
			case (1):
				p1Errors[index1]++;
				break;
			case (2):
				p2Errors[index1]++;
				break;
			}
		} else {
			imgErrors[index1][index2] = false;
		}
	}
	
	public void setImages(ArrayList<Image> images){
		this.images = images;
	}
}
