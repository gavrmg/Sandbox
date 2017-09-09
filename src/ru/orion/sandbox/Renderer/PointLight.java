package ru.orion.sandbox.Renderer;

import javax.vecmath.*;

public class PointLight {
	private Point3f Position;
	private Vector3f Color;
	private float intensity;
	
	
	
	
	private Attenuation attenuation;
	
	
	public PointLight(float px,float py,float pz,float cx,float cy,float cz, float constant,float linear, float exponential,float intensity) {
		Position = new Point3f(px,py,pz);
		Color = new Vector3f(cx,cy,cz);
		this.intensity = intensity;
		this.attenuation= new Attenuation(constant,linear,exponential);
	}
	
	public PointLight(PointLight pointLight) {
		Position = new Point3f(pointLight.getPosition());
		Color = new Vector3f(pointLight.getColor());
		this.intensity = pointLight.getIntensity();
		this.attenuation= new Attenuation(this.attenuation);
	}
	
	public PointLight(Point3f pos, Vector3f color, float linear, float exponential) {
		Position = pos;
		Color = color;
//		this.linear = linear;
//		this.exponential = exponential;
	}

	public Point3f getPosition() {
		return Position;
	}

	public void setPosition(Point3f position) {
		Position = position;
	}

	public Vector3f getColor() {
		return Color;
	}

	public void setColor(Vector3f color) {
		Color = color;
	}


	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}


	public Attenuation getAttenuation() {
		return attenuation;
	}


	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}
}
