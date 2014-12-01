package container;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	public void doLogicStuff() throws InterruptedException
	{
		ArrayList<Thread> thrs = new ArrayList<Thread>();

		for(final ControllableSpaceShip css : myShips)
		{
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					try
					{
						css.doIt();
					}
					catch (InterruptedException e)
					{}
				}});
			thrs.add(t);
			t.start();
			t.join();
		}
		for(Thread t : thrs)
		{
			t.join();
		}
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
