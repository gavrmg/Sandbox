package ru.orion.sandbox;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.vecmath.*;






import com.bulletphysics.collision.broadphase.AxisSweep3_32;
import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.bulletphysics.*;

import ru.orion.sandbox.Renderer.Camera;
import ru.orion.sandbox.Renderer.Mesh;
import ru.orion.sandbox.Renderer.Vertex;

/*
 * The scene is responsible for calling the physics updates as well as managing objects currently in-game;
 */

public class Scene {
	private ArrayList<StaticEntity> Entities;//Static geometry objects
	private DiscreteDynamicsWorld DynWorld;
	private CollisionDispatcher dispatcher;
	private SequentialImpulseConstraintSolver constraintSolver;
	private AxisSweep3_32 Broadphase;
	private DefaultCollisionConfiguration collisionConfiguration;
	private ArrayList<Actor> Actors;//Dynamic geometry objects
	private PlayerCharacter playerCharacter;
	Transform temp = new Transform();

	public Scene() {
		//Bullet.init();

		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		constraintSolver = new SequentialImpulseConstraintSolver();
		Broadphase = new AxisSweep3_32(new Vector3f(-128f,-128f,-128f),new Vector3f(128f,128f,128));//The bounding box



		DynWorld = new DiscreteDynamicsWorld(dispatcher,Broadphase,constraintSolver,collisionConfiguration);
		Entities = new ArrayList<StaticEntity>();
		Actors = new ArrayList<Actor>();
		DynWorld.setGravity(new Vector3f(0,-10f,0));
		DynWorld.getDispatchInfo().allowedCcdPenetration = 0f;
		setPlayerCharacter(new PlayerCharacter());
		//DynWorld.
	}

	public void AddEntity() {

	}

	public void addPlayerCharacter(Input input, Camera camera,float mass, Mesh mesh, Vector3f pos) {
		Matrix4f transformMatrix = new Matrix4f();
		transformMatrix.setIdentity();
		transformMatrix.setTranslation(pos);
		SphereShape Shape = new SphereShape(1);
		Vector3f localInertia = new Vector3f();
		Shape.calculateLocalInertia(mass, localInertia);
//		RigidBody body = new RigidBody(mass,new DefaultMotionState(new Transform(transformMatrix)),shape );
//		DynWorld.addCollisionObject(body);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, new DefaultMotionState(new Transform(transformMatrix)), Shape,localInertia);
		RigidBody body = new RigidBody(rbInfo); //for testing purposes only
		//body.cal
		body.setRestitution(0.5f);
		body.setDamping(0.2f, 0.2f);
		body.setFriction(0.5f);
		PlayerCharacter character = new PlayerCharacter(body,mesh,camera);
		DynWorld.addRigidBody(body);
		//DynWorld.addAction(character);
		input.setCharacter(character);
		Actors.add(character);
		playerCharacter = character;

	}

	public void AddDynamicEntity(CollisionShape shape, Vector3f pos, float mass, Mesh mesh) {
		Matrix4f transformMatrix = new Matrix4f();
		transformMatrix.setIdentity();
		transformMatrix.setTranslation(pos);
		SphereShape Shape = new SphereShape(1);
		Vector3f localInertia = new Vector3f();
		Shape.calculateLocalInertia(mass, localInertia);
//		RigidBody body = new RigidBody(mass,new DefaultMotionState(new Transform(transformMatrix)),shape );
//		DynWorld.addCollisionObject(body);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, new DefaultMotionState(new Transform(transformMatrix)), Shape,localInertia);
		RigidBody body = new RigidBody(rbInfo); //for testing purposes only
		//body.cal
		body.setRestitution(0.5f);
		body.setDamping(0.2f, 0.2f);
		body.setFriction(0.5f);
		DynWorld.addRigidBody(body);
		Entities.add(new SolidEntity(body, mesh) );

	}


	public void AddTerrainMesh(Mesh terrain, Vector3f ScenePos) {
		Matrix4f transformMatrix = new Matrix4f();
		transformMatrix.setIdentity();
		transformMatrix.setTranslation(ScenePos);
		Vector3f localInertia = new Vector3f();
		//System.out.println(terrain.getVertexDataBuffer().limit());
		TriangleIndexVertexArray mesh = new TriangleIndexVertexArray();

		IndexedMesh terrainMesh = new IndexedMesh();
		terrainMesh.indexType = ScalarType.INTEGER;
		terrainMesh.numTriangles = terrain.getIndeciesDataBuffer().limit()/12;
		terrainMesh.numVertices = terrain.getVertexDataBuffer().limit()/(Vertex.size*4);
		terrainMesh.triangleIndexBase = terrain.getIndeciesDataBuffer();
		terrainMesh.triangleIndexStride = 12;
		terrainMesh.vertexBase = terrain.getVertexDataBuffer();
		terrainMesh.vertexStride = 36;
		mesh.addIndexedMesh(terrainMesh);
		BvhTriangleMeshShape Shape = new BvhTriangleMeshShape(mesh, false);

		//BoxShape Shape = new BoxShape(new Vector3f(6.4f,6.4f,6.4f));
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, new DefaultMotionState(new Transform(transformMatrix)), Shape,localInertia);

		RigidBody body = new RigidBody(rbInfo);
		SolidEntity terrainEntity = new SolidEntity(body,terrain);
		//body.setLinearVelocity(new Vector3f(0,15,0));
//		DynWorld.addCollisionObject(body);
		DynWorld.addRigidBody(body);
		Entities.add(terrainEntity);
	}

	public ArrayList<StaticEntity> getStaticEntities() {
		return Entities;
	}

	public ArrayList<Actor> getActors() {
		return Actors;
	}

	public void setActors(ArrayList<Actor> actors) {
		Actors = actors;
	}

	public void setEntities(ArrayList<StaticEntity> entities) {
		Entities = entities;
	}

	public PlayerCharacter getPlayerCharacter() {
		return playerCharacter;
	}

	public void setPlayerCharacter(PlayerCharacter playerCharacter) {
		this.playerCharacter = playerCharacter;
	}

	public DiscreteDynamicsWorld getDynWorld() {
		return DynWorld;
	}

	public void setDynWorld(DiscreteDynamicsWorld world) {
		DynWorld = world;
	}

/*	public ArrayList<Point3i> getCurrentChunk(){
		for(Actor actor: Actors) {
		//	if(actor.get)
		}
	}*/
	public void update(float timeStep) {
		DynWorld.updateActions(timeStep);
		DynWorld.stepSimulation(timeStep,10);
		for(Actor actor : Actors) {
			actor.updateAction(DynWorld, timeStep);
		}
		//playerCharacter.updateAction(DynWorld, timeStep);
		//Entities.get(1).getBody().getMotionState().getWorldTransform(temp);
		//System.out.println(temp.origin);
		//DynWorld.performDiscreteCollisionDetection();
		//DynWorld.debugDrawWorld();
	}

	public void dispose() {
		for (StaticEntity entity : Entities) {
			DynWorld.removeRigidBody(entity.getBody());

		}
		DynWorld.destroy();
	}
}
