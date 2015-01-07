package main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSortedMap;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.furnace.algorithms.vertexcentric.GraphComputer;
import com.tinkerpop.furnace.algorithms.vertexcentric.VertexMemory;
import com.tinkerpop.furnace.algorithms.vertexcentric.computers.SerialGraphComputer;
import com.tinkerpop.furnace.algorithms.vertexcentric.programs.ranking.DegreeRankProgram;
import com.tinkerpop.furnace.algorithms.vertexcentric.programs.ranking.PageRankProgram;

public class TinkerpopMain {
	public static void main(String[] args) {
		Graph graph = new Neo4j2Graph("data/neograph");
//		Graph graph = new OrientGraph("memory:test");
//		OrientGraphFactory factory = new OrientGraphFactory("plocal:data/orientgraph");
//		OrientGraphFactory factory = new OrientGraphFactory("memory:test");
//		Graph graph = factory.getTx();
//		Graph graph = new OrientGraph("plocal:data/orientgraph");
		
		graph = TinkerGraphFactory.createTinkerGraph();
		Vertex a = graph.addVertex(null);
		Vertex b = graph.addVertex(null);
		a.setProperty("name","marko");
		b.setProperty("name","peter");
		Edge e = graph.addEdge(null, a, b, "knows");
		e.setProperty("since", 2006);
		graph.shutdown();
		
		
		pagerank(graph);
//		degreeRank(graph);
//		degreeRank(TinkerGraphFactory.createTinkerGraph());
		
//		int count = 0;
//		for( Vertex v : graph.getVertices() )
//			count += 1;
//		System.out.println("Count: "+count);
		graph.shutdown();
	}
	
	protected static void degreeRank(Graph graph) {
		DegreeRankProgram program = DegreeRankProgram.create().build();
//		ParallelGraphComputer computer = new ParallelGraphComputer(graph, program, GraphComputer.Isolation.BSP);
		SerialGraphComputer computer = new SerialGraphComputer(graph, program, GraphComputer.Isolation.BSP);
		computer.execute();
		VertexMemory results = computer.getVertexMemory();
		
		for( Vertex vertex : graph.getVertices() ) {
			System.out.println(results.getProperty(vertex, DegreeRankProgram.DEGREE));
		}
	}

	protected static void pagerank(Graph graph) {
		PageRankProgram program = PageRankProgram.create().vertexCount(2).iterations(3).build();
		SerialGraphComputer computer = new SerialGraphComputer(graph, program, GraphComputer.Isolation.BSP);
		computer.execute();
		
		VertexMemory results = computer.getVertexMemory();
		System.out.println(results);
		
		double total = 0.0d;
        final Map<String, Double> map = new HashMap<String, Double>();
        for (Vertex vertex : graph.getVertices()) {
            double pageRank = Double.parseDouble(results.getProperty(vertex, PageRankProgram.PAGE_RANK).toString());
            total = total + pageRank;
            map.put(vertex.getProperty("name") + " ", pageRank);
        }
        for (Map.Entry<String, Double> entry : ImmutableSortedMap.copyOf(map, new Comparator<String>() {
            public int compare(final String key, final String key2) {
                int c = map.get(key2).compareTo(map.get(key));
                return c == 0 ? -1 : c;
            }
        }).entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("Total: "+total);
        System.out.println("Runtime: "+computer.getGraphMemory().getRuntime());
	}
}
