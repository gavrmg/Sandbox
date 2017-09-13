package ru.orion.sandbox;

import javax.vecmath.Vector3f;

import org.lwjgl.PointerBuffer;

import com.bulletphysics.linearmath.Transform;

import ru.orion.sandbox.Renderer.Camera;
import ru.orion.sandbox.Renderer.DiffuseLight;
import ru.orion.sandbox.Renderer.Mesh;
//import ru.orion.sandbox.Renderer.Mesh3texcoord;
import ru.orion.sandbox.Renderer.PointLight;
import ru.orion.sandbox.Renderer.RenderUtils;
import ru.orion.sandbox.Renderer.TestTerrainShader;
import ru.orion.sandbox.Renderer.Texture;
import ru.orion.sandbox.Renderer.TextureAtlas;
//import ru.orion.sandbox.Renderer.Transform;
import ru.orion.sandbox.Renderer.Vertex;
import ru.orion.sandbox.raytraycerenderer.CLEnvironment;
import ru.orion.sandbox.raytraycerenderer.Kernel;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryUtil.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3i;

public class Game {

	private Mesh mesh1,mesh2;
	private Mesh mesh;
	//private Mesh DynMesh = new Mesh(new V)
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
	private Scene testScene;
	private Transform tempTransform;
	private Input input;
	private TerrainGenerator terrainGenerator;
	private World world;
	private CLEnvironment environment;
	private Kernel testKernel;
	private long testBuffer;
	public Game() {
		//Mesher.genMeshDualContour();
		float x = 64;
		CLUtils.initCL();
		environment = new CLEnvironment();
		testKernel = new Kernel(environment,ResourceLoader.LoadKernel("FastSquareRoot.cl"),"FastSquareRoot");
		testKernel.compileKernel();
		testKernel.createKernel();

		IntBuffer testbuf = memAllocInt(60);
		for(int i = 0; i < 60; i++)
			testbuf.put(i,0);
		/*for(int i = 0; i < 10; i++)
			System.out.println(testbuf.get(i));*/
		testbuf.rewind();
		int[] err = new int[1];
		testBuffer = clCreateBuffer(environment.getContext(), CL_MEM_READ_WRITE|CL_MEM_COPY_HOST_PTR, testbuf, IntBuffer.wrap(err));

		//int k = clEnqueueWriteBuffer(environment.getCommandQueue(), testBuffer, false, 0, testbuf, null, null);
		//clFinish(environment.getCommandQueue());

		int j = clSetKernelArg1p(testKernel.getKernel(),0,testBuffer);

		PointerBuffer workSize = memAllocPointer(1);
		//workSize.free();
		workSize.put(0,60);

		j = clEnqueueNDRangeKernel(environment.getCommandQueue(), testKernel.getKernel(), 1, null, workSize, null, null, null);
		clFinish(environment.getCommandQueue());
		//workSize.free();
		/*for(int i = 0; i < 10; i++)
			System.out.println(testbuf.get(i));
			*/
		j = clEnqueueReadBuffer(environment.getCommandQueue(), testBuffer, false, 0, testbuf, null, null);
		clFinish(environment.getCommandQueue());
		//clBuff
		for(int i = 0; i < 60; i++)
			System.out.println(testbuf.get(i));
		memFree(testbuf);
		j = clReleaseMemObject(testBuffer);
		world = new World("test");
		testScene = new Scene();
		ModelView = new Matrix4f();
		MVIT = new Matrix4f();
		temporary = new Matrix4f();
		input = new Input(EngineContext.windowID,null);
		terrainGenerator = new TerrainGenerator();
		TerrainChunk test = terrainGenerator.genTerrainChunk(new Point3i(0,0,0));
		color = new Vector3f();
		EngineContext.SCREEN_WIDGTH = 800;
		EngineContext.SCREEN_HEIGTH = 600;
		//mesh = Mesher.genGreedyCubesBufferedFromChunk3tex(test, id);
		mesh = Mesher.genMeshDualContour();
		mesh.createMesh();
		mesh1 = new Mesh(new Vertex[] {

				new Vertex(new Vector3f(-0.5f , 0, -0.5f),new Vector3f(0,0,2)),
				new Vertex(new Vector3f(0.5f , 0, -0.5f),new Vector3f(0,1,2)),
				new Vertex(new Vector3f(0 , 0, 0.5f),new Vector3f(1,0,2)),
				new Vertex(new Vector3f(0 , 1, 0),new Vector3f(1,1,2))

		},new int[] {0,1,2,1,2,3,0,1,3,0,2,3});
		mesh1.createMesh();
		testScene.AddTerrainMesh(mesh, new javax.vecmath.Vector3f(0,0,0));
		camera = new Camera(800,600,0.01f,1000f,67);
		testScene.addPlayerCharacter(input,camera,1,mesh1,new Vector3f(0,5,0));
		Atlas = ResourceLoader.LoadTextureAtlas("test.png", id );
		testlight = new PointLight(0f,0f,-15f,1f,1f,1f,0f,0f,1f,10f);
		difflight = new DiffuseLight(0.2f,1f,1f,1f);
		basicshader = new TestTerrainShader();
		basicshader.addPointLight("testlight");
		basicshader.addDiffLight("difflight");
		basicshader.setDiffLight("difflight", difflight);
		basicshader.addUniform("amb");
		basicshader.setUniformf("amb", 0.5f);
		transform = new Transform();
		tempTransform = new Transform();
		world.writeChunk(test);
		/*for(int i = 0; i < 100000;i++)
			test = world.loadChunk(new Point3i());*/

	}

	public void input() {

	}

	Matrix4f ModelView;
	Matrix4f temporary;
	Matrix4f Projection;
	Matrix4f MVIT;

	public void render() {
	//	glBindTexture(GL_TEXTURE_2D,texture.getId());

		RenderUtils.setTextureAtlas(Atlas);
		basicshader.bind();
		basicshader.setPointLight("testlight", testlight,camera.getView());
		basicshader.setDiffLight("difflight", difflight);
		basicshader.setUniformf("amb", 0.5f);
		for(StaticEntity entity : testScene.getStaticEntities()) {
			entity.getBody().getWorldTransform(tempTransform).getMatrix(temporary);
			ModelView.set(camera.getView());
			basicshader.setPointLight("testlight", testlight,ModelView);
			ModelView.mul(temporary);
			MVIT.set(ModelView);
			basicshader.updateUniforms(ModelView, camera.getProjection() /*temp*/, MVIT, color);
			entity.getMesh().draw();
		}
		for(Actor entity : testScene.getActors()) {
			entity.getBody().getWorldTransform(tempTransform).getMatrix(temporary);
			ModelView.set(camera.getView());
			basicshader.setPointLight("testlight", testlight,ModelView);
			ModelView.mul(temporary);
			MVIT.set(ModelView);
			basicshader.updateUniforms(ModelView, camera.getProjection() /*temp*/, MVIT, color);
			entity.getMesh().draw();
		}
	}

	public void update() {
		long start,stop;
		start = System.currentTimeMillis();
		input.update();
		testScene.update(0.01f);
		camera.update();
		stop = System.currentTimeMillis();
		//System.out.println(stop-start);
	}

	public void cleanUp() {
		testScene.dispose();
		mesh.disposeMesh();
		mesh1.disposeMesh();
		testKernel.destroy();
		environment.destroy();
		CLUtils.destroyCL();
	}

}
