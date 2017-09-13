package ru.orion.sandbox;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;

import ru.orion.sandbox.Renderer.Mesh;

/*
 * A basic class representing a dynamic object in the world
 */

public class Actor extends ActionInterface {
	private Vector3f forward;
	private Vector3f right;
	private Vector3f up;
	private Matrix4f transformation;
	private Transform transform;
	private Quat4f orientation;
	private RigidBody body;
	private Mesh mesh;
	
	public static final Vector3f basicForward = new Vector3f(0,0,1);
	public static final Vector3f basicUp = new Vector3f(0,1,0);
	public static final Vector3f basicRight = new Vector3f(1,0,0);
	
	public Actor() {
		
	}
	
	public Actor(RigidBody body, Mesh mesh) {
		//super(body, mesh);
		this.body = body;
		this.mesh = mesh;
		forward = new Vector3f(0,0,1);
		up = new Vector3f(0,1,0);
		right = new Vector3f();
		right.cross(forward,up);
		transform = new Transform();
		body.getWorldTransform(transform);
		transformation = new Matrix4f();
		//this.setForward(new Vector3f(1,0,0));
		//body.getOrientation(orientation);
		//orientation.
		//body.getMotionState().setWorldTransform(new Transform());
	}
	/*public Vector3f getForward() {
		return this.getBody().
	}*/
	@Override//A function that must be overriden in every child. Is called within bullet to update actors state
	public void updateAction(CollisionWorld arg0, float arg1) {//arg0 - World this actor belongs to, arg1 - delta time in seconds
		// TODO Auto-generated method stub
		
	}
	public void update() {
		
	}
	public Quat4f getOrientation() {
		return orientation;
	}
	public void setOrientation(Quat4f orientation) {
		this.orientation = orientation;
	}
	public Vector3f getForward() {
		return forward;
	}
	public void setForward(Vector3f forward) {
		this.forward = forward;
	}
	public Vector3f getRight() {
		return right;
	}
	public void setRight(Vector3f right) {
		this.right = right;
	}
	public Vector3f getUp() {
		return up;
	}
	public void setUp(Vector3f up) {
		this.up = up;
	}
	@Override
	public void debugDraw(IDebugDraw arg0) {
		// TODO Auto-generated method stub
		
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
	public Matrix4f getTransformation() {
		transform.getMatrix(transformation);
		return transformation;
	}
	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}
	
}
