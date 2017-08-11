package ru.orion.sandbox;

import org.joml.Vector3f;

public class DiffuseLight {
	private float coeff;//0 - 1;
	private Vector3f Color;
	
	public DiffuseLight(float coeff, float cx, float cy, float cz) {
		this.coeff = coeff;
		this.Color = new Vector3f(cx,cy,cz);
	}
	
	public DiffuseLight(float coeff, Vector3f Color) {
		this.coeff = coeff;
		this.Color = Color;
	}
	
	public float getCoeff() {
		return coeff;
	}
	public void setCoeff(float coeff) {
		this.coeff = coeff;
	}
	public Vector3f getColor() {
		return Color;
	}
	public void setColor(Vector3f color) {
		Color = color;
	}
}
