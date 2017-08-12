package ru.orion.sandbox.Renderer;

public class TextureAtlas {
	private int TextureArray;
	private int TexUnitHeight,TexUnitWidght;
	private String filename;
	public TextureAtlas(String filename, short[] id,int w, int h) {
		this.filename = filename;
		this.setTexUnitWidght(w);
		this.setTexUnitHeight(h);
	}
	public int getTextureArray() {
		return TextureArray;
	}
	public void setTextureArray(int textureArray) {
		TextureArray = textureArray;
	}
	public int getTexUnitHeight() {
		return TexUnitHeight;
	}
	public void setTexUnitHeight(int texUnitHeight) {
		TexUnitHeight = texUnitHeight;
	}
	public int getTexUnitWidght() {
		return TexUnitWidght;
	}
	public void setTexUnitWidght(int texUnitWidght) {
		TexUnitWidght = texUnitWidght;
	}
}
