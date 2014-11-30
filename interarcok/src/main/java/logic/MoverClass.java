package logic;

import communication.Communication;
import communication.Loggers;
import container.Galaxy;
import container.MySpaceShips;

public class MoverClass
{
	MySpaceShips mss;
	public int owning = 0;
	private Long lastGotGalaxy;
	private Long lastGotWhereAre;
	private Integer oldGalaxyData = 700;
	private Integer oldAreData = 500;


	public MoverClass()
	{
		Communication.getGalaxy();
		lastGotGalaxy = System.currentTimeMillis();
		mss = Communication.whereIs();
		Communication.whereAre();
		lastGotWhereAre = System.currentTimeMillis();
	}

	private void printStatistic()
	{
		System.out.println("Statistic:");
		System.out.println("Packages num: " + Galaxy.packages.size());
		System.out.println("Planets num: " + Galaxy.planets.size());
		System.out.println("Ships num: " + Galaxy.ships.size());
		System.out.println("Teams num: " + Galaxy.teams.size() + ", names:");
		for(String s : Galaxy.teams.keySet())
		{
			System.out.println(s);
		}
	}

	public void Go() throws InterruptedException
	{
		printStatistic();
		long reallyWait = 0;
		//while(true)
		{
			Integer minWaiting = mss.doLogicStuff(reallyWait);

			Long now = System.currentTimeMillis();

			// if fresh data-s, and need to wait
			if(now - lastGotGalaxy < oldGalaxyData &&
				now - lastGotWhereAre < oldAreData &&
				minWaiting > 202)
			{
				int sleeping = minWaiting - 202;
				Thread.sleep(sleeping);
			}

			Long now1 = System.currentTimeMillis();
			if(now1 - lastGotGalaxy > oldGalaxyData || minWaiting >= 202)
			{
				Communication.getGalaxy();
				lastGotGalaxy = System.currentTimeMillis();
			}

			Long now2 = System.currentTimeMillis();
			if(now2 - lastGotWhereAre > oldAreData || minWaiting >= 101)
			{
				Communication.whereAre();
				lastGotWhereAre = System.currentTimeMillis();
			}
			Long now3 = System.currentTimeMillis();

			reallyWait = now3 - now;

			Loggers.logLogger.info("MinWaiting : " + minWaiting + " reallyWait : " + reallyWait);
		}
	}
}
