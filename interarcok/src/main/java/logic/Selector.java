package logic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

import container.ControllableSpaceShip;
import container.Galaxy;
import container.MySpaceShips;
import container.Planet;
import container.Package;
import container.SpaceShip;

public class Selector
{
	public static class SpaceShipComparator implements Comparator<String>
	{
		@Override
		public int compare(String o1, String o2)
		{
			SpaceShip s1 = Galaxy.getSpaceShip(o1);
			SpaceShip s2 = Galaxy.getSpaceShip(o2);
			int time = s1.arriveWhen.compareTo(s2.arriveWhen);
			if(time != 0) return time;
			return s1.getUniqueId().compareTo(s2.getUniqueId());
		}
	}

	static HashMap<String,TreeSet<String>> planet_arrivers_with_package;
	public static HashMap<String,TreeSet<String>> planet_arrivers_without_package;
	/*static HashMap<Planet,TreeSet<SpaceShip>> there;*/

	static double PriorityOfNobodysPlanet = 100;
	static double PriorityOfNobodysPackage = 1000;
	public static Random rand = new Random(System.currentTimeMillis());

	public static void recalculatePTS()
	{
		planet_arrivers_with_package = new HashMap<String,TreeSet<String>>();
		planet_arrivers_without_package = new HashMap<String,TreeSet<String>>();
		long now = System.currentTimeMillis();
		for(Entry<String, SpaceShip> ss : Galaxy.ships.entrySet())
		{
			if(ss.getValue().teamName.equals(MySpaceShips.teamName)) continue;

			if(ss.getValue().arriveWhen > now && ss.getValue().targetPlanetName != null)
			{
				String who = ss.getValue().targetPlanetName;
				TreeSet<String> tsss;
				if(ss.getValue().pack == null)
				{
					tsss = planet_arrivers_without_package.get(who);

					if(tsss == null)
					{
						tsss = new TreeSet<String>(new SpaceShipComparator());
						planet_arrivers_without_package.put(who, tsss);
					}
				}
				else
				{
					tsss = planet_arrivers_with_package.get(who);

					if(tsss == null)
					{
						tsss = new TreeSet<String>(new SpaceShipComparator());
						planet_arrivers_with_package.put(who, tsss);
					}
				}


				tsss.add(ss.getValue().getUniqueId());
			}
		}
	}

	public static Planet calculateNext(ControllableSpaceShip css)
	{
		boolean hasPackage = css.pack != null;

		long now = System.currentTimeMillis();
		// vagy van csomagunk (amit nem tudunk letenni), vagy van felvehető csomag a jelenlegi bolygón
		if(hasPackage)
		{
			System.out.println(css.getUniqueId() + " keresunk leteendo bolygot");
			// eloszor keresunk egy ures bolygot, ahova van eselyunk erkezni,
			// ezek kozul a legkisebbet valasztjuk
			Planet pmin = null;
			double minDist = Double.POSITIVE_INFINITY;

			for(Planet pl : Galaxy.planets.values())
			{
				if(		pl.ownerName == null &&
						pl.claim == null &&
						!pl.name.equals(css.planetName) &&
						!pl.name.equals(Galaxy.getPackage(css.pack).lastPlanetName))
				{
					TreeSet<String> tsssw = planet_arrivers_without_package.get(pl.name);
					TreeSet<String> tsss = planet_arrivers_with_package.get(pl.name);
					double d = now + (long)(pl.distance(Galaxy.getPlanet(css.planetName)) / SpaceShip.speedWithtPackage + 1);

					if( 	(tsss == null ||
								Galaxy.getSpaceShip(tsss.first()).arriveWhen > d )
							)
					{

						double dist = pl.distance(Galaxy.getPlanet(css.planetName));
						if(		pl.pkgs.size() > 0 &&
								Galaxy.getPackage(pl.pkgs.get(0)).lastOwnerName == null &&
								(tsssw == null || Galaxy.getSpaceShip(tsssw.first()).arriveWhen > d) &&
								Galaxy.getPackage(pl.pkgs.get(0)).claim == null)
						{
							dist -= 5;
						}

						if(dist < minDist)
						{
							minDist = dist;
							pmin = pl;
						}
					}
				}
			}

			if(pmin != null)
			{
				if(pmin.pkgs.size() > 0)
				{
					Galaxy.getPackage(pmin.pkgs.get(0)).claim = css.getUniqueId();
					pmin.claim = css.getUniqueId();
				}

				System.out.println(css.getUniqueId() + " go to " + pmin.getName() + " elv le lehet tenni + " + (pmin.pkgs.size() == 1));
				return pmin;
			}

			// ha nem sikerult, akkor probaljunk meg valakit kovetni
		}
		// ha nincs nálunk csomag, és nem is található a jelenlegi bolygónkon
		else
		{
			System.out.println(css.getUniqueId() + " keresunk felvehető csomagot");
			// keressünk egy felvehető package-t
			// ha van
			//Planet pmin = null;
			Package pkg = null;
			double minDist = Double.POSITIVE_INFINITY;
			for(Package pa : Galaxy.packages.values())
			{
				if( 	(pa.lastOwnerName == null || !pa.lastOwnerName.equals(MySpaceShips.teamName) ) &&
						pa.claim == null &&
						!pa.isMoveing.get() &&
						!Galaxy.getPlanet(pa.lastPlanetName).hasMine.get() &&
						!pa.lastPlanetName.equals(css.planetName) )
				{
					TreeSet<String> tsssw = planet_arrivers_without_package.get(pa.lastPlanetName);
					TreeSet<String> tsss = planet_arrivers_with_package.get(pa.lastPlanetName);
					double d = now + (long)(Galaxy.getPlanet(pa.lastPlanetName).distance(Galaxy.getPlanet(css.planetName)) / SpaceShip.speed + 1);
					if(		(tsssw == null ||
								(Galaxy.getSpaceShip(tsssw.first()).arriveWhen > d ||
								(Galaxy.getPlanet(pa.lastPlanetName).pkgs.size() > 1 &&
								(tsssw.size() < 2 || ((SpaceShip)tsssw.toArray()[1]).arriveWhen > d)
							))
							))
					{
						double dist = Galaxy.getPlanet(pa.lastPlanetName).distance(Galaxy.getPlanet(css.planetName));
						if( Galaxy.getPlanet(pa.lastPlanetName).pkgs.size() > 1 )
						{
							dist -= (pa.lastOwnerName == null) ? 5 : 2;
						}
						else if(pa.lastOwnerName == null)
						{
							dist -= 10;
						}

						if( tsss != null && Galaxy.getSpaceShip(tsss.first()).arriveWhen < d )
						{
							dist += 5;
						}

						if(dist < minDist)
						{
							minDist = dist;
							//pmin = pa.lastPlanet;
							pkg = pa;
						}
					}
				}
			}
			if(pkg != null)
			{
				pkg.claim = css.getUniqueId();
				System.out.println(css.getUniqueId() + " go to " + Galaxy.getPlanet(pkg.lastPlanetName).getName() + " van felveheto csomag " + pkg.getPackageId());
				return Galaxy.getPlanet(pkg.lastPlanetName);
				// return pmin;
			}
			// ha nincs, követünk valakit
		}
		/*
		// koveto modszer.
		System.out.println(css.getUniqueId() + " kovessunk valakit");

		SpaceShip ssmin = null;
		long minTimeDist = Long.MAX_VALUE;
		double speed = hasPackage ? SpaceShip.speedWithtPackage : SpaceShip.speed;
		long saveArriveWhen = css.arriveWhen;

		for(Entry<Planet, TreeSet<SpaceShip>> pc : planet_arrivers_without_package.entrySet())
		{
			if(		css.planet.equals(pc.getKey()) ||
					(css.pack != null && css.pack.lastPlanet.equals(pc.getKey())) &&
					pc.getKey().claim != null)
				continue;

			css.arriveWhen = now + (long)(pc.getKey().distance(css.planet) / speed + 1);

			SpaceShip ss = pc.getValue().lower(css);
			if(ss != null)
			{
				long dist = css.arriveWhen - ss.arriveWhen;
				if(dist < minTimeDist)
				{
					minTimeDist = dist;
					ssmin = ss;
				}
			}
		}
		css.arriveWhen = saveArriveWhen;


		if(ssmin != null) // követjük ss-t
		{
			System.out.println(css.getUniqueId() + " go to " + ssmin.targetPlanet.getName() + " kovesd " + ssmin.getUniqueId());
			return ssmin.targetPlanet;
		}

		// hmm. nem tudunk senkit követni, és be fognak minket mindenhol előzni.



		// ha szerzünk csomagot menjünk egy olyan helyre, ahol van (nem lefagyott egyén) üresen áll más bolygója felett ( vagy előttünk van )
		if(hasPackage)
		{
			double minCost = Double.POSITIVE_INFINITY;
			SpaceShip mss = null;
			for(SpaceShip ss : Galaxy.ships.values())
			{
				if(		!ss.team.areWe() &&
						ss.pack == null &&
						ss.planet != null &&
						ss.planet.pkgs.size() == 1 &&
						!ss.team.equals(ss.planet.owned) &&
						ss.arriveWhen - ss.inPlanetSince < 550 &&
						!ss.planet.equals(css.planet) &&
						!ss.planet.equals(css.pack.lastPlanet)
					)
				{
					TreeSet<SpaceShip> tsss = planet_arrivers_with_package.get(ss.planet);

					double c = now + (long)(ss.planet.distance(css.planet) / SpaceShip.speedWithtPackage + 1);

					if(		(tsss == null ||
							tsss.first().arriveWhen < c ))
					{
						c += 10;
					}
					if(c < minCost)
					{
						minCost = c;
						//pmin = pa.lastPlanet;
						mss = ss;
					}
				}
			}
			if(mss != null)
			{
				System.out.println(css.getUniqueId() + " go to " + mss.planet.getName() + " kovesd2 " + mss.getUniqueId());
				return mss.planet;
			}
		}
		// ha nincs csomagunk menjünk el egy olyan bolygóra, ami nem a mienk, és van rajta csomag
		else
		{
			for(Planet p : Galaxy.planets.values())
			{
				if(		p.owned != null &&
						!p.owned.areWe() &&
						!p.equals(css.planet) &&
						p.pkgs.size() > 0
						)
				{
					System.out.println(css.getUniqueId() + " go to " + p.getName() + " because there's a package ");
					return p;
				}
			}
		}

		// getRand bolygó
		Planet prand = css.planet;
		while(css.planet.equals(prand) ||
				(css.pack != null && css.pack.lastPlanet.equals(prand)))
		{

			int nth = rand.nextInt(Galaxy.planets.size());
			prand = (Planet) Galaxy.planets.values().toArray()[nth];
		}
		System.out.println(css.getUniqueId() + " go to " + prand.getName() + " because rand ");
		return prand;*/
		return null;
	}

}
