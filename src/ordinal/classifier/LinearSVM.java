package ordinal.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

import ordinal.data.Data;
import ordinal.data.Instance;
import ordinal.data.Intervals;


public class LinearSVM implements Classifier{
	
	/** divide the data set into training, heldout and test data. */
	private Data trainData;
	private Data heldoutData;
	private Data testData;
	
	public static int MAX_ITERATION = 100;
	
	private int K;		// # of labels. 
	private int S; 		// # of ordinal features
	
	/** Intervals for all ordinal features i.e M(i,j) represents the jth end point 
	 * of ith ordinal features */
	private List<Intervals> M;
	
	// Model parameters. 
	private double[][] ordinalWeights;
	private double[][] realWeights;
	private double[] psi;
	private List<Intervals> ordinalIntervals;
	private List<Integer> ordinalIntervalSizes;
	
	private int dimension;
	private int numOfLabels;
	private int ordinalSize;
	private int realSize;
	
	private double regularizer = 100;
	
	public static final int MAX_ITERATIONS = 10;

	
	public void init(){
		dimension = trainData.getDimension();
		numOfLabels = trainData.getNumOfLabels();
		ordinalSize = trainData.getNumOfOrdinalFeatures();
		realSize = trainData.getNumOfRealFeatures();
		ordinalWeights = new double[numOfLabels][ordinalSize];	 // this is K * S  dimension. 
		realWeights = new double[numOfLabels][realSize];
		psi = new double[trainData.instances.size()];
		// should get from the train data...TODO
		ordinalIntervals = trainData.getOrdinalIntervals();    // TODO check.. 
		ordinalIntervalSizes = trainData.getOridnalIntervalSizes();
		K = numOfLabels;
		
	}
	public LinearSVM(){
	}
	
	public void setTrainData(Data trainData){
		this.trainData = trainData;
	}
	
	public void setTestData(Data testData){
		this.testData = testData;
	}
	
	public void learn() throws Exception{
		for (int itr = 0; itr < 1; itr++){
			updateWeights();
			updateIntervals();
		}
	}
	
	
	private void updateIntervals(){
		int D = dimension;
		int N = trainData.getNumOfInstances();
		int K = numOfLabels;
		
		
		
	}
	
	
	
	/**
	 * Quadratic programming, needs to construct each component needed for compute weights. 
	 * The program should be in the form of : (1/2)x'Px + q'x +  r
	 * 									 	s.t Gx <= h
	 * 											Ax  = b
	 * We basically need to create P, Q and G in our case. 
	 * 
	 * G: (NK + N)*(Dk+N)      X: DK+N  G: (NK+N) * (DK + N)   H: NK+N   P: DK+N
	 * @throws Exception 
	 */
	private void updateWeights() throws Exception{
		int DK_N = dimension * numOfLabels + trainData.getNumOfInstances();
		int NK = trainData.getNumOfInstances() * numOfLabels;
		int DK = dimension * numOfLabels;
		int N = trainData.getNumOfInstances();
		
		
		double[][] P = new double[DK_N][DK_N];
		double[] H = new double[NK];
		double[][] G = new double[NK][DK_N];
		
		// fill out P
		for (int i = 0; i < DK_N; i++){
			P[i][i] = 1;
		}
		
		for (int i = 0; i < trainData.getNumOfInstances(); i++){ // N
			Instance instance = trainData.instances.get(i);
			for (int k = 1; k <= numOfLabels; k++){  // K
				// fill out H
				if (instance.getLabel() == k){
					H[i * numOfLabels + k -1] = 0;
				}else{
					H[i * numOfLabels + k -1] = -1;
				}
				
				// fill out G
				List<Integer> ordinalFeatures = instance.getOrdinalFeatures();
				List<Double> ordinalRealFeatures = convertFromOrdinalToReal(ordinalFeatures);
				List<Double> realFeatures = instance.getRealFeatures();
				// set psi
				G[i*K + k-1][DK + i] = -1;
				
				// if k equals to label, then all the entries will be zero. 
				if (instance.getLabel() != k){ 
					// set common one - for true label
					int trueLabel = instance.getLabel();
					// for ordinal values.
					int j = 0;
					for (int s = (trueLabel-1)*dimension; s < (trueLabel-1)*dimension + ordinalSize; s++){
						G[i*K + k-1][s] = 1.0 * ordinalRealFeatures.get(j) * (-1);
						j++;
					}
					// for real values
					j = 0; 
					for (int s = (trueLabel-1)*dimension + ordinalSize; s < trueLabel*dimension; s++){
						G[i*K + k-1][s] = realFeatures.get(j) * (-1);
						j++;
					}
					
					// set for current version
					j = 0;
					for (int s = (k-1)*dimension; s < (k-1)*dimension + ordinalSize; s++){
						G[i*K + k-1][s] = ordinalRealFeatures.get(j);
						j++;
					}
					// for real values
					j = 0; 
					for (int s = (k-1)*dimension + ordinalSize; s < k*dimension; s++){
						G[i*K + k-1][s] = realFeatures.get(j);
						j++;
					}
				}	
			}			
		}
		// create Q
		double[] Q = new double[DK_N];
		for (int i = DK_N - 1; i >= DK_N-N; i--){
			Q[i] = regularizer;
		}
				
		
		for (int i = 0; i < ordinalIntervals.size(); i++){
			System.out.print("Interval for " + i + "th ordinal: [0,");
			Intervals intervals =  ordinalIntervals.get(i);
			for (int j = 0; j < intervals.size(); j++){
				System.out.print(new DecimalFormat("#0.00").format(intervals.getEndPoint(j)) + ",");
			}
			System.out.println("1]");
		}
		
		System.out.println("print out P");
		printMatrix(P);
		System.out.println("print out G");
		printMatrix(G);
		System.out.println("print out H");
		printVector(H);
		System.out.println("print out Q");
		printVector(Q);
		
		
		
		
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, Q, 0);
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[NK];
		for (int i = 0; i < NK; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], H[i]);
		}
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		double[] initialVals = new double[DK_N];
		for (int i = 0; i < initialVals.length; i++){
			initialVals[i] = 0;
		}
		for (int i = initialVals.length-1; i >= initialVals.length-N; i--){
			initialVals[i] = 1;
		}
		or.setInitialPoint(initialVals);
		or.setFi(inequalities); //if you want x>0 and y>0
		
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		double[] sol = opt.getOptimizationResponse().getSolution();
		for (int i = 0; i < sol.length; i++){
			System.out.println(sol[i]);
		}
		
	}
	
	
	
	private void convertSolToParams(double[] sol){
		
	}
	
	private List<Double> convertFromOrdinalToReal(List<Integer> ordinalFeatures){
		List<Double> result = new ArrayList<Double>(this.ordinalSize);
		for (int i = 0; i < this.ordinalSize; i++){
			int currFeature = ordinalFeatures.get(i);
			Intervals currInterval = this.ordinalIntervals.get(i);
			double begin, end;
			if (currFeature > currInterval.size()){
				begin = currInterval.getLastEndPoint();
				end = 1;
			} else if(currFeature == 1){
				begin = 0;
				end = currInterval.getFirstEndPoint();
			} else{
				begin = currInterval.getEndPoint(currFeature - 2);
				end = currInterval.getEndPoint(currFeature - 1);
				
			}
			result.add((begin + end)/2);
		}
		return result;
	}

	public double computeObjective(){
		return 0.0;
	}
	
	@Override
	public double computeAccuracy(Data data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int classify(Instance instance) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void train() {
		
	}
	
	
	public void printMatrix(double[][] matrix){
		for (int i= 0; i < matrix.length;i++){
			for (int j = 0; j < matrix[0].length; j++){
				System.out.print(new DecimalFormat("#0.00").format(matrix[i][j]) + ",") ;
			}
			System.out.println();
		}
	}
	
	public void printVector(double[] vector){
		for (int i = 0; i < vector.length; i++){
			System.out.print(new DecimalFormat("#0.00").format(vector[i]) + ",");
		}
		System.out.println();
	}
	

}
