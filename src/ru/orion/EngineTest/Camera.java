package ru.orion.EngineTest;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	private float widgth;
	private float heigth;
	private float zNear;
	private float zFar;
	private float FieldOfView;
	
	private Vector3f forward;
	private Vector3f back;
	private Vector3f up;
	private Vector3f right;
	private Vector3f left;
	private Vector3f pos;
	
	private Vector3f translation;
	private Matrix4f res;
	private Matrix4f view;
	private Matrix4f projection;

	private float CameraVelocityCoeff = 1f/50;
	public Camera(float widgth,float height, float zNear, float zFar,float fieldOfView) {
		this.setWidgth(widgth);
		this.setHeigth(height);
		this.setzNear(zNear);
		this.setzFar(zFar);
		this.setFieldOfView((float) Math.toRadians(fieldOfView));
		this.translation = new Vector3f();
		this.forward = new Vector3f(0.0f,0.0f,-1f).normalize();
		this.up = new Vector3f(0f,1f,0f).normalize();
		this.right = new Vector3f();
		this.left = new Vector3f();
		this.back = new Vector3f();
		forward.cross(up,right);
		forward.negate(back);
		right.negate(left);
		right.normalize();
		this.pos = new Vector3f(0f,0f,0f);
		this.view = new Matrix4f().identity();
		this.projection = new Matrix4f().identity();
		res = new Matrix4f();
		glfwSetKeyCallback(EngineContext.windowID, new GLFWKeyCallback() {
			
			@Override
			public void invoke(long arg0, int arg1, int arg2, int arg3, int arg4) {
				keyCallback( arg0,  arg1,  arg2,  arg3,  arg4);
			}
		});
	}

	public float getWidgth() {
		return widgth;
	}

	public void setWidgth(float widgth) {
		this.widgth = widgth;
	}

	public float getHeigth() {
		return heigth;
	}

	public void setHeigth(float heigth) {
		this.heigth = heigth;
	}

	public float getzNear() {
		return zNear;
	}

	public void setzNear(float zNear) {
		this.zNear = zNear;
	}

	public float getzFar() {
		return zFar;
	}

	public void setzFar(float zFar) {
		this.zFar = zFar;
	}

	public float getFieldOfView() {
		return FieldOfView;
	}

	public void setFieldOfView(float fieldOfView) {
		FieldOfView = fieldOfView;
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

	public void setProjection(Matrix4f projection) {
		this.projection = projection;
	}

	public void calculateProjectionMatrix() {
		projection.setPerspective(FieldOfView, widgth/heigth, zNear, zFar);
	}
	
	private void normVectors() {
		forward.normalize();
		back.normalize();
		up.normalize();
		right.normalize();
		left.normalize();
	}
	
	
	public void rotateX(float angle) {
		forward.rotateAbout(angle, right.x,right.y,right.z);
		normVectors();
		
		right.cross(forward,up);
		forward.negate(back);
	}
	public void rotateY(float angle) {
		forward.rotateAbout(angle, 0, 1.f,0);
		forward.cross(up,right);
		right.negate(left);
		forward.negate(back);
		normVectors();
	}
	public void rotateZ(float angle) {
		forward.rotateAbout(angle, up.x, up.y, up.z);
		
	}
	public void translate(Vector3f delta) {
		pos.add(delta);
	}
	public void translate(float f, float g, float h) {
		pos.add(f,g,h);
	}

	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	
	
	public Matrix4f getProjectionView() {
		forward.normalize();
		projection.setPerspective(FieldOfView, EngineContext.SCREEN_WIDGTH/EngineContext.SCREEN_WIDGTH, zNear, zFar)/*.lookAt(pos.x,pos.y,pos.z,pos.x+forward.x,pos.y+forward.y,pos.z+forward.z,up.x,up.y,up.z)*/;
		view.setLookAt(pos.x,pos.y,pos.z,pos.x+forward.x,pos.y+forward.y,pos.z+forward.z,up.x,up.y,up.z);
		projection.mul(view,res);
		return res;
	}
	
	//Temporary input
	
	private void keyCallback(long window, int key, int scancode, int action, int mods) {
		translation.set(0);
		if(key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
			translation.set(forward).mul(CameraVelocityCoeff);
		}
		if(key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			translation.set(back).mul(CameraVelocityCoeff);
		}
		if(key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			translation.set(left).mul(CameraVelocityCoeff);
		}
		if(key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			translation.set(right).mul(CameraVelocityCoeff);
		}
		if(key == GLFW_KEY_UP && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
			rotateX(0.01f);
		}
		if(key == GLFW_KEY_DOWN && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			rotateX(-0.01f);
		}
		if(key == GLFW_KEY_LEFT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			rotateY(0.01f);
		}
		if(key == GLFW_KEY_RIGHT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			rotateY(-0.01f);
		}
		translate(translation);
	}

	
	
	
}
