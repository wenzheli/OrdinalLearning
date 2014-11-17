package metriclearning;

import java.io.IOException;

public class app {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stu
        Data train_data =  new Data("data/trX_norm.txt", "data/trY.txt");
        Data test_data = new Data("data/teX_norm.txt","data/teY.txt");
        //KNN knn = new KNN(3, train_data.getData(), train_data.getLabel(),
        //                    test_data.getData(), test_data.getLabel());
        //double accuracy = knn.computeTestAccuracy();
        //System.out.println("accuracy for testing data is: " + accuracy);
        double bestAccuracy = 0;
        // [0, m1], [m1,m2], [m2,m3], [m3, 1]
        for (double m1 = 0.05; m1 < 1; m1 = m1 + 0.05){
            for (double m2 = m1 + 0.05; m2 < 1; m2 = m2 + 0.05){
                for (double m3 = m2 + 0.05; m3 < 1; m3 = m3 + 0.05){
                    double[] start = new double[4];
                    double[] end = new double[4];
                    start[0] = 0;
                    start[1] = m1;
                    start[2] = m2;
                    start[3] = m3;
                    end[0] = m1;
                    end[1] = m2;
                    end[2] = m3;
                    end[3] = 1;
                                     
                    KNN knn = new KNN(3, train_data.getOrdinalData(), train_data.getLabel(),
                            test_data.getOrdinalData(), test_data.getLabel());
                    knn.setOrdinalRange(start, end);
                    double accuracy = knn.computeTestAccuracyOrdinal();
                    if (accuracy > bestAccuracy){
                        bestAccuracy = accuracy;
                    }
                    System.out.println("[" + m1 + "," + m2 +"," + m3 + "]" + accuracy);
                    System.out.println("Best accuracy so far: " + bestAccuracy);
                }
            }
        }
        
    }
}
