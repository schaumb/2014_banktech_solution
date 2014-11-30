package logic;

import communication.Communication;
import container.Galaxy;
import container.MySpaceShips;

public class MoverClass
{
	MySpaceShips mss;
	public int owning = 0;

	public MoverClass()
	{
		Communication.getGalaxy();
		mss = Communication.whereIs();
		Communication.whereAre();
	}

	public void Go()
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
}
