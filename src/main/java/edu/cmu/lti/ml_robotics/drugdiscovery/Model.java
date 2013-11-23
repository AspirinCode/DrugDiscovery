package edu.cmu.lti.ml_robotics.drugdiscovery;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class Model {
	
	Classifier classifier;
	Instances trainingSet;
	
	
	
	public Model(Classifier classifier, Instances trainingSet) {
		super();
		this.classifier = classifier;
		this.trainingSet = trainingSet;
	}
	public Classifier getClassifier() {
		return classifier;
	}
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}
	public Instances getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(Instances trainingSet) {
		this.trainingSet = trainingSet;
	}
	
	

}
