package graph.dagsp;

import graph.util.Graph;
import graph.metrics.Metrics;


import java.util.*;


/**
 * Shortest/Longest paths on DAG using DP over topological order.
 * Edges weights are used as durations (non-negative expected).
 */
public class DAGShortestPath {
    private final Graph g; private final Metrics metrics;
    public DAGShortestPath(Graph g, Metrics m){ this.g=g; this.metrics=m; }


    public static class Result{
public final Map<String,Double> dist; public final Map<String,String> parent;
public Result(Map<String,Double> dist, Map<String,String> parent){ this.dist=dist; this.parent=parent; }
}


// assumes topological order provided
public Result shortestFrom(String source, List<String> topoOrder){
    metrics.start();
    Map<String,Double> dist = new HashMap<>();
    Map<String,String> parent = new HashMap<>();
    for(String v: g.nodes()) dist.put(v, Double.POSITIVE_INFINITY);
    dist.put(source, 0.0);
    Set<String> inTopo = new HashSet<>(topoOrder);
    for(String u: topoOrder){
        if(!inTopo.contains(u)) continue;
        if(dist.get(u)==Double.POSITIVE_INFINITY) continue; // unreachable
        for(Graph.Edge e: g.neighbors(u)){
            metrics.inc("relaxations");
            if(dist.get(e.to) > dist.get(u) + e.weight){
                dist.put(e.to, dist.get(u) + e.weight);
                parent.put(e.to, u);
            }
        }
    }
    metrics.stop();
    return new Result(dist,parent);
}


// longest path (assumes no positive cycles and DAG)
public Result longestFrom(String source, List<String> topoOrder){
    metrics.start();
    Map<String,Double> dist = new HashMap<>(); Map<String,String> parent = new HashMap<>();
    for(String v: g.nodes()) dist.put(v, Double.NEGATIVE_INFINITY);
    dist.put(source, 0.0);
    Set<String> inTopo = new HashSet<>(topoOrder);
    for(String u: topoOrder){
        if(!inTopo.contains(u)) continue;
        if(dist.get(u)==Double.NEGATIVE_INFINITY) continue;
        for(Graph.Edge e: g.neighbors(u)){
            metrics.inc("relaxations");
            if(dist.get(e.to) < dist.get(u) + e.weight){
                dist.put(e.to, dist.get(u) + e.weight);
                parent.put(e.to, u);
            }
        }
    }
    metrics.stop();
    return new Result(dist,parent);
}


// helper to reconstruct path
public static List<String> reconstruct(Map<String,String> parent, String target){
    LinkedList<String> p = new LinkedList<>(); String cur = target;
    while(cur!=null){ p.addFirst(cur); cur = parent.get(cur); }
    return p;
}
}
