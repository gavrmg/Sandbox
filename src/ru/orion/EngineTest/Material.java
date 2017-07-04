package ru.orion.EngineTest;

import org.joml.Vector3f;

public class Material {
	private Texture texture;
	private Vector3f color;
	
	public Material(Texture texture, Vector3f color) {
		this.setTexture(texture);
		this.setColor(color);
	}
	
	public Material(Vector3f color) {
		setTexture(null);
		this.setColor(color);
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
}
