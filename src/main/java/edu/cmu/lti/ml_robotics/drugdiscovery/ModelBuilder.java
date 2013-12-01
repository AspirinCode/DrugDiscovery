package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.Instance;

import weka.core.converters.ArffLoader.ArffReader;

public class ModelBuilder {

	public static void main(String args[]) {

		try {
			ModelBuilder modelBuilder=new ModelBuilder();
			
			/*String orgTrainingFile="data/Dorothea.trainset/dorothea_train.data";

			String trainArffFile="data/Dorothea.trainset/dorothea_train.data.arff";*/
			
			String orgTrainingFile="train.dorothea";
			String labelFile="label.dorothea";
			String trainArffFile="train.dorothea.arff.back";
			
			
			/*String orgTrainingFile="data/Thrombin.trainset/Thrombin.train";
			String labelFile="data/Thrombin.trainset/thrombin.labels";
			String trainArffFile="data/Thrombin.trainset/Thrombin.train.arff";*/
			
			/*String orgTestFile="data/Dorothea.testset/dorothea_valid.data";
			String trainArffFile="data/Dorothea.trainset/dorothea_train.data.arff";
			String orgTestFile="data/Dorothea.testset/dorothea_valid.data";
			String testLabelFile="data/Dorothea.testset/dorothea_valid.labels";
			String testArffFile="data/Dorothea.testset/dorothea_valid.data.arff";*/
			
			/*String orgTestFile="data/Thrombin.testset/Thrombin.test";
			String testArffFile="data/Dorothea.testset/dorothea_valid.data.arff";
			/*
			String orgTrainingFile="data/Thrombin.trainset/Thrombin.train";
			String labelFile="data/Thrombin.trainset/thrombin.labels";
			String trainArffFile="data/Thrombin.trainset/Thrombin.train.arff";
			String orgTestFile="data/Thrombin.testset/Thrombin.test";
			String testLabelFile="data/Thrombin.testset/ThrombinKey";
			String testArffFile="data/Thrombin.testset/Thrombin.test.arff.back";*/
			
			String orgTestFile="train1.dorothea";
			String testLabelFile="label1.dorothea";
			String testArffFile="train1.dorothea.arff.back";
			
			

		
			StatisticalAnalyser statsAnalyser=new StatisticalAnalyser();
			int numFeatures=statsAnalyser.getNumFeatures(orgTrainingFile);
			Preprocessing prepr=new Preprocessing(numFeatures);
			prepr.convertIntoARFFNumeric(orgTrainingFile, labelFile, trainArffFile);
			prepr.convertIntoARFFNumeric(orgTestFile, testLabelFile, testArffFile);
			
			
			Model model=modelBuilder.train(trainArffFile);
			modelBuilder.test(testArffFile, model.getTrainingSet(),model.getClassifier());
			
			double accuracy=modelBuilder.calcClassificationAccuracy(testArffFile, model.getTrainingSet(), model.getClassifier());
			System.out.println(accuracy);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Model train(String trainArffFile) throws Exception {

		ArffReader arffTrain = new ArffReader(new FileReader(trainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		SMOreg classifier = new SMOreg();
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
	
	public double calcClassificationAccuracy(String testArffFile,Instances trainingSet,Classifier classifier)throws Exception{
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		int correct=0;
		int total=0;
		for(int i=0;i<testingSet.numInstances();i++){
						
			Instance instance=testingSet.instance(i);
			instance.setDataset(trainingSet);
			String label=instance.toString(instance.classIndex());
			double prediction=classifier.classifyInstance(instance);
			//System.out.println(label+"\t"+prediction);
			
			if(label.startsWith("-1") && prediction<0.0){
				correct++;
			}else if(label.startsWith("1") && prediction>0.0){
				correct++;
			}
			total++;
		}
		
		double accuracy=correct/(double)total;
		return accuracy;
	}

	public double calcClassificationAccuracy(Instances testingSet,Instances trainingSet,Classifier classifier)throws Exception{
		
		int correct=0;
		int total=0;
		for(int i=0;i<testingSet.numInstances();i++){
						
			Instance instance=testingSet.instance(i);
			instance.setDataset(trainingSet);
			String label=instance.toString(instance.classIndex());
			double prediction=classifier.classifyInstance(instance);
			//System.out.println(label+"\t"+prediction);
			
			if(label.startsWith("-1") && prediction<0.0){
				correct++;
			}else if(label.startsWith("1") && prediction>0.0){
				correct++;
			}
			total++;
		}
		
		double accuracy=correct/(double)total;
		return accuracy;
	}

	
}
