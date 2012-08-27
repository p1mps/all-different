package org.alldifferent;

import java.util.Calendar;
import java.util.Vector;

public class CSP {

	
	private ConstraintAllDifferent constraint;
	//private long finishedTime = 0;
	
	
	/**
	 * @param args
	 */
	
	public long getTime(){
		Calendar c= Calendar.getInstance();
		long t = c.getTimeInMillis();
		return t;
	}
	
	
	public long calculateTime(long t1,long t2){
		return t2-t1;
	}
	
	
	
	//genera problema con numero di variabili random e cardinalità di dominio
	//i parametro indica l'"iterazione": il numero di variabili e la somma della cardinalità del loro dominio
	public void generateRandom(int index){
		
		ConstraintAllDifferent c = new ConstraintAllDifferent();
		
		for(int i = 0; i < index; i++){
			
			Domain d = new Domain();
			
			int a = (int)(Math.random()*index);
			int b = (int)(Math.random()*index);
			
			System.out.println(a + " " + b);
			//scambio i valori se a è maggiore di b
			if(a > b)
			{
				int t = a;
				a = b;
				b = t;
			}
			
			//aggiungo valori nell'intervallo [a,b]
			Vector<Integer> values = new Vector<Integer>();
			
			for(int j = a; j <= b; j++)
				values.add(new Integer(j));
			
			d.setValues(values);
			Variable v = new Variable(i,d);
			c.addVariable(v);
			
			
		}
		
		this.constraint = c;
		
	}
	
	public boolean checkSolution() {
		
		int k = 0;
		
		for (int i = 0; i < constraint.getVariables().size(); i++) {
			if(constraint.getVariables().get(i).getValue().equals(new Integer(-1)) == false)
				k = k+1;
			
		}
		
		System.out.println("check solution: " + constraint.getVariables().size() + " " + k);
		return k == constraint.getVariables().size() -1;
		
	}
	
	

	//implementazione primo algoritmo
	public boolean consistentArcConsistency(){
		
		//vettore variabili risolte
		Vector<Variable> q = new Vector<Variable>();
		
		//boolean foundOne = false;
		
		//le variabili che hanno |D(x_i)=1| posso prenderle subito
		for(int i = 0; i < constraint.getVariables().size(); i++){
			if(constraint.getVariables().get(i).getDomain().getValues().size() == 1){
				 
				q.add(constraint.getVariables().get(i)); 
			}
			
		}

		if(q.size() == constraint.getVariables().size())
			return true;
		
		
	
		System.out.println("Problema da vedere se è consistente");
		System.out.println(this);
		
		while(q.isEmpty() == false){
			
			//prendo xi primo elemento della coda q
			Variable xi = (Variable) q.firstElement();
			//la rimuovo dalla coda
			q.remove(0);
			//indice della variabile
			int i = xi.getId();

			for (int j = 0; j < constraint.getVariables().size() - i; j++) {
				if(constraint.getVariables().get(j).getDomain().intersect(xi.getDomain())){
					//System.out.println(constraint.getVariables().get(j));
					//System.out.println(xi.getDomain().getValues() + " " + j);
					constraint.getVariables().get(j).getDomain().removeValues(xi.getDomain());
					//System.out.println(xi.getDomain().getValues());
					//System.out.println(constraint.getVariables().get(j));
					if(constraint.getVariables().get(j).getDomain().isEmpty())
						return false;
					if(constraint.getVariables().get(j).getDomain().getSize() == 1)
						q.add(constraint.getVariables().get(j));
				}
			}
			
			
				
			}
			
		System.out.println("Problema reso consistente");
		System.out.println(this);
			
		return true;
		
	}
	
	
	public void printSolution() {
		
		boolean success = false;
		this.backtracking(0, success);
		
		if(success)
			System.out.println("FOUND SOLUTION!");
		
		for (int i = 0; i < constraint.getVariables().size(); i++) {
			System.out.println(constraint.getVariables().get(i));
			
		}
	}
	
	//j indice variabile success booleano per la ricorsione
	//call backtracking(0,false);
	//parametro enum per scegliere il Domain filter
	public void backtracking(int j,boolean success){
		
		System.out.println("seleziono variabile " +j);
		
		while(constraint.getVariables().get(j).getDomain().isEmpty() == false && success == false){
			
			for(int d = 0; d < constraint.getVariables().get(j).getDomain().getValues().size(); d++){
				
				//System.out.println("setto valore " +d);
				//salvo dominio
				Domain dBefore = constraint.getVariables().get(j).getDomain();
				//valore finale variabile se success diventa true
				Integer valueBefore = constraint.getVariables().get(j).getDomain().getValues().get(d);
				//rimuovo valore d dal dominio
				dBefore.removeValue(d);
				System.out.println("rimosso valore " + constraint.getVariables().get(j).getDomain().getValues().get(d));		
				
				
				
				//System.out.println(constraint.getVariables().get(j));
				
				
				
				if(consistentArcConsistency()){				
					
					//nuovo dominio variabile {d}
					Vector<Integer> domainValue = new Vector<Integer>();
					domainValue.add(valueBefore);
					Domain newDomain = new Domain();
					newDomain.setValues(domainValue);
					constraint.getVariables().get(j).setDomain(newDomain);
					constraint.getVariables().get(j).setValue(valueBefore);



					if(j == constraint.getVariables().size() - 1){
						success = true;
					}
					
					if(success == false){
						//constraint.getVariables().get(j).setDomain(dBefore);
						System.out.println("faccio backtracking");
						backtracking(j + 1, success);
						
					}
				}
				constraint.getVariables().get(j).setDomain(dBefore);
				constraint.getVariables().get(j).setValue(new Integer(-1));
				
			}
			
		}
		
	}
	
	public String toString() {
		
		String s = "";
		s = constraint.toString();
		return s;
	}
	
	public static void main(String[] args) {

		CSP c = new CSP();
		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
		Vector<Integer> valuesX1 = new Vector<Integer>();
		Vector<Integer> valuesX2 = new Vector<Integer>();
		Vector<Integer> valuesX3 = new Vector<Integer>();
		
		valuesX1.add(0);
		valuesX1.add(1);
		valuesX1.add(2);
		valuesX2.add(0);
		valuesX2.add(1);
		valuesX2.add(2);
		valuesX3.add(0);
		valuesX3.add(1);
		valuesX3.add(2);
		valuesX3.add(3);
		
		Domain dX1 = new Domain();
		Domain dX2 = new Domain();
		Domain dX3 = new Domain();
		
		dX1.setValues(valuesX1);
		dX2.setValues(valuesX2);
		dX3.setValues(valuesX3);
		
		
		Variable x1 = new Variable(1,dX1);
		Variable x2 = new Variable(2,dX2);
		Variable x3 = new Variable(3,dX3);
		
		constraint.addVariable(x1); 
		constraint.addVariable(x2);
		constraint.addVariable(x3);
		
		c.setConstraint(constraint);
		System.out.println(c);
		boolean success = false;
		c.backtracking(0, success);
		System.out.println(success);
		System.out.println(c);
		c.printSolution();
		
	}


	public ConstraintAllDifferent getConstraint() {
		return constraint;
	}


	public void setConstraint(ConstraintAllDifferent constraint) {
		this.constraint = constraint;
	}


	
	
	
	
	
}
