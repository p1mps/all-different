package org.alldifferent;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.FSTORE;

public class CSP {


	private ConstraintAllDifferent constraint;
	//tempi per la risoluzione dei problemi
	private Vector<Long> finishedTimes = new Vector<Long>();
	private boolean consistent = false;
	//file di log
	private FileWriter fstreamlog; 
	private BufferedWriter outlog;
	//file tempi
	private FileWriter fstreamtime; 
	private BufferedWriter outlogtime;
	private Cronometro cronometro = new Cronometro();
//	
	public void createFiles(){
	
		File log = new File("log.txt");
		File time = new File("time.txt");
		
		if(log.exists())
			log.delete();
		
		if(time.exists())
			time.delete();
		
	}	
	
	public CSP() {
		
		 
	}

	public CSP(CSP c) {
		// TODO Auto-generated constructor stub
		this.constraint = new ConstraintAllDifferent(c.getConstraint());
	}

	//scrive sul file di log i tempi per la risoluzione
	public void dumpTimes(String s){

		try {
			
			fstreamlog = new FileWriter("log.txt", true);
			outlog = new BufferedWriter(fstreamlog);
			outlog.write(this.toString());
			outlog.newLine();
			outlog.write(s);
			System.out.println("tempo: "+s);
			for (int i = 0; i < constraint.getVariables().size(); i++) {
				if(constraint.getVariables().get(i).getDomain().getValues().size() > 1)
					System.out.println("ERROREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
				
			}
			outlog.newLine();
			outlog.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	/**
	 * @param args
	 */

	public double getTime(){
		Calendar c= Calendar.getInstance();
		double t = c.getTimeInMillis();
		
		
		System.err.println("tempo  "+ t);
		return t;
	}


	public double calculateTime(Double t1,Double t2){
		return t2-t1;
	}



	//genera 100 problemi random, li risolve e scrive sul file di log i risultati
	//index == numero variabili	
	public void generateRandom(int index){
		
		System.out.println("index "+index);

		double averageTime = 0,sumTime=0;
		
		for (int k = 0; k < 100; k++) {
			System.out.println("k="+k);

			ConstraintAllDifferent c = new ConstraintAllDifferent();

			for(int i = 0; i < index; i++){

				Domain d = new Domain();

				int a = (int)(Math.random()*index);
				int b = (int)(Math.random()*index);

				//System.out.println(a + " " + b);
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
			
			CSP copy = new CSP(this);
			cronometro.avanza();
			finishedTimes.add(cronometro.leggi());
			System.out.println("prima " +this);
			this.backtracking(0, false, copy);
			System.out.println("dopo " +this);
			long lettura = cronometro.leggi();
			finishedTimes.add(lettura);
			
			System.out.println("ci ho messo "+lettura);
			cronometro.azzera();
			
			this.dumpTimes("tempo "+lettura);
			sumTime = sumTime + lettura;

		}

		averageTime = sumTime/100.0;
		
		try {
			fstreamtime = new FileWriter("time.txt", true);
			outlogtime = new BufferedWriter(fstreamtime);
			outlogtime.write(index + " " + averageTime);
			System.out.println("tempo medio "+  index + " variabili: "+averageTime);
			outlogtime.newLine();
			outlogtime.close();
			
		} catch (IOException e) {

	}


	}

	public boolean checkSolution() {

		int k = 0;

		for (int i = 0; i < constraint.getVariables().size(); i++) {
			if(constraint.getVariables().get(i).getDomain().getValues().size() == 1)
				k = k+1;

		}
		boolean ok = false;
		//System.out.println("check solution: " + constraint.getVariables().size() + " " + k);

		if(k == constraint.getVariables().size())
			ok = true;

		return ok;


	}



	//implementazione primo algoritmo
	public boolean consistentArcConsistency(){

		//vettore variabili risolte
		Vector<Variable> q = new Vector<Variable>();

		//boolean foundOne = false;

		//le variabili che hanno |D(x_i)=1| posso prenderle subito
		for(int i = 0; i < constraint.getVariables().size(); i++){
			if(constraint.getVariables().get(i).getDomain().getValues().size() == 1){
				//System.out.println("metto nella coda "+constraint.getVariables().get(i));
				q.add(constraint.getVariables().get(i)); 
			}

		}



		//		System.out.println("Problema da vedere se è consistente");
		//		System.out.println(this);
		//		
		while(q.isEmpty() == false){

			//prendo xi primo elemento della coda q
			Variable xi = (Variable) q.firstElement();
			//la rimuovo dalla coda
			q.remove(0);
			//indice della variabile
			int i = xi.getId();
			//System.out.println("controllo "+xi + " id:"+i+ " "+constraint.getVariables().size());

			for (int j = i+1; j < constraint.getVariables().size(); j++) {
				//System.out.println("con "+constraint.getVariables().get(j));
				if(xi.getDomain().intersect(constraint.getVariables().get(j).getDomain())){
					//System.out.println(j + " " + i);
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

		//		System.out.println("Problema reso consistente");		System.out.println(this);
		//			
		return true;

	}


	public void printSolution() {
		
		for (int i = 0; i < constraint.getVariables().size(); i++) {
			System.out.println(constraint.getVariables().get(i));
		}

	}

	//reset variabili da posizione j
	public void resetVariables(Vector<Variable> v,int j) {

		for (int i = 0; i < v.size(); i++) {


			System.out.println("vecchia variabile "+constraint.getVariables().get(i) );


			constraint.getVariables().insertElementAt(v.get(i), i);
			constraint.getVariables().remove(i);
			System.out.println("nuova variabile "+constraint.getVariables().get(i));

		}


	}


	public boolean consistent() {	

		boolean consistency = true;

		for (int i = 0; i < constraint.getVariables().size() && consistency; i++) {
			if(constraint.getVariables().get(i).getDomain().getValues().size()!=1)
				consistency = false;
		}
		return consistency;

	}

	//j indice variabile success booleano per la ricorsione
	//call backtracking(0,false);
	//parametro enum per scegliere il Domain filter
	public void backtracking(int j,boolean success,CSP copy){

		//System.out.println("copia csp1" + copy);

		while(j < constraint.getVariables().size() && !success){

			for (int d = 0; d < constraint.getVariables().get(j).getDomain().getValues().size() && j < constraint.getVariables().size() && !success; d++) {

				System.out.println("variabile "+  j+ " vale " +constraint.getVariables().get(j).getDomain().getValues().get(d));
				//se sono all'inizio devo salvarmi i domini
				
				if(j == constraint.getVariables().size() -1 && consistentArcConsistency()){
					System.out.println("ho finito!");
					this.consistent = true;
					success = true;
					return;
				}
				else{
					//xj = {d}
					if(constraint.getVariables().get(j).getDomain().getValues().size() > 0){
					Domain newDomain = new Domain();
					Vector<Integer> values = new Vector<Integer>();
					
					values.add(constraint.getVariables().get(j).getDomain().getValues().get(d));
					newDomain.setValues(values);
					constraint.getVariables().get(j).setDomain(newDomain);
					//System.out.println("copia csp2" + copy);
					}
					if(consistentArcConsistency() && consistent()){
						System.out.println("backtracking\n"+this);
						
						backtracking(j+1, success,copy);
						//System.out.println("consistente");
					}
					else{
						//System.out.println("ricopio csp" + copy);
						//DEVO COPIARE DA J AL SIZE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
						this.constraint.getVariables().get(j).getDomain().removeValue(d);
						
						copy = new CSP(this);
						//copy = new CSP(this);
						//						System.out.println("copia csp" + copy);
						//						System.out.println("nuovo csp" + this);
					}
				
				}

			}

			j++;


		}

	}















	//		
	//		System.out.println("seleziono variabile " +j);
	//		
	//		while(constraint.getVariables().get(j).getDomain().isEmpty() == false && success == false){
	//			
	//			for(int d = 0; d < constraint.getVariables().get(j).getDomain().getValues().size() && success == false; d++){
	//				
	//				//System.out.println("setto valore " +constraint.getVariables().get(j).getDomain().getValues().get(d));
	//				//salvo dominio
	//				System.out.println(d);
	//				//valore finale variabile se success diventa true
	//				Integer valueBefore = constraint.getVariables().get(j).getDomain().getValues().get(d);
	//				//rimuovo valore d dal dominio
	//				constraint.getVariables().get(j).getDomain().removeValue(d);
	//				
	//				
	//				
	//				Domain dBefore = constraint.getVariables().get(j).getDomain();
	//				//System.out.println("rimosso valore "+constraint.getVariables().get(j));
	//				//nuovo dominio variabile {d}
	//				
	//				if(consistent()){
	//				
	//					success = (j == constraint.getVariables().size()-1);
	//					Vector<Integer> domainValue = new Vector<Integer>();
	//					domainValue.add(valueBefore);
	//					Domain newDomain = new Domain();
	//					newDomain.setValues(domainValue);
	//					constraint.getVariables().get(j).setDomain(newDomain);
	////					System.out.println("nuova variabile "+constraint.getVariables().get(j));
	////					System.out.println("nuovo dominio variabile  "+newDomain.toString());
	//					constraint.getVariables().get(j).setValue(valueBefore);
	//					System.out.println("success è false!");
	//				
	//				//System.out.println(constraint.getVariables().get(j));
	//					if(success == false){
	//						
	//					
	//						
	//						//constraint.getVariables().get(j).setDomain(dBefore);
	//						System.out.println("faccio backtracking "+ j);
	//						//System.out.println(constraint.getVariables().size());
	//						backtracking(j + 1, success);
	//						System.out.println(this);
	//						//devo risistemare i domini
	//						//success = true;
	//						//System.out.println("dopo ricorsione "+success);
	//						//constraint.getVariables().get(j).setDomain(dBefore);
	//						
	//					}
	//					//constraint.getVariables().get(j).setValue(valueBefore);
	//					
	//						
	//						
	//					
	//
	//					
	//				}
	//				
	//				//resetVariables(variables,j+1);
	//				
	//
	//				//constraint.getVariables().get(j).setValue(new Integer(-1));
	//				
	//			}
	//			
	//		}







	public String toString() {

		String s = "";
		s = s + constraint.toString();
		s = s + "consistent: " + consistent;
		return s;
	}

	public static void main(String[] args) {
		CSP c = new CSP();

		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
		Vector<Integer> valuesX1 = new Vector<Integer>();
		Vector<Integer> valuesX2 = new Vector<Integer>();
		Vector<Integer> valuesX3 = new Vector<Integer>();

		//valuesX1.add(0);
		valuesX1.add(1);
		valuesX1.add(2);
		
		valuesX2.add(1);
		valuesX2.add(2);
		valuesX3.add(2);
		//valuesX3.add(0);
		//				valuesX3.add(1);
		//				valuesX3.add(2);
		//				

		Domain dX1 = new Domain();
		Domain dX2 = new Domain();
		Domain dX3 = new Domain();

		dX1.setValues(valuesX1);
		dX2.setValues(valuesX2);
		dX3.setValues(valuesX3);


		Variable x1 = new Variable(0,dX1);
		Variable x2 = new Variable(1,dX2);
		Variable x3 = new Variable(2,dX3);

		constraint.addVariable(x1); 
		constraint.addVariable(x2);
		constraint.addVariable(x3);

		//		c.setConstraint(constraint);
		//		System.out.println(c);
		//
		//		boolean success = false;
		//		CSP copy = new CSP(c);
		//		c.backtracking(0, success,copy);
		//
		//		System.out.println(c);
		//		success = c.consistent();
		//		System.out.println(success);
//		c.createFiles();
//		
		for (int i = 3; i < 5; i++) {
			c.generateRandom(i);	
		}
		c.setConstraint(constraint);
		CSP copy = new CSP(c);
		System.out.println(c);
		boolean success = false;
		c.backtracking(0, success, copy);
		System.out.println(c);
		System.out.println(success);
	}


	public ConstraintAllDifferent getConstraint() {
		return constraint;
	}


	public void setConstraint(ConstraintAllDifferent constraint) {
		this.constraint = constraint;
	}

	public boolean isConsistent() {
		return consistent;
	}

	public void setConsistent(boolean consistent) {
		this.consistent = consistent;
	}







}
