package container;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

public class Package
{
	public String claim = null;

	public Package(JSONObject pack, boolean isMoveing) throws JSONException
	{
		packageId = pack.getInt("packageId");

		this.isMoveing.set(isMoveing);
		lastPlanetName = pack.getString("lastPlanet");

		lastOwnerName = pack.optString("lastOwner");

		Galaxy.packages.put(packageId, this);
	}

	public AtomicBoolean isMoveing = new AtomicBoolean(); //  ha mozgasba van, vagy ha epp kivalasztottuk felvetelre!
	final Integer packageId;
	public String lastPlanetName;
	public String lastOwnerName;

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
