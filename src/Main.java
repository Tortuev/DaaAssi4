import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graph.dagsp.DAGShortestPath;
import graph.metrics.SimpleMetrics;
import graph.scc.TarjanSCC;
import graph.scc.TarjanSCC.CondensationResult;
import graph.topo.TopologicalSort;
import graph.util.Graph;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {
        // 1️ Load JSON input file
        String path = args.length > 0 ? args[0] : "data/small1.json";
        System.out.println("Loading: " + path);

        // Read JSON and parse into object
        String json = new String(Files.readAllBytes(Paths.get(path)));
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        // 2️ Build directed weighted graph
        Graph g = new Graph();

        // Load nodes
        JsonArray nodes = root.getAsJsonArray("nodes");
        for (int i = 0; i < nodes.size(); i++) {
            JsonElement n = nodes.get(i);
            g.addNode(n.getAsString());
        }

        // Load edges with optional weights
        JsonArray edges = root.getAsJsonArray("edges");
        for (int i = 0; i < edges.size(); i++) {
            JsonArray arr = edges.get(i).getAsJsonArray();
            String from = arr.get(0).getAsString();
            String to = arr.get(1).getAsString();
            double w = arr.size() > 2 ? arr.get(2).getAsDouble() : 1.0;
            g.addEdge(from, to, w);
        }

        // 3 Run Tarjan SCC algorithm
        SimpleMetrics m1 = new SimpleMetrics();
        TarjanSCC tarjan = new TarjanSCC(g, m1);
        List<List<String>> comps = tarjan.run();
        System.out.println("SCCs found: " + comps.size());
        for (int i = 0; i < comps.size(); i++) System.out.println("C" + i + ": " + comps.get(i));
        m1.printReport("Tarjan");

        // 4️ Build condensation graph (DAG of components)
        CondensationResult cr = tarjan.buildCondensation();
        System.out.println("Condensed nodes: " + cr.condensed.nodes());
        System.out.println("Edges: " + cr.condensed.edgeCount());

        // 5️ Compute topological order on condensation DAG
        SimpleMetrics m2 = new SimpleMetrics();
        TopologicalSort topo = new TopologicalSort(cr.condensed, m2);
        List<String> order = topo.kahn();
        System.out.println("Topo order of components: " + order);
        m2.printReport("Kahn condensation");

        // Expand derived order of original nodes
        List<String> derived = new ArrayList<>();
        for (String compNode : order) {
            int cid = Integer.parseInt(compNode.substring(1));
            derived.addAll(cr.components.get(cid));
        }
        System.out.println("Derived order of tasks: " + derived);

        // ⃣ Compute shortest and longest paths on DAG
        SimpleMetrics m3 = new SimpleMetrics();
        DAGShortestPath dsp = new DAGShortestPath(cr.condensed, m3);
        if (order.size() == cr.condensed.nodes().size()) {
            String source = order.get(0);

            // Shortest paths
            DAGShortestPath.Result shortest = dsp.shortestFrom(source, order);
            System.out.println("Shortest distances from " + source + ":");
            for (String v : shortest.dist.keySet())
                System.out.println(v + ": " + shortest.dist.get(v));
            m3.printReport("DAG-SP-shortest");

            // Longest paths (critical path)
            SimpleMetrics m4 = new SimpleMetrics();
            DAGShortestPath dsp2 = new DAGShortestPath(cr.condensed, m4);
            DAGShortestPath.Result longest = dsp2.longestFrom(source, order);
            System.out.println("Longest distances from " + source + ":");
            for (String v : longest.dist.keySet())
                System.out.println(v + ": " + longest.dist.get(v));
            m4.printReport("DAG-SP-longest");

            // Identify and reconstruct the critical path
            String far = null;
            double best = Double.NEGATIVE_INFINITY;
            for (Map.Entry<String, Double> e : longest.dist.entrySet()) {
                if (e.getValue() > best) {
                    best = e.getValue();
                    far = e.getKey();
                }
            }
            System.out.println("Critical (longest) path length: " + best + " to node " + far);
            System.out.println("Path: " + DAGShortestPath.reconstruct(longest.parent, far));
        } else {
            System.out.println("Condensation graph not a DAG? Topo order incomplete.");
        }
    }
}
