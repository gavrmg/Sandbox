package ru.orion.EngineTest;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class Game {
	
	private Mesh mesh;
	private BasicShader basicshader;
	private float temp = 0;
	private Transform transform;
	private Camera camera;
	private Texture texture;
	private Input InputHandler;
	private Vector3f color;

	public Game() {
		mesh = new Mesh(
				new Vertex[]{
					new Vertex(new Vector3f(-0.5f,-0.5f,0f),new Vector2f(0f,0f)),
					new Vertex(new Vector3f(0.5f,-0.5f,0f),new Vector2f(0f,0.016f)),
					new Vertex(new Vector3f(0f,0.5f,0f),new Vector2f(1f,0f)),
					new Vertex(new Vector3f(0f,-0.5f,0.5f),new Vector2f(0.5f,0.5f)),
				},
				new int[] {0,2,1,0,3,2,3,1,2,0,1,3}
			);
		color = new Vector3f();
		mesh.createMesh();
		EngineContext.SCREEN_WIDGTH = 800;
		EngineContext.SCREEN_HEIGTH = 600;
		
		camera = new Camera(800,600,0.01f,1000f,67);
		
		texture = ResourceLoader.LoadTexture("test.png");
		basicshader = new BasicShader();
		transform = new Transform();


	}

	public void input() {
		
	}
	
	public void render() {
		RenderUtils.setTexture(texture);
	//	glBindTexture(GL_TEXTURE_2D,texture.getId());

		basicshader.bind();
		mesh.draw();
		
	}
	
	public void update() {
		temp+=0.01f;
		//transform.setRotation((float) Math.sin(temp), 0,1,0);
		transform.setTranslation(/*(float) Math.sin(temp)*3*/0, 0,-3);
		basicshader.updateUniforms(transform.getTransformation(), camera.getProjectionView(), color.set((float) Math.abs(Math.sin(temp))));
	}
	
	public void cleanUp() {
		mesh.disposeMesh();
	}
	
}
