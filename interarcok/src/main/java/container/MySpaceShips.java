package container;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySpaceShips extends Owner
{
	enum GlobalState{ Collecting , Defending , Stealing , Migrating }
	enum ShipState{ Collector , Defender , Stealer , Migrator , Crashed }

	private class ControllableSpaceShip extends SpaceShip
	{
		private ShipState shipState;
		private Integer waiting = 1;

		public ControllableSpaceShip( JSONObject whereIs ) throws JSONException
		{
			super(MySpaceShips.this, whereIs);
			shipState = ShipState.Collector;
		}

		public void setShipState(ShipState shipState)
		{
			this.shipState = shipState;
		}

		public boolean readyToCommand()
		{
			return arriveAfterMs == 0;
		}

		public int getReadyToNextCommand()
		{
			return arriveAfterMs + waiting;
		}

		public void elsapedTime(long many)
		{
			if(arriveAfterMs < many)
			{
				many -= arriveAfterMs;
				arriveAfterMs = 0;

				if(waiting > many)
				{
					arriveAfterMs = waiting - (int)many;
				}
				else
				{
					planet = targetPlanet;
					targetPlanet = null;
				}
				waiting = 1;
			}
			else
			{
				arriveAfterMs -= (int)many;
			}

		}
	}

	ArrayList<ControllableSpaceShip> myShips = new ArrayList<ControllableSpaceShip>();
	Integer remainingMines;
	GlobalState globalState;

	public MySpaceShips( JSONObject job ) throws JSONException
	{
		name = job.getString("userName");
		remainingMines = job.getInt("remainingMines");

		JSONArray pls = job.getJSONArray("ships");
		for( int i = 0; i < pls.length() ; ++i )
		{
			JSONObject sps = pls.getJSONObject(i);

			myShips.add(new ControllableSpaceShip(sps));
		}

		Galaxy.teams.put(name, this);

		globalState = GlobalState.Collecting;
	}

	public Integer doLogicStuff(long reallyWait)
	{
		for(ControllableSpaceShip css : myShips)
		{
			css.elsapedTime(reallyWait);

		}
		// set Global state
		// set Ship's state

		for(ControllableSpaceShip css : myShips)
		{
			if(css.readyToCommand())
			{
				// TODO
			}
		}

		///
		Integer minWait = Integer.min(
				myShips.get(0).getReadyToNextCommand(), Integer.min(
				myShips.get(1).getReadyToNextCommand(),
				myShips.get(2).getReadyToNextCommand()));

		return minWait;
	}

	@Override
	boolean areWe()
	{
		return true;
	}

	@Override
	ArrayList<SpaceShip> ships()
	{
		return new ArrayList<SpaceShip>(myShips);
	}
}
