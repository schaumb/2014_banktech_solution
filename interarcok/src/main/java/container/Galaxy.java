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
}
