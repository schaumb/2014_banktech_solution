package container;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

import logic.PlanetGraph;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Galaxy
{
	public static class Package
	{
		public Package(JSONObject pack)
		{
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
		Integer fee;

		public double pathLength()
		{
			return origin.distance(target);
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

		public Integer getFee() {
			return fee;
		}


		@Override
		public int hashCode() {
			return packageId;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || !(obj instanceof Package))
				return false;

			Package other = (Package) obj;
			return Objects.equals(packageId, other.packageId);
		}
	}

	public static class Planet
	{
		public class PackageSort implements Comparator<Package>
		{
			@Override
			public int compare(Package o1, Package o2) {
				double d1 = PlanetGraph.getDist(Planet.this, o1.getTarget());
				double d2 = PlanetGraph.getDist(Planet.this, o2.getTarget());
				if( d1 != d2 ) return (int)Math.signum(d1 - d2);
				return o1.packageId.compareTo(o2.packageId);
			}
		}

		public Planet(JSONObject planet)
		{
			name = planet.getString("name");
			coord = new Point2D.Double(
					planet.getDouble("x"),
					planet.getDouble("y"));
		}

		String name;
		Point2D coord;
		public TreeSet<Package> packages = new TreeSet<Package>(new PackageSort());

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

			PlanetGraph.setDistances(this.planets.values());

			for( Package e : packages.values() )
			{
				e.origin = planets.get(e.origin_s);
				e.target = planets.get(e.target_s);

				e.origin.packages.add(e);
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
