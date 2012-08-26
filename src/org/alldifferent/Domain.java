package org.alldifferent;

import java.util.Vector;

public class Domain {

	private Vector<Integer> values = new Vector<Integer>(); 
	
	
	public boolean isEmpty(){
		return values.isEmpty();
	}
	
	public void instantiate(Vector<Integer> v){

		values = v;

	}
	
	public int getSize(){
		return values.size();
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
		
		
	}
	
	
	
	public void removeValue(int i) {
		values.remove(i);
		
	}
	
	
	public Vector<Integer> getValues() {
		return values;
	}

	public void setValues(Vector<Integer> values) {
		this.values = values;
	}
	
	
	
}
