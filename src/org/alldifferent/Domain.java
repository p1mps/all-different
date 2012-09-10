package org.alldifferent;

import java.util.Vector;


public class Domain {

	private Vector<Integer> values = new Vector<Integer>(); 
	private Range interval;

	public Domain(){

	}

	public Domain(Domain d){

		for (int i = 0; i < d.getValues().size(); i++) {
			values.add(new Integer(d.getValues().get(i)));
		}

		this.interval = new Range(d.getMin(),d.getMax());
	}


	public boolean isEmpty(){
		return values.isEmpty();
	}

	public void instantiate(Vector<Integer> v){
		values = v;
	}

	public int getSize(){
		return values.size();
	}

	public void removeValues(Vector<Integer> v){

		for(int i=0; i<v.size();i++)
			if(this.values.contains(v.get(i)))
				this.values.remove(v.get(i));

	}
	//ritorna l'insieme degli interi dell'intersezione i 2 domini
	public Vector<Integer> intersectInterval(Range interval){

		Vector<Integer> v = new Vector<Integer>();
		if(this.getValues().contains(interval.getStart()))
			v.add(interval.getStart());
		if(this.getValues().contains(interval.getEnd()))
			v.add(interval.getEnd());

		return v;

	}


	//ritorna true se l'intersezione è non vuota
	public boolean intersect(Domain d){


		for(int i = 0; i < d.getSize(); i++){
			if(this.getValues().contains(d.getValues().get(i)) == true){
				return true;
			}
		}

		return false;
	}

	//toglie da this tutti i valori che ci sono in d ma non in this
	public void removeValues(Domain d){

		for(int i = 0; i < d.getSize(); i++){
			if(this.getValues().contains(d.getValues().get(i)) == true)
				values.remove(d.getValues().get(i));

		}
		buildInterval();
	}



	public void removeValue(int i) {
		values.remove(i);

	}


	public Integer getMax() {

		if(!values.isEmpty()){
			Integer max = values.get(0);

			for (int i = 1; i < values.size(); i++) {
				if(values.get(i) >= max)
					max = values.get(i);
			}
			return max;
		}
		else
			return null;
	}

	public Integer getMin() {

		if(!values.isEmpty()){
			Integer min = values.get(0);

			for (int i = 1; i < values.size(); i++) {
				if(values.get(i) < min)
					min = values.get(i);
			}
			return min;
		}
		else
			return null;
	}



	public void buildInterval() {

		this.interval = new Range(getMin(),getMax());

	}





	public String toString() {

		String s = "";
		s = s + values;// + "\n"; 
		return s;

	}

	public Vector<Integer> getValues() {
		return values;
	}

	public void setValues(Vector<Integer> values) {
		this.values = values;
	}



	public Range getInterval() {
		return interval;
	}


	public void setInterval(Range interval) {
		this.interval = interval;
	}


}
