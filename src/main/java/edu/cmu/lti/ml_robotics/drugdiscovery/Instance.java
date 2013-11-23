package edu.cmu.lti.ml_robotics.drugdiscovery;

import java.util.HashSet;

public class Instance {
	Integer label;
	HashSet<Integer>featureVector;
	
	public Instance(Integer l,HashSet<Integer>f){
		label=l;
		featureVector=f;
	}
	
	public Integer getLabel() {
		return label;
	}
	public void setLabel(Integer label) {
		this.label = label;
	}
	public HashSet<Integer> getFeatureVector() {
		return featureVector;
	}
	public void setFeatureVector(HashSet<Integer> featureVector) {
		this.featureVector = featureVector;
	}
	
	

}
