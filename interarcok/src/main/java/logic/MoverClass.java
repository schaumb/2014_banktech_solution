package logic;

import communication.Communication;
import container.Galaxy;
import container.MySpaceShips;

public class MoverClass
{
	MySpaceShips mss;


	public MoverClass()
	{
		Communication.getGalaxy();
		Communication.whereIs();
		Communication.whereAre();
		Selector.recalculatePTS();
	}

	private void printStatistic()
	{
		System.out.println("Statistic:");
		System.out.println("Packages num: " + Galaxy.packages.size());
		System.out.println("Planets num: " + Galaxy.planets.size());
		System.out.println("Ships num: " + Galaxy.ships.size());
		System.out.println("Teams num: " + Galaxy.teams.size() + ", names:");
	}

	public void consistency() throws InterruptedException
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				try
				{
					while(true)
					{
						Communication.whereAre();
						Selector.recalculatePTS();
						Thread.sleep(500);
					}
				}
				catch (InterruptedException e)
				{}
			}}).start();
	}

	public void Go() throws InterruptedException
	{
		mss.doLogicStuff();

		consistency();

		while(true)
		{
			Communication.getGalaxy();
			Thread.sleep(2020);
			printStatistic();
		}
	}
}
