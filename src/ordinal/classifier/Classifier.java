package ordinal.classifier;

import ordinal.data.Data;
import ordinal.data.Instance;


public interface Classifier {
	
	/**
	 *  Compute accuracy for given data set
	 */
	double computeAccuracy(Data data);
	
	/**
	 *  Classify the instance, return predicted label, which we assume
	 *  it is integer value... 
	 *  TODO: should support multilabel classification... 
	 */
	int classify(Instance instance);
}
