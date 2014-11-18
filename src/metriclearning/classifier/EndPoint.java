package metriclearning.classifier;

public class EndPoint {
	private double L;
	private double R;
	
	public EndPoint(double l, double r){
		this.L = l;
		this.R = r;
	}
	
	public double getL(){ return L; }
	public double getR(){ return R; }
}
