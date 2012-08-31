package org.alldifferent;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

//tipi di consistenza da applicare durante il backtracking




public class CSP {


	static private Algo algo;
	
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
	private Chronometer chronometer = new Chronometer();

	public void createFiles(){
	
		File log = new File(CSP.algo + " log.txt");
		File time = new File(CSP.algo + "time.txt");
		
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
			
			fstreamlog = new FileWriter(CSP.algo + " log.txt", true);
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
	public void generateRandom(int index,Algo algo){
		

		double averageTime = 0,sumTime=0;
		
		for (int k = 0; k < 100; k++) {

			ConstraintAllDifferent c = new ConstraintAllDifferent();

			for(int i = 0; i < index; i++){

				Domain d = new Domain();

				int a = (int)(Math.random()*index);
				int b = (int)(Math.random()*index);

				
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
			chronometer.start();
			finishedTimes.add(chronometer.read());
			this.backtracking(0, false, copy);
			System.out.println("dopo " +this);
			long lettura = chronometer.read();
			finishedTimes.add(lettura);
			
			System.out.println("ci ho messo "+lettura);
			chronometer.clean();
			
			this.dumpTimes("tempo "+lettura);
			sumTime = sumTime + lettura;

		}

		averageTime = sumTime/100.0;
		
		try {
			fstreamtime = new FileWriter(CSP.algo + " time.txt", true);
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
		
		if(k == constraint.getVariables().size())
			ok = true;

		return ok;


	}



	//implementazione primo algoritmo
	public boolean consistentArcConsistency(){

		//vettore variabili risolte
		Vector<Variable> q = new Vector<Variable>();

		//le variabili che hanno |D(x_i)=1| posso prenderle subito
		for(int i = 0; i < constraint.getVariables().size(); i++){
			if(constraint.getVariables().get(i).getDomain().getValues().size() == 1){
				q.add(constraint.getVariables().get(i)); 
			}

		}



		while(q.isEmpty() == false){

			//prendo xi primo elemento della coda q
			Variable xi = (Variable) q.firstElement();
			//la rimuovo dalla coda
			q.remove(0);
			//indice della variabile
			int i = xi.getId();
			

			for (int j = i+1; j < constraint.getVariables().size(); j++) {
				
				if(xi.getDomain().intersect(constraint.getVariables().get(j).getDomain())){
					constraint.getVariables().get(j).getDomain().removeValues(xi.getDomain());
					if(constraint.getVariables().get(j).getDomain().isEmpty())
						return false;
					if(constraint.getVariables().get(j).getDomain().getSize() == 1)
						q.add(constraint.getVariables().get(j));
				}
			}



		}

	return true;

	}



	public boolean consistent() {	

		boolean consistency = true;

		for (int i = 0; i < constraint.getVariables().size() && consistency; i++) {
			if(constraint.getVariables().get(i).getDomain().getValues().size()!=1)
				consistency = false;
		}
		return consistency;

	}

	public boolean consistentBound(){
		
		
		return false;
		
	}
	
	
	//j indice variabile success booleano per la ricorsione
	//call backtracking(0,false);
	//parametro enum per scegliere il Domain filter
	public void backtracking(int j,boolean success,CSP copy){

		//System.out.println("copia csp1" + copy);

		while(j < constraint.getVariables().size() && !success){

			for (int d = 0; d < constraint.getVariables().get(j).getDomain().getValues().size() && j < constraint.getVariables().size() && !success; d++) {

				if(j == constraint.getVariables().size() -1 && consistentArcConsistency() && consistent()){
					this.consistent = true;
					success = true;
					return;
				}
				else{
					//xj = {d}
					if(constraint.getVariables().get(j).getDomain().getValues().size() > d){
						Domain newDomain = new Domain();
						Vector<Integer> values = new Vector<Integer>();

						values.add(constraint.getVariables().get(j).getDomain().getValues().get(d));
						newDomain.setValues(values);
						constraint.getVariables().get(j).setDomain(newDomain);
					}

					//forse consistent non serve mica :\
					switch (CSP.algo) {
					case arc:
						if(consistentArcConsistency() && consistent()){
							success = false;
							backtracking(j+1, success,copy);

						}
						else{
							this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
							this.constraint.getVariables().get(j).getDomain().removeValue(d);
							this.consistent = false;
							copy = new CSP(this);
						}

						break;

						
					case bounds:
						if(consistentBound() && consistent()){
							success = false;
							backtracking(j+1, success,copy);

						}
						else{
							this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
							this.constraint.getVariables().get(j).getDomain().removeValue(d);
							this.consistent = false;
							copy = new CSP(this);
						}

						break;
						
						
						
					default:
						break;
					}
				}
			}

			j++;
		}
	}




	public String toString() {

		String s = "";
		s = s + constraint.toString();
		s = s + "consistent: " + consistent;
		return s;
	}

	public static void main(String[] args) {
		CSP c = new CSP();
//
//		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
//		Vector<Integer> valuesX1 = new Vector<Integer>();
//		Vector<Integer> valuesX2 = new Vector<Integer>();
//		Vector<Integer> valuesX3 = new Vector<Integer>();
//
//		//valuesX1.add(0);
//		valuesX1.add(1);
//		valuesX1.add(2);
//		
//		valuesX2.add(1);
//		valuesX2.add(2);
//		valuesX3.add(2);
//		//valuesX3.add(0);
//		//				valuesX3.add(1);
//		//				valuesX3.add(2);
//		//				
//
//		Domain dX1 = new Domain();
//		Domain dX2 = new Domain();
//		Domain dX3 = new Domain();
//
//		dX1.setValues(valuesX1);
//		dX2.setValues(valuesX2);
//		dX3.setValues(valuesX3);
//
//
//		Variable x1 = new Variable(0,dX1);
//		Variable x2 = new Variable(1,dX2);
//		Variable x3 = new Variable(2,dX3);
//
//		constraint.addVariable(x1); 
//		constraint.addVariable(x2);
//		constraint.addVariable(x3);

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
		CSP.algo = Algo.arc;
		
		for (int i = 4; i < 10; i++) {
			c.generateRandom(i,Algo.arc);	
		}
		
		CSP.algo = Algo.bounds;
		
		for (int i = 4; i < 10; i++) {
			c.generateRandom(i,Algo.arc);	
		}
		
		
//		c.setConstraint(constraint);
//		CSP copy = new CSP(c);
//		System.out.println(c);
//		boolean success = false;
//		c.backtracking(0, success, copy);
//		System.out.println(c);
//		System.out.println(success);
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
