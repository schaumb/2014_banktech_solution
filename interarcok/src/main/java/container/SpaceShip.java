package container;

import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

public class SpaceShip
{
	public static double speed = 170;
	public static double speedWithtPackage = 150;

	public Owner team;
	public Planet planet = null;
	public Planet targetPlanet = null;
	public Long arriveWhen;
	public Package pack = null;
	public Integer shipNum;
	public Long inPlanetSince;

	public boolean hasPackage()
	{
		return pack != null;
	}

	public SpaceShip( Owner team, JSONObject ss ) throws JSONException
	{
		this.team = team;
		arriveWhen = System.currentTimeMillis() + ss.optLong("arriveAfterMs", 0);
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

		if(Galaxy.ships.containsKey(getUniqueId()) &&
				Objects.equals(Galaxy.ships.get(getUniqueId()).planet,planet))
		{
			inPlanetSince = Galaxy.ships.get(getUniqueId()).inPlanetSince;
		}
		else
		{
			inPlanetSince = System.currentTimeMillis();
		}

		Galaxy.ships.put(getUniqueId(), this);
	}

	public String getUniqueId()
	{
		return team.name + "-" + shipNum.toString();
	}
}
