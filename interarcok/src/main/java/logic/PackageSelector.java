package logic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
		LinkedList<Package> pkgs;
		Integer optimum = 0;

		Information(Information info)
		{
			this.pkgs = new LinkedList<Package>(info.pkgs);
		}

		Information()
		{
			this.pkgs = new LinkedList<Package>();
		}

		boolean inThere(Package p)
		{
			return pkgs.contains(p);
		}

		void putThere(Package p)
		{
			pkgs.add(p);
		}
	}

	public static LinkedList<Package> calculateNeededPackagesToRoute(
			ArrayList<Planet> planets, Collection<Package> pkgs,
			Integer maxPackage)
	{
		HashMap<Planet,Integer> convFrom = new HashMap<Planet,Integer>();

		for(int i = 0;i < planets.size();++i)
		{
			convFrom.put(planets.get(i),i);
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

		ArrayList<TreeMap<Integer,Information>> optimums = new ArrayList<TreeMap<Integer,Information>>();

		for( int i = 0; i < maxPackage ; ++i )
		{
			optimums.add(new TreeMap<Integer,Information>());
		}
		for(int j = 0; j < maxPackage; ++j )
		{
			TreeMap<Integer,Information> map = optimums.get(j);

			map.put(0, new Information());

			for(Pair p : entries)
			{
				// TODO sulyozni a ccucokat, azalapjan a package-ket kivalasztani!!
				double suly = 0.0;
				Map.Entry<Integer,Information> prv = map.floorEntry(p.getKey());
				/*
				Map.Entry<Integer,Information> now = map.floorEntry(p.getValue());

				for( int k = 0 ; k < j;  ++k )
				{
					Map.Entry<Integer,Information> prv_prv = optimums.get(k).floorEntry(p.getKey());
					Map.Entry<Integer,Information> prv_now = optimums.get(k).floorEntry(p.getValue());

				}*/
			}
		}
		return optimums.get(2).lastEntry().getValue().pkgs;
	}
}
