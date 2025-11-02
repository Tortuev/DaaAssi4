package graph.topo;


import graph.util.Graph;
import graph.metrics.Metrics;


import java.util.*;


public class TopologicalSort {
    private final Graph g; private final Metrics metrics;
    public TopologicalSort(Graph g, Metrics metrics){ this.g = g; this.metrics = metrics; }


    public List<String> kahn(){
        metrics.start();
        Map<String,Integer> indeg = new HashMap<>();
        for(String v: g.nodes()) indeg.put(v, 0);
        for(String u: g.nodes()) for(Graph.Edge e: g.neighbors(u)) indeg.put(e.to, indeg.get(e.to)+1);
        Deque<String> q = new ArrayDeque<>();
        for(Map.Entry<String,Integer> e: indeg.entrySet()) if(e.getValue()==0) { q.add(e.getKey()); metrics.inc("kahn_push"); }
        List<String> order = new ArrayList<>();
        while(!q.isEmpty()){
            String u = q.removeFirst(); metrics.inc("kahn_pop"); order.add(u);
            for(Graph.Edge e: g.neighbors(u)){
                indeg.put(e.to, indeg.get(e.to)-1); metrics.inc("kahn_edge");
                if(indeg.get(e.to)==0){ q.add(e.to); metrics.inc("kahn_push"); }
            }
        }
        metrics.stop();
// If order size < nodes -> cycle present
        return order;
    }
}
