package it.unisa.microapp.test;
/*package it.unisa.microapp.testing;


import it.unisa.microapp.ConcreteComponentCreator;
import it.unisa.microapp.DeployParser;
import it.unisa.microapp.InvalidComponentException;
import it.unisa.microapp.components.MAComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import org.jgrapht.*;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class GraphTester {

  private	ArrayList<Integer> chestaifacenn;
	
	public GraphTester(){

	}

	public void test(){
		DirectedGraph<Integer, DirEdge> grafo=new DefaultDirectedGraph<Integer, DirEdge>(DirEdge.class);
		grafo.addVertex(0);
		grafo.addVertex(1);
		grafo.addVertex(2);
		grafo.addVertex(3);
		grafo.addVertex(4);
		grafo.addVertex(5);
		grafo.addVertex(6);
		grafo.addVertex(7);

		grafo.addEdge(0,1);
		grafo.addEdge(1,4);
		grafo.addEdge(0,2);
		grafo.addEdge(0,3);
		grafo.addEdge(2,5);
		grafo.addEdge(3,4);
		grafo.addEdge(3,5);
		grafo.addEdge(4,5);
		grafo.addEdge(5,6);
		grafo.addEdge(6,7);
		
		DepthFirstOrder<Integer, DirEdge>  topord=new DepthFirstOrder<Integer, DirEdge> (grafo, 1);
		chestaifacenn=topord.reversePost();
	}
	
	public void testGraph(){
		DeployParser parser=new DeployParser (new File("Testing.xml"), new ConcreteComponentCreator());
		DirectedGraph<MAComponent, DirEdge> grafo = null;
		try {
			grafo=parser.createGraph();
		} catch (InvalidComponentException e) {
		}
		Utils.verbose(grafo);
	}
	
	public String avind(){
		return chestaifacenn.toString();
	}
	

	public static void main(String[] args) {
		GraphTester test=new GraphTester();
		test.testGraph();
	}

}*/
