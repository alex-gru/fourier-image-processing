import java.util.ArrayList;
import java.util.Collections;

public class Classifier {
	
	private int k;
	private ArrayList<Image> allImages;
	private ArrayList<Image> knn;
	
	public Classifier(ArrayList<Image> allImages, int k){
		this.allImages = allImages;
		this.k = k;
	}
	
	public void classify() {		
		setFeatureVectors();
		for(Image i : allImages){
			setDistances(i);
			setKNN(i);
			classifyPattern(i);
			isError(i);
		}
	}
	
	
	private void setFeatureVectors(){
		for(Image i : allImages){
			//i.setFeatureVector(getFeatureVector(/*used feature*/,i));
		}
	}
	
	private void setDistances(Image testImage){
		ArrayList<Double> testVector = testImage.getFeatureVector();
		ArrayList<Double> trainingsVector;
		for(int i=0; i<allImages.size(); i++){
			Double distance = 0.0;
			Image trainingsImage = allImages.get(i);
			if (trainingsImage.getPatientID() == testImage.getPatientID())
				continue;
			trainingsVector = trainingsImage.getFeatureVector();
			for(int j=0; j<testVector.size(); j++){
				distance += Math.pow(testVector.get(j)-trainingsVector.get(j),2);				
			}
			distance = Math.sqrt(distance);
			trainingsImage.setDistance(distance);
		}
	}
	
	private void setKNN(Image image){
		knn = new ArrayList<Image>();
		Collections.sort(allImages);
		for(int i=0; i<k; i++){
			knn.add(allImages.get(i));
		}
	}
	
	private void classifyPattern(Image image){
		int [] classCount = new int [6];
		int max = 0;
		for(Image i : knn){
			switch (i.getPatternClass()){
			case 1:
				classCount[0]++;
				break;
			case 2:
				classCount[1]++;
				break;
			case 3:
				classCount[2]++;
				break;
			case 4:
				classCount[3]++;
				break;
			case 5:
				classCount[4]++;
				break;
			case 6:
				classCount[5]++;
				break;
			}
		}
		for(int i=0; i<classCount.length; i++){
            if(classCount[i] > max){
                max = classCount[i];
                image.setDeterminedClass(i+1);
            }
        }
	}	
	
	private void isError(Image image) {
		if (image.getDeterminedClass() != image.getPatternClass())
			image.setError(true);
	}
}
