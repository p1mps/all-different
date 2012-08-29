package org.alldifferent;

import java.util.Vector;

public class Variable {

	public Variable(int id, Domain domain) {
		this.id = id;
		this.domain = domain;
	}


	public Variable() {
		super();
	}


	private int id;
	private Domain domain;
	//forse inutile
	private Integer value = new Integer(-1);
	
	
	
	public Variable(int id,Vector<Integer> values){
		this.id = id;
		this.domain.setValues(values);
	}


	@Override
	public String toString() {
		
		String s = "";
		s = s + "x" + id + " ";
		//s = s + "variable x" + id + " ";
		/*s = s + "domain: {";
		
		for(int i = 0; i < domain.getSize(); i++){
			s = s + domain.getValues().get(i);
			if(i != domain.getSize() - 1)
				s = s + ",";
		}
		
		s = s + "}";*/
		
		if(value.equals(new Integer(-1)) == false){
			s = s + "\n";
			s = s + "final value:" + value;
		}
		
		return s;
	}


	public int getId() {
		return id;
	}



	public Domain getDomain() {
		return domain;
	}


	public void setDomain(Domain domain) {
		this.domain = domain;
	}


	public Integer getValue() {
		return value;
	}

	//settare dominio a 0!
	public void setValue(Integer value) {
		this.value = value;
	}


	
}
