import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Classifier {

	private int k;
	private int errors;
	private ArrayList<Image> allImages;
	private ArrayList<Image> knn;
	private StringBuilder data = new StringBuilder();

	public Classifier(ArrayList<Image> allImages) {
		this.allImages = allImages;	
	}

	public void classify(int feature, int k) {
		this.k = k;
		errors = 0;
		FileWriter writer;
		BufferedWriter out;
		
		StringBuilder folder = new StringBuilder();
		folder.append("data\\");
		folder.append(allImages.get(0).getMagOrPhase());
		folder.append(allImages.get(0).getColorChannel()+"\\");
		folder.append("knn"+k+"\\");
		if(feature == 0)
			folder.append("full\\");
		else if (feature == 1)
			folder.append("ring\\");
		setFeatureVectors(feature);
		for (Image i : allImages) {
			data.append("Patient: " + i.getPatientID() + "\n");
			data.append("ImageName: " + i.getImgName() + "\n");
			data.append("magOrPhase: " + i.getMagOrPhase() + " coloChannel :"
					+ i.getColorChannel() + "\n\n");
			data.append("FeatureVector: \n");
			for (Double d : i.getFeatureVector()) {
				data.append(d + " ");
			}
			data.append("\n\n");
			setDistances(i);
			setKNN(i);
			classifyPattern(i);
			data.append("Class: " + i.getPatternClass() + "\n");
			data.append("DETERMIND CLASS: " + i.getDeterminedClass() + "\n");
			isError(i);
			// data.append("ImageData: \n");
						// for (int j=0; j<i.getImage()[0].length; j++){
						// for (int k=0; k<i.getImage()[1].length; k++){
						// data.append(i.getImage()[j][k]+" ");
						// }
						// data.append("\n");
						// }
			try {
				File file = new File(folder.toString()+i.getImgName()+".dat");
				file.getParentFile().mkdirs();
				writer = new FileWriter(file);
				out = new BufferedWriter(writer);
				out.write(data.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			data.delete(0, data.length());
		}

		try {
			writer = new FileWriter(folder.toString()+"classification.dat");
			out = new BufferedWriter(writer);
			for (Image i : allImages)
				out.write("id: " + i.getPatientID() + " img: " + i.getImgName()
						+ " pattern: " + i.getPatternClass() + " determind:"
						+ i.getDeterminedClass() + "\n");
			double error = (double)errors/allImages.size();
			out.write("\nErrors: "+ Math.round(error*100)+"%");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setFeatureVectors(int feature) {
		for (Image i : allImages) {
			i.setFeatureVector(FeatureVector.extractVector(i.getImage(),feature));
		}
	}

	private void setDistances(Image testImage) {
		ArrayList<Double> testVector = testImage.getFeatureVector();
		ArrayList<Double> trainingsVector;
		for (int i = 0; i < allImages.size(); i++) {
			Double distance = 0.0;
			Image trainingsImage = allImages.get(i);
			if (trainingsImage.getPatientID() == testImage.getPatientID())
				continue;
			trainingsVector = trainingsImage.getFeatureVector();
			for (int j = 0; j < testVector.size(); j++) {
				distance += Math.pow(testVector.get(j) - trainingsVector.get(j), 2);
			}
			distance = Math.sqrt(distance);
			trainingsImage.setDistance(distance);
//			data.append("TrainingImage: " + trainingsImage.getImgName()+" Class: " + trainingsImage.getPatternClass() + "\n");
//			data.append("Distance: " + distance + "\n");
		}
	}

	private void setKNN(Image image) {
		knn = new ArrayList<Image>();
		Collections.sort(allImages);
		for (int i = 0; i < k; i++) {
			knn.add(allImages.get(i));
		}
		data.append("KNN:\n");
		for (Image i : knn){
			data.append(i.getImgName()+" Class: "+i.getPatternClass()+"\n");
		}
		data.append("\n");
	}

	private void classifyPattern(Image image) {
		int[] classCount = new int[6];
		int max = 0;
		for (Image i : knn) {
			switch (i.getPatternClass()) {
			case 0:
				classCount[0]++;
				break;
			case 1:
				classCount[1]++;
				break;
			case 2:
				classCount[2]++;
				break;
			case 3:
				classCount[3]++;
				break;
			case 4:
				classCount[4]++;
				break;
			case 5:
				classCount[5]++;
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

	private void isError(Image image) {
		if (image.getDeterminedClass() != image.getPatternClass()){
			image.setError(true);
			errors++;
		}
		
	}
}
