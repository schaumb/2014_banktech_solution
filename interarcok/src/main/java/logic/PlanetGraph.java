package logic;

import container.Galaxy.Planet;
import container.Galaxy.Package;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class PlanetGraph extends SimpleDirectedWeightedGraph<Planet,DefaultWeightedEdge>
{
	private static final long serialVersionUID = 1L;
	private static final double magicNumber = Math.sqrt(2) - 1;

	public PlanetGraph(Collection<Planet> planets)
	{
		super(DefaultWeightedEdge.class);
		for(Planet p : planets)
		{
			addVertex(p);
		}
	}
	public ArrayList<Planet> getShortestPath(Collection<Package> packages , Planet from)
	{
		removeAllEdges(edgeSet());

		for( Planet p1 : vertexSet() )
		{
			for( Planet p2 : vertexSet() )
			{
				if( !p1.equals(p2) )
				{
					double originLength = p1.distance(p2);
					double upperLimit = originLength * magicNumber;
					double weight = 0;
					for(Package p : packages)
					{
						double plus = p1.distance(p.getOrigin()) + p2.distance(p.getTarget());
						if( plus < upperLimit )
						{
							weight += 1 - plus / upperLimit;
						}
					}
					setEdgeWeight(addEdge(p1, p2),weight);
				}
			}
		}

		ArrayList<Planet> planets = new ArrayList<Planet>();
		planets.add(from);
		Planet last = from;
		int size = vertexSet().size();
		// Schaum's Algorithm - directed graph shortest tree that just one route - (Approximation)

		while(planets.size() != size)
		{
			Planet next = null;
			double max = Double.NEGATIVE_INFINITY;
			for( DefaultWeightedEdge dwe : outgoingEdgesOf(last) )
			{
				if( getEdgeWeight(dwe) > max && !planets.contains(getEdgeTarget(dwe)) )
				{
					next = getEdgeTarget(dwe);
					max = getEdgeWeight(dwe);
				}
			}
			if( next == null )
			{
				double min = Double.POSITIVE_INFINITY;

				for( Planet p : vertexSet() )
				{
					double dist = p.distance(last);
					if( dist < min && !planets.contains(p) )
					{
						next = p;
						min = dist;
					}
				}
			}
			planets.add(next);
			last = next;
		}
		return planets;
	}
}
