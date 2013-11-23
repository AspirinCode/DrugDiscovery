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
			ModelBuilder modelBuilder=new ModelBuilder();
			String orgTrainingFile="data/Dorothea.trainset/dorothea_train.data";
			String labelFile="data/Dorothea.trainset/dorothea_train.labels";
			String trainArffFile="data/Dorothea.trainset/dorothea_train.data.arff";
			String orgTestFile="data/Dorothea.testset/dorothea_valid.data";
			String testLabelFile="data/Dorothea.testset/dorothea_valid.labels";
			String testArffFile="data/Dorothea.testset/dorothea_valid.data.arff";
			/*
			String orgTrainingFile="data/Thrombin.trainset/Thrombin.train";
			String labelFile="data/Thrombin.trainset/thrombin.labels";
			String trainArffFile="data/Thrombin.trainset/Thrombin.train.arff";
			String orgTestFile="data/Thrombin.testset/Thrombin.test";
			String testLabelFile="data/Thrombin.testset/ThrombinKey";
			String testArffFile="data/Thrombin.testset/Thrombin.test.arff";
			*/
			

		
			StatisticalAnalyser statsAnalyser=new StatisticalAnalyser();
			int numFeatures=statsAnalyser.getNumFeatures(orgTrainingFile);
			Preprocessing prepr=new Preprocessing(numFeatures);
			prepr.convertIntoARFF(orgTrainingFile, labelFile, trainArffFile);
			prepr.convertIntoARFF(orgTestFile, testLabelFile, testArffFile);
			
			
			Model model=modelBuilder.train(trainArffFile);
			modelBuilder.test(testArffFile, model.getTrainingSet(),model.getClassifier());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Model train(String trainArffFile) throws Exception {

		ArffReader arffTrain = new ArffReader(new FileReader(trainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		SMO classifier = new SMO();
		classifier.buildClassifier(trainingSet);

		return new Model(classifier,trainingSet);
	}

	public void test(String testArffFile,Instances trainingSet,Classifier classifier) throws Exception{
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		Evaluation eTest = new Evaluation(trainingSet);
		eTest.evaluateModel(classifier, testingSet);
		  		
		String strSummary = eTest.toSummaryString(); 
		System.out.println(strSummary);
				
	}
	
	
}
