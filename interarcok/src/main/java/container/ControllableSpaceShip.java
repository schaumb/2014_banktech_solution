package container;

import java.util.TreeSet;

import logic.Selector;

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
		case -2 :
			break;
		case -1 : crash(); break;
		default:
			pack.isMoveing.set(false);
			pack.lastOwner = team;
			pack.lastPlanet = planet;
			planet.owned = team;
			planet.pkgs.add(pack);

			pack = null;
			break;
		}
		return res >= 0;
	}

	private int pick(Package pack)
	{
		int res = Communication.pickPackage(pack.packageId, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		case 0 :
			this.pack = pack;
		case -2 : // valaki elvitte
			pack.isMoveing.set(true);
			pack.claim = null;
			planet.pkgs.remove(pack);
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
			planet.hasMine.set(true);
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

	public void doIt() throws InterruptedException
	{
		if(planet.owned == null && pack != null && pack.lastPlanet != planet)
		{
			drop();
		}

		for(final Package p : planet.pkgs)
		{
			if( p.claim == null || getUniqueId().equals(p.claim))
			{
				if(pick(p) != -2)
				{
					break;
				}
			}
		}

		if(planet.owned != null && planet.owned.areWe() && !planet.hasMine.get())
		{
			TreeSet<SpaceShip> ss = Selector.planet_arrivers_without_package.get(planet);
			if(Selector.rand.nextInt(20) == 0
					|| (ss != null && ss.size() > 0))
			{
				installMine();
			}
		}

		targetPlanet = Selector.calculateNext(this);

		if(targetPlanet != null) // always true
		{
			go();
			Thread.sleep(arriveWhen - System.currentTimeMillis());

			planet = targetPlanet;
			targetPlanet = null;
			planet.claim = null;
		}
	}
}

