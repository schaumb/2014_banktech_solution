package container;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Galaxy
{
	public static ConcurrentHashMap<Integer, Package> packages = new ConcurrentHashMap<Integer, Package>();
	public static ConcurrentHashMap<String, Planet> planets = new ConcurrentHashMap<String, Planet>();
	public static ConcurrentHashMap<String, Owner> teams = new ConcurrentHashMap<String, Owner>();
	public static ConcurrentHashMap<String, SpaceShip> ships = new ConcurrentHashMap<String, SpaceShip>();

	static
	{
		packages.put(null, null);
		planets.put(null, null);
		teams.put(null, null);
		ships.put(null, null);
	}
	public static void parsePlanets( JSONObject job ) throws JSONException
	{
		JSONArray pls = job.getJSONArray("planets");
		for( int i = 0; i < pls.length() ; ++i )
		{
			JSONObject planet = pls.getJSONObject(i);

			new Planet(planet);
		}
	}

	public static void parseTheySpaceShips(JSONObject job)  throws JSONException
	{
		JSONArray pls = job.getJSONArray("userPositions");
		for( int i = 0; i < pls.length() ; ++i )
		{
			JSONObject planet = pls.getJSONObject(i);

			new TheySpaceShips(planet);
		}
	}

	public static Package getPackage(Integer i)
	{
		return packages.get(i);
	}
	public static Planet getPlanet(String i)
	{
		return planets.get(i);
	}
	public static Owner getTeam(String i)
	{
		return teams.get(i);
	}
	public static SpaceShip getSpaceShip(String i)
	{
		return ships.get(i);
	}
}
