package ordinal.app;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

public class QPTest {
	public static void main(String[] args) throws Exception{
		// Objective function
				double[][] P = new double[][] {{ 2, 0 }, { 0, 8 }};
			
				double[] q= new double[]{-8,-16};
				PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, q, 0);

				//equalities
				//double[][] A = new double[][]{{1,1}};
				//double[] b = new double[]{1};

				//inequalities
				ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
				inequalities[0] = new LinearMultivariateRealFunction(new double[]{1, 1}, 5);
				inequalities[1] = new LinearMultivariateRealFunction(new double[]{1, 0}, 3);
				
				//optimization problem
				OptimizationRequest or = new OptimizationRequest();
				or.setF0(objectiveFunction);
				or.setInitialPoint(new double[] { 0, 0});
				or.setFi(inequalities); //if you want x>0 and y>0
				//or.setA(A);
				//or.setB(b);
				or.setToleranceFeas(1.E-12);
				or.setTolerance(1.E-12);
				
				//optimization
				JOptimizer opt = new JOptimizer();
				opt.setOptimizationRequest(or);
				int returnCode = opt.optimize();
				
				double[] solution =opt.getOptimizationResponse().getSolution();
		for (int i = 0; i < solution.length; i++){
			System.out.println(solution[i]);
		}
	}
}
