package ru.orion.sandbox.Renderer;

//import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;


public class Vertex {
	public static final int size = 9;
	private Vector3f Pos;
	private Vector3f TexCoord;
	private Vector3f Normal;
	public Vertex(Vector3f Pos) {
		this.setPos(Pos);
		this.TexCoord = new Vector3f(0,0,0);
	}
	public Vertex(Vector3f Pos,Vector3f texCoord) {
		this.Pos = Pos;
		this.TexCoord = texCoord;
		this.Normal = new Vector3f();
	}
	public Vector3f getPos() {
		return Pos;
	}
	public void setPos(Vector3f pos) {
		Pos = pos;
	}
	public Vector3f getTexCoord() {
		return TexCoord;
	}
	public void setTexCoord(Vector3f texCoord) {
		TexCoord = texCoord;
	}
	public Vector3f getNormal() {
		return Normal;
	}
	public void setNormal(Vector3f normal) {
		Normal = normal;
	}
	
}