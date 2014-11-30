package container;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Galaxy
{
	public static HashMap<Integer, Package> packages = new HashMap<Integer, Package>();
	public static HashMap<String, Planet> planets = new HashMap<String, Planet>();
	public static HashMap<String, Owner> teams = new HashMap<String, Owner>();
	public static HashMap<String, SpaceShip> ships = new HashMap<String, SpaceShip>();

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
