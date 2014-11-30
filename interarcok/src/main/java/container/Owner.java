package container;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Owner
{
	String name;

	public abstract boolean areWe();
	public abstract ArrayList<SpaceShip> ships();
	public LinkedList<Planet> claimPlanets()
	{
		return new LinkedList<Planet>();
	}

}
