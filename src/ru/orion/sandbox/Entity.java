package ru.orion.sandbox;
import com.bulletphysics.dynamics.*;
public abstract class Entity {
	public abstract void update();
	private RigidBody body;
	public RigidBody getBody() {
		return body;
	}
	public void setBody(RigidBody body) {
		this.body = body;
	}
}
