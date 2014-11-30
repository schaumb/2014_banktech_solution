package logic;

import communication.Communication;
import container.MySpaceShips;

public class MoverClass
{
	Communication c;
	MySpaceShips mss;
	public int owning = 0;

	public MoverClass(Communication c)
	{
		this.c = c;
		c.getGalaxy();
		mss = c.whereIs();
		c.whereAre();
	}

	public void Go()
	{

	}
}
