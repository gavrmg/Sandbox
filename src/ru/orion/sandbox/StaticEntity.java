package ru.orion.sandbox;
import com.bulletphysics.dynamics.*;

import ru.orion.sandbox.Renderer.Mesh;

/*
 * A basic class for static geometry
 */

public class StaticEntity {
	
	public void update() {};
	private RigidBody body;
	private Mesh mesh;
	public StaticEntity(RigidBody body) {
		this.body = body;
	}
	public RigidBody getBody() {
		return body;
	}
	public void setBody(RigidBody body) {
		this.body = body;
	}
	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
}
