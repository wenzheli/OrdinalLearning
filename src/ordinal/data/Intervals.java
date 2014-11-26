package ordinal.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Intervals {
	
	/**
	 * End points of the intervals. i.e  [0, m1, m2,...mk,..1]
	 * If there are total K ordinal values, then there are K-1 end points 
	 * we need to estimate. 
	 */
	List<Double> endPoints;
	
	/**
	 * Create list of end points randomly
	 * @param numOrdinalVals
	 */
	public Intervals(int numOfOrdinalVals){
		endPoints = new ArrayList<Double>();
		for (int i = 1; i <= numOfOrdinalVals - 1; i++)
			endPoints.add(1.0*i/numOfOrdinalVals);
		
		// sort in increasing order
		Collections.sort(endPoints);
	}
	
	public double getEndPoint(int index){
		return endPoints.get(index);
	}
	
	public double getLastEndPoint(){
		return endPoints.get(size()-1);
	}
	
	public double getFirstEndPoint(){
		return endPoints.get(0);
	}
	
	public int size(){
		return endPoints.size();
	}	
}
