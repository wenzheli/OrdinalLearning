package metriclearning.classifier;

public class KNN {
	private double[][] trainData;
	private double[][] testData;
	private int[] trainLabel;
	private int[] testLabel;
	private double[] ordinal_start;
	private double[] ordinal_end;
	
	private int K;
	
	public KNN(int k, double[][] trainData, int[] trainLabel, double[][] testData, int[] testLabel){
		this.K = k;
		this.trainData = trainData;
		this.trainLabel = trainLabel;
		this.testData = testData;
		this.testLabel = testLabel;
	}
	
	public void setOrdinalRange(double[] ordinal_start, double[] ordinal_end){
	    this.ordinal_end = ordinal_end;
	    this.ordinal_start = ordinal_start;
	}
	
	public double computeTestAccuracyOrdinal(){
	    int total = testData.length;
        int accurate = 0;
        
        for (int i = 0; i < testData.length; i++){
            double[] tData = testData[i];
            // classify tData
            int label = classify(tData, true);
            if (label == testLabel[i]){
                accurate++;
            }
        }
        
        return accurate*1.0/total;
	}
	
	// test accuracy based on training data 
	public double computeTestAccuracy(){
		int total = testData.length;
		int accurate = 0;
		
		for (int i = 0; i < testData.length; i++){
			double[] tData = testData[i];
			// classify tData
			int label = classify(tData, false);
			if (label == testLabel[i]){
				accurate++;
			}
		}
		
		return accurate*1.0/total;
	}
	
	// classify the data indexed by idx, and return the label. 
	public int classify(double[] data, boolean isOrdinal){
		int[] topKIdxs = new int[K];
		double[] topKDists = new double[K];
		for (int k = 0; k < K; k++)
			topKDists[k] = Double.MAX_VALUE;
		
		// iterate over all the data sets.
		for (int j = 0; j < trainData.length; j++){
			double dist = 0.0;
			if (!isOrdinal){
				dist = computeEuclideanDist(data, trainData[j]);
			}else{
				dist = computeExcepctedDistance(0, data, trainData[j]);
			}
			updateTopKDists(topKDists, topKIdxs, dist, j);
		}
		
		int bestLabel = computeBestLabel(topKDists, topKIdxs);
		
		return bestLabel;
	}
	
	private int computeBestLabel(double[] topKDists, int[] topKIdxs){
		int[] labels = new int[3];
		for (int i = 0; i < topKIdxs.length; i++){
			labels[trainLabel[topKIdxs[i]]-1]++;
		}
		
		int bestLabel = 0;
		int maxCnt = Integer.MIN_VALUE;
		for (int k = 0; k < K; k++){
			if (labels[k] > maxCnt){
				bestLabel = k;
				maxCnt = labels[k];
			}
		}
		
		if (maxCnt > 1){
			return bestLabel+1;
		}else{
			// get the one with least distance
			double minDist = Double.MAX_VALUE;
			int minIdx = 0;
			for (int i = 0; i < K; i++){
				if (topKDists[i] < minDist){
					minDist = topKDists[i];
					minIdx = topKIdxs[i];
				}
			}
			
			return trainLabel[minIdx];
		}
	
	}
	
	private void updateTopKDists(double[] topKDists, int[] topKIdxs, double distance, int targetIdx){
		int maxIdx = 0;
		double maxVal = Double.MIN_VALUE;
		for (int i = 0; i < topKIdxs.length; i++){
			if (topKDists[i] > maxVal){
			    maxVal = topKDists[i];
				maxIdx = i;
			}
		}
		
		if (distance < maxVal){
			topKDists[maxIdx] = distance;
			topKIdxs[maxIdx] = targetIdx;
		}
	}
	
	private double computeExcepctedDistance(int idx, double[] point1,
				double[] point2){
		double dist = 0.0;
		for (int i = 0; i < point1.length; i++){
			if (idx == i){
			    double s1 = ordinal_start[(int) (Math.round(point1[idx])-1)];
			    double e1 = ordinal_end[(int) (Math.round(point1[idx])-1)];
			    double s2 = ordinal_start[(int) (Math.round (point2[idx])-1)];
			    double e2 = ordinal_end[(int) (Math.round(point2[idx])-1)];
			    
			    dist += Math.pow(e1-s1, 2)/12 + Math.pow(e2-s2, 2)/12 + Math.pow(e1+s1, 2)/4 + Math.pow(e2+s2, 2)/4
			            -2*(e1+s1)*(e2+s2)/4;
				
			} else{
				dist += Math.pow(point1[1]- point2[i], 2);
			}
		}
		return dist;
	}
	
	private double computeEuclideanDist(double[] point1, double[] point2){
		double dist = 0.0;
		for (int i = 0; i < point1.length; i++){
			dist += Math.pow(point1[i]-point2[i], 2); 
		}
		
		return dist;
	}
}
