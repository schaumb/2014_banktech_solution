package container;

import java.util.TreeSet;

import logic.MoverClass;
import logic.Selector;

import org.json.JSONException;
import org.json.JSONObject;

import communication.Communication;
import communication.Loggers;

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
			planet.owned = team;
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
	}

	public void KiirMindent()
	{
		String str = "";
		str += "packages\n";
		for(Package p : Galaxy.packages.values())
		{
			str += p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanet.name + " " + (p.lastOwner == null ? "null" : p.lastOwner.name) + "\n";
		}
		str += "planets\n";
		for(Planet p : Galaxy.planets.values())
		{
			str += p.name + " " + p.pkgs.size() + " " + (p.owned == null ? "null" : p.owned.name) + " pkgs:";
			for(Package pk : p.pkgs)
			{
				str += pk.packageId + " ";
			}
			str += "\n";
		}
		str += "ships\n";
		for(SpaceShip p : Galaxy.ships.values())
		{
			str += p.getUniqueId() + " " + p.arriveWhen + " p:" + (p.planet == null ? "null" : p.planet.name) + " t:" +
					(p.targetPlanet == null ? "null" : p.targetPlanet.name) + " pack:" + (p.pack == null ? "null" : p.pack.packageId) + "\n";
		}
		Loggers.logLogger.info(str);
	}
	public void doIt() throws InterruptedException
	{
		boolean bugsearch = false;
		while(true)
		{
			Communication.whereIs();
			Communication.getGalaxy();
			Communication.whereAre();
			Selector.recalculatePTS();
			KiirMindent();
			if(planet.owned == null && pack != null && pack.lastPlanet != planet)
			{
				if(!drop())
				{
					bugsearch = true;
				}
				Package p = pack;
				System.out.println("b4 - " + p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanet.name + " " + (p.lastOwner == null ? "null" : p.lastOwner.name));

			}

			if(pack == null)
			{
				//for(final Package p : planet.pkgs)
				for(Package p : Galaxy.packages.values())
				{

					if( (p.claim == null || getUniqueId().equals(p.claim)) &&
							(p.lastOwner == null || !p.lastOwner.areWe()))
					{
						System.out.println("after - " + p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanet.name + " " + (p.lastOwner == null ? "null" : p.lastOwner.name));
						if(pick(p) != -2)
						{
							break;
						}
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

				long save = MoverClass.time.get();
				if(save > arriveWhen)
				{
					MoverClass.time.compareAndSet(save, arriveWhen);
				}

				long t = arriveWhen - System.currentTimeMillis() - 303;
				if(t > 0)
				{
					Thread.sleep(t);
				}

				if(bugsearch)
				{
					System.exit(1);
				}

				planet = targetPlanet;
				targetPlanet = null;
				planet.claim = null;
			}
		}
	}
}

