package org.alldifferent;

import java.util.Vector;



public class Range  {

	private Integer start; 
	private Integer end;
	private Vector<Integer> values = new Vector<Integer>();
	
	Range(Integer start,Integer end){
		
		if(start != null && end != null){
			this.setStart(start);
			this.setEnd(end);
			for(int i=start; i <= end; i++)
				values.add(i);
		}
	}
	
	public String toString(){
		if(!values.isEmpty())
			return values.toString();
		else
			return "";
	}
	
	public int size(){
		
		return end-start;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}
	
	

}
