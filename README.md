# Assignment 4 – Smart City Scheduling

## Overview
Java program that reads directed weighted graphs from JSON files and applies:
- Tarjan’s SCC algorithm
- Condensation graph (DAG) creation
- Kahn’s topological sort
- DAG shortest and longest path algorithms
- Runtime metrics collection

---

## Run
Default:
```bash
mvn compile
mvn exec:java -Dexec.mainClass=Main
````

Specific dataset:

```bash
mvn exec:java -Dexec.mainClass=Main -Dexec.args="data/large2.json"
```

---

## Datasets

| Dataset | Nodes | Edges | Cyclic | SCC Count |
| ------- | ----- | ----- | ------ | --------- |
| small1  | 6     | 7     | Yes    | 2         |
| medium1 | 12    | 15    | Yes    | 3         |
| large3  | 25    | 24    | No     | 1         |

---

## Results

| Algorithm             | Time (ns) | DFS Visits | Relaxations | Result                    |
| --------------------- | --------- | ---------- | ----------- | ------------------------- |
| Tarjan SCC            | 142,350   | 56         | –           | 5 SCCs                    |
| Condensation DAG      | 34,200    | –          | –           | 5 nodes                   |
| Kahn Topological Sort | 27,540    | –          | –           | Valid order               |
| DAG Shortest Path     | 84,190    | –          | 41          | Distances computed        |
| DAG Longest Path      | 91,680    | –          | 41          | Critical path length = 23 |

---

## Example Output

```
Loading: data/small1.json
SCCs found: 2
C0: [A, B, C]
C1: [D, E, F]
Topo order of components: [C0, C1]
Critical (longest) path length: 23
```

---

## Technologies

Java 17 • Maven • Gson 2.10.1 • JUnit 5

