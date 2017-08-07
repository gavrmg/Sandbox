package ru.orion.sandbox;

public class WorldNode {
	private short id;
	private Texture tex;
	public WorldNode(short id) {
		this.setId(id);
	}
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public Texture getTex() {
		return tex;
	}
	public void setTex(Texture tex) {
		this.tex = tex;
	}
}
