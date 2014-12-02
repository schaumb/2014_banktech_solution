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
			Galaxy.getPlanet(planetName).ownerName = teamName;
			break;
		case -1 : crash(); break;
		default:
			Package pack = Galaxy.getPackage(this.pack);
			pack.isMoveing.set(false);
			pack.lastOwnerName = teamName;
			pack.lastPlanetName = planetName;
			Planet planet = Galaxy.getPlanet(this.planetName);
			planet.ownerName = teamName;
			planet.pkgs.add(this.pack);

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
			this.pack = pack.packageId;
		case -2 : // valaki elvitte
			pack.isMoveing.set(true);
			pack.claim = null;
			Planet planet = Galaxy.getPlanet(this.planetName);
			planet.pkgs.remove(pack);
			break;
		}
		return res;
	}

	private void go()
	{
		int res = Communication.go(targetPlanetName, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		default:
			arriveWhen = System.currentTimeMillis() + res;
			planetName = targetPlanetName;
			Galaxy.getPlanet(planetName).claim = null;
			break;
		}
	}

	private void installMine()
	{
		int res = Communication.installMine(planetName, shipNum);
		switch(res)
		{
		case -1 : crash(); break;
		default:
			Planet planet = Galaxy.getPlanet(planetName);
			planet.hasMine.set(true);
			break;
		}
	}
	public void crash()
	{
		arriveWhen = System.currentTimeMillis() + 120000;
		targetPlanetName = planetName;
	}

	public void KiirMindent()
	{
		String str = "";
		str += "packages\n";
		for(Package p : Galaxy.packages.values())
		{
			str += p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanetName + " " + p.lastOwnerName + "\n";
		}
		str += "planets\n";
		for(Planet p : Galaxy.planets.values())
		{
			str += p.name + " " + p.pkgs.size() + " " + p.ownerName + " pkgs:";
			for(Integer pk : p.pkgs)
			{
				str += pk + " ";
			}
			str += "\n";
		}
		str += "ships\n";
		for(SpaceShip p : Galaxy.ships.values())
		{
			str += p.getUniqueId() + " " + p.arriveWhen + " p:" + p.planetName + " t:" +
					p.targetPlanetName + " pack:" + p.pack + "\n";
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
			Planet planet = Galaxy.getPlanet(planetName);
			Package pack = Galaxy.getPackage(this.pack);
			if(planet.ownerName == null && pack != null && pack.lastPlanetName != planetName)
			{
				if(!drop())
				{
					bugsearch = true;
				}
				Package p = pack;
				System.out.println("b4 - " + p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanetName + " " + p.lastOwnerName);

			}

			if(pack == null)
			{
				//for(final Package p : planet.pkgs)
				for(Package p : Galaxy.packages.values())
				{

					if( (p.claim == null || getUniqueId().equals(p.claim)) &&
							(p.lastOwnerName == null || !p.lastOwnerName.equals(teamName)))
					{
						System.out.println("after - " + p.packageId + " " + p.isMoveing.get() + " " + p.lastPlanetName + " " + p.lastOwnerName);
						if(pick(p) != -2)
						{
							break;
						}
					}
				}
			}

			if(planet.ownerName != null && planet.ownerName.equals(teamName) && !planet.hasMine.get())
			{
				TreeSet<String> ss = Selector.planet_arrivers_without_package.get(planet);
				if(Selector.rand.nextInt(20) == 0
						|| (ss != null && ss.size() > 0))
				{
					installMine();
				}
			}

			targetPlanetName = Selector.calculateNext(this).name;

			if(targetPlanetName != null) // always true
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
			}
		}
	}
}

