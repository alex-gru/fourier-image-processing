import java.util.ArrayList;

public class Image implements Comparable<Image>,java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private String imgName;
	private int patientID;
	private int patternClass;
	private int determinedClass;
	private int magOrPhase;
	private int colorChannel;
	private Double distance = Double.MAX_VALUE;
	private boolean error = false;
	
	private Double [][] image;
	
	private ArrayList<Double> featureVector;
	
	@Override
	public int compareTo(Image img) {
        return this.distance.compareTo(img.getDistance());
    }
	
	////////////getter and setter methods
	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public int getPatternClass() {
		return patternClass;
	}

	public void setPatternClass(int patternClass) {
		this.patternClass = patternClass;
	}

	public int getDeterminedClass() {
		return determinedClass;
	}

	public void setDeterminedClass(int determinedClass) {
		this.determinedClass = determinedClass;
	}
	
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public ArrayList<Double> getFeatureVector() {
		return featureVector;
	}

	public void setFeatureVector(ArrayList<Double> featureVector) {
		this.featureVector = featureVector;
	}

	public int getPatientID() {
		return patientID;
	}

	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Double [][] getImage() {
		return image;
	}

	public void setImage(Double [][] image) {
		this.image = image;
	}

	public int getMagOrPhase() {
		return magOrPhase;
	}

	public void setMagOrPhase(int magOrPhase) {
		this.magOrPhase = magOrPhase;
	}

	public int getColorChannel() {
		return colorChannel;
	}

	public void setColorChannel(int colorChannel) {
		this.colorChannel = colorChannel;
	}
}
