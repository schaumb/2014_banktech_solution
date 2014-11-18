package container;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Objects;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Galaxy
{
	public static class Package implements Comparable<Package>
	{
		public Package(JSONObject pack)
		{
			System.out.println(pack.toString());

			System.out.println("Gotted package ID : " + pack.getInt("packageId"));
			packageId = pack.getInt("packageId");
			origin_s = pack.getString("originalPlanet");
			target_s = pack.getString("targetPlanet");
			fee = pack.getInt("fee");
		}

		Integer packageId;
		Planet origin;
		Planet target;
		String origin_s;
		String target_s;
		public Integer fee;

		public double pathLength()
		{
			return origin.distance(target);
		}

		public double idleLength(Package p2)
		{
			return target.distance(p2.origin);
		}

		public Planet getOrigin() {
			return origin;
		}

		public Planet getTarget() {
			return target;
		}

		public Integer getPackageId() {
			return packageId;
		}

		@Override
		public int hashCode() {
			final int prime = 71;
			int result = 1;
			result = prime * result
					+ ((origin_s == null) ? 0 : origin_s.hashCode());
			result = prime * result
					+ ((packageId == null) ? 0 : packageId.hashCode());
			result = prime * result
					+ ((target_s == null) ? 0 : target_s.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || !(obj instanceof Package))
				return false;

			Package other = (Package) obj;
			if (!Objects.equals(origin_s, other.origin_s) ||
					!Objects.equals(packageId, other.packageId) ||
					!Objects.equals(target_s, other.target_s))
				return false;

			return true;
		}

		@Override
		public int compareTo(Package o) {
			return packageId.compareTo(o.packageId);
		}
	}

	public static class Planet implements Comparable<Planet>
	{
		public Planet(JSONObject planet)
		{
			name = planet.getString("name");
			coord = new Point2D.Double(
					planet.getDouble("x"),
					planet.getDouble("y"));
		}

		String name;
		Point2D coord;

		public Double distance(Planet p2)
		{
			return coord.distance(p2.coord);
		}

		public String getName() {
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

		@Override
		public int compareTo(Planet o) {
			return name.compareTo(o.name);
		}


	}

	public HashMap<Integer, Package> packages = new HashMap<Integer, Package>();
	public HashMap<String,Planet> planets = new HashMap<String,Planet>();

	public Galaxy( JSONObject job )
	{
		try
		{
			JSONArray pls = job.getJSONArray("planets");
			for( int i = 0; i < pls.length() ; ++i )
			{
				JSONObject planet = pls.getJSONObject(i);

				Planet newPlanet = new Planet(planet);
				planets.put(newPlanet.name, newPlanet);

				JSONArray pks = planet.getJSONArray("packages");
				for( int j = 0; j < pks.length() ; ++j )
				{
					JSONObject pack = pks.getJSONObject(j);
					Package newPackage = new Package(pack);

					packages.put(newPackage.packageId, newPackage);
				}
			}
			for( Package e : packages.values() )
			{
				e.origin = planets.get(e.origin_s);
				e.target = planets.get(e.target_s);
			}
			System.out.println("Planets size: " + planets.size());
			System.out.println("Packages size: " + packages.size());
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}
