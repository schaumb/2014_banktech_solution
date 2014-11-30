package container;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Planet
{
	public Planet(JSONObject planet) throws JSONException
	{
		name = planet.getString("name");
		coord = new Point2D.Double(
				planet.getDouble("x"),
				planet.getDouble("y"));

		Galaxy.planets.put(name, this);

		JSONArray pks = planet.optJSONArray("packages");
		if(pks != null)
		{
			for( int j = 0; j < pks.length() ; ++j )
			{
				JSONObject pack = pks.getJSONObject(j);

				pkgs.add(new Package(pack, false));
			}
		}
	}

	String name;
	Point2D coord;
	public Owner owned;
	Boolean hasmine = false;
	ArrayList<Package> pkgs = new ArrayList<Package>();

	public Double distance(Planet p2)
	{
		return coord.distance(p2.coord);
	}

	public String getName()
	{
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Planet))
			return false;

		Planet other = (Planet) obj;
		if (!Objects.equals(coord, other.coord) || !Objects.equals(name, other.name))
			return false;

		return true;
	}
}
