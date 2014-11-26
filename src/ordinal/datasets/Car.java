package ordinal.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ordinal.data.Data;
import ordinal.data.Instance;

public class Car {
	public static Data getData() throws IOException{
		 List<Instance> instances =  new ArrayList<Instance>();
		 File file = new File("data/car_data.txt");
	     BufferedReader br = null;
	     String sCurrentLine = "";
	     br = new BufferedReader(new FileReader(file));
	     while ((sCurrentLine = br.readLine()) != null){
	    	 String[] strs = sCurrentLine.split(",");
	    	 List<Integer> ordinalFeatures =  new ArrayList<Integer>();
	    	 List<Double> realFeatures = new ArrayList<Double>();
	    	 for (int i = 0; i < strs.length - 1; i++){
	    		 ordinalFeatures.add(Integer.parseInt(strs[i]));
	    	 }
	    	 Instance instance = new Instance();
	    	 instance.setOrdinalFeatures(ordinalFeatures);
	    	 instance.setRealFeatures(realFeatures);
	    	 instance.setLable(Integer.parseInt(strs[strs.length-1]));
	    	 
	    	 instances.add(instance);
	     }
	     
	     Data data = new Data(instances);
	     
	     return data;
	}
}
