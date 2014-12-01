package container;

import java.util.ArrayList;

public abstract class Owner
{
	String name;
	int remainingMines = 0;

	public abstract boolean areWe();
	public abstract ArrayList<SpaceShip> ships();

}
