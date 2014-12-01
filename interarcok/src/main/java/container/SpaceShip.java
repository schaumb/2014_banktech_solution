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
		shipNum = ss.getInt("shipNum");

		inPlanetSince = System.currentTimeMillis();
		arriveWhen = inPlanetSince + ss.optLong("arriveAfterMs", 0);

		String tmp;
		tmp = ss.optString("planetName");
		if( tmp != null )
		{
			planet = Galaxy.planets.get(tmp);
		}

		SpaceShip oldMe = Galaxy.ships.get(getUniqueId());
		if(oldMe != null)
		{
			if(oldMe.arriveWhen < arriveWhen)
			{
				oldMe.arriveWhen = arriveWhen;
			}

			if(!Objects.equals(oldMe.planet,planet))
			{
				oldMe.inPlanetSince = inPlanetSince;
				oldMe.planet = planet;
			}

			JSONObject mypackage = ss.optJSONObject("pack");
			if( mypackage != null )
			{
				Package op = Galaxy.packages.get(new Package(mypackage, true).packageId);

				if(!op.equals(oldMe.pack))
				{
					if( oldMe.pack != null )
					{
						oldMe.pack.isMoveing.set(false);
						oldMe.pack.lastOwner = team;
						oldMe.pack.lastPlanet = oldMe.targetPlanet;
						oldMe.pack.lastPlanet.pkgs.add(oldMe.pack);
					}
					oldMe.pack = op;
				}

			}
			else if( oldMe.pack != null )
			{
				oldMe.pack.isMoveing.set(false);
				oldMe.pack.lastOwner = team;
				oldMe.pack.lastPlanet = oldMe.targetPlanet;
				oldMe.pack.lastPlanet.pkgs.add(oldMe.pack);
			}

			tmp = ss.optString("targetPlanetName");
			if( tmp != null )
			{
				oldMe.targetPlanet = Galaxy.planets.get(tmp);
			}

		}
		else
		{
			inPlanetSince = System.currentTimeMillis();
			arriveWhen = inPlanetSince + ss.optLong("arriveAfterMs", 0);

			tmp = ss.optString("planetName");
			if( tmp != null )
			{
				planet = Galaxy.planets.get(tmp);
			}

			tmp = ss.optString("targetPlanetName");
			if( tmp != null )
			{
				targetPlanet = Galaxy.planets.get(tmp);
			}

			JSONObject mypackage = ss.optJSONObject("pack");
			if( mypackage != null )
			{
				pack = new Package(mypackage, true);
			}

			Galaxy.ships.put(getUniqueId(), this);
		}
	}

	public String getUniqueId()
	{
		return team.name + "-" + shipNum.toString();
	}
}
