package metriclearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Data {
    private String file_input_data;
    private String file_input_label;
    
    public Data(String file_input_data, String file_input_label){
        this.file_input_data = file_input_data;
        this.file_input_label = file_input_label;
    }
    
    public double[][] getOrdinalData() throws IOException{
        double[][] input_data = getData();
        for (int i = 0; i < input_data.length; i++){
            input_data[i][0] = input_data[i][0] * 3 + 1;
        }
        
        return input_data;
    }
    
    public double[][] getData() throws IOException{
        File file = new File(file_input_data);
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
        br = new BufferedReader(new FileReader(file));
        int rowIdx = 0;
        while ((sCurrentLine = br.readLine()) != null){
            String[] tokens = sCurrentLine.trim().split("\\s+");
            for (int j = 0; j < tokens.length; j++){
                result[rowIdx][j] = Double.parseDouble(tokens[j]);
            }
            rowIdx++;
        }
        
        return result;
    }
    
    
    public int[] getLabel() throws IOException{
        File file = new File(file_input_label);
        BufferedReader br = null;
        String sCurrentLine = "";
        br = new BufferedReader(new FileReader(file));
        int row = 0;
        while ((sCurrentLine = br.readLine()) != null){
            row++;
        }
        
        int[] result = new int[row];
        br = new BufferedReader(new FileReader(file));
        int rowIdx = 0;
        while ((sCurrentLine = br.readLine()) != null){
            String[] tokens = sCurrentLine.trim().split("\\s+");
            result[rowIdx] = (int) Double.parseDouble(tokens[0]);
            rowIdx++;
        }
        
        return result;
    }
}
