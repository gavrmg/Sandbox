package ru.orion.sandbox.Renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;


public class Vertex {
	public static final int size = 5;
	private Vector3f Pos;
	private Vector2f TexCoord;
	public Vertex(Vector3f Pos) {
		this.setPos(Pos);
		this.setTexCoord(new Vector2f(0,0));
	}
	public Vertex(Vector3f Pos,Vector2f texCoord) {
		this.setPos(Pos);
		this.TexCoord=texCoord;
	}
	public Vector3f getPos() {
		return Pos;
	}
	public void setPos(Vector3f pos) {
		Pos = pos;
	}
	public Vector2f getTexCoord() {
		return TexCoord;
	}
	public void setTexCoord(Vector2f texCoord) {
		TexCoord = texCoord;
	}
	
}
