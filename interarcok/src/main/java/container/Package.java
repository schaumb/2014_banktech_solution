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
		Package oldMe = Galaxy.packages.get(packageId);

		if(oldMe != null)
		{

			String tmp = pack.optString("lastOwner");
			if( tmp != null )
			{
				oldMe.lastOwner = Galaxy.teams.get(tmp);
			}

			lastPlanet = Galaxy.planets.get(pack.getString("lastPlanet"));

			if(!lastPlanet.equals(oldMe.lastPlanet))
			{
				oldMe.lastPlanet.pkgs.remove(this);

				if(!isMoveing)
				{
					lastPlanet.pkgs.add(this);
					lastPlanet.owned = oldMe.lastOwner;
				}
				oldMe.lastPlanet = lastPlanet;
			}
			else if(isMoveing && !oldMe.isMoveing.get())
			{
				oldMe.lastPlanet.pkgs.remove(this);
			}

			oldMe.isMoveing.set(isMoveing);
		}
		else
		{
			this.isMoveing.set(isMoveing);
			lastPlanet = Galaxy.planets.get(pack.getString("lastPlanet"));

			String tmp = pack.optString("lastOwner");
			if( tmp != null )
			{
				lastOwner = Galaxy.teams.get(tmp);

				if(!isMoveing)
				{
					lastPlanet.owned = lastOwner;
				}
			}

			Galaxy.packages.put(packageId, this);
		}
	}

	public AtomicBoolean isMoveing = new AtomicBoolean(); //  ha mozgasba van, vagy ha epp kivalasztottuk felvetelre!
	final Integer packageId;
	public Planet lastPlanet;
	public Owner lastOwner = null;

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
