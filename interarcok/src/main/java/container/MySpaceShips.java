package container;

import java.util.ArrayList;
import java.util.HashMap;
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
		int moveingPackage = 0;
		HashMap<Owner,Integer> pckgnum = new HashMap<Owner,Integer>();
		String str = new String();
		str += "Planets\n";
		pckgnum.put(null, 0);
		for(Owner o : Galaxy.teams.values())
		{
			pckgnum.put(o, 0);
		}
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

			pckgnum.put(pl.getValue().lastOwner, pckgnum.get(pl.getValue().lastOwner) + 1);

			if(pl.getValue().isMoveing)
			{
				++moveingPackage;
			}
		}
		str += "Ships\n";
		for(Entry<String, SpaceShip> pl : Galaxy.ships.entrySet())
		{
			if(pl.getValue().team.areWe())
			{
				System.out.println( pl.getKey() + " " +
						" in: " +(pl.getValue().planet == null? "null" : pl.getValue().planet.name) +
						" since: " + (System.currentTimeMillis()-pl.getValue().inPlanetSince) +
						" Targe" + (pl.getValue().targetPlanet == null ? "null" : pl.getValue().targetPlanet.name) +
						" Packa" + (pl.getValue().pack == null ? "null" : pl.getValue().pack.packageId));
			}
			str += pl.getKey() + " " +
					" in: " +(pl.getValue().planet == null? "null" : pl.getValue().planet.name) +
					" since: " + (System.currentTimeMillis()-pl.getValue().inPlanetSince) +
					" Targe" + (pl.getValue().targetPlanet == null ? "null" : pl.getValue().targetPlanet.name) +
					" Packa" + (pl.getValue().pack == null ? "null" : pl.getValue().pack.packageId) + "\n";
		}
		Loggers.logLogger.info(str);
		System.out.println(pckgnum.get(this) + "/" + moveingPackage + " = my/move");
		for(Integer i : pckgnum.values())
		{
			System.out.print(i + " ");
		}
		System.out.println();

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
