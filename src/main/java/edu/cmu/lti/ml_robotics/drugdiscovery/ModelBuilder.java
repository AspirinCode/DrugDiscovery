package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class ModelBuilder {

	public static void main(String args[]) {

		try {
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Classifier train(String trainArffFile) throws Exception {

		ArffReader arffTrain = new ArffReader(new FileReader(trainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		SMO classifier = new SMO();
		classifier.buildClassifier(trainingSet);

		return classifier;
	}

	public void test(String testArffFile,Classifier classifier) throws Exception{
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		Evaluation eTest = new Evaluation(testingSet);
		eTest.evaluateModel(classifier, testingSet);
		  		
		String strSummary = eTest.toSummaryString(); 
		System.out.println(strSummary);
				
	}
	
	
}
