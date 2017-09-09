package ru.orion.sandbox.Renderer;

//import org.joml.AxisAngle4f;
//import org.joml.Matrix4f;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
import javax.vecmath.*;

//import org.joml.Quaternionf;
//import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallback;
//import javax.vecmath.*;
import ru.orion.sandbox.EngineContext;

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
	private Vector3f temp;
	private static final Vector3f X = new Vector3f(1,0,0);
	private static final Vector3f Y = new Vector3f(0,1,0);
	private static final Vector3f Z = new Vector3f(0,0,1);
	
	private Matrix4f translation;
	private Matrix4f rotationMatrix;
	private Matrix4f res;
	private javax.vecmath.Matrix4f view;
	private Matrix4f projection;
	private Quat4f rotation;
	private float CameraVelocityCoeff = 1f/5;
	public Camera(float widgth,float height, float zNear, float zFar,float fieldOfView) {
		this.setWidgth(widgth);
		this.setHeigth(height);
		this.setzNear(zNear);
		this.setzFar(zFar);
		this.setFieldOfView((float) Math.toRadians(fieldOfView));
		this.translation = new Matrix4f();
		this.rotationMatrix = new Matrix4f();
		this.forward = new Vector3f(0.0f,0.0f,-1f);
		this.up = new Vector3f(0f,1f,0f);
		this.right = new Vector3f(0f,0f,0f);
		this.left = new Vector3f(0f,0f,0f);
		this.back = new Vector3f(0f,0f,0f);
		right.cross(forward,up);
		back.negate(forward);
		left.negate(right);
		right.normalize();
		rotation = new Quat4f();
		this.pos = new Vector3f(0f,0f,0f);
		this.temp = new Vector3f(0f,0f,0f);
		this.view = new javax.vecmath.Matrix4f();
		this.projection = new Matrix4f();
		rotationMatrix.setIdentity();
		translation.setIdentity();
		setPerspective(projection,FieldOfView, EngineContext.SCREEN_HEIGTH,EngineContext.SCREEN_WIDGTH, zNear, zFar);
		res = new Matrix4f();
		delta = new Vector3f();
		this.rotation.set(0f,0f,0f,1f);
		view.setIdentity();
		view.mul(rotationMatrix,translation);
/*		glfwSetKeyCallback(EngineContext.windowID, new GLFWKeyCallback() {
			
			@Override
			public void invoke(long arg0, int arg1, int arg2, int arg3, int arg4) {
				keyCallback( arg0,  arg1,  arg2,  arg3,  arg4);
			}
		});*/
	}
	
	public void update() {
		//temp.s
		rotationMatrix.set(rotation);
		rotationMatrix.transform(forward);
		rotationMatrix.transform(up);
		rotationMatrix.setIdentity();
		rotation.set(0,0,0,1);
		right.cross(up, forward);
		back.negate(forward);
		left.negate(right);
		//right.cross(up, forward);
		if(up.dot(Y)<0) {//camera flip check
			forward.x = 0;
			forward.z = 0;
			if(forward.y<0) {
				forward.y = -1;
			}
			else {
				forward.y = 1;
				//up.cross(right, forward);
			
			}
			up.cross(forward, right);
		}
		normVectors();

		view.set(new float[]{right.x,right.y,right.z,0,up.x,up.y,up.z,0,forward.x,forward.y,forward.z,0,0,0,0,1});
		temp.set(-pos.x, -pos.y, -pos.z);
		this.translation.setTranslation(temp);
//		this.rotationMatrix.set(rotation);
		//System.out.println(translation);
		//rotationMatrix.setIdentity();
		//rotationMatrix.set(new float[]{right.x,right.y,right.z,0,up.x,up.y,up.z,0,forward.x,forward.y,forward.z,0,0,0,0,1});
		//translation.set(new float[] {1,0,0,-pos.x,0,1,0,-pos.y,0,0,1,-pos.z,0,0,0,1});
		//view.mul(translation,rotationMatrix);
		view.mul(translation);
		rotation.set(0,0,0,1);
		//System.out.println(forward);
	}
	
	private void setPerspective(Matrix4f m,float fov, float height,float width,float zNear,float zFar) {
		float tanHalfFOV = (float)Math.tan(fov/2);
		float AR = width/height;
		float zRange = zNear-zFar;
		m.m00 = (1f/tanHalfFOV)/AR; 	m.m01 = 0; 					m.m02 = 0; 						m.m03 = 0;
		m.m10 = 0; 					m.m11 = 1f/tanHalfFOV/**AR*/; 	m.m12 = 0; 						m.m13 = 0;
		m.m20 = 0; 					m.m21 = 0; 					m.m22 = (-zNear-zFar)/zRange; 	m.m23 = 2*zFar*zNear/zRange;
		m.m30 = 0; 					m.m31 = 0; 					m.m32 = 1; 						m.m33 = 0;
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
	}
	
	private void normVectors() {
		forward.normalize();
		back.normalize();
		up.normalize();
		right.normalize();
		left.normalize();
	}
	
	
	public void rotateX(float angle) {
//		forward.(angle, right.x,right.y,right.z);
		Quat4f rotationX = new Quat4f();
		float halfSin = (float) Math.sin(angle);
		float halfCos = (float) Math.cos(angle);
		rotationX.set(right.x*halfSin, right.y*halfSin, right.z*halfSin, halfCos);
//		rotationX.set(right.x*halfSin, right.y*halfSin, right.z*halfSin, halfCos);
		rotation.mul(rotationX);
//		right.cross(forward,up);
//		forward.negate(back);
	}
	public void rotateY(float angle) {
//		forward.rotateAbout(angle, 0, 1.f,0);
		Quat4f rotationY = new Quat4f();
		float halfSin = (float) Math.sin(angle);
		float halfCos = (float) Math.cos(angle);
		rotationY.set(Y.x*halfSin, Y.y*halfSin, Y.z*halfSin, halfCos);
		rotation.mul(rotationY);
	}
	public void rotateZ(float angle) {
//		forward.rotateAbout(angle, up.x, up.y, up.z);
		Quat4f rotationZ = new Quat4f();
		float halfSin = (float) Math.sin(angle);
		float halfCos = (float) Math.cos(angle);
		rotationZ.set(forward.x*halfSin, forward.y*halfSin, forward.z*halfSin, halfCos);
		rotation.mul(rotationZ);
		
	}
	public void translate(Vector3f delta) {
		pos.add(delta);
	}
	public void translate(float f, float g, float h) {
		//pos.(f,g,h);
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
	
	private void setView(Vector3f pos, Vector3f up, Vector3f forward) {
		view.set(new float[]{-pos.x,-pos.y,-pos.z,0,up.x,up.y,up.z,0,pos.x+forward.x,pos.y+forward.y,pos.z+forward.z,0,0,0,0,1});
		
		
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	public javax.vecmath.Matrix4f getView() {
		forward.normalize();
		//view.set(new float[]{right.x,right.y,right.z,-pos.x,up.x,up.y,up.z,-pos.y,forward.x,forward.y,forward.z,-pos.z,0,0,0,1});
		//view.(pos.x,pos.y,pos.z,pos.x+forward.x,pos.y+forward.y,pos.z+forward.z,up.x,up.y,up.z);
		//res.mul(rotationMatrix,translation);
		return view;
	}
	
	//Temporary input
	private Vector3f delta;
	private void keyCallback(long window, int key, int scancode, int action, int mods) {
		//translation.setIdentity();
		delta.set(0,0,0);
		normVectors();
		if(key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
			forward.scale(CameraVelocityCoeff);
			delta.add(forward);
		}
		if(key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			back.scale(CameraVelocityCoeff);
			delta.add(back);
		}
		if(key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			left.scale(CameraVelocityCoeff);
			delta.add(left);
		}
		if(key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
			right.scale(CameraVelocityCoeff);
			delta.add(right);
		}
		if(key == GLFW_KEY_P && (action == GLFW_PRESS)) {
			System.out.println(view);
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
		translate(delta);
		rotationMatrix.set(rotation);
		rotationMatrix.transform(forward);
		rotationMatrix.transform(up);
		rotationMatrix.setIdentity();
		rotation.set(0,0,0,1);
		right.cross(forward, up);
		back.negate(forward);
		left.negate(right);
		normVectors();
		//System.out.println(pos);

	}

	public Matrix4f getRotationMatrix() {
		return rotationMatrix;
	}

	public void setRotationMatrix(Matrix4f rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}

	
	
	
}
