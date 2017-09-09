package ru.orion.sandbox;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;

import ru.orion.sandbox.Renderer.Mesh;
/*
 * Class representin a base for Player Character or any NPC
 */
public class Character extends Actor {
	private boolean inAir;
	private Vector3f temp;
	
	public Character() {
		
	}
	
	public Character(RigidBody body, Mesh mesh) {
		super(body, mesh);
		temp = new Vector3f();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void updateAction(CollisionWorld arg0, float arg1) {
		
	}

	public boolean isInAir() {
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public Vector3f getTemp() {
		return temp;
	}

	public void setTemp(Vector3f temp) {
		this.temp = temp;
	}

}
