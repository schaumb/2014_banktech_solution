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
		Communication.getGalaxy();
		Communication.whereAre();
		Selector.recalculatePTS();
		for(int i = 0; i < 2; ++i)
		{
			Long minWaitingFor = mss.doLogicStuff();

			Long now = System.currentTimeMillis();
			Long minWaiting = minWaitingFor - now;

			// if fresh data-s, and need to wait
			if(minWaiting > 202)
			{
				Thread.sleep(minWaiting - 202);
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
				Selector.recalculatePTS();
				lastGotWhereAre = System.currentTimeMillis();
			}
			Loggers.logLogger.info("MinWaiting : " + minWaiting + " reallyWait : " + (System.currentTimeMillis()-now));
			//if((System.currentTimeMillis()-now) == 0)
			//	break;
		}
	}
}
