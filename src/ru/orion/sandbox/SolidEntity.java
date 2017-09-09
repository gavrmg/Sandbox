package ru.orion.sandbox;

import com.bulletphysics.dynamics.RigidBody;

import ru.orion.sandbox.Renderer.Mesh;

public class SolidEntity extends StaticEntity {
	
	//private Mesh mesh;

	public SolidEntity(RigidBody body, Mesh mesh) {
		super(body);
		this.setMesh(mesh);
		// TODO Auto-generated constructor stub
	}

}
