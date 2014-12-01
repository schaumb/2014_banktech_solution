package logic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

import container.ControllableSpaceShip;
import container.Galaxy;
import container.Planet;
import container.Package;
import container.SpaceShip;

public class Selector
{
	public static class SpaceShipComparator implements Comparator<SpaceShip>
	{
		@Override
		public int compare(SpaceShip o1, SpaceShip o2)
		{
			int time = o1.arriveWhen.compareTo(o2.arriveWhen);
			if(time != 0) return time;
			return o1.getUniqueId().compareTo(o2.getUniqueId());
		}
	}

	static HashMap<Planet,TreeSet<SpaceShip>> planet_arrivers_with_package;
	public static HashMap<Planet,TreeSet<SpaceShip>> planet_arrivers_without_package;
	/*static HashMap<Planet,TreeSet<SpaceShip>> there;*/
	static double PriorityOfNobodysPlanet = 100;
	static Random rand = new Random(System.currentTimeMillis());

	public static void recalculatePTS()
	{
		planet_arrivers_with_package = new HashMap<Planet,TreeSet<SpaceShip>>();
		planet_arrivers_without_package = new HashMap<Planet,TreeSet<SpaceShip>>();
		long now = System.currentTimeMillis();
		for(Entry<String, SpaceShip> ss : Galaxy.ships.entrySet())
		{
			if(ss.getValue().team.areWe()) continue;

			if(ss.getValue().arriveWhen > now && ss.getValue().targetPlanet != null)
			{
				Planet who = ss.getValue().targetPlanet;
				TreeSet<SpaceShip> tsss;
				if(ss.getValue().pack == null)
				{
					tsss = planet_arrivers_without_package.get(who);

					if(tsss == null)
					{
						tsss = new TreeSet<SpaceShip>(new SpaceShipComparator());
						planet_arrivers_without_package.put(who, tsss);
					}
				}
				else
				{
					tsss = planet_arrivers_with_package.get(who);

					if(tsss == null)
					{
						tsss = new TreeSet<SpaceShip>(new SpaceShipComparator());
						planet_arrivers_with_package.put(who, tsss);
					}
				}


				tsss.add(ss.getValue());
			}
			/*
			else if(ss.getValue().planet != null)
			{
				Planet who = ss.getValue().planet;
				TreeSet<SpaceShip> tsss = there.get(who);

				if(tsss == null)
				{
					tsss = new TreeSet<SpaceShip>(new SpaceShipComparator());
					pts.put(who, tsss);
				}

				tsss.add(ss.getValue());
			}*/
		}
	}

	public static Planet calculateNext(ControllableSpaceShip css)
	{
		boolean containsNobodysPackage = css.planet.containsNobodysPackage();
		boolean hasPickPack = containsNobodysPackage ||
				((css.planet.owned == null || !css.planet.owned.areWe()) && css.planet.pkgs.size() > 0 && !css.planet.pkgs.get(0).isMoveing);
		boolean claimToPackage = hasPickPack && css.pack == null;

		long now = System.currentTimeMillis();
		// vagy van csomagunk (amit nem tudunk letenni), vagy van felvehető csomag a jelenlegi bolygón
		if((css.pack != null && css.planet.owned != null) ||
				claimToPackage)
		{
			System.out.println(css.getUniqueId() + " keresunk leteendo bolygot");
			// eloszor keresunk egy ures bolygot, ahova van eselyunk erkezni,
			// ezek kozul a legkisebbet valasztjuk
			Planet pmin = null;
			double minDist = Double.POSITIVE_INFINITY;

			for(Entry<String, Planet> p : Galaxy.planets.entrySet())
			{
				Planet pl = p.getValue();
				TreeSet<SpaceShip> tsss = planet_arrivers_with_package.get(pl);
				if( pl.owned == null &&
						!pl.equals(css.planet) &&
						(css.pack == null || !css.pack.lastPlanet.equals(pl)) &&
						(tsss == null ||
							tsss.first().arriveWhen >
							now + (long)(pl.distance(css.planet) / SpaceShip.speed + 1) )
						)
				{
					double dist = pl.distance(css.planet);
					if(dist < minDist)
					{
						minDist = dist;
						pmin = pl;
					}
				}
			}
			if(claimToPackage)
			{
				if(containsNobodysPackage)
				{
					css.planet.setNobodysPackageToMove();
				}
				else
				{
					css.planet.pkgs.get(0).isMoveing = true;
				}
			}

			if(pmin != null)
			{
				System.out.println(css.getUniqueId() + " go to " + pmin.getName() + " van csomag v felv");
				return pmin;
			}

			// ha nem sikerult, akkor probaljunk meg valakit kovetni
		}
		// ha nincs nálunk csomag, és nem is található a jelenlegi bolygónkon
		// vagy letesszük, és (feltételezve) felveszünk
		if( (css.pack == null && !hasPickPack) ||
				(css.pack != null && css.planet.owned == null && css.pack.lastPlanet != css.planet) )
		{
			System.out.println(css.getUniqueId() + " keresunk felvehetp csomagot");
			// keressünk egy felvehető package-t
			// ha van
			//Planet pmin = null;
			Package pkg = null;
			double minDist = Double.POSITIVE_INFINITY;
			for(Entry<Integer, Package> p : Galaxy.packages.entrySet())
			{
				Package pa = p.getValue();
				if( pa.lastOwner == null &&
						!pa.isMoveing )
				{

					TreeSet<SpaceShip> tsss = planet_arrivers_without_package.get(pa.lastPlanet);

					if(!pa.lastPlanet.hasmine &&
						!pa.lastPlanet.equals(css.planet) &&
							(tsss == null ||
							tsss.first().arriveWhen >
							now + (long)(pa.lastPlanet.distance(css.planet) / SpaceShip.speed + 1)) )
					{
						double dist = pa.lastPlanet.distance(css.planet) - (pa.lastPlanet.owned==null ? PriorityOfNobodysPlanet : 0 );
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
				System.out.println(css.getUniqueId() + " go to " + pkg.lastPlanet.getName() + " van felveheto csomag " + pkg.getPackageId());
				pkg.isMoveing = true;
				return pkg.lastPlanet;
				// return pmin;
			}
			// ha nincs, követünk valakit
		}

		// koveto modszer.
		System.out.println(css.getUniqueId() + " kovessunk valakit");

		SpaceShip ssmin = null;
		long minTimeDist = Long.MAX_VALUE;
		boolean maybeWithPackage = hasPickPack || css.pack != null;

		double speed = maybeWithPackage ? SpaceShip.speedWithtPackage : SpaceShip.speed;

		long saveArriveWhen = css.arriveWhen;

		for(Entry<Planet, TreeSet<SpaceShip>> pc : planet_arrivers_without_package.entrySet())
		{
			if(css.planet.equals(pc.getKey()) ||
					(css.pack != null && css.pack.lastPlanet.equals(pc.getKey())))
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

		if(claimToPackage)
		{
			if(containsNobodysPackage)
			{
				css.planet.setNobodysPackageToMove();
			}
			else
			{
				css.planet.pkgs.get(0).isMoveing = true;
			}
		}

		if(ssmin != null) // követjük ss-t
		{
			System.out.println(css.getUniqueId() + " go to " + ssmin.targetPlanet.getName() + " kovesd " + ssmin.getUniqueId());
			return ssmin.targetPlanet;
		}

		// hmm. nem tudunk senkit követni, és be fognak minket mindenhol előzni.



		// ha szerzünk csomagot menjünk egy olyan helyre, ahol van (nem lefagyott egyén) üresen áll más bolygója felett ( vagy előttünk van )

		// ha nincs csomagunk menjünk el egy olyan bolygóra, ami nem a mienk

		Planet prand = css.planet;

		if(css.pack == null && !hasPickPack)
		{
			for(Planet p : Galaxy.planets.values())
			{
				if(p.owned == null || !p.owned.areWe())
				{
					prand = p;
					break;
				}
			}
		}

		// getRand bolygó
		while(css.planet.equals(prand) ||
				(css.pack != null && css.pack.lastPlanet.equals(prand)))
		{

			int nth = rand.nextInt(Galaxy.planets.size());
			prand = (Planet) Galaxy.planets.values().toArray()[nth];
		}
		System.out.println(css.getUniqueId() + " go to " + prand.getName() + " because rand ");
		return prand;
	}

}
