package org.alldifferent;

import java.util.Vector;

public class ConstraintAllDifferent {
	
	private Vector<Variable> variables = new Vector<Variable>();

	public void addVariable(Variable v){
		variables.add(v);
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
