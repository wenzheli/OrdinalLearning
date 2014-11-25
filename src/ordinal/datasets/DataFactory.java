package ordinal.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ordinal.data.Data;
import ordinal.data.Feature;
import ordinal.data.Instance;
import ordinal.data.OrdinalFeature;
import ordinal.data.RealFeature;

/**
 * Factory class to generate correct format data based on the 
 * input file. 
 * @author Wenzhe
 *
 */
public class DataFactory {	
	private final static Logger logger = Logger.getLogger(DataFactory.class.getName()); 
	
	/**
	 * Generate formatted data by processing the input files. The input should
	 * input the indexes of ordinal values, since they need special treatments. 
	 * 
	 * @param inputFeatureFile		input feature file.. 
	 * @param inputLabelFile		input label file 
	 * @param ordinalIndexs			ordinal indexes. i.e [1,3,..]
	 * @return						formatted data, which can be directly fed into the classifier		
	 */
	public static Data createData(String inputFeatureFile, String inputLabelFile, Set<Integer> ordinalIndexes) 
			throws IOException{
		double[][] unformattedData = getData(inputFeatureFile);
		int[] labels = getLabel(inputLabelFile);
		return formatData(unformattedData, labels, ordinalIndexes);
	}
	
	
	public static Data formatData(double[][] unformattedData, int[] labels, Set<Integer> ordinalIndexes){
		if (unformattedData == null || labels == null 
				||unformattedData.length != labels.length){
			String errorMsg = "error for the data: either data or label is null value"
					+ "or data and labels have different size";
			System.out.println(errorMsg);
			logger.log(Level.SEVERE, errorMsg);
		}
		
		int size = unformattedData.length;
		int dimension = unformattedData[0].length;
		List<Instance> instances = new ArrayList<Instance>(size);  
		for (int i = 0; i < size; i++){
			// process each data. 
			Instance instance = new Instance();
						
			// construct feature vector for each instance. 
			List<Feature> features = new ArrayList<Feature>();
			// process each feature. we need to do special treatment for ordinal feature
			for (int j = 0 ; j < dimension; j++){
				Feature feature;
				if (ordinalIndexes.contains(j)){ // if it is ordinal feature
					feature = new OrdinalFeature((int)Math.round(unformattedData[i][j]));
					
				}else{ // otherwise... 
					feature = new RealFeature(Math.round(unformattedData[i][j]));
				}
				features.add(feature);
			}
			
			// set the feature vector for the instance
			instance.setFeatures(features);
			
			// set the class label for instance
			instance.setLable(labels[i]);
			
			// add instance into the list
			instances.add(instance);
		}
		
		return new Data(instances);
	}
	
    public static double[][] getData(String inputFeatureFile) throws IOException{
        File file = new File(inputFeatureFile);
        BufferedReader br = null;
        String sCurrentLine = "";
        br = new BufferedReader(new FileReader(file));
        int row = 0;
        int column = 0;
        while ((sCurrentLine = br.readLine()) != null){
            row++;
            String[] tokens = sCurrentLine.trim().split("\\s+");
            column = tokens.length;
        }
        
        double[][] result = new double[row][column];
        br.close();
        
        br = new BufferedReader(new FileReader(file));
        int rowIdx = 0;
        while ((sCurrentLine = br.readLine()) != null){
            String[] tokens = sCurrentLine.trim().split("\\s+");
            for (int j = 0; j < tokens.length; j++){
                result[rowIdx][j] = Double.parseDouble(tokens[j]);
            }
            rowIdx++;
        }
        br.close();
        return result;
    }
    
    
    public static int[] getLabel(String inputLabelFile) throws IOException{
        File file = new File(inputLabelFile);
        BufferedReader br = null;
        String sCurrentLine = "";
        br = new BufferedReader(new FileReader(file));
        int row = 0;
        while ((sCurrentLine = br.readLine()) != null){
            row++;
        }
        br.close();
        
        int[] result = new int[row];
        br = new BufferedReader(new FileReader(file));
        int rowIdx = 0;
        while ((sCurrentLine = br.readLine()) != null){
            String[] tokens = sCurrentLine.trim().split("\\s+");
            result[rowIdx] = (int) Double.parseDouble(tokens[0]);
            rowIdx++;
        }
        br.close();
        return result;
    }
}
