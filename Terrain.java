package ru.orion.sandbox;

import com.bulletphysics.dynamics.RigidBody;

import ru.orion.sandbox.Renderer.Mesh;

public class Terrain extends StaticEntity {
	private Mesh mesh;
	//private Vector3f position
	public Terrain(Mesh mesh, RigidBody body) {
		super(body);
		this.mesh = mesh;
		
	}
	public void update() {
		// TODO Auto-generated method stub
		
	}
	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

}
