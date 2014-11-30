package container;

import org.json.JSONException;
import org.json.JSONObject;

import communication.Communication;

public class ControllableSpaceShip extends SpaceShip
{
	public enum ShipState{ Collector , Defender , Stealer , Migrator , Crashed }

	public ShipState shipState;

	public ControllableSpaceShip( JSONObject whereIs, Owner own ) throws JSONException
	{
		super(own, whereIs);
		shipState = ShipState.Collector;
	}

	public void setShipState(ShipState shipState)
	{
		this.shipState = shipState;
	}

	private void drop()
	{
		int res = Communication.dropPackage(shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		case 0 :
			pack.isMoveing = false;
			pack.lastOwner = team;
			pack.lastPlanet = planet;
			planet.owned = team;
			planet.pkgs.add(pack);

			pack = null;
			break;
		}
	}

	private int pick(Package pack)
	{
		int res = Communication.pickPackage(pack.packageId, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		case 0 :
			pack.isMoveing = true;
			this.pack = pack;
			break;
		}
		return res;
	}

	private void go()
	{
		int res = Communication.go(targetPlanet.name, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		default:
			arriveWhen = System.currentTimeMillis() + res;
			break;
		}
	}

	public void crash()
	{
		setShipState(ShipState.Crashed);
		arriveWhen = System.currentTimeMillis() + 120000;
		targetPlanet = planet;
		planet = null;
	}

	public void doIt()
	{
		if(planet.owned == null || planet.owned.areWe())
		{
			if(pack != null && pack.lastPlanet != planet)
			{
				drop();
			}
		}

		if(pack == null && planet.pkgs.size() > 0)
		{
			for(Package pkg : planet.pkgs)
			{
				if(pkg.lastOwner.areWe()) continue;

				int res = pick(pkg);
				if(res != -2)
				{
					break;
				}
			}
		}

		if(targetPlanet != null)
		{
			go();
		}
	}

	public boolean isArrived()
	{
		if(arriveWhen <= System.currentTimeMillis())
		{
			planet = targetPlanet;
			targetPlanet = null;
			team.claimPlanets().remove(planet);
			return true;
		}
		return false;
	}
}

