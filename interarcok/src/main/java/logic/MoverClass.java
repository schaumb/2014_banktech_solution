package logic;

import java.util.concurrent.atomic.AtomicLong;

import communication.Communication;
import container.MySpaceShips;

public class MoverClass
{
	MySpaceShips mss;
	static final public AtomicLong time = new AtomicLong();


	public MoverClass()
	{
		Communication.getGalaxy();
		mss = Communication.whereIs();
		Communication.whereAre();
		Selector.recalculatePTS();
	}

	public void Go() throws InterruptedException
	{
		mss.doLogicStuff();
	}
}
