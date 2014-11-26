package ordinal.app;

import ordinal.classifier.LinearSVM;
import ordinal.data.Data;
import ordinal.datasets.Car;

public class TestApp{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	for (int i = 0; i < 1; i++){
        Data trainData = Car.getData();
        LinearSVM svm = new LinearSVM();
        svm.setTrainData(trainData);
        svm.init();
        
        svm.learn();
    	}
    }
}
