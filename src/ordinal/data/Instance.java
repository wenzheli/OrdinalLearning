package ordinal.data;

import java.util.List;

/**
 * Instance class, representing each data point. 
 * Each instance is divided into two parts such that x^i = [p^i, r^i],
 * where p^i represents the ordinal part, and r^i represents real value part. 
 * For arbitrary data set, we first need to reorder ordinal and real values parts
 * in this form. 
 * 
 * @author Wenzhe
 *
 */
public class Instance {
	private List<Integer> ordinalFeatures;
	private List<Double> realFeatures;
	private int label;
	
	public void setLable(int label){
		this.label = label;
	}
	
	public int getLabel(){
		return label;
	}
	
	public void setOrdinalFeatures(List<Integer> ordinalFeatures){
		this.ordinalFeatures = ordinalFeatures;
	}
	
	public List<Integer> getOrdinalFeatures(){
		return ordinalFeatures;
	}
	
	public int getDimension(){
		return ordinalFeatures.size() + realFeatures.size();
	}
	
	public String toString(){
		StringBuilder stBuilder = new StringBuilder();
		stBuilder.append("[");
		stBuilder.append("(ordinal)");
		for (Integer ordinal : ordinalFeatures){
			stBuilder.append(ordinal.toString());
			stBuilder.append(",");
		}
		for (Double real : realFeatures){
			stBuilder.append("(real)");
			stBuilder.append(real.toString());
			stBuilder.append(",");
		}
		stBuilder.append("]");
		
		return stBuilder.toString();
	}
}
