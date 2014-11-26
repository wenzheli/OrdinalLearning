package ordinal.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private int numOfRealFeatures;
	private int numberOfLabels;
	private List<Intervals> ordinalIntervals;
	private List<Integer> ordinalIntervalSizes;
	
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
	public Data(List<Instance> instances){
		this.instances = instances;
		
		this.numberOfInstances = instances.size();
		this.dimension = instances.get(0).getDimension();
		this.numOfOrdinalFeatures = instances.get(0).getOrdinalFeatures().size();
		this.numOfRealFeatures = instances.get(0).getRealFeatures().size();
		
		List<Set<Integer>> ordinalFeatureSet = new ArrayList<Set<Integer>>(numOfOrdinalFeatures);
		for (int i = 0; i < numOfOrdinalFeatures; i++){
			ordinalFeatureSet.add(new HashSet<Integer>());
		}
		Set<Integer> uniqueLabels = new HashSet<Integer>();
		for (Instance instance : instances){
			// record each ordinal feature for each instance
			for (int i = 0; i < instance.getOrdinalFeatures().size(); i++){
				Set<Integer> ordinalFeature = ordinalFeatureSet.get(i);
				ordinalFeature.add(instance.getOrdinalFeatures().get(i));
				ordinalFeatureSet.set(i, ordinalFeature);
			}
			uniqueLabels.add(instance.getLabel());
		}
		
		numberOfLabels = uniqueLabels.size();
		
		// compute ordinal related information 
		ordinalIntervals = new ArrayList<Intervals>(numOfOrdinalFeatures);
		ordinalIntervalSizes =  new ArrayList<Integer>(numOfOrdinalFeatures);
		
		for (int i = 0; i < numOfOrdinalFeatures; i++){
			ordinalIntervals.add(new Intervals(ordinalFeatureSet.get(i).size()));
			ordinalIntervalSizes.add(ordinalFeatureSet.get(i).size());
		}
	}
	
	
	public List<Intervals> getOrdinalIntervals(){
		return ordinalIntervals;
	}
	
	public List<Integer> getOridnalIntervalSizes(){
		return ordinalIntervalSizes;
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
	
	public int getNumOfRealFeatures(){
		return numOfRealFeatures;
	}
	
	public void setNumOfRealFeatures(int numOfRealFeatures){
		this.numOfRealFeatures = numOfRealFeatures;
	}

}
