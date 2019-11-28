package object;


import java.util.PriorityQueue;

import application.Main.JTEPropertyType;
import file.PropertiesManager;
import game.JTEGameData;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;



public class Dijkstra {
	transient PropertiesManager props = PropertiesManager.getPropertiesManager();
	private ArrayList<Vertex> vertexs;
	private static Dijkstra dijkstra;

	private Dijkstra(){
    	ArrayList<String> greenCards = props.getPropertyOptionsList(JTEPropertyType.GREEN_CARD_NAME_OPTIONS);
    	ArrayList<String> redCards = props.getPropertyOptionsList(JTEPropertyType.RED_CARD_NAME_OPTIONS);
    	ArrayList<String> yellowCards = props.getPropertyOptionsList(JTEPropertyType.YELLOW_CARD_NAME_OPTIONS);
    	ArrayList<String> cards = new ArrayList<>();
    	cards.addAll(greenCards);
    	cards.addAll(redCards);
    	cards.addAll(yellowCards);
    	vertexs = new ArrayList<>();
    	for(String cardName: cards){
    		Vertex v = new Vertex(cardName);
    		vertexs.add(v);
		}
    	for(Vertex v: vertexs){
			ArrayList<String> landCityNames = props.getPropertyOptionsList(v.name+"_LAND");
			ArrayList<Edge> edges = new ArrayList<>();
			for(String cityName: landCityNames){
				edges.add(new Edge(getVertex(cityName), 1));
			}
			ArrayList<String> seaCityNames = props.getPropertyOptionsList(v.name+"_SEA");
			for(String cityName: seaCityNames){
				edges.add(new Edge(getVertex(cityName), 6));
			}
			v.adjacencies = edges.toArray(new Edge[0]);
		}
	}

	public Vertex getVertex(String name){
		for(Vertex vertex: vertexs){
			if(vertex.name.equals(name))
				return vertex;
		}
		return null;
	}

	public void resetVertexs(){
		for(Vertex vertex: vertexs){
			vertex.minDistance = Double.POSITIVE_INFINITY;
			vertex.previous = null;
		}
	}

	public static Dijkstra getDijkstra(){
		if(dijkstra == null)
			dijkstra = new Dijkstra();
		return dijkstra;
	}

	public void computePaths(String sourceName) {
		Vertex source = getVertex(sourceName);
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);
		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();
			// Visit each edge exiting u
			for (Edge e : u.adjacencies) {
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);
					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
					}
				}
			}
	}

	public ArrayList<String> getShortestPathTo(String targetName) {
		Vertex target = getVertex(targetName);
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);
		Collections.reverse(path);
		resetVertexs();
		return getNames(path);
	}

	public ArrayList<String> getNames(ArrayList<Vertex> vertexs){
		ArrayList<String> names = new ArrayList<>();
		for(Vertex vertex: vertexs){
			names.add(vertex.name);
		}
		return names;
	}
}



class Vertex implements Comparable<Vertex> {

	public final String name;
	public Edge[] adjacencies;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Vertex previous;

	public Vertex(String argName) {
		name = argName;
	}

	public String toString() {
		return name;
	}

	public int compareTo(Vertex other) {
		return Double.compare(minDistance, other.minDistance);
	}
}


class Edge {
	public final Vertex target;
	public final double weight;

	public Edge(Vertex argTarget, double argWeight) {
		target = argTarget;
		weight = argWeight;
	}
}