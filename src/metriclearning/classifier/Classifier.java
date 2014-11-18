package metriclearning.classifier;


public interface Classifier {
	
	/**
	 *  Give a training data, compute the accuracy based on that. 
	 */
	double computeAccuracy();
	
	/**
	 *  Classify the instance, return predicted label, which we assume
	 *  it is integer value... 
	 *  TODO: should support multilabel classification... 
	 */
	int classify(Instance instance);
}
