package graph.util;


import java.util.*;


/**
 * Directed weighted graph utility. Nodes are strings.
 */
public class Graph {
    private final Map<String, List<Edge>> adj = new HashMap<>();


    public static class Edge {
        public final String to;
        public final double weight;
        public Edge(String to, double weight) { this.to = to; this.weight = weight; }
        public String toString(){ return String.format("(%s,%.2f)", to, weight); }
    }


    public void addNode(String v){ adj.putIfAbsent(v, new ArrayList<>()); }
    public void addEdge(String from, String to, double weight){
        addNode(from); addNode(to);
        adj.get(from).add(new Edge(to, weight));
    }
    public Set<String> nodes(){ return Collections.unmodifiableSet(adj.keySet()); }
    public List<Edge> neighbors(String v){ return adj.getOrDefault(v, Collections.emptyList()); }
    public int edgeCount(){ return adj.values().stream().mapToInt(List::size).sum(); }


    // Build reverse graph
    public Graph reverse(){
        Graph g = new Graph();
        for(String v: adj.keySet()) g.addNode(v);
        for(String u: adj.keySet()){
            for(Edge e: adj.get(u)) g.addEdge(e.to, u, e.weight); // weight carried but used only for structure
        }
        return g;
    }
}
