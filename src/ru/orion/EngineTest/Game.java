package ru.orion.sandbox;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class Game {
	
	private Mesh mesh1,mesh2;
	private Mesh3texcoord mesh;
	private TestTerrainShader basicshader;
	private float temp = 0;
	private Transform transform;
	private Camera camera;
	private Texture texture;
	private Input InputHandler;
	private Vector3f color;
	private TextureAtlas Atlas;
	private Mesh[] meshes;
	private short[] id = {1,2,3,4,5,6,7,8,9};
	private PointLight testlight;
	private DiffuseLight difflight;
	public Game() {
		TerrainChunk test = new TerrainChunk();
		//test.getTerrain()[4]=1;
		//test.getTerrain()[2]=1;
		//test.getTerrain()[0]=2;
		//test.getTerrain()[3]=2;
		//mesh1 = Mesher.genGreedyCubesFromChunk(test, id[0]);
		//mesh2 = Mesher.genGreedyCubesFromChunk(test, id[1]);
		//mesh1.createMesh();
		//mesh2.createMesh();//this is causing mistake. Probably
		color = new Vector3f();
		EngineContext.SCREEN_WIDGTH = 800;
		EngineContext.SCREEN_HEIGTH = 600;
		//meshes = Mesher.genGreedyCubesFromChunk(test, id);
		//for(int i = 0; i < meshes.length;i++)
			//meshes[i].createMesh();
		//mesh = Mesher.genGreedyCubesFromChunk3tex(test, id);
		//mesh.createMesh();
		mesh = Mesher.genGreedyCubesBufferedFromChunk3tex(test, id);
		mesh.createMesh();
		camera = new Camera(800,600,0.01f,1000f,67);
		Atlas = ResourceLoader.LoadTextureAtlas("test.png", id );
		//texture = ResourceLoader.LoadTexture("test.png");
		testlight = new PointLight(0f,1f,-1f,1f,1f,1f,0f,0f,1f,10f);
		difflight = new DiffuseLight(0.2f,1f,1f,1f);
		basicshader = new TestTerrainShader();
		basicshader.addPointLight("testlight");
		//basicshader.setPointLight("testlight", testlight);
		basicshader.addDiffLight("difflight");
		basicshader.setDiffLight("difflight", difflight);
		basicshader.addUniform("amb");
		basicshader.setUniformf("amb", 0.5f);
		transform = new Transform();
		

	}

	public void input() {
		
	}
	
	public void render() {
	//	glBindTexture(GL_TEXTURE_2D,texture.getId());
		
		RenderUtils.setTextureAtlas(Atlas);
		basicshader.bind();
		basicshader.setPointLight("testlight", testlight,camera.getView());
		basicshader.setDiffLight("difflight", difflight);
		basicshader.setUniformf("amb", 0.5f);
/*		for(int i = 0; i < meshes.length;i++) {
			//basicshader.setUniformi("id", id[i]);
			meshes[i].draw();
		}*/
		mesh.draw();
	}
	
	public void update() {
		temp+=0.01f;
		//transform.setRotation((float) Math.sin(temp), 0,1,0);
		basicshader.updateUniforms(transform.getTransformation().mul(camera.getView()), camera.getProjection(), (transform.getTransformation().mul(camera.getView())).transpose().invert(), color.set((float) Math.abs(Math.sin(temp))));
	}
	
	public void cleanUp() {
		/*for(int i = 0; i < meshes.length;i++)
			meshes[i].disposeMesh();
			*/
		mesh.disposeMesh();
		//mesh1.disposeMesh();
		//mesh2.disposeMesh();
	}
	
}
