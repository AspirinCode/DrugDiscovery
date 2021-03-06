package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Utils {
	
	public static ArrayList<Integer> loadLabels(String labelFile) throws Exception {

		BufferedReader bfr = new BufferedReader(new FileReader(labelFile));
		String str;
		ArrayList<Integer> labels = new ArrayList<Integer>();
		while ((str = bfr.readLine()) != null) {
			str=str.trim();
			Integer label = -1;
			if(str.startsWith("A")||str.startsWith("1")){
				label=1;
			}
			
			labels.add(label);
		}
		bfr.close();
		bfr = null;

		return labels;
	}


}
