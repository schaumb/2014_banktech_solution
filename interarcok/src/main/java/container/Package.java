package container;

import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Package
{
	public Package(JSONObject pack, boolean isMoveing) throws JSONException
	{
		this.isMoveing = isMoveing;
		packageId = pack.getInt("packageId");
		lastPlanet = Galaxy.planets.get(pack.getString("lastPlanet"));

		String tmp = pack.optString("lastOwner");
		if( tmp != null && !tmp.equals("null") )
		{
			lastOwner = Galaxy.teams.get(tmp);

			if(!isMoveing)
			{
				lastPlanet.owned = lastOwner;
			}
		}

		Galaxy.packages.put(packageId, this);
	}

	boolean isMoveing;
	Integer packageId;
	Planet lastPlanet;
	Owner lastOwner = null;

	public Integer getPackageId()
	{
		return packageId;
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
