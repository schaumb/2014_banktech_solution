package container;

import java.util.ArrayList;
import java.util.LinkedList;

import logic.Selector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySpaceShips extends Owner
{
	public enum GlobalState{ Collecting , Defending , Stealing , Migrating }

	ArrayList<ControllableSpaceShip> myShips = new ArrayList<ControllableSpaceShip>();
	Integer remainingMines;
	GlobalState globalState;
	LinkedList<Planet> claimPlanets = new LinkedList<Planet>();

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

		globalState = GlobalState.Collecting;
	}

	public Long doLogicStuff() throws InterruptedException
	{


		// set Global state

		// set Ship's state and next command

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

	@Override
	public LinkedList<Planet> claimPlanets()
	{
		return claimPlanets;
	}
}
