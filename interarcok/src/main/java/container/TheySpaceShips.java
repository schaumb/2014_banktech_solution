package container;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TheySpaceShips extends Owner
{
	public TheySpaceShips( JSONObject job ) throws JSONException
	{
		name = job.getString("userName");
		Owner oldMe = Galaxy.teams.get(name);

		if(oldMe != null)
		{
			JSONArray pls = job.getJSONArray("ships");
			for( int i = 0; i < pls.length() ; ++i )
			{
				JSONObject sps = pls.getJSONObject(i);

				new SpaceShip(this, sps);
			}
		}
		else
		{
			JSONArray pls = job.getJSONArray("ships");
			for( int i = 0; i < pls.length() ; ++i )
			{
				JSONObject sps = pls.getJSONObject(i);

				theyShips.add(new SpaceShip(this, sps));
			}

			Galaxy.teams.put(name, this);
		}
	}

	@Override
	public boolean areWe() {
		return false;
	}

	ArrayList<SpaceShip> theyShips = new ArrayList<SpaceShip>();

	@Override
	public ArrayList<SpaceShip> ships()
	{
		return theyShips;
	}

}
