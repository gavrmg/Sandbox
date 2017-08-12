package ru.orion.sandbox;

import java.util.ArrayList;
import com.bulletphysics.dynamics.*;
public class Scene {
	private ArrayList<Entity> Entities;
	private DiscreteDynamicsWorld DynWorld;
	public Scene() {
	//	World = new DiscreteDynamicsWorld();
	}

	public ArrayList<Entity> getEntities() {
		return Entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		Entities = entities;
	}

	public DiscreteDynamicsWorld getWorld() {
		return DynWorld;
	}

	public void setWorld(DiscreteDynamicsWorld world) {
		DynWorld = world;
	}
}
