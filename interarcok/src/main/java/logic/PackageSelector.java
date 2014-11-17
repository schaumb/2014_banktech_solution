package logic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import container.Galaxy.Package;
import container.Galaxy.Planet;

public class PackageSelector
{
	public static class Pair extends AbstractMap.SimpleEntry<Integer, Integer>  implements Comparable<Pair>
	{
		private static final long serialVersionUID = 1L;

		Package pkg;

		public Pair(Integer arg0, Integer arg1, Package pkg) {
			super(arg0, arg1);
			this.pkg = pkg;
		}

		@Override
		public int compareTo(Pair o) {
			int i = getValue().compareTo(o.getValue());
			if( i != 0 ) return i;
			i = getKey().compareTo(o.getKey());
			if( i != 0 ) return i;
			return pkg.compareTo(o.pkg);
		}
	};

	private static class Information
	{
		TreeSet<Package> pkgs;
		double weight;

		Information(Information info, double plusWeight, Package pkg)
		{
			this.weight = info.weight + plusWeight;
			this.pkgs = new TreeSet<Package>(info.pkgs.comparator());
			this.pkgs.addAll(info.pkgs);
			this.pkgs.add(pkg);
		}

		Information(Comparator<Package> pkgcp)
		{
			this.weight = 0;
			this.pkgs = new TreeSet<Package>(pkgcp);
		}

		boolean inThere(Package p)
		{
			return pkgs.contains(p);
		}
	}

	public static class PackageComparator implements Comparator<Package>
	{
		HashMap<Planet,Integer> convFrom;

		public PackageComparator(HashMap<Planet,Integer> convFrom)
		{
			this.convFrom = convFrom;
		}

		@Override
		public int compare(Package o1, Package o2)
		{
			int i;
			i = convFrom.get(o1.getOrigin()).compareTo(convFrom.get(o2.getOrigin()));
			if( i != 0 ) return i;
			i = convFrom.get(o1.getTarget()).compareTo(convFrom.get(o2.getTarget()));
			if( i != 0 ) return i;
			i = o1.fee.compareTo(o2.fee);
			if( i != 0 ) return -i;
			return o1.compareTo(o2);
		}
	}

	public static LinkedList<Package> calculateNeededPackagesToRoute(
			ArrayList<Planet> planets, Collection<Package> pkgs,
			Integer maxPackage)
	{
		// every planet got an index
		HashMap<Planet,Integer> convFrom = new HashMap<Planet,Integer>();

		// calculating 2 Planet distance;
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> distances =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		for(int i = 0;i < planets.size();++i)
		{
			convFrom.put(planets.get(i),i);
			distances.addVertex(i);

			double dist = 0;
			for( int j = i - 1; j >= 0; --j )
			{
				dist += planets.get(j).distance(planets.get(j+1));
				distances.setEdgeWeight(distances.addEdge(j, i), dist);
			}
		}

		TreeSet<Pair> entries = new TreeSet<Pair>();

		for( Package pkg : pkgs )
		{
			Integer i = convFrom.get(pkg.getOrigin());
			Integer j = convFrom.get(pkg.getTarget());
			if( i < j )
			{
				Pair p = new Pair(i,j,pkg);
				entries.add(p);
			}
		}

		TreeMap<Integer,Information> map = new TreeMap<Integer,Information>();

		map.put(0, new Information(new PackageComparator(convFrom)));

		for(int j = 0; j < maxPackage; ++j )
		{
			for(Pair p : entries)
			{
				Information last = map.floorEntry(p.getKey()).getValue();

				if( last.inThere(p.pkg) )
				{
					continue;
				}

				double weight = p.pkg.fee.doubleValue() /
						distances.getEdgeWeight(distances.getEdge(p.getKey(), p.getValue()));

				Information maxInfo = new Information(last, weight, p.pkg);

				Information now = map.floorEntry(p.getValue()).getValue();

				if(now.weight < maxInfo.weight)
				{
					map.put(p.getValue(), maxInfo);
				}
			}
			Information lastInfo = map.lastEntry().getValue();
			map.clear();
			map.put(0, lastInfo);
		}
		return new LinkedList<Package>(map.lastEntry().getValue().pkgs);
	}
}
