package bvafourier.classification;

import java.util.ArrayList;

public class Image implements Comparable<Image>{
	
	private String picName;
	private int patientID;
	private int patternClass;
	private int determinedClass;
	private Double distance = Double.MAX_VALUE;
	private boolean error = false;
	
	private double [][] yMagnitude;
	private double [][] uMagnitude;
	private double [][] vMagnitude;
	
	private double [][] yPhase;
	private double [][] uPhase;
	private double [][] vPhase;
	
	private ArrayList<Double> featureVector;
	
	@Override
	public int compareTo(Image img) {
        return this.distance.compareTo(img.getDistance());
    }
	
	////////////getter and setter methods
	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
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

	public double [][] getyMagnitude() {
		return yMagnitude;
	}

	public void setyMagnitude(double [][] yMagnitude) {
		this.yMagnitude = yMagnitude;
	}

	public double [][] getuMagnitude() {
		return uMagnitude;
	}

	public void setuMagnitude(double [][] uMagnitude) {
		this.uMagnitude = uMagnitude;
	}

	public double [][] getvMagnitude() {
		return vMagnitude;
	}

	public void setvMagnitude(double [][] vMagnitude) {
		this.vMagnitude = vMagnitude;
	}

	public double [][] getyPhase() {
		return yPhase;
	}

	public void setyPhase(double [][] yPhase) {
		this.yPhase = yPhase;
	}

	public double [][] getuPhase() {
		return uPhase;
	}

	public void setuPhase(double [][] uPhase) {
		this.uPhase = uPhase;
	}

	public double [][] getvPhase() {
		return vPhase;
	}

	public void setvPhase(double [][] vPhase) {
		this.vPhase = vPhase;
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
}
