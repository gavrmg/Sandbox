package ru.orion.sandbox;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.glfw.GLFWKeyCallback;
import ru.orion.sandbox.Renderer.Camera;
import ru.orion.sandbox.Renderer.Mesh;

public class PlayerCharacter extends Character {
	private Camera camera;
	//private Vector3f ;
	//private InputState currentInputState = new InputState();
	private Vector3f cameraPos;
	private Transform transform;
	//private Vector3f temp;
	public PlayerCharacter(RigidBody body, Mesh mesh, Camera camera) {
		super(body, mesh);
		cameraPos = new Vector3f();
		this.setCamera(camera);
		transform = new Transform();
		body.getMotionState().getWorldTransform(transform);
		cameraPos.set(transform.origin);
		cameraPos.add(new Vector3f(0,0.7f,0));
		camera.setPos(cameraPos);
		this.getForward().set(camera.getForward());
		//.this.get
		//glfwSet
	}
	public PlayerCharacter() {
		// TODO Auto-generated constructor stub
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	@Override
	public void updateAction(CollisionWorld arg0, float arg1) {
		//transform = new Transform();
		this.getBody().getLinearVelocity(this.getTemp());
		/*if(Math.abs(this.getTemp().z)>0.0001) {
			System.out.println("In air!");
			this.setInAir(true);
		}
		else
			this.setInAir(false);
			*/
		this.getBody().getMotionState().getWorldTransform(transform);
		cameraPos.set(transform.origin);
		cameraPos.add(new Vector3f(0,0.99f,0));
		camera.setPos(cameraPos);
		//System.out.println(camera.getPos());
		//this.getBody().getWor
		//this.getTransformation().transform(Actor.basicForward,camera.getForward());
		//this.getTransformation().transform(Actor.basicUp,camera.getUp());
		
		this.getForward().set(camera.getForward());
		this.getForward().y = 0;
		this.getUp().set(camera.getUp());
		this.getRight().set(camera.getRight());
		this.getRight().y = 0;
		if(this.getForward().dot(camera.getForward())<0) {
			camera.getForward().x=0;
			camera.getForward().z=0;
			if(camera.getForward().y<0) {
				camera.getForward().y=-1;
				
			}
			else
				camera.getForward().y=1;
			

		}
		//transform.basis.
	}
	
}
