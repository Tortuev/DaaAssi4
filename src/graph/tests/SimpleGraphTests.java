package graph.tests;


import graph.metrics.SimpleMetrics;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.util.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SimpleGraphTests {
    @Test
    public void testTarjanSmall(){
        Graph g = new Graph();
        g.addEdge("A","B",1); g.addEdge("B","C",1); g.addEdge("C","A",1); g.addEdge("C","D",1);
        SimpleMetrics m = new SimpleMetrics();
        TarjanSCC t = new TarjanSCC(g,m);
        List<List<String>> comps = t.run();
        assertEquals(2, comps.size()); // {A,B,C} and {D}
    }


    @Test
    public void testTopoOnDAG(){
        Graph g = new Graph();
        g.addEdge("X","Y",1); g.addEdge("Y","Z",1);
        SimpleMetrics m = new SimpleMetrics();
        TopologicalSort topo = new TopologicalSort(g,m);
        List<String> order = topo.kahn();
        assertEquals(3, order.size());
        assertTrue(order.indexOf("X") < order.indexOf("Y"));
    }
}
