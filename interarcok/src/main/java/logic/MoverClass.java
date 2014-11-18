package logic;

import java.util.ArrayList;
import java.util.LinkedList;
import communication.Communication;
import container.Galaxy;
import container.SpaceShip;
import container.Galaxy.Package;
import container.Galaxy.Planet;

public class MoverClass
{
	Communication c;
	Galaxy g;
	SpaceShip ss;
	PlanetGraph pg;

	public MoverClass(Communication c)
	{
		this.c = c;
		this.g = c.getGalaxy();
		this.ss = new SpaceShip( g.planets , c.whereIs() );
		this.pg = new PlanetGraph( g.planets.values() );
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

		while(true)
		{
			ArrayList<Planet> route = pg.getShortestPath( g.packages.values() , ss.planet );
			LinkedList<Package> pckgs = PackageSelector.calculateNeededPackagesToRoute(
					route,
					g.packages.values(),
					3 - ss.packages.size());

			for( int i = 0; i < route.size() - 1; ++i )
			{
				System.out.println("Arrived to " + route.get(i).getName());

				boolean needNext = false;
				for( int j = 0; j < ss.packages.size() ; )
				{
					if( ss.packages.get(j).getTarget().equals(route.get(i)) )
					{
						Integer gottedFee = c.dropPackage(ss.packages.get(j).getPackageId());
						g.packages.remove(ss.packages.get(j).getPackageId());
						System.out.println("Gotted Fee: " + gottedFee);
						ss.packages.remove(j);
					}
					else
					{
						if(ss.packages.get(j).getTarget().equals(route.get(i+1)))
						{
							needNext = true;
						}
						++j;
					}
				}
				while( pckgs.size() > 0 && pckgs.getFirst().getOrigin().equals(route.get(i)) )
				{
					ss.packages.add(pckgs.getFirst());

					if( pckgs.getFirst().getTarget().equals(route.get(i+1)) )
					{
						needNext = true;
					}

					Integer places = c.pickPackage(pckgs.removeFirst().getPackageId());
					System.out.println("Remaining place: " + places);
				}

				if( pckgs.size() > 0 && pckgs.getFirst().getOrigin().equals(route.get(i+1)) )
				{
					needNext = true;
				}

				if( needNext )
				{
					System.out.println("Next station is " + route.get(i+1).getName() );
					Thread.sleep(c.go(route.get(i+1).getName()));
				}
				else
				{
					System.out.println("No needed the next station - " + route.get(i+1).getName() + " - skipping" );
				}
			}
		}
	}
}
