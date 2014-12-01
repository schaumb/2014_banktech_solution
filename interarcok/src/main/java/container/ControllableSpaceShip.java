package container;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import communication.Communication;

public class ControllableSpaceShip extends SpaceShip
{
	public ControllableSpaceShip( JSONObject whereIs, Owner own ) throws JSONException
	{
		super(own, whereIs);
	}

	private boolean drop()
	{
		int res = Communication.dropPackage(shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		case 1 :
			pack.isMoveing = false;
			pack.lastOwner = team;
			pack.lastPlanet = planet;
			planet.owned = team;
			planet.pkgs.add(pack);

			pack = null;
			break;
		default:
			// kellő inkonzisztencia
			planet.owned = team;
		}
		return res == 1;
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
			planet.pkgs.remove(pack);
			break;
		case -2 : // vki elvitte :(
			pack.isMoveing = true;
			planet.pkgs.remove(pack);
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
			planet = null;
			break;
		}
	}

	private void installMine()
	{
		int res = Communication.installMine(planet.name, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		default:
			planet.hasmine = true;
			team.remainingMines = res;
			break;
		}
	}
	public void crash()
	{
		arriveWhen = System.currentTimeMillis() + 120000;
		targetPlanet = planet;
		planet = null;
		System.out.println(getUniqueId() + " crashed!");
	}

	public void doIt()
	{
		if(planet == null || arriveWhen > System.currentTimeMillis()) return;
		boolean isDropped = false;
		boolean isPicked = false;
		if(planet.owned == null)
		{
			if(pack != null && pack.lastPlanet != planet)
			{
				isDropped = drop();
			}
		}

		if(pack == null && planet.pkgs.size() > 0)
		{
			for(Package pkg : planet.pkgs)
			{
				if(pkg.lastOwner != null && pkg.lastOwner.areWe()) continue;

				int res = pick(pkg);
				if(res != -2)
				{
					isPicked = res == 0;
					break;
				}

			}
		}

		if(isDropped && team.remainingMines > 0)
		{
			if(new Random(System.currentTimeMillis()).nextInt(isPicked?20:10) == 0)
			{
				installMine();
			}
		}

		if(targetPlanet != null)
		{
			go();
		}
		else
		{
			System.out.println("NO TARGET " + getUniqueId());
			System.exit(1);
		}
	}

	public boolean isArrived()
	{
		if(arriveWhen <= System.currentTimeMillis())
		{
			planet = targetPlanet;
			targetPlanet = null;
			return true;
		}
		return false;
	}
}

