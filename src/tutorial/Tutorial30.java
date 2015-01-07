package tutorial;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import com.tinkerpop.gremlin.process.computer.ComputerResult;
import com.tinkerpop.gremlin.process.computer.VertexProgram;
import com.tinkerpop.gremlin.process.computer.ranking.pagerank.PageRankVertexProgram;
import com.tinkerpop.gremlin.structure.Vertex;

public class Tutorial30 {
	public void execute() throws Exception {
		String dir = "data/graph";
		FileUtils.deleteDirectory(new File(dir));
//		Graph g = TinkerFactory.createClassic();
		Neo4jGraph g = Neo4jGraph.open(dir);
		
		Vertex marko 	= g.addVertex(		"name", "marko", "age", 29);
		Vertex vadas 	= g.addVertex(		"name", "vadas", "age", 27);
		Vertex lop 		= g.addVertex(		"name", "lop", "lang", "java");
		Vertex josh 	= g.addVertex(		"name", "josh", "age", 32);
		Vertex ripple 	= g.addVertex(		"name", "ripple", "lang", "java");
		Vertex peter 	= g.addVertex(		"name", "peter", "age", 35);
		
		marko.addEdge(	"knows", 	vadas, 	"weight", 0.5f);
		marko.addEdge(	"knows", 	josh, 	"weight", 1.0f);
		marko.addEdge(	"created", 	lop, 	"weight", 0.4f);
		josh.addEdge(	"created", 	ripple, "weight", 1.0f);
		josh.addEdge(	"created", 	lop, 	"weight", 0.4f);
		peter.addEdge(	"created", 	lop, 	"weight", 0.2f);

		// g.compute(PageRankVertexProgram.build().create().execute(vertex, messenger, memory);)
		VertexProgram<Double> pagerank = PageRankVertexProgram.build()
										.vertexCount(6)
										.create();
		
		ComputerResult result = g.compute().program(PageRankVertexProgram.build().create()).submit().get();
		
		// result.graph.V.map{[it.get().value("name"), it.get().value(PageRankVertexProgram.PAGE_RANK)]}
		
		result.graph().V().map(it ->  {
			String key = it.get().value("name");
			double val = it.get().value(PageRankVertexProgram.PAGE_RANK);
			System.out.println(key +" -> "+val);
			return it;
		});
		
		g.close();
	}
}
