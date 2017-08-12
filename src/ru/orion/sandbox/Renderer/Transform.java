package ru.orion.sandbox.Renderer;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Transform {
	private Vector3f translation;
	private AxisAngle4f rotation;
	private Vector3f scale;
	private Matrix4f res;
	public Transform() {
		translation = new Vector3f();
		rotation = new AxisAngle4f();
		scale = new Vector3f(1,1,1);
		res = new Matrix4f().identity();
		
	}
	
	public Matrix4f getTransformation() {
	//	return res.identity().translate(translation).rotate(rotation).scale(scale);
		return new Matrix4f().identity().translate(translation).rotate(rotation).scale(scale);
	}

	public Vector3f getTranslation() {
		return translation;
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}
	public void setTranslation(float x,float y,float z) {
		this.translation = new Vector3f(x,y,z);
	}

	public AxisAngle4f getRotation() {
		return rotation;
	}

	public void setRotation(float rotation, Vector3f Axis) {
		this.rotation.set(rotation,Axis);
	}
	public void setRotation(float angle,float x,float y,float z) {
		this.rotation.set(angle,x,y,z);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	public void setScale(float x,float y,float z) {
		this.scale= new Vector3f(x,y,z);
	}
	
/*	public Matrix4f getProjectedTransformation(Camera camera) {
		//getProjectionMatrix().mul(getTransformation());
//		return camera.getProjectionMatrix().mul(viewMatrix.lookAt(camera.getPos(),camera.getForward(),camera.getUp())).mul(getTransformation());
	}
	/*public void setTranslation(Vector3f translation) {
		translationMatrix.translation(translation);
	}*/
}
