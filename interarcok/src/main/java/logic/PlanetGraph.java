package logic;

import container.Galaxy.Planet;
import java.util.Collection;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class PlanetGraph
{
	public static SimpleDirectedWeightedGraph<Planet,DefaultWeightedEdge> distances = null;

	public static void setDistances(Collection<Planet> planets)
	{
		distances = new SimpleDirectedWeightedGraph<Planet,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		for( Planet p : planets )
		{
			distances.addVertex(p);
		}

		for( Planet p1 : distances.vertexSet() )
		{
			for( Planet p2 : distances.vertexSet() )
			{
				if( !p1.equals(p2) )
				{
					distances.setEdgeWeight(distances.addEdge(p1, p2),p1.distance(p2));
				}
			}
		}
	}
	public static double getDist(Planet p1, Planet p2)
	{
		if(p1.equals(p2)) return 0;
		return distances.getEdgeWeight(distances.getEdge(p1, p2));
	}
}
