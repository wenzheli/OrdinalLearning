package metriclearning.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Data {

	public List<Instance> instances;
	public int numberOfInstances;
	public int dimension;
	public int numberOfLabels;
	public List<Map<Integer, EndPoint>> ordinalMap;
	
	public Data(List<Instance> instances){
		this.instances = instances;
	}
  
}
