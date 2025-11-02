package graph.scc;
import graph.util.Graph;
import graph.metrics.Metrics;


import java.util.*;


/**
 * Tarjan's algorithm for SCCs. Produces list of components (each a list of nodes).
 */
public class TarjanSCC {
    private final Graph g;
    private final Metrics metrics;


    private final Map<String,Integer> disc = new HashMap<>();
    private final Map<String,Integer> low = new HashMap<>();
    private final Deque<String> stack = new ArrayDeque<>();
    private final Set<String> onStack = new HashSet<>();
    private final List<List<String>> components = new ArrayList<>();
    private int time = 0;


    public TarjanSCC(Graph g, Metrics metrics){ this.g = g; this.metrics = metrics; }


    public List<List<String>> run(){
        metrics.start();
        for(String v: g.nodes()){
            if(!disc.containsKey(v)) dfs(v);
        }
        metrics.stop();
        return components;
    }
private void dfs(String u){
    disc.put(u, time); low.put(u, time); time++;
    stack.push(u); onStack.add(u);
    metrics.inc("dfs_visit");
    for(Graph.Edge e: g.neighbors(u)){
        metrics.inc("dfs_edges");
        String v = e.to;
        if(!disc.containsKey(v)){
            dfs(v);
            low.put(u, Math.min(low.get(u), low.get(v)));
        } else if(onStack.contains(v)){
            low.put(u, Math.min(low.get(u), disc.get(v)));
        }
    }
    if(low.get(u).equals(disc.get(u))){
        List<String> comp = new ArrayList<>();
        while(true){
            String w = stack.pop(); onStack.remove(w);
            comp.add(w);
            if(w.equals(u)) break;
        }
        components.add(comp);
    }
}


/** Build condensation graph: each component becomes a node. Returns mapping and new graph. */
public static class CondensationResult{
    public final Map<String,Integer> nodeToComp; // node -> comp id
    public final Graph condensed;
    public final List<List<String>> components;
    public CondensationResult(Map<String,Integer> nodeToComp, Graph condensed, List<List<String>> components){
        this.nodeToComp = nodeToComp; this.condensed = condensed; this.components = components;
    }
}


public CondensationResult buildCondensation(){
    Map<String,Integer> map = new HashMap<>();
    for(int i=0;i<components.size();i++){
        for(String v: components.get(i)) map.put(v,i);
    }
    Graph cg = new Graph();
    for(int i=0;i<components.size();i++) cg.addNode("C"+i);
    for(String u: g.nodes()){
        for(Graph.Edge e: g.neighbors(u)){
            int cu = map.get(u), cv = map.get(e.to);
            if(cu!=cv) cg.addEdge("C"+cu, "C"+cv, e.weight);
        }
    }
    return new CondensationResult(map, cg, components);
}
}
