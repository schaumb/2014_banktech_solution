package container;

import org.json.JSONException;
import org.json.JSONObject;

public class SpaceShip
{
	public Owner team;
	public Planet planet = null;
	public Planet targetPlanet = null;
	public Integer arriveAfterMs;
	public Package pack = null;
	public Integer shipNum;

	public SpaceShip( Owner team, JSONObject ss ) throws JSONException
	{
		this.team = team;
		arriveAfterMs = ss.optInt("arriveAfterMs", 0);
		shipNum = ss.getInt("shipNum");

		String tmp;

		tmp = ss.optString("planetName");
		if( tmp != null && !tmp.equals("null") )
		{
			planet = Galaxy.planets.get(tmp);
		}

		tmp = ss.optString("targetPlanetName");
		if( tmp != null && !tmp.equals("null") )
		{
			targetPlanet = Galaxy.planets.get(tmp);
		}

		JSONObject mypackage = ss.optJSONObject("pack");
		if( mypackage != null && !mypackage.equals("null") )
		{
			pack = new Package(mypackage, true);
		}

		Galaxy.ships.put(getUniqueId(), this);
	}

	public String getUniqueId()
	{
		return team.name + "-" + shipNum.toString();
	}
}
