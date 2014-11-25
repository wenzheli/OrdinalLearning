package ordinal.data;

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
		for (int i = 0; i < numOfOrdinalVals - 1; i++)
			endPoints.add(Math.random());
		
		// sort in increasing order
		Collections.sort(endPoints);
	}
	
	public int size(){
		return endPoints.size();
	}	
}
