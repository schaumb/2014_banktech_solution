package container;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySpaceShips extends Owner
{
	enum GlobalState{ Collecting , Defending , Stealing , Migrating }
	enum ShipState{ Collector , Defender , Stealer , Migrator }

	private class ControllableSpaceShip extends SpaceShip
	{
		private ShipState shipState;
		public ControllableSpaceShip( JSONObject whereIs ) throws JSONException
		{
			super(MySpaceShips.this, whereIs);
			shipState = ShipState.Collector;
		}

		public void setShipState(ShipState shipState)
		{
			this.shipState = shipState;
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
