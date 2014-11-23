package logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import communication.Communication;
import container.Galaxy;
import container.Galaxy.Package;
import container.Galaxy.Planet;
import container.SpaceShip;

public class MoverClass
{
	Communication c;
	Galaxy g;
	SpaceShip ss;
	PlanetGraph pg;
	public int sumFee = 0;
	public int deliver = 0;

	public MoverClass(Communication c)
	{
		this.c = c;
		this.g = c.getGalaxy();
		this.ss = new SpaceShip( g.planets , c.whereIs() );
	}

	public static class Route
	{
		Planet from;
		Planet to;
		Double sumFee;
		Integer count;

		public Route(Planet from, Planet to) {
			this.from = from;
			this.to = to;
			this.sumFee = 0.;
			this.count = 0;
		}

	}
	public static class WidthClass
	{
		TreeSet<Package> packages;
		LinkedList<Route> route = new LinkedList<Route>();

		double width;

		WidthClass(Collection<Package> beginpkgs, Planet fromWho)
		{
			this(beginpkgs, fromWho, null);
		}

		WidthClass(WidthClass wc, List<Package> p)
		{
			this(wc.packages,p.get(0).getOrigin(),p);
		}

		private WidthClass(Collection<Package> beginpkgs, Planet fromWho, List<Package> np)
		{
			packages = new TreeSet<Package>(fromWho.new PackageSort());
			packages.addAll(beginpkgs);

			if(np != null) packages.addAll(np);

			for(Package p : packages)
			{
				if(route.isEmpty())
				{
					route.add(new Route(fromWho,p.getTarget()));
				}
				else if(!route.getLast().to.equals(p.getTarget()))
				{
					route.add(new Route(route.getLast().to,p.getTarget()));
				}
				double fee_for1 = p.getFee().doubleValue() / route.size();

				for(Route r : route)
				{
					r.sumFee += fee_for1;
					++r.count;
				}
			}
			setWidth();
		}

		public void setWidth()
		{
			width = 0;
			for(Route r:route)
			{
				width += r.sumFee / PlanetGraph.getDist(r.from, r.to) * (170 - 20 * r.count);
			}
			if(route.size()>0)
			{
				width /= (double)route.size();
			}
		}

		public ArrayList<Package> newby(Planet pl)
		{
			ArrayList<Package> res = new ArrayList<Package>();

			for(Package p : packages)
			{
				if(p.getOrigin().equals(pl))
				{
					res.add(p);
				}
			}

			return res;
		}

		public Planet getNextStation()
		{
			if(route.size() > 0)
			{
				return route.getFirst().to;
			}

			return null;
		}

		public ArrayList<Package> next()
		{
			ArrayList<Package> res = new ArrayList<Package>();

			if(route.isEmpty()) return res;

			Route r = route.removeFirst();
			Planet target = r.to;

			for(Package p : packages)
			{
				if(p.getTarget().equals(target))
				{
					res.add(p);
				}
			}

			for(Package p : res)
			{
				packages.remove(p);
			}

			setWidth();

			return res;
		}
	}

	private WidthClass calcMax(TreeSet<Package> pkgs, WidthClass base, LinkedList<Package> prevs, int num)
	{
		if(num == 0) return base;

		WidthClass max = base;
		Iterator<Package> it = pkgs.iterator();

		if(!prevs.isEmpty())
		{
			while(!prevs.getLast().equals(it.next())){}
		}

		while( it.hasNext() )
		{
			Package p = it.next();

			prevs.addFirst(p);

			WidthClass test = new WidthClass(base,prevs);
			if(max.width < test.width )
			{
				max = test;
			}


			WidthClass rec = calcMax(pkgs, base, prevs, num - 1);
			if(max.width < rec.width)
			{
				max = rec;
			}

			prevs.removeLast();
		}
		return max;
	}

	public void Go() throws InterruptedException
	{
		if(ss.planet == null || ss.arrivesAfterMs > 0)
		{
			if(ss.targetPlanet == null)
			{
				c.game_over("Respond problem in SpaceShip - we are nowhere");
			}
			ss.planet = ss.targetPlanet;
			Thread.sleep(ss.arrivesAfterMs);
		}

		WidthClass wc = new WidthClass(ss.packages, ss.planet);

		while(true)
		{
			ArrayList<Package> deleted = wc.next();
			for(Package p : deleted)
			{
				c.dropPackage(p.getPackageId());
				ss.packages.remove(p);
				sumFee += p.getFee();
				++deliver;
				System.out.println("Got fee : " + p.getFee() + " sum: " + sumFee);
			}
			System.out.print("Az eredeti: " + wc.width);
			wc = calcMax(ss.planet.packages, wc, new LinkedList<Package>(), 3-wc.packages.size());
			System.out.println(" az uj:" + wc.width);

			ArrayList<Package> news = wc.newby(ss.planet);

			for(Package p : news)
			{
				if(ss.packages.contains(p)) continue;
				c.pickPackage(p.getPackageId());
				ss.packages.add(p);
				ss.planet.packages.remove(p);
			}

			if(wc.getNextStation() == null)
			{
				double min = Double.POSITIVE_INFINITY;
				Planet maybeNext = ss.planet;
				for(DefaultWeightedEdge edge : PlanetGraph.distances.outgoingEdgesOf(ss.planet))
				{
					Planet perhapsNext = PlanetGraph.distances.getEdgeTarget(edge);
					if(perhapsNext.packages.size() > 0 &&
							PlanetGraph.distances.getEdgeWeight(edge) < min)
					{
						min = PlanetGraph.distances.getEdgeWeight(edge);
						maybeNext = perhapsNext;
					}
				}
				ss.planet = maybeNext;
			}
			else
			{
				ss.planet = wc.getNextStation();
			}
			Thread.sleep(c.go(ss.planet.getName()));
		}
	}
}
