package metriclearning.classifier;

import java.util.List;

/**
 * Instance class, representing each data point. 
 * @author Wenzhe
 *
 */
public class Instance {
	private List<Feature> features;
	private int label;
	
	public void setLable(int label){
		this.label = label;
	}
	
	public int getLabel(){
		return label;
	}
	
	public void setFeatures(List<Feature> features){
		this.features = features;
	}
	
	public List<Feature> getFeatures(){
		return features;
	}
	
	public int getDimension(){
		return features.size();
	}
	
	public String toString(){
		StringBuilder stBuilder = new StringBuilder();
		stBuilder.append("[");
		for (Feature feature: features){
			if (feature instanceof OrdinalFeature){
				stBuilder.append(feature.toString());
				stBuilder.append("(ordinal) ");
			} else{ // if feature is real value... 
				stBuilder.append(feature.toString());
			}
			stBuilder.append(",");
		}
		stBuilder.append("]");
		
		return stBuilder.toString();
	}
}
