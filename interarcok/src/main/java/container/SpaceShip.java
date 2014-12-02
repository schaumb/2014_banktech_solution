package container;

import org.json.JSONException;
import org.json.JSONObject;

public class SpaceShip
{
	public static double speed = 170;
	public static double speedWithtPackage = 150;

	public String teamName;
	public String planetName;
	public String targetPlanetName;
	public Long arriveWhen;
	public Integer pack = null;
	public Integer shipNum;
	public Long inPlanetSince;

	public SpaceShip( Owner team, JSONObject ss ) throws JSONException
	{
		this.teamName = team.name;
		shipNum = ss.getInt("shipNum");

		inPlanetSince = System.currentTimeMillis();
		arriveWhen = inPlanetSince + ss.optLong("arriveAfterMs", 0);

		planetName = ss.optString("planetName");

		targetPlanetName = ss.optString("targetPlanetName");

		JSONObject mypackage = ss.optJSONObject("pack");
		if( mypackage != null )
		{
			pack = new Package(mypackage, true).packageId;
		}

		Galaxy.ships.put(getUniqueId(), this);
	}

	public String getUniqueId()
	{
		return teamName + "-" + shipNum.toString();
	}
}
