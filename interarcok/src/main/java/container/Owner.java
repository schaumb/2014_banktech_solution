package container;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Owner
{
	String name;
	int remainingMines = 0;

	public abstract boolean areWe();
	public abstract ArrayList<SpaceShip> ships();

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Owner))
			return false;

		Owner other = (Owner) obj;
		return Objects.equals(name, other.name);
	}
}
