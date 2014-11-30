package logic;

import java.util.Map.Entry;


import container.ControllableSpaceShip;
import container.Galaxy;
import container.Planet;
import container.Package;

public class Selector
{
	public static Planet calculateNext(ControllableSpaceShip css)
	{
		if(css.pack != null)
		{
			Planet pmin = null;
			double minDist = Double.POSITIVE_INFINITY;
			for(Entry<String, Planet> pl : Galaxy.planets.entrySet())
			{
				if( (pl.getValue().owned == null || pl.getValue().owned.areWe()) &&
						!pl.getValue().equals(css.planet))
				{
					double dist = pl.getValue().distance(css.planet);
					if(dist < minDist)
					{
						minDist = dist;
						pmin = pl.getValue();
					}
				}
			}
			return pmin;
		}
		else
		{
			Planet pmin = null;
			double minDist = Double.POSITIVE_INFINITY;
			for(Entry<Integer, Package> pc : Galaxy.packages.entrySet())
			{
				if(!pc.getValue().isMoveing &&
						pc.getValue().lastOwner == null &&
						!pc.getValue().lastPlanet.equals(css.planet) &&
						(css.shipState != ControllableSpaceShip.ShipState.Collector ||
						!css.team.claimPlanets().contains(pc.getValue().lastPlanet))
					)
				{
					double dist = pc.getValue().lastPlanet.distance(css.planet);
					if(dist < minDist)
					{
						minDist = dist;
						pmin = pc.getValue().lastPlanet;
					}
				}
			}
			return pmin;
		}
	}

}
