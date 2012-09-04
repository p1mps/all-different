package org.alldifferent;

import java.util.Vector;

public class ConstraintAllDifferent {
	
	private Vector<Variable> variables = new Vector<Variable>();

	public void addVariable(Variable v){
		variables.add(v);
	}


	public ConstraintAllDifferent(ConstraintAllDifferent c){
		for (int i = 0; i < c.getVariables().size(); i++) {
			variables.add(new Variable(c.getVariables().get(i)));
			
		}
		
	}
	
	//k è |K|
	public boolean isHall(Range r,int k){
		
		if(r.size() == k)
			return true;
		
		return false;
	}
	
	
	
	//conta domini variabili in un determinato range
	public Vector<Variable> countVariables(Range range){
		
		Vector<Variable> v = new Vector<Variable>();
		
		for (int i = 0; i < this.variables.size(); i++) {
			if(!variables.get(i).getDomain().isEmpty() && variables.get(i).getDomain().getInterval().getStart() >= range.getStart() && variables.get(i).getDomain().getInterval().getEnd() <= range.getEnd())
				v.add(variables.get(i));			
		}
		
		return v;
	}
	public ConstraintAllDifferent() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		
		String s = "Problem Variables: \n";
		
		for(int i = 0; i < variables.size(); i++){
			
			s = s + variables.get(i).toString() + "\n";
		}
		
		s = s + "alldifferent( ";
		
		for(int i = 0; i < variables.size(); i++){
			s = s + "x" + i + " ";
		}
		
		s = s + ")\n";
		
		return s;
	}

	public Vector<Variable> getVariables() {
		return variables;
	}
	
	
	
}
