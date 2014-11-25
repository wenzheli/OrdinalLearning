package ordinal.data;

import java.util.List;

/**
 * Include all the instances. We pass the data object to classifier. 
 * 
 * @author Wenzhe
 *
 */
public class Data {
	
	public List<Instance> instances;
	private int numberOfInstances;
	private int dimension;
	private int numOfOrdinalFeatures;
	private int numberOfLabels;
	
	/**
	 * Create data object. <isOrdinals> argument contains the indexes of 
	 * all the ordinal features. Based on this information, we will re-order
	 * the attributes of the data point. 
	 * 
	 * Another important pre-processing step is to make sure the ordinal features
	 * starting from 1 such that  1,2,...,K. We need to correct in this format
	 * if not. 
	 * 
	 * TODO this function is not complete
	 * 
	 * @param ordinalIndexs 		the indexes of ordinal features. 
	 */
	public Data(List<Instance> instances, List<Integer> ordinalIndexs){
		this.instances = instances;
	}
	
	public int getNumOfInstances(){
		return numberOfInstances;
	}
	
	public int getDimension(){
		return dimension;
	}
	
	public int getNumOfOrdinalFeatures(){
		return numOfOrdinalFeatures;
	}
	
	public int getNumOfLabels(){
		return numberOfLabels;
	}
}
