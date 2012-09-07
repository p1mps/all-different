/**
 * 
 */
package org.alldifferent;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 * @author simonetiso
 *
 */
public class Grafo {
	
	private Graph<Object, DefaultEdge> completeGraph;
	
	private Vector<Variable> vars;
	private Set<DefaultEdge> visitedEdge;
	private String startMMVertex;
	private String finishMMVertex;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int nValuesVars = 3;
		int nValuesDms = 4;
		Vector<Variable> vars = new Vector<Variable>();
		
		/*
		//Definisco i valori dei domini
		Vector<Integer> valuesX1 = new Vector<Integer>();
		Vector<Integer> valuesX2 = new Vector<Integer>();
		Vector<Integer> valuesX3 = new Vector<Integer>();

		valuesX1.add(0);
		valuesX1.add(1);
		valuesX1.add(2);
		valuesX2.add(3);
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
		
		//definisco le variabili
		
		vars.add(0, new Variable(1,dX1));
		vars.add(1, new Variable(2,dX2));
		vars.add(2, new Variable(3,dX3));
		*/
		int domCard = 3;
		CSP c = new CSP();
		c.generateRandom(domCard,"bipartite");
		vars = c.getConstraint().getVariables();
		nValuesVars = vars.size();
		nValuesDms = domCard;
		
		Grafo g = new Grafo();
		g.createBipartiteGraph(nValuesVars, nValuesDms, vars);
		
	}
	
	
	//rimpiazza un vecchio vertice con uno nuovo mantenendo le connessioni
	private boolean replaceVertex(Object oldVertex, Object newVertex)
    {
		//controllo i parametri in ingresso
        if ((oldVertex == null) || (newVertex == null)) {
            return false;
        }
        
        //mi salvo gli archi connessi al vecchio vertice
        Set<DefaultEdge> relatedEdges = completeGraph.edgesOf(oldVertex);
        //aggiungo il nuovo vertice al grafo
        completeGraph.addVertex(newVertex);

        Object sourceVertex;
        Object targetVertex;
        
        //ciclo sugli archi legati al nodo
        for (DefaultEdge e : relatedEdges) {
        	
        	//ricavo sorgente e destinazione
            sourceVertex = completeGraph.getEdgeSource(e);
            targetVertex = completeGraph.getEdgeTarget(e);
            
            //caso in cu ci sia un loop
            if (sourceVertex.equals(oldVertex) && targetVertex.equals(oldVertex)) {
                completeGraph.addEdge(newVertex, newVertex);
            } 
            else {
                if (sourceVertex.equals(oldVertex) ) {
                    completeGraph.addEdge(newVertex, targetVertex);
                } else {
                    completeGraph.addEdge(sourceVertex, newVertex);
                }
            }
        }
        completeGraph.removeVertex(oldVertex);
        return true;
    }
	
	//ritorna true se verAdj contiene tmp
	private boolean contains(Set<Object> verAdj, Object tmp) {
		
		Iterator<Object> it = verAdj.iterator();
		
		while(it.hasNext()) {
			
			if(it.next().getClass() == tmp.getClass())
				return true;
		}
		
		return false;
		
	}
	
	//questo metodo crea un grafo bipartito con variabili e valori dei domini
	public void createBipartiteGraph(int nValuesVars, int nValuesDms, Vector<Variable> variables) {
		vars = variables;
		startMMVertex = new String("S");
		finishMMVertex = new String("F");
		
		//Generazione di un grafo bipartito completo
		completeGraph = new SimpleDirectedGraph<Object, DefaultEdge>(DefaultEdge.class);
		CompleteBipartiteGraphGenerator<Object, DefaultEdge> bip = new CompleteBipartiteGraphGenerator<Object, DefaultEdge>(nValuesVars, nValuesDms);
		//Tipo di vertici che deve creare il generatore
		VertexFactory<Object> v = new ClassBasedVertexFactory<Object>(Object.class);

		//uso il generatore per creare il grafo bipartito
		bip.generateGraph(completeGraph, v, null);
	
		//uso un iteratore per scorrere l'albero in ampiezza
		//BreadthFirstIterator<Object, DefaultEdge> it = new BreadthFirstIterator<Object, DefaultEdge>(completeGraph);

		//Rimpiazzo tutti i vertici (unico modo per modificare i vertici)
		Set<Object> vertices = new HashSet<Object>();
		vertices.addAll(completeGraph.vertexSet());
		Integer counter = 0;
		int i = 0;
		Object tmp = new Object();
		
		//ciclo su tutti i vertici per rimpiazzare i vecchi vertici con i nuovi
		for (Object vertex : vertices) {
			
			if(i == 0) {
				replaceVertex(vertex, vars.get(i));
				tmp = vars.get(i);
				i++;
			}
			else {
				//prendo i vertici adiacenti a vertex
				Set<Object> verAdj = new HashSet<Object>();
				NeighborIndex<Object, DefaultEdge> adj = new NeighborIndex<Object, DefaultEdge>(completeGraph);
				verAdj.addAll(adj.neighborsOf(vertex));
				
				//se il vertex ha come adiacenze una variabile, non posso inserirne un'altra
				//questo perchÔøΩ ogni arco collega una variabile ad un valore solo nel suo dominio
				if(!contains(verAdj, tmp) && i != vars.size()) {
					replaceVertex(vertex, vars.get(i));
					//aggiorno tmp con la variabile appena inserita
					tmp = vars.get(i);
					i++;
				}
				else {
					replaceVertex(vertex, (Object) counter++);
				}
				
			}
			
		}
		//Fino a qui ho creato un grafo bipartito COMPLETO
		
		System.out.println(vars.get(0).getDomain().getValues());
		System.out.println(vars.get(1).getDomain().getValues());
		System.out.println(vars.get(2).getDomain().getValues());
		
		//Sistemo il grafo con i domini delle variabili
		Set<DefaultEdge> temp = new HashSet<DefaultEdge>();
		Set<DefaultEdge> allEdge = completeGraph.edgeSet();
		
		//ciclo su ogni arco e controllo se il valore target ÔøΩ contenuto nel dominio della variabile
		//altrimenti rimuovo l'arco dal grafo
		for(DefaultEdge e : allEdge) {
			
			//controllo gli archi la cui sorgente è una variabile
			if(completeGraph.getEdgeSource(e).getClass() == Variable.class) {
				Variable source = (Variable) completeGraph.getEdgeSource(e);
				Integer target = (Integer) completeGraph.getEdgeTarget(e);
				//System.out.println("Valore nel dominio: " + source.getDomain().getValues());
				//System.out.println("Valore target: " + target.intValue());
				if(!source.getDomain().getValues().contains(target)) {
					//aggiungo l'arco nel vettore delle rimozioni
					temp.add(e);
				}
			}
			else {
				//controllo gli archi la cui sorgente è un valore del dominio
				Integer source = (Integer) completeGraph.getEdgeSource(e);
				Variable target = (Variable) completeGraph.getEdgeTarget(e);
				//System.out.println("Valore nel dominio: " + target.getDomain().getValues());
				//System.out.println("Valore target: " + source.intValue());
				if(!target.getDomain().getValues().contains(source)) {
					//aggiungo l'arco nel vettore delle rimozioni
					temp.add(e);
				}
			}
			
		}
		
		completeGraph.removeAllEdges(temp);
		//ho creato il grafo secondo i domini delle variabili
		
		//mi prendo tutti i vertici del grafo
		vertices = new HashSet<Object>();
		vertices.addAll(completeGraph.vertexSet());
		
		//aggiungo i vertici source e finish al grafo
		completeGraph.addVertex(startMMVertex);
		completeGraph.addVertex(finishMMVertex);
		
		for(Object vertex : vertices) {
			//se il vertice è una variabile
			if(vertex.getClass() == Variable.class){
				completeGraph.addEdge(startMMVertex, vertex);
			}
			else {
				completeGraph.addEdge(vertex, finishMMVertex);
			}
			
		}
		
		printGraph();

		System.out.println(completeGraph.toString());		
	}
	
	//metodo che stampa il grafo
	public void printGraph() {
		
		//Stampo la struttura del grafo
		Iterator<Object> iter = new DepthFirstIterator<Object, DefaultEdge>(completeGraph);
		Object vertex;
		while (iter.hasNext()) {
			vertex = iter.next();
			System.out.println(
					"Vertex " + vertex.toString() + " is connected to: "
							+ completeGraph.edgesOf(vertex).toString());
		}
	
	}
	
	/*
	public Grafo computeMaximumMatching() {
		
	}
	
	
	public Grafo computeSCCs() {
		
	}
	*/
}
