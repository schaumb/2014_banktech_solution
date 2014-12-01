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

	public SpaceShip( Owner team, JSONObject ss ) throws JSONException
	{
		this.team = team;

		inPlanetSince = System.currentTimeMillis();
		arriveWhen = inPlanetSince + ss.optLong("arriveAfterMs", 0);
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

		SpaceShip previousMe = Galaxy.ships.get(getUniqueId());

		if(previousMe != null)
		{
			if(previousMe.arriveWhen > arriveWhen)
			{
				arriveWhen = previousMe.arriveWhen;
			}

			if(Objects.equals(previousMe.planet,planet))
			{
				inPlanetSince = Galaxy.ships.get(getUniqueId()).inPlanetSince;
			}
		}

		Galaxy.ships.put(getUniqueId(), this);
	}

	public String getUniqueId()
	{
		return team.name + "-" + shipNum.toString();
	}
}
