package graph.metrics;


public interface Metrics {
    void start();
    void stop();
    void inc(String counter);
    long getDurationNs();
    long getCount(String counter);
    void printReport(String header);
}
