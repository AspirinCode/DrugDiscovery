package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

public class StatisticalAnalyser {

	static int numFeaturesThrombin = 139351;

	public static void main(String args[]) {

		try {
			StatisticalAnalyser main = new StatisticalAnalyser();
			String trainFile = "data/Thrombin.trainset/Thrombin.train";
			String labelFile = "data/Thrombin.trainset/thrombin.labels";
			
			main.findSignificantFeatures(trainFile, labelFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void findSignificantFeatures(String trainFile, String labelFile)
			throws Exception {

		BufferedReader trFile = new BufferedReader(new FileReader(trainFile));
		BufferedReader lblFile = new BufferedReader(new FileReader(labelFile));
		String str;
		String label;
		HashMap<Integer, Instance> sparseMatrix = new HashMap<Integer, Instance>();
		int index = 0;
		while ((str = trFile.readLine()) != null) {
			str = str.trim();

			if (str.equals("")) {
				continue;
			}

			String rec[] = str.trim().split("[ ]");
			label = lblFile.readLine();
			Integer intLabel = -1;
			if (label.equalsIgnoreCase("A")) {
				intLabel = 1;
			}

			HashSet<Integer> featureVector = new HashSet<Integer>();
			for (int i = 0; i < rec.length; i++) {
				Integer feature = Integer.parseInt(rec[i]);
				featureVector.add(feature);
			}

			sparseMatrix.put(index, new Instance(intLabel, featureVector));
			index++;
		}
		trFile.close();
		trFile = null;

		lblFile.close();
		lblFile = null;

		int lambda=3;
		int features[]=new int[numFeaturesThrombin];
		for (int j = 0; j < numFeaturesThrombin; j++) {

			int sumPos=0;
			int sumNeg=0;
			for (int i = 0; i < index; i++) {
				Instance instance=sparseMatrix.get(i);
				Integer y=instance.getLabel();
				int val=0;
				if(instance.getFeatureVector().contains(j)){
					val=1;
				}
				if(y==1){
					sumPos+=val;
				}else{
					sumNeg+=val;
				}
			}			
			
			features[j]=sumPos-lambda*sumNeg;
		}

		for(int i=0;i<features.length;i++){
			System.out.println(features[i]);
		}
		
	}

}
