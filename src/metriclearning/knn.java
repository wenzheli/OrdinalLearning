package metriclearning;

public class knn {
	private double[][] trainData;
	private double[][] testData;
	private int[] trainLabel;
	private int[] testLabel;
	private double[] ordinal_start;
	private double[] ordinal_end;
	
	private int K;
	
	public knn(int k, double[][] trainData, int[] trainLabel, double[][] testData, int[] testLabel){
		this.K = k;
		this.trainData = trainData;
		this.trainLabel = trainLabel;
		this.testData = testData;
		this.testLabel = testLabel;
	}
	
	public double computeTestAccuracyOrdinal(){
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
			labels[trainLabel[i]-1]++;
		}
		
		int bestLabel = 0;
		int maxCnt = Integer.MIN_VALUE;
		for (int k = 0; k < K; k++){
			if (labels[k] < maxCnt){
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
		int minIdx = 0;
		double minVal = Double.MAX_VALUE;
		for (int i = 0; i < topKIdxs.length; i++){
			if (topKDists[i] < minVal){
				minVal = topKDists[i];
				minIdx = i;
			}
		}
		
		if (distance < minVal){
			topKDists[minIdx] = distance;
			topKIdxs[minIdx] = targetIdx;
		}
	}
	
	private double computeExcepctedDistance(int idx, double[] point1,
				double[] point2){
		double dist = 0.0;
		for (int i = 0; i < point1.length; i++){
			if (idx == i){
				dist +=   Math.pow(ordinal_end[(int) point1[idx]]-ordinal_start[(int) point1[idx]], 2)/12 
						+ Math.pow(ordinal_end[(int) point2[idx]]-ordinal_start[(int) point2[idx]], 2)/12 
						+ Math.pow(ordinal_start[(int) point1[idx]] + ordinal_end[(int)point1[idx]], 2)/4 
						+ Math.pow(ordinal_start[(int) point2[idx]] + ordinal_end[(int)point2[idx]], 2)/4
						- 2 * (ordinal_start[(int) point1[idx]] + ordinal_end[(int)point1[idx]]) 
							* (ordinal_start[(int) point2[idx]] + ordinal_end[(int)point2[idx]])/4;
			} else{
				dist += Math.pow(point1[1]- point2[i], 2);
			}
		}
		return Math.sqrt(dist);
	}
	
	private double computeEuclideanDist(double[] point1, double[] point2){
		double dist = 0.0;
		for (int i = 0; i < point1.length; i++){
			dist += Math.pow(point1[i]-point2[i], 2); 
		}
		
		return Math.sqrt(dist);
	}
}
