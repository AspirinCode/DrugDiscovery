package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class ActiveLearning {
	
	public static void main(String args[]){
		
		try{
			ActiveLearning main=new ActiveLearning();
			String fracTrainArffFile="train.dorothea.arff";
			String remainingTrainArffFile="train1.dorothea.arff";
			String testArffFile="data/Dorothea.testset/dorothea_valid.data.arff";
			/*String fracTrainArffFile="train.arff.back";
			String remainingTrainArffFile="train1.arff.back";
			String testArffFile="data/Thrombin.testset/Thrombin.test.arff.back";*/
			
			//main.activeLearningForSelectingInstances(fracTrainArffFile, remainingTrainArffFile,testArffFile);
			double p=0.3;
			main.randomlySelectingInstances(fracTrainArffFile, remainingTrainArffFile, testArffFile, p);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void activeLearningForSelectingInstances(String fracTrainArffFile,String remainingTrainArffFile,String testArffFile) throws Exception{
		
		ArffReader arffTrain = new ArffReader(new FileReader(remainingTrainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter("active.plot"));
		
		ModelBuilder modelBuilder=new ModelBuilder();
		Model model=modelBuilder.train(fracTrainArffFile);
		
		Instances alreadyTrained=model.getTrainingSet();
		Classifier svmClassifier=model.getClassifier();
		
		///////
		/*for(int i=0;i<trainingSet.numInstances();i++){
			alreadyTrained.add(trainingSet.instance(i));
		}
		svmClassifier.buildClassifier(alreadyTrained);*/
		///////
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		//double accuracy=modelBuilder.calcClassificationAccuracy(testingSet, trainingSet, svmClassifier);
		//System.out.println(accuracy);
	
		double classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);
		double instanceFeatureCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		double cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
		System.out.println("Initial cost: "+cost+"\tAccuracy: "+classificationAccuracy);
		bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
		bfw.newLine();
		for(int i=0;i<trainingSet.numInstances();i++){
			
			Instance newInstance=trainingSet.instance(i);
			String label=newInstance.toString(newInstance.classIndex());
			//System.out.println(label);
			newInstance.setDataset(alreadyTrained);
			double prediction=svmClassifier.classifyInstance(newInstance);
			//double scores[]=svmClassifier.distributionForInstance(newInstance);
		
			
			//System.out.println(scores[0]+"\t"+scores[1]);
			if(prediction<0.30 && prediction>-0.3){
				//System.out.println("Ambiguous: "+label+"\t"+prediction);
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);								
				instanceFeatureCost+=1*Math.log(alreadyTrained.numAttributes());
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
				
			}else{
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);				
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
			}
			
			System.out.println("Cost: "+cost+"\tAccuracy: "+classificationAccuracy);
			bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
			bfw.newLine();
			if(i%10==0){
				bfw.flush();
			}
		}
		bfw.close();
		bfw=null;
		double totalCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		System.out.println("Total cost on whole Training set: "+totalCost);
	}

	public void randomlySelectingInstances(String fracTrainArffFile,String remainingTrainArffFile,String testArffFile,double p) throws Exception{
		
		ArffReader arffTrain = new ArffReader(new FileReader(remainingTrainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter("random.plot"));
		
		
		ModelBuilder modelBuilder=new ModelBuilder();
		Model model=modelBuilder.train(fracTrainArffFile);
		
		Instances alreadyTrained=model.getTrainingSet();
		Classifier svmClassifier=model.getClassifier();
		
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		
		double classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);
		double instanceFeatureCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		double cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
		System.out.println("Initial cost: "+cost+"\t"+classificationAccuracy);
		bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
		bfw.newLine();
		
		for(int i=0;i<trainingSet.numInstances();i++){
			
			Instance newInstance=trainingSet.instance(i);
			//String label=newInstance.toString(newInstance.classIndex());
			//System.out.println(label);
			//newInstance.setDataset(alreadyTrained);
			//double prediction=svmClassifier.classifyInstance(newInstance);
			//double scores[]=svmClassifier.distributionForInstance(newInstance);
		
			double randomNum=Math.random();
			//System.out.println(scores[0]+"\t"+scores[1]);
			if(randomNum<p){
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);								
				instanceFeatureCost+=1*Math.log(alreadyTrained.numAttributes());
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
				
			}else{
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);				
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
			}
			
			System.out.println("Cost: "+cost+"\tAccuracy: "+classificationAccuracy);
			bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
			bfw.newLine();
			if(i%10==0){
				bfw.flush();
			}
		}
		
		double totalCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		System.out.println("Total cost on whole Training set: "+totalCost);
		bfw.close();
		bfw=null;
		
	}

/*
public void activeLearningForSelectingInstances1(String fracTrainArffFile,String remainingTrainArffFile,String testArffFile) throws Exception{
		
		ArffReader arffTrain = new ArffReader(new FileReader(remainingTrainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter("active1.plot"));
		
		ModelBuilder modelBuilder=new ModelBuilder();
		Model model=modelBuilder.train(fracTrainArffFile);
		
		Instances alreadyTrained=model.getTrainingSet();
		Classifier svmClassifier=model.getClassifier();
		
		///////
		///for(int i=0;i<trainingSet.numInstances();i++){
			//alreadyTrained.add(trainingSet.instance(i));
		//}
		//svmClassifier.buildClassifier(alreadyTrained);
		///////
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		//double accuracy=modelBuilder.calcClassificationAccuracy(testingSet, trainingSet, svmClassifier);
		//System.out.println(accuracy);
	
		double classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);
		double instanceFeatureCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		double cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
		System.out.println("Initial cost: "+cost+"\tAccuracy: "+classificationAccuracy);
		bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
		bfw.newLine();
		for(int i=0;i<trainingSet.numInstances();i++){
			
			Instance newInstance=trainingSet.instance(i);
			String label=newInstance.toString(newInstance.classIndex());
			//System.out.println(label);
			newInstance.setDataset(alreadyTrained);
			double prediction=svmClassifier.classifyInstance(newInstance);
			//double scores[]=svmClassifier.distributionForInstance(newInstance);
		
			
			//System.out.println(scores[0]+"\t"+scores[1]);
			if(prediction<0.30 && prediction>-0.3){
				//System.out.println("Ambiguous: "+label+"\t"+prediction);
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);								
				instanceFeatureCost+=1*Math.log(alreadyTrained.numAttributes());
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
				
			}else{
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);				
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
			}
			
			System.out.println("Cost: "+cost+"\tAccuracy: "+classificationAccuracy);
			bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
			bfw.newLine();
			if(i%10==0){
				bfw.flush();
			}
		}
		bfw.close();
		bfw=null;
		double totalCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		System.out.println("Total cost on whole Training set: "+totalCost);
	}

	public void randomlySelectingInstances1(String fracTrainArffFile,String remainingTrainArffFile,String testArffFile,double p) throws Exception{
		
		ArffReader arffTrain = new ArffReader(new FileReader(remainingTrainArffFile));
		Instances trainingSet = arffTrain.getData();
		trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter("random1.plot"));
		
		
		ModelBuilder modelBuilder=new ModelBuilder();
		Model model=modelBuilder.train(fracTrainArffFile);
		
		Instances alreadyTrained=model.getTrainingSet();
		Classifier svmClassifier=model.getClassifier();
		
		
		ArffReader arff = new ArffReader(new FileReader(testArffFile));
		Instances testingSet= arff.getData();
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		
		double classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);
		double instanceFeatureCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		double cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
		System.out.println("Initial cost: "+cost+"\t"+classificationAccuracy);
		
		for(int i=0;i<trainingSet.numInstances();i++){
			
			Instance newInstance=trainingSet.instance(i);
			//String label=newInstance.toString(newInstance.classIndex());
			//System.out.println(label);
			//newInstance.setDataset(alreadyTrained);
			//double prediction=svmClassifier.classifyInstance(newInstance);
			//double scores[]=svmClassifier.distributionForInstance(newInstance);
		
			double randomNum=Math.random();
			//System.out.println(scores[0]+"\t"+scores[1]);
			if(randomNum>p){
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);								
				instanceFeatureCost+=1*Math.log(alreadyTrained.numAttributes());
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
				
			}else{
				alreadyTrained.add(newInstance);
				svmClassifier.buildClassifier(alreadyTrained);
				classificationAccuracy=modelBuilder.calcClassificationAccuracy(testingSet, alreadyTrained, svmClassifier);				
				cost=instanceFeatureCost;///(classificationAccuracy*classificationAccuracy);
			}
			
			System.out.println("Cost: "+cost+"\tAccuracy: "+classificationAccuracy);
			bfw.write(String.valueOf(alreadyTrained.numInstances())+"\t"+cost+"\t"+classificationAccuracy);
			bfw.newLine();
			if(i%10==0){
				bfw.flush();
			}
		}
		
		double totalCost=alreadyTrained.numInstances()*Math.log(alreadyTrained.numAttributes());
		System.out.println("Total cost on whole Training set: "+totalCost);
		
	}

*/
}
