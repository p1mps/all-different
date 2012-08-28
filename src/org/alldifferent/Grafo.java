/**
 * 
 */
package org.alldifferent;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

/**
 * @author simonetiso
 *
 */
public class Grafo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//creo un grafo semplice
    	SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//Generazione di un grafo bipartito completo
    	
    	Graph<Object, DefaultEdge> bipGraph = new SimpleGraph<Object, DefaultEdge>(DefaultEdge.class);
    	CompleteBipartiteGraphGenerator<Object, DefaultEdge> bip = new CompleteBipartiteGraphGenerator<Object, DefaultEdge>(4, 5);
    	VertexFactory<Object> v = new ClassBasedVertexFactory<Object>(Object.class);
    	
    	//uso il generatore per creare il grafo bipartito
		bip.generateGraph(bipGraph, v, null);
		
		BreadthFirstIterator<Object, DefaultEdge> it = new BreadthFirstIterator<Object, DefaultEdge>(bipGraph);
		Object nodo = it.next();
		System.out.println(nodo.toString());
		
    	//Definizione dei vertici
    	String v1 = "x_1";
    	String v2 = "d1_1";
    	
    	//Aggiungo i vertici al grafo
    	g.addVertex(v1);
    	g.addVertex(v2);
    	
    	//Connetto i vertici con un arco e setto il peso dell'arco
    	//0 = arco non usato
    	//1 = arco usato
    	g.setEdgeWeight(g.addEdge(v1, v2), 0);
    	
    	//setto il peso di un arco dati i vertici
    	g.setEdgeWeight(g.getEdge(v1, v2), 4);
    	
    	
    	//Stampo il peso dell'arco
    	//System.out.println(g.getEdgeWeight(g.getEdge(v1, v2)));
    	System.out.println(bipGraph.toString());

	}

}
