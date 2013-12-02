package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffLoader.ArffReader;

public class Preprocessing<CustomComparator> {

	//static int numFeaturesDorothea = 100000;
	//static int numFeaturesThrombin=139351;
	int numFeatures;
	double minimum = -0.5;
	double maximum = 0.5;
	double alpha=0.01;
	static double C=0.0001;

	public Preprocessing(int n){
		numFeatures=n;
	}
	public static void main(String args[]) {

		try {

			/*String trainFile = "data/Thrombin.trainset/Thrombin.train";//"data/Dorothea.trainset/dorothea_train.data";
			String validationFile = "data/Thrombin.testset/Thrombin.test";//"data/Dorothea.testset/dorothea_valid.data";
			String validationLabelFile = "data/Thrombin.testset/ThrombinKey";//"data/Dorothea.testset/dorothea_valid.labels";
			String labelFile = "data/Thrombin.trainset/thrombin.labels";//"data/Dorothea.trainset/dorothea_train.labels";
			String arffTrainFile="data/Thrombin.trainset/Thrombin.train.arff";//"data/Dorothea.trainset/dorothea_train.data.arff";
			String arffValidFile="data/Thrombin.testset/Thrombin.testset.arff";//"data/Dorothea.testset/dorothea_valid.data.arff";
			String testLabelFile="data/Thrombin.testset/thrombin.test.labels";
			*/
			String trainFile = "data/Dorothea.trainset/dorothea_train.data";
			String validationFile = "data/Dorothea.testset/dorothea_valid.data";
			String validationLabelFile = "data/Dorothea.testset/dorothea_valid.labels";
			String labelFile = "data/Dorothea.trainset/dorothea_train.labels";
			String arffTrainFile="data/Dorothea.trainset/dorothea_train.data.arff";
			String arffValidFile="data/Dorothea.testset/dorothea_valid.data.arff";
			String testLabelFile="data/Dorothea.testset/dorothea_valid.labels";

			
			
			StatisticalAnalyser statsAnalyser=new StatisticalAnalyser();
			int n=statsAnalyser.getNumFeatures(trainFile);
			Preprocessing main = new Preprocessing(n);
			double fraction=0.3;
			main.selectFractionOfTrainingData(trainFile, labelFile, fraction);

			main.findUniqueFeatures(trainFile,labelFile);
			//main.separateLabelsFromThrombinDataSet(validationFile, "data/Thrombin.testset/Thrombin.test",testLabelFile);
			//main.convertIntoARFF(trainFile,labelFile,arffTrainFile);
			
			//Instances trainingSet = main.getTrainingData(trainFile, labelFile);
			 //LinearRegression classifier=new LinearRegression();
			 
			//main.convertIntoARFF(validationFile, validationLabelFile, arffValidFile);
			

			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

		
	public void separateLabelsFromThrombinDataSet(String trainFile,String newTrainFile,String labelFile)throws Exception{
		
		BufferedReader bfr=new BufferedReader(new FileReader(trainFile));
		BufferedWriter bfw=new BufferedWriter(new FileWriter(newTrainFile));
		
		String str;
		
		while((str=bfr.readLine())!=null){
			
			String rec[]=str.trim().split("[,]");
			StringBuilder builder=new StringBuilder();
			for(int i=1;i<rec.length;i++){
				if(rec[i].equals("1")){
					builder.append(i);
					builder.append(" ");
				}
			}
			//System.out.println(builder.toString());
			bfw.write(builder.toString());			
			//bfw.write(rec[0]);
			bfw.newLine();
		
		}
		
		bfr.close();
		bfr=null;
		bfw.close();
		bfw=null;
		
	}
	
	public void convertIntoARFF(String trainFile,String labelFile,String arffFile)throws Exception{
		
		ArrayList<Integer>labels=Utils.loadLabels(labelFile);
		
		BufferedReader bfr = new BufferedReader(new FileReader(
				trainFile));
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter(arffFile));
		bfw.write("@RELATION compounds");
		bfw.newLine();
		bfw.newLine();
		
		for(int i=0;i<numFeatures;i++){
			bfw.write("@ATTRIBUTE "+"f"+i+" NUMERIC");
			bfw.newLine();
		}
		bfw.write("@ATTRIBUTE class {Active,Inactive}");
		bfw.newLine();
		bfw.newLine();
		
				
		String str;
		int count=0;
		bfw.write("@DATA");
		bfw.newLine();
		while ((str = bfr.readLine()) != null) {

			str=str.trim();
			if(str.equals("")){
				continue;
			}
			
			String rec[] = str.trim().split("[ ]");
			StringBuilder features=new StringBuilder();
			
			features.append("{");
			for (int i = 0; i < rec.length; i++) {
				
				
				int feature = Integer.parseInt(rec[i])-1;
				features.append(feature+" 1, ");
								
			}
			
			String label="";
			if(labels.get(count)==1){
				label="Active";
			}else{
				label="Inactive";
			}
			features.append((numFeatures)+" "+label);
			features.append("}");
		    bfw.write(features.toString());
		    bfw.newLine();
		    count++;
		}
		bfr.close();
		bfr = null;
		bfw.close();
		bfw=null;
		

		
	}

	public void convertIntoARFFNumeric(String trainFile,String labelFile,String arffFile)throws Exception{
		
		ArrayList<Integer>labels=Utils.loadLabels(labelFile);
		
		BufferedReader bfr = new BufferedReader(new FileReader(
				trainFile));
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter(arffFile));
		bfw.write("@RELATION compounds");
		bfw.newLine();
		bfw.newLine();
		
		for(int i=0;i<numFeatures;i++){
			bfw.write("@ATTRIBUTE "+"f"+i+" NUMERIC");
			bfw.newLine();
		}
		bfw.write("@ATTRIBUTE class NUMERIC");
		bfw.newLine();
		bfw.newLine();
		
				
		String str;
		int count=0;
		bfw.write("@DATA");
		bfw.newLine();
		while ((str = bfr.readLine()) != null) {

			str=str.trim();
			if(str.equals("")){
				continue;
			}
			
			String rec[] = str.trim().split("[ ]");
			StringBuilder features=new StringBuilder();
			
			features.append("{");
			for (int i = 0; i < rec.length; i++) {
				
				
				int feature = Integer.parseInt(rec[i])-1;
				features.append(feature+" 1, ");
								
			}
			
			String label="";
			if(labels.get(count)==1){
				label="1";
			}else{
				label="-1";
			}
			features.append((numFeatures)+" "+label);
			features.append("}");
		    bfw.write(features.toString());
		    bfw.newLine();
		    count++;
		}
		bfr.close();
		bfr = null;
		bfw.close();
		bfw=null;
		

		
	}

	public void findUniqueFeatures(String trainFile,String labelFile) throws Exception {

		BufferedReader bfr = new BufferedReader(new FileReader(
				trainFile));//"data/Dorothea.trainset/dorothea_train.data"
		String str;
		//HashSet<Integer> features = new HashSet<Integer>();
		
		HashMap<Integer, HashSet<Integer>> features = new HashMap<Integer, HashSet<Integer>>();
		int row =0;
		while ((str = bfr.readLine()) != null) {
			String rec[] = str.trim().split("[ ]");

			for (int i = 0; i < rec.length; i++) {
				int feature = Integer.parseInt(rec[i]);
				HashSet<Integer> temp;
				if(features.containsKey(feature)){
				temp = features.get(feature);
				}
				else{
					temp = new HashSet<Integer>();
				}
				temp.add(row);

				features.put(feature, temp);
				}
			//System.out.println("Size: " + features.size());
			row++;

		}
		int totalRows = row;
		bfr.close();
		bfr = null;
		
		bfr = new BufferedReader(new FileReader(
				labelFile));
		ArrayList<Integer> labels  = new ArrayList<Integer>();
		HashSet<Integer> posRows = new HashSet<Integer>();
		HashSet<Integer> negRows = new HashSet<Integer>();

		row =0;
		while ((str = bfr.readLine()) != null) {
			labels.add(Integer.parseInt(str.trim()));
			if(Integer.parseInt(str.trim()) == -1)
				negRows.add(row);
			else 
				posRows.add(row);
			
			row++;
		}
		
		HashSet<Integer> allRows = new HashSet<Integer>();
		for(int r=0;r<totalRows;r++){
			allRows.add(r);
		}
	
			
		HashMap<Integer, Double> mi = new HashMap<Integer,Double>();
		HashSet<Integer> discardedFeatures = new HashSet<Integer>();
		Set<Integer> keys = features.keySet();
		for(Integer i: keys){
				HashSet<Integer> a = features.get(i);
				Double m =0.0;
				for(int r=0;r<totalRows;r++){	
					Double px= 0.0, py = 0.0, pxy = 0.0;
				if(a.contains(r))
					px = 1.0/a.size();
				else 
					px = 1.0/(totalRows-a.size());
				if(labels.get(r) == 1)
					py = 1.0/posRows.size();
				else
					py = 1.0/negRows.size();
				int s;

				if(a.contains(r) && labels.get(r) == 1){
					Set<Integer> intersection = new HashSet<Integer>(a);

					intersection.retainAll(posRows);
					pxy = 1.0/intersection.size();
					
				}
				else if(a.contains(r) && labels.get(r) == -1){
					Set<Integer> intersection = new HashSet<Integer>(a);

					intersection.retainAll(negRows);
					pxy = 1.0/intersection.size();
					
				}
				else if(!a.contains(r) && labels.get(r) == 1){
					Set<Integer> intersection = new HashSet<Integer>(posRows);
					Set<Integer> temp = new HashSet<Integer>(allRows);
					temp.removeAll(a);
					intersection.retainAll(temp);
					pxy = 1.0/intersection.size();

				}
				else{
					Set<Integer> intersection = new HashSet<Integer>(negRows);
					Set<Integer> temp = new HashSet<Integer>(allRows);
					temp.removeAll(a);
					intersection.retainAll(temp);
					pxy = 1.0/intersection.size();				
				}
				m += pxy * Math.log(pxy/(px*py));
				
				
			}
				mi.put(i, m);
				System.out.println(m);
	}
		
		System.out.println("Total Unique Features: " + features.size());
		
		
		class CustomComparator implements Comparator<Integer> {

			Map<Integer, Double> base;

			public CustomComparator(HashMap<Integer, Double> scores) {
				this.base = scores;
			}

			public int compare(Integer arg0, Integer arg1) {
				if (base.get(arg0) < base.get(arg1)) {
					return 1;
				} else if (base.get(arg0) > base.get(arg1)) {
					return -1;
				} else {
					if (arg0 < arg1)
						return -1;
					else
						return 1;
				}
			}

		}
		
		CustomComparator c = new CustomComparator(mi);

		TreeMap<Integer, Double> sortedMap = new TreeMap<Integer, Double>(c);
		sortedMap.putAll(mi);
		int i=0;
		for (Entry<Integer, Double> entry : sortedMap.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue() );
			i++;
			if (i == 201)
				break;
			
		}


/*
		BufferedReader bfr1 = new BufferedReader(new FileReader(
				testFile));//"data/Dorothea.testset/dorothea_test.data"

		while ((str = bfr1.readLine()) != null) {

			String rec[] = str.trim().split("[ ]");

			for (int i = 0; i < rec.length; i++) {
				int feature = Integer.parseInt(rec[i]);
				if (!features.contains(feature)) {
					System.out.println("Not found in training set: " + feature);
				}
			}

		}
		bfr1.close();
		bfr1 = null;
		*/

	}
	
	public void selectFractionOfTrainingData(String trainFile,String labelFile,double fraction) throws Exception{
		
		StatisticalAnalyser statsAnalyser=new StatisticalAnalyser();
		statsAnalyser.findPosNegSampleStatistics(labelFile);
		int nPos=statsAnalyser.getNumPosSamples();
		int nNeg=statsAnalyser.getNumNegSamples();
		
		int takenPos=(int)(nPos*fraction);
		int takenNeg=(int)(nNeg*fraction);
				
		BufferedReader trainBfr=new BufferedReader(new FileReader(trainFile));
		BufferedReader labelBfr=new BufferedReader(new FileReader(labelFile));
		
		ArrayList<String>posList=new ArrayList<String>();
		ArrayList<String>negList=new ArrayList<String>();
		
		String str;
		while((str=trainBfr.readLine())!=null){
			str=str.trim();
			if(str.equals("")){
				continue;
			}
			String str1=labelBfr.readLine();
			if(str1.startsWith("1") ||str1.startsWith("A")){
				posList.add(str);
			}else{
				negList.add(str);
			}
			
		}
		trainBfr.close();
		trainBfr=null;
		labelBfr.close();
		labelBfr=null;
		
		ArrayList<String>selectedPos=new ArrayList<String>();
		Random random=new Random();
		while(takenPos>0){
			int index=Math.abs(random.nextInt())%posList.size();
			selectedPos.add(posList.get(index));
			posList.remove(index);			
			takenPos--;
		}
		
		ArrayList<String>selectedNeg=new ArrayList<String>();
		while(takenNeg>0){
			int index=Math.abs(random.nextInt())%negList.size();
			selectedNeg.add(negList.get(index));
			negList.remove(index);
			takenNeg--;
		}
		
		BufferedWriter bfw=new BufferedWriter(new FileWriter("train1.dorothea"));
		BufferedWriter lbfw=new BufferedWriter(new FileWriter("label1.dorothea"));
		for(int i=0;i<posList.size();i++){
			bfw.write(posList.get(i));
			bfw.newLine();
			lbfw.write("1");
			lbfw.newLine();
		}
		for(int i=0;i<negList.size();i++){
			bfw.write(negList.get(i));
			bfw.newLine();
			lbfw.write("-1");
			lbfw.newLine();
		}
		
		bfw.close();
		bfw=null;
		lbfw.close();
		lbfw=null;
		
		BufferedWriter newBfw=new BufferedWriter(new FileWriter("train.dorothea"));
		BufferedWriter newLabelBfw=new BufferedWriter(new FileWriter("label.dorothea"));
		
		while(!selectedNeg.isEmpty() || !selectedPos.isEmpty()){
		
			if(random.nextBoolean() && !selectedPos.isEmpty()){
				newBfw.write(selectedPos.get(0));				
				newBfw.newLine();
				newLabelBfw.write("1");
				newLabelBfw.newLine();
				selectedPos.remove(0);
			}else{
				newBfw.write(selectedNeg.get(0));
				newBfw.newLine();
				newLabelBfw.write("-1");
				newLabelBfw.newLine();
				selectedNeg.remove(0);
			}
			
		}
		
		newBfw.close();
		newBfw=null;
		newLabelBfw.close();
		newLabelBfw=null;
		
		
	}
	
	

	/*
	public Instances getTrainingData(String trainFile, String labelFile)
			throws Exception {

		ArrayList<Integer> labelList = Utils.loadLabels(labelFile);

		FastVector fvWekaAttributes = new FastVector(numFeatures + 1);
		Attribute features[] = new Attribute[numFeatures];
		for (int i = 0; i < features.length; i++) {
			features[i] = new Attribute("f" + i);
			fvWekaAttributes.addElement(features[i]);
		}
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement("Active");
		fvClassVal.addElement("Inactive");
		Attribute classAttribute = new Attribute("classifciation", fvClassVal);
		fvWekaAttributes.addElement(classAttribute);

		Instances isTrainingSet = new Instances("Compound Classification",
				fvWekaAttributes, 1000);
		isTrainingSet.setClassIndex(numFeatures);

		BufferedReader bfr = new BufferedReader(new FileReader(trainFile));
		String str;
		int count = 0;
		while ((str = bfr.readLine()) != null) {

			String rec[] = str.trim().split("[ ]");

			Instance instance = new Instance(numFeatures + 1);
			int nextIdx = 0;
			for (int i = 0; i < numFeatures; i++) {

				int featureVal = 0;
				if (nextIdx < rec.length) {
					int featureIdx = Integer.parseInt(rec[nextIdx]) - 1;

					if (i == featureIdx) {
						featureVal = 1;
						nextIdx++;
					}
				}
				instance.setValue((Attribute) fvWekaAttributes.elementAt(i),
						featureVal);

			}
			instance.setValue(
					(Attribute) fvWekaAttributes.elementAt(numFeatures),
					labelList.get(count));
			count++;
		}
		bfr.close();
		bfr = null;

		return isTrainingSet;
	}
*/


}
