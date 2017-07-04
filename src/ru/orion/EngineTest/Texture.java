package ru.orion.EngineTest;
	
public class Texture {
	
	private String name;
	private int id;
	int width;
	int heigth;
	
	public Texture(String filename) {
		this.setName(filename);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
