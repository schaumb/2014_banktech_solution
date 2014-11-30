package container;

import java.util.ArrayList;

public abstract class Owner
{
	String name;

	abstract boolean areWe();
	abstract ArrayList<SpaceShip> ships();

}
