package ru.orion.sandbox;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderUtils {
	public static void initGraphics() {
		createCapabilities();
		//glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		glClearColor(1,1,1,1);

	}
	
	public static void clearScreen() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	public static void setTexture(Texture texture) {
		glBindTexture(GL_TEXTURE_2D, texture.getId());
	}
	public static void setTextureAtlas(TextureAtlas texture) {
		glBindTexture(GL_TEXTURE_2D_ARRAY, texture.getTextureArray());
	}
}
