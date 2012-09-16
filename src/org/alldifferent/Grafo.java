package org.alldifferent;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 * 
 * <p>Questa classe denominata "Grafo" permette di creare un oggetto grafo e su di esso calcolare
 * la consistenza sugli iperarchi mediante l'invocazione del metodo "hyperArcConsistency".</p>
 *
 * @author Simone Tiso
 * @version 1.0
 */
public class Grafo {
	
	/** <p>Grafo completo bipartito</p> */
	private Graph<Object, DefaultEdge> completeGraph = null;
	
	/** <p>Grafo G_m costruito orientendo gli archi secondo l'appartenenza a M (accoppiamento massimo)</p> */
	private Graph<Object, DefaultEdge> GM_Graph = null;
	
	/** <p>Mappatura del calcolo dell'accoppiamento massimo di G</p> */
	private Map<DefaultEdge,Double> M = null;
	
	/** <p>Vettore dei vertici liberi M-free</p> */
	private Set<Object> M_freeVertex = null;
	
	/** <p>Vettore dei vertici appartenenti ad M</p> */
	private Set<Object> Mnode = null;
 	
	/** <p>Vettore delle variabili</p> */
	private Vector<Variable> vars = null;
	
	/** <p>Vertice di inizio per il calcolo del flusso massimo</p> */
	private String startMMVertex;
	
	/** <p>Vertice di fine per il calcolo del flusso massimo</p> */
	private String finishMMVertex;
	
	/** <p>Vettore archi usati relativamente al calcolo della consistenza</p> */
	private Set<DefaultEdge> usedArcs = null;
	
	/** <p>Vettore archi non usati relativamente al calcolo della consistenza</p> */
	private Set<DefaultEdge> notUsedArcs = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		//Definisco i valori dei domini
		Vector<Integer> valuesX1 = new Vector<Integer>();
		Vector<Integer> valuesX2 = new Vector<Integer>();
		Vector<Integer> valuesX3 = new Vector<Integer>();

		valuesX1.add(1);
		//valuesX1.add(1);
		valuesX1.add(2);
		//valuesX2.add(3);
		valuesX2.add(2);
		valuesX2.add(0);
		//valuesX3.add(0);
		valuesX3.add(1);
		//valuesX3.add(2);

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
		
	}
	
	/**
	 * <p>Metodo principale da invocare quando si decide di ottenere la cosistenza sugli archi in un determinato 
	 * grafo.</p>
	 *  
	 * @return Ritorna true se si riesce ad ottenere la consistenza, altrimenti false
	 */
	public boolean hyperArcConsistency() {
		
		this.createBipartiteGraph(this.vars.size(), this.vars.size(), this.vars);
		this.computeMaximumMatching();
		this.computeSCC();
		
		Object s = null;
		Object d = null;
		
		Variable tmp;
		for(DefaultEdge a : notUsedArcs) {
			
			s = GM_Graph.getEdgeSource(a);
			d = GM_Graph.getEdgeTarget(a);
			
			if(s.getClass() == Variable.class) {
				tmp = (Variable) s;
				tmp.getDomain().removeObject(d);
			}
			else {
				tmp = (Variable) d;
				tmp.getDomain().removeObject(s);
			}
			
			if(tmp.getDomain().getSize() == 0) {
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * <p>Setta il vettore delle variabili</p>
	 * 
	 * @param variables vettore delle variabili usate dalla classe
	 * 
	 */
	public void setVars(Vector<Variable> variables) {
		this.vars = variables;
	}

	/**
	 * <p>Rimpiazza un vecchio vertice con uno nuovo mantenendo le connessioni</p>
	 * 
	 * @param oldVertex vecchio vertice da rimpiazzare
	 * @param newVertex nuovo vertice da inserire
	 * @return true se riesce a rimpiazzare il vertice, altrimenti false
	 */
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
	
	/**
	 * Ritorna true se verAdj contiene tmp
	 * 
	 * @param verAdj Insieme di vertici adiacenti ad un oggetto
	 * @param tmp oggetto temporaneo per effettuare il confronto
	 * 
	 */
	private boolean contains(Set<Object> verAdj, Object tmp) {
		
		Iterator<Object> it = verAdj.iterator();
		
		while(it.hasNext()) {
			
			if(it.next().getClass() == tmp.getClass())
				return true;
		}
		
		return false;
		
	}
	
	/**
	 * <p>Questo metodo crea un grafo bipartito con variabili e valori dei domini</p>
	 * 
	 * @param nValuesVars numero di variabili del grafo
	 * @param nValuesDms numero di valori nei domini delle variabili
	 * @param variables vettore delle variabili da inserire nel grafo bipartito
	 * 
	 */
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
		
		//Sistemo il grafo con i domini delle variabili
		Set<DefaultEdge> temp = new HashSet<DefaultEdge>();
		Set<DefaultEdge> allEdge = completeGraph.edgeSet();
		Set<DefaultEdge> invertArc = new HashSet<DefaultEdge>();
		
		//ciclo su ogni arco e controllo se il valore target ÔøΩ contenuto nel dominio della variabile
		//altrimenti rimuovo l'arco dal grafo
		for(DefaultEdge e : allEdge) {
			
			//controllo gli archi la cui sorgente è una variabile
			if(completeGraph.getEdgeSource(e).getClass() == Variable.class) {
				Variable source = (Variable) completeGraph.getEdgeSource(e);
				Integer target = (Integer) completeGraph.getEdgeTarget(e);
				if(!source.getDomain().getValues().contains(target)) {
					//aggiungo l'arco nel vettore delle rimozioni
					temp.add(e);
				}
			}
			else {
				//controllo gli archi la cui sorgente è un valore del dominio
				Integer source = (Integer) completeGraph.getEdgeSource(e);
				Variable target = (Variable) completeGraph.getEdgeTarget(e);
				if(!target.getDomain().getValues().contains(source)) {
					//aggiungo l'arco nel vettore delle rimozioni
					temp.add(e);
				}
				else {
					//mi salvo l'arco che succ andrò a girare (se necessario)
					invertArc.add(e);
				}
			}
			
		}
		
		completeGraph.removeAllEdges(temp);
		//ho creato il grafo secondo i domini delle variabili
		
		arcSystem(invertArc);
		
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
		
	}
	
	/**
	 * <p>Metodo che orienta gli archi da variabile a valore nel dominio</p>
	 * @param invertArc insieme degli archi da invertire
	 * 
	 *  
	 */
	private void arcSystem(Set<DefaultEdge> invertArc) {
		
		for(DefaultEdge e : invertArc) {
			Integer source = (Integer) completeGraph.getEdgeSource(e);
			Variable target = (Variable) completeGraph.getEdgeTarget(e);
			
			completeGraph.addEdge(target, source);
		}
		
		completeGraph.removeAllEdges(invertArc);
	}

	/**
	 * <p>Metodo che stampa il grafo con una formattazione predefinita</p>
	 *  
	 *  
	 */
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
	
	/**
	 * <p>Metodo che calcola il massimo accoppiamento tra le due partizioni del grafo bipartito</p>
	 * 
	 *  
	 */
	public void computeMaximumMatching() {
		
		GM_Graph = new SimpleDirectedGraph<Object, DefaultEdge>(DefaultEdge.class);
		EdmondsKarpMaximumFlow<Object, DefaultEdge> MM = new EdmondsKarpMaximumFlow<Object, DefaultEdge>((DirectedGraph<Object, DefaultEdge>) completeGraph);
		
		//calcolo una rete di flusso del grafo per trovare l'abbinamento massimo
		MM.calculateMaximumFlow(startMMVertex, finishMMVertex);
		M = new HashMap<DefaultEdge, Double>(MM.getMaximumFlow());
		
		//rimuovo i vertici per il calcolo del flusso
		completeGraph.removeVertex(startMMVertex);
		completeGraph.removeVertex(finishMMVertex);
		
		
		//Creo il grafo orientato GM dell'abbinamento massimo di G e oriento gli archi nel seguente modo
		//archi che appartengono ad M da variabile a valore nel dominio
		//archi che non appartengono ad M da valore nel dominio a variabile
		Set<DefaultEdge> arcs = M.keySet();
		notUsedArcs = new HashSet<DefaultEdge>();
		
		Mnode = new HashSet<Object>();
		for(DefaultEdge a : arcs) {
			
			Object v = completeGraph.getEdgeSource(a);
			Object d = completeGraph.getEdgeTarget(a);
			
			
			//escludo i vertici sorgente e destinazione
			if(v.getClass() == Variable.class && d.getClass() == Integer.class) {
				
				//aggiungo i 2 vertici
				GM_Graph.addVertex(v);
				GM_Graph.addVertex(d);
				
				//oriento l'arco nel modo opportuno
				if(M.get(a) != 0) {
					GM_Graph.addEdge(v, d);
					
					//aggiungo i nodi in Mnode che mi indica quali vertici appartendono ad M
					if(!Mnode.contains(v))
						Mnode.add(v);
					if(!Mnode.contains(d))
						Mnode.add(d);
				}
				else {
					GM_Graph.addEdge(d, v);
					//marco gli archi che sono in GM ma non in M come non usati
					notUsedArcs.add(GM_Graph.getEdge(d, v));
				}
			}
	
		}
		
		//cerco i vertici M-free
		Set<Object> vertices = new HashSet<Object>(GM_Graph.vertexSet());
		M_freeVertex = new HashSet<Object>();
		//ciclo sui vertici v in GM e se incontro un vertice che non appartiene ad M lo aggiungo al vettore dei vertici M-free
		for(Object v : vertices) {
			if(!Mnode.contains(v)) {
				M_freeVertex.add(v);
			}
		}
		
	}
	
	/**
	 * <p>Metodo che calcola le componenti fortemente connesse del grafo</p>
	 *  
	 *  
	 */
	public void computeSCC() {
		
		StrongConnectivityInspector<Object, DefaultEdge> SCC = new StrongConnectivityInspector<Object, DefaultEdge>((DirectedGraph<Object, DefaultEdge>) GM_Graph);
		usedArcs = new HashSet<DefaultEdge>();
		java.util.List<DirectedSubgraph<Object, DefaultEdge>> g = SCC.stronglyConnectedSubgraphs();
		
		BreadthFirstIterator<Object, DefaultEdge> it = null;
		
		for(DirectedGraph<Object, DefaultEdge> c : g) {
			
			//se la componente contiene archi
			if(c.edgeSet().size() != 0) {
				//prendo tutti i suoi archi e li marco come usati
				usedArcs.addAll(c.edgeSet());
			}
			
		}
		
		Object vertex = null;
		Set<DefaultEdge> arcs = new HashSet<DefaultEdge>();
		for (Object free : M_freeVertex) {
			//istanzio un iteratore iniziando da un vertice free
			it = new BreadthFirstIterator<Object, DefaultEdge>(GM_Graph, free);
			while(it.hasNext()) {
				vertex = it.next();
				arcs = GM_Graph.edgesOf(vertex);
				for(DefaultEdge e : arcs) {
					if(!usedArcs.contains(e)) {
						usedArcs.add(e);
					}
				}
			}	
		}
		
	}
	
	
}
