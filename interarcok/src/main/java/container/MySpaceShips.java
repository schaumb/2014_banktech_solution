package container;

import java.util.ArrayList;
import java.util.Map.Entry;

import logic.Selector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import communication.Loggers;

public class MySpaceShips extends Owner
{
	ArrayList<ControllableSpaceShip> myShips = new ArrayList<ControllableSpaceShip>();

	public MySpaceShips( JSONObject job ) throws JSONException
	{
		name = job.getString("userName");
		remainingMines = job.getInt("remainingMines");

		JSONArray pls = job.getJSONArray("ships");
		for( int i = 0; i < pls.length() ; ++i )
		{
			JSONObject sps = pls.getJSONObject(i);

			myShips.add(new ControllableSpaceShip(sps, this));
		}

		Galaxy.teams.put(name, this);
	}

	public Long doLogicStuff() throws InterruptedException
	{
		int myPackages = 0;
		int moveingPackage = 0;
		String str = new String();
		str += "Planets\n";
		for(Entry<String, Planet> pl : Galaxy.planets.entrySet())
		{
			str += pl.getValue().name + " " +
					(pl.getValue().owned == null? "null" : pl.getValue().owned.name) + "\n";
		}
		str += "Packages\n";
		for(Entry<Integer, Package> pl : Galaxy.packages.entrySet())
		{
			str += pl.getKey() + " " + pl.getValue().isMoveing  + " " +
					(pl.getValue().lastOwner == null? "null" : pl.getValue().lastOwner.name) + "\n";
			if(pl.getValue().lastOwner != null && pl.getValue().lastOwner.areWe())
			{
				++myPackages;
			}
			if(pl.getValue().isMoveing)
			{
				++moveingPackage;
			}
		}
		str += "Ships\n";
		for(Entry<String, SpaceShip> pl : Galaxy.ships.entrySet())
		{
			str += pl.getKey() + " " +
					" in: " +(pl.getValue().planet == null? "null" : pl.getValue().planet.name) +
					" since: " + (System.currentTimeMillis()-pl.getValue().inPlanetSince) +
					" Targe" + (pl.getValue().targetPlanet == null ? "null" : pl.getValue().targetPlanet.name) +
					" Packa" + (pl.getValue().pack == null ? "null" : pl.getValue().pack.packageId) + "\n";
		}
		Loggers.logLogger.info(str);
		System.out.println(myPackages + "/" + moveingPackage + " = my/move");

		for(ControllableSpaceShip css : myShips)
		{
			if(css.isArrived())
			{
				css.targetPlanet = Selector.calculateNext(css);
			}
		}

		ArrayList<Thread> thrs = new ArrayList<Thread>();

		for(final ControllableSpaceShip css : myShips)
		{
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					css.doIt();
				}});
			thrs.add(t);
			t.start();
		}

		for(Thread t : thrs)
		{
			t.join();
		}

		///
		Long minWaitFor = Long.min(
				myShips.get(0).arriveWhen, Long.min(
				myShips.get(1).arriveWhen,
				myShips.get(2).arriveWhen));

		return minWaitFor;
	}

	@Override
	public boolean areWe()
	{
		return true;
	}

	@Override
	public ArrayList<SpaceShip> ships()
	{
		return new ArrayList<SpaceShip>(myShips);
	}
}
