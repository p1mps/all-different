package org.alldifferent;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

//tipi di consistenza da applicare durante il backtracking




public  class CSP {


	static private Algo algo;

	private ConstraintAllDifferent constraint;

	//tempi per la risoluzione dei problemi
	private Vector<Long> finishedTimesArc = new Vector<Long>();
	private Vector<Long> finishedTimesBounds = new Vector<Long>();
	private Vector<Long> finishedTimesRange = new Vector<Long>();
	double averageTimeArc = 0,sumTimeArc=0;
	double averageTimeBounds = 0,sumTimeBounds=0;
	double averageTimeRange = 0,sumTimeRange=0;

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
//			System.out.println("tempo: "+s);
//			for (int i = 0; i < constraint.getVariables().size(); i++) {
//				if(constraint.getVariables().get(i).getDomain().getValues().size() > 1)
//					System.out.println("ERROREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
//
//			}
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



	public void writeTimes(int index){

		try {
			fstreamtime = new FileWriter("arc" + " time.txt", true);
			outlogtime = new BufferedWriter(fstreamtime);
			outlogtime.write(index + " " + averageTimeArc);
			//System.out.println("tempo medio "+  index + " variabili: "+averageTime);
			outlogtime.newLine();
			outlogtime.close();
			fstreamtime = new FileWriter("bounds" + " time.txt", true);
			outlogtime = new BufferedWriter(fstreamtime);
			outlogtime.write(index + " " + averageTimeBounds);
			//System.out.println("tempo medio "+  index + " variabili: "+averageTime);
			outlogtime.newLine();
			outlogtime.close();
			fstreamtime = new FileWriter("ranges" + " time.txt", true);
			outlogtime = new BufferedWriter(fstreamtime);
			outlogtime.write(index + " " + averageTimeRange);
			//System.out.println("tempo medio "+  index + " variabili: "+averageTime);
			outlogtime.newLine();
			outlogtime.close();

		} catch (IOException e) {

		}

	}


	//genera 100 problemi random, li risolve con i 3 algoritmi e scrive sul file di log i risultati
	//index == numero variabili	
	public void generateRandom(int index){


		//creo 100 problemi
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
				d.buildInterval();
				Variable v = new Variable(i,d);
				c.addVariable(v);


			}


			this.constraint = c;

			//lo risolvo con tutti e 3 gli algoritmi e me lo salvo:
			int i = 0;
			CSP copy = new CSP(this);
			long lettura = 0;
			while(i < 3){

				switch(i){

				case 0:
					CSP.algo = Algo.arc;
					chronometer.start();
					finishedTimesArc.add(chronometer.read());
					this.backtracking(0, false, copy);
					//System.out.println("dopo " +this);
					lettura = chronometer.read();
					finishedTimesArc.add(lettura);
					//System.out.println("ci ho messo "+lettura);
					chronometer.clean();
					this.dumpTimes("tempo "+lettura);
					sumTimeArc = sumTimeArc + lettura;
					break;


				case 1:
					CSP.algo = Algo.bounds;
					chronometer.start();
					finishedTimesBounds.add(chronometer.read());
					this.backtracking(0, false, copy);
					//System.out.println("dopo " +this);
					lettura = chronometer.read();
					finishedTimesBounds.add(lettura);
					//System.out.println("ci ho messo "+lettura);
					chronometer.clean();
					this.dumpTimes("tempo "+lettura);
					sumTimeBounds = sumTimeBounds + lettura;
					break;


				case 2:
					CSP.algo = Algo.range;
					chronometer.start();
					finishedTimesRange.add(chronometer.read());
					this.backtracking(0, false, copy);
					//System.out.println("dopo " +this);
					lettura = chronometer.read();
					finishedTimesRange.add(lettura);
					//System.out.println("ci ho messo "+lettura);
					chronometer.clean();
					this.dumpTimes("tempo "+lettura);	
					sumTimeRange = sumTimeRange + lettura;
					break;


				}

				i++;

			}
		}

		averageTimeArc = sumTimeArc/100.0;
		averageTimeBounds = sumTimeBounds/100.0;
		averageTimeRange = sumTimeRange/100.0;

		this.writeTimes(index);


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
			if(constraint.getVariables().get(i).getDomain().isEmpty())
				consistency = false;
		}
		return consistency;

	}

	public void consistentBound(){

//		boolean foundHall = true;
		
		//System.out.println("controllo "+this);
		
		for (int i = 0; i < this.constraint.getVariables().size(); i++) {

			if(!this.constraint.getVariables().get(i).getDomain().isEmpty()){
				Range r = new Range(this.constraint.getVariables().get(i).getDomain().getInterval().getStart(),this.constraint.getVariables().get(i).getDomain().getInterval().getEnd());
				Vector<Variable> v  = this.constraint.countVariables(r);
				//System.out.println("intervallo "+r +" contiene "+ v.size());
				//è un intervallo di Hall facciamo l'intersezione tra I e K_I e,se non vuota,
				//rimuoviamo i valori dal dominio della variabile per tutte le varibili che non ci sono in k
				for (int j = 0; j < v.size(); j++) {
						
					if(!v.isEmpty() && this.constraint.isHall(r, v.size()) && !v.get(j).equals(this.constraint.getVariables().get(i))){
						//System.out.println("hall per "+i);
						Vector<Integer> valuesIntersected = this.constraint.getVariables().get(j).getDomain().intersectInterval(r);
						
						if(!valuesIntersected.isEmpty()){
							
	//						foundHall = false;
							this.constraint.getVariables().get(j).getDomain().removeValues(valuesIntersected);
							this.constraint.getVariables().get(j).getDomain().buildInterval();
						}
	
					}
				}
			}
		}

//		return foundHall;

	}

	public Integer getMaxDomain(Vector<Variable> variables) {

		if(!variables.isEmpty()){
			Integer max = variables.get(0).getDomain().getMax();


			for (int i = 1; i < variables.size() && max != null; i++) {
				variables.get(i).getDomain().getMax();
				if(!variables.get(i).getDomain().isEmpty() && variables.get(i).getDomain().getMax() > max)
					max = variables.get(i).getDomain().getMax();
			}
			return max;
		}

		return 0;
	}

	public Integer getMinDomain(Vector<Variable> variables) {

		if(!variables.isEmpty()){
			Integer min = variables.get(0).getDomain().getMin();


			for (int i = 1; i < variables.size() && min != null; i++) {
				if(!variables.get(i).getDomain().isEmpty() && variables.get(i).getDomain().getMin() > min)
					min = variables.get(i).getDomain().getMin();
			}
			return min;
		}

		return 0;
	}


	public void consistentRange() {


		//trovo tutti i sottoinsiemi delle variabili
//		System.out.println("solving range");
		Vector<Vector<Variable>> subsets = Subset.getSubsets(this.constraint.getVariables());

		for (int v = 0; v < this.constraint.getVariables().size(); v++) {
			for (int i = 0; i < subsets.size(); i++) {
				for (int j = 0; j < subsets.get(i).size(); j++) {
					if(!subsets.get(i).isEmpty() && !subsets.get(i).contains(this.constraint.getVariables().get(v)) && this.getMaxDomain(subsets.get(i)) != null && this.getMinDomain(subsets.get(i))!= null)
					{
						Range r = new Range(this.getMaxDomain(subsets.get(i)),this.getMinDomain(subsets.get(i)));
						
						for (int k = 0; k < subsets.get(i).size(); k++) {
								
							if(this.constraint.isHall(r,subsets.get(i).size()) && !subsets.get(i).get(k).equals(this.constraint.getVariables().get(v))){
	
								Vector<Integer> valuesIntersected = this.constraint.getVariables().get(k).getDomain().intersectInterval(r);
	
								if(!valuesIntersected.isEmpty()){
									this.constraint.getVariables().get(k).getDomain().removeValues(valuesIntersected);
									if(this.constraint.getVariables().get(k).getDomain().isEmpty())
										subsets.get(i).remove(k);
								}

						}
						}
					}
					else
						subsets.get(i).remove(j);
				}

			}
		}
		
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
						this.constraint.getVariables().get(j).getDomain().buildInterval();
					}

					//forse consistent non serve mica :\
					switch (CSP.algo) {
					case arc:
						if(consistentArcConsistency() && consistent()){
							//System.out.println("consistent");
							success = false;
							backtracking(j+1, success,copy);

						}
						else{
							this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
							this.constraint.getVariables().get(j).getDomain().removeValue(d);
							this.constraint.getVariables().get(j).getDomain().buildInterval();
							this.consistent = false;
							copy = new CSP(this);
							//System.out.println("not consistent");

						}

						break;


					case bounds:
						consistentBound();
						//System.out.println("bound");
						if(consistent()){
							
							success = false;
							backtracking(j+1, success,copy);

						}
						else{
							//System.out.println("not consistent");
							this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
							this.constraint.getVariables().get(j).getDomain().removeValue(d);
							this.constraint.getVariables().get(j).getDomain().buildInterval();
							this.consistent = false;
							copy = new CSP(this);
						}

						break;


					case range:
						consistentRange();
						if(consistent()){
							success = false;
							backtracking(j+1, success,copy);

						}
						else{
							this.setConstraint(new ConstraintAllDifferent(copy.getConstraint()));
							this.constraint.getVariables().get(j).getDomain().removeValue(d);
							this.constraint.getVariables().get(j).getDomain().buildInterval();
							this.consistent = false;
							copy = new CSP(this);
						}

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
//		//
//		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
//		Vector<Integer> valuesX1 = new Vector<Integer>();
//		Vector<Integer> valuesX2 = new Vector<Integer>();
//		Vector<Integer> valuesX3 = new Vector<Integer>();
//
//		
//		valuesX1.add(1);
//		valuesX1.add(2);
//		valuesX1.add(3);
//		valuesX2.add(1);
//		valuesX2.add(2);
//		//valuesX3.add(1);
//		valuesX3.add(0);
//		valuesX3.add(1);
//		valuesX3.add(2);
//		//		//				
//		//
//		Domain dX1 = new Domain();
//		Domain dX2 = new Domain();
//		Domain dX3 = new Domain();
//		//
//		dX1.setValues(valuesX1);
//		dX2.setValues(valuesX2);
//		dX3.setValues(valuesX3);
//		dX1.buildInterval();
//		dX2.buildInterval();
//		dX3.buildInterval();
//
//		//
//		//
//		Variable x1 = new Variable(0,dX1);
//		Variable x2 = new Variable(1,dX2);
//		Variable x3 = new Variable(2,dX3);
//		//
//		constraint.addVariable(x1); 
//		constraint.addVariable(x2);
//		constraint.addVariable(x3);
//
//		c.setConstraint(constraint);
//		System.out.println(c);
//		//
//		CSP.algo = Algo.range;
//		boolean success = false;
//		CSP copy = new CSP(c);
//		c.backtracking(0, success,copy);
//		//
//		System.out.println(c);
		//		success = c.consistent();
		//		System.out.println(success);
		//		c.createFiles();
		//		
		//		CSP.algo = Algo.arc;

		//DEVO GENERARLI TUTTI UGUALI E RISOLVERLI CON ALTRI ALGORITMIIIIIIIIIIIIIII
		//		CSP c = new CSP();
				for (int i = 4; i < 20; i++) {
					c.generateRandom(i);	
				}
		//		
		//		CSP.algo = Algo.bounds;
		//		
		//		for (int i = 4; i < 10; i++) {
		//			c.generateRandom(i,Algo.arc);	
		//		}
		//		

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
