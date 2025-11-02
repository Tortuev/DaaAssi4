package graph.metrics;


import java.util.HashMap;
import java.util.Map;


public class SimpleMetrics implements Metrics {
    private long startNs = 0; private long duration = 0;
    private final Map<String, Long> counters = new HashMap<>();


    @Override public void start(){ startNs = System.nanoTime(); }
    @Override public void stop(){ duration = System.nanoTime() - startNs; }
    @Override public void inc(String counter){ counters.put(counter, counters.getOrDefault(counter,0L)+1); }
    @Override public long getDurationNs(){ return duration; }
    @Override public long getCount(String counter){ return counters.getOrDefault(counter,0L); }
    @Override public void printReport(String header){
        System.out.println("--- Metrics: " + header + " ---");
        System.out.println("Duration (ns): " + duration);
        for(Map.Entry<String, Long> e: counters.entrySet()) System.out.println(e.getKey()+": "+e.getValue());
        System.out.println("---------------------------");
    }
}
