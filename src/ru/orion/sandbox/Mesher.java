package ru.orion.sandbox;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import ru.orion.sandbox.Renderer.Mesh;
//import ru.orion.sandbox.Renderer.Mesh;
import ru.orion.sandbox.Renderer.Vertex;
//import ru.orion.sandbox.Renderer.Vertex;

import static org.lwjgl.system.MemoryUtil.*;

/*
 * A class containing static functions for generating geometry(Meshes).
 */

public class Mesher {
	private static Vector3f[] cubictranslation = {
			new Vector3f(-1f / TerrainChunk.NODEPERMETER, -1f / TerrainChunk.NODEPERMETER,
					-1f / TerrainChunk.NODEPERMETER),
			new Vector3f(1f / TerrainChunk.NODEPERMETER, -1f / TerrainChunk.NODEPERMETER,
					-1f / TerrainChunk.NODEPERMETER),
			new Vector3f(1f / TerrainChunk.NODEPERMETER, -1f / TerrainChunk.NODEPERMETER,
					1f / TerrainChunk.NODEPERMETER),
			new Vector3f(-1f / TerrainChunk.NODEPERMETER, -1f / TerrainChunk.NODEPERMETER,
					1f / TerrainChunk.NODEPERMETER),
			new Vector3f(-1f / TerrainChunk.NODEPERMETER, 1f / TerrainChunk.NODEPERMETER,
					-1f / TerrainChunk.NODEPERMETER),
			new Vector3f(1f / TerrainChunk.NODEPERMETER, 1f / TerrainChunk.NODEPERMETER,
					-1f / TerrainChunk.NODEPERMETER),
			new Vector3f(1f / TerrainChunk.NODEPERMETER, 1f / TerrainChunk.NODEPERMETER,
					1f / TerrainChunk.NODEPERMETER),
			new Vector3f(-1f / TerrainChunk.NODEPERMETER, 1f / TerrainChunk.NODEPERMETER,
					1f / TerrainChunk.NODEPERMETER)

	};
	private static Vector3f[] cubictranslationi = { new Vector3f(0, 0, 0), new Vector3f(1, 0, 0),
			new Vector3f(1, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), new Vector3f(1, 1, 0),
			new Vector3f(1, 1, 1), new Vector3f(0, 1, 1)

	};
	private static int[][][] nearestVoxels = {{{0,-1,-1},{0,-1,0},{0,0,0},{0,0,-1}},{{-1,0,-1},{-1,0,0},{0,0,0},{0,0,-1}},{{-1,-1,0},{-1,0,0},{0,0,0},{0,-1,0}}};

	public static Mesh genStupidMeshFromChunk(TerrainChunk chunk) {
		ArrayList<Vertex> vertecies = new ArrayList<Vertex>();
		ArrayList<Integer> indecies = new ArrayList<Integer>();
		Vertex current;
		boolean flag;
		float x, y, z;
		for (int i = 0; i < TerrainChunk.SIZE * TerrainChunk.SIZE * TerrainChunk.SIZE; i++) {
			if (chunk.getTerrain()[i] != 0) {
				x = (float) (i >> 12);
				y = (float) ((i >> 6) & 63);
				z = (float) (i & 63);
				for (int k = 0; k < 8; k++) {
					// current = new Vertex(
					// new Vector3f(x, y, z).mul(1f /
					// TerrainChunk.NODEPERMETER).add(cubictranslation[k]));
					// vertecies.add(current);
				}
				indecies.add(vertecies.size() - 1);
				indecies.add(vertecies.size() - 2);
				indecies.add(vertecies.size() - 3);
				indecies.add(vertecies.size() - 1);
				indecies.add(vertecies.size() - 3);
				indecies.add(vertecies.size() - 4);
				indecies.add(vertecies.size() - 5);
				indecies.add(vertecies.size() - 6);
				indecies.add(vertecies.size() - 7);
				indecies.add(vertecies.size() - 5);
				indecies.add(vertecies.size() - 7);
				indecies.add(vertecies.size() - 8);
				indecies.add(vertecies.size() - 1);
				indecies.add(vertecies.size() - 5);
				indecies.add(vertecies.size() - 6);
				indecies.add(vertecies.size() - 1);
				indecies.add(vertecies.size() - 2);
				indecies.add(vertecies.size() - 6);
				indecies.add(vertecies.size() - 2);
				indecies.add(vertecies.size() - 6);
				indecies.add(vertecies.size() - 7);
				indecies.add(vertecies.size() - 2);
				indecies.add(vertecies.size() - 3);
				indecies.add(vertecies.size() - 7);
				indecies.add(vertecies.size() - 3);
				indecies.add(vertecies.size() - 7);
				indecies.add(vertecies.size() - 8);
				indecies.add(vertecies.size() - 3);
				indecies.add(vertecies.size() - 4);
				indecies.add(vertecies.size() - 8);
				indecies.add(vertecies.size() - 4);
				indecies.add(vertecies.size() - 1);
				indecies.add(vertecies.size() - 5);
				indecies.add(vertecies.size() - 4);
				indecies.add(vertecies.size() - 8);
				indecies.add(vertecies.size() - 5);
			}
		}
		int[] inds = new int[indecies.size() + 1];
		Vertex[] verts = new Vertex[vertecies.size() + 1];
		for (int i = 0; i < indecies.size(); i++) {
			inds[i] = indecies.get(i);
		}
		for (int i = 0; i < vertecies.size(); i++) {
			verts[i] = vertecies.get(i);
		}
		return new Mesh(verts, inds);

	}

	// public static

	/*
	 * This is the function responsible for generating a mesh< which is later used
	 * for physics simulation and rendering.
	 *
	 * Parameters are TerrainChunk class which stores the terrain data, and array of
	 * shorts representing all possible IDs.
	 */

	public static Mesh genGreedyCubesBufferedFromChunk3tex(TerrainChunk chunk, short[] id) {
		int size = TerrainChunk.SIZE;
		float coordShift = (1f / TerrainChunk.NODEPERMETER) / 2;
		float dimCoord;
		short[] terrain = chunk.getTerrain();
		short[] mask = new short[size * size];
		int[] x = new int[3];
		int[] q = new int[3];
		int u;
		int v;
		int n = 0;
		int i = 0;
		int j = 0;
		int k = 0;
		int w = 0;
		int h = 0;
		int r, b;
		int count = 1;
		float U1;
		float V1;
		float U2;
		float V2;
		long start, stop;
		long elapsed = 0;
		int current, nextPlane;
		int shiftV;
		boolean done;
		FloatBuffer Verts = memAllocFloat(size * size * size * 12 * 6);
		IntBuffer Inds = memAllocInt(size * size * size * 18 * 6);
		int vertexCounter = 0;
		start = System.currentTimeMillis();
		for (int dim = 0; dim < 3; dim++) {
			x[0] = 0;
			x[1] = 0;
			x[2] = 0;
			u = (dim + 2) % 3;
			v = (dim + 1) % 3;
			shiftV = (int) Math.pow(size, v);
			// for each 2d plane
			for (x[dim] = -1; x[dim] < size; x[dim]++) {
				n = 0;
				dimCoord = (float) (x[dim] - 32) / TerrainChunk.NODEPERMETER;
				if ((x[dim] == -1) || (x[dim] == size - 1)) {
					if (x[dim] == -1) {
						x[dim] += 1;
						for (x[u] = 0; x[u] < size; x[u]++) {
							x[v] = 0;
							current = (int) ((x[0]) + size * ((x[1]) + size * (x[2])));
							for (x[v] = 0; x[v] < size; x[v]++) {
								mask[n] = (terrain[current] != 0) ? (short) -terrain[current] : 0;
								current += shiftV;
								n++;
							}
						}
						x[dim] -= 1;
					} else if (x[dim] == size - 1) {
						for (x[u] = 0; x[u] < size; x[u]++) {
							x[v] = 0;
							current = (int) ((x[0]) + size * ((x[1]) + size * (x[2])));
							for (x[v] = 0; x[v] < size; x[v]++) {
								mask[n] = (terrain[current] != 0) ? terrain[current] : 0;
								current += shiftV;
								n++;
							}
						}
					}
				} else {
					q[dim] = 1;
					for (x[u] = 0; x[u] < size; x[u]++) {
						x[v] = 0;
						current = (int) ((x[0]) + size * ((x[1]) + size * (x[2])));
						for (x[v] = 0; x[v] < size; x[v]++) {
							nextPlane = current + count;
							if (terrain[current] != terrain[nextPlane]) {
								if (terrain[nextPlane] == 0)
									mask[n] = terrain[current];
								else if (terrain[current] == 0)
									mask[n] = (short) -terrain[nextPlane];
								else
									mask[n] = 0;

							} else
								mask[n] = 0;
							current += shiftV;
							n++;
						}
					}
				}
				// get quads;
				n = 0;
				for (r = 0; r < size; r++) {
					for (b = 0; b < size;) {
						done = false;
						if (mask[n] != 0) {
							// compute width
							for (w = 1; w + (b) < size && mask[n + w] == mask[n]; w++)
								;
							// compute height;
							for (h = 1; h + (r) < size && mask[n + (h << 6)] == mask[n];) {
								for (k = 0; k < w; k++) {
									if (mask[n + (h << 6) + k] != mask[n]) {
										done = true;
										break;
									}
								}
								if (done)
									break;
								h++;
							}
							done = false;
							// add a quad
							U1 = (float) (r - 32) / TerrainChunk.NODEPERMETER;
							V1 = (float) (b - 32) / TerrainChunk.NODEPERMETER;
							U2 = (float) (r + h - 1 - 32) / TerrainChunk.NODEPERMETER;
							V2 = (float) (b + w - 1 - 32) / TerrainChunk.NODEPERMETER;
							// TODO: Manually set position
							vertexCounter = Verts.position();
							Verts.put(vertexCounter + dim, dimCoord + coordShift)
									.put(vertexCounter + u, U1 - coordShift).put(vertexCounter + v, V1 - coordShift)
									.put(vertexCounter + 3, 0).put(vertexCounter + 4, 0)
									.put(vertexCounter + 5, Math.abs(mask[n]))
									.put(vertexCounter + dim + 6, Math.signum(mask[n]));
							Verts.position(vertexCounter + Vertex.size);
							vertexCounter = Verts.position();
							Verts.put(vertexCounter + dim, dimCoord + coordShift)
									.put(vertexCounter + u, U2 + coordShift).put(vertexCounter + v, V1 - coordShift)
									.put(vertexCounter + 3, 0).put(vertexCounter + 4, w)
									.put(vertexCounter + 5, Math.abs(mask[n]))
									.put(vertexCounter + dim + 6, Math.signum(mask[n]));
							Verts.position(vertexCounter + Vertex.size);
							vertexCounter = Verts.position();
							Verts.put(vertexCounter + dim, dimCoord + coordShift)
									.put(vertexCounter + u, U2 + coordShift).put(vertexCounter + v, V2 + coordShift)
									.put(vertexCounter + 3, h).put(vertexCounter + 4, w)
									.put(vertexCounter + 5, Math.abs(mask[n]))
									.put(vertexCounter + dim + 6, Math.signum(mask[n]));
							Verts.position(vertexCounter + Vertex.size);
							vertexCounter = Verts.position();
							Verts.put(vertexCounter + dim, dimCoord + coordShift)
									.put(vertexCounter + u, U1 - coordShift).put(vertexCounter + v, V2 + coordShift)
									.put(vertexCounter + 3, h).put(vertexCounter + 4, 0)
									.put(vertexCounter + 5, Math.abs(mask[n]))
									.put(vertexCounter + dim + 6, Math.signum(mask[n]));
							Verts.position(vertexCounter + Vertex.size);

							Inds.put(vertexCounter / Vertex.size);
							Inds.put(vertexCounter / Vertex.size - 1);
							Inds.put(vertexCounter / Vertex.size - 2);
							Inds.put(vertexCounter / Vertex.size);
							Inds.put(vertexCounter / Vertex.size - 3);
							Inds.put(vertexCounter / Vertex.size - 2);

							// zero the quad in mask
							for (i = 0; i < h; i++)
								for (j = 0; j < w; j++) {
									mask[n + (i << 6) + j] = (short) 0;
								}
							n += w;
							b += w;
						} else {
							n++;
							b++;
						}
					}
				}

			}
			count *= size;
		}
		// Verts.ca
		stop = System.currentTimeMillis();
		Verts.flip();
		Inds.flip();
		ByteBuffer vertecies = (memAlloc(Verts.limit() * 4));
		ByteBuffer indecies = (memAlloc(Inds.limit() * 4));
		// int temp = 0;
		// TODO: Change to ByteBuffers in order to make JBullet work
		while (vertecies.hasRemaining()) {
			vertecies.putFloat(Verts.get());
			// temp++;
		}
		// temp = 0;
		while (indecies.hasRemaining()) {
			indecies.putInt(Inds.get());
		}
		/*
		 * for(temp = 0; temp < 20;temp++)
		 * System.out.println((float)vertecies.get(temp*6));
		 */
		Mesh mesh = new Mesh(vertecies, indecies);
		memFree(Verts);
		memFree(Inds);
		elapsed += (stop - start);

		// System.out.println(elapsed);
		// return new Mesh(vertecies,indecies);

		return mesh;
	}

	public static Mesh genCulledMesh(TerrainChunk chunk, short[] id) {

		int size = chunk.getTerrain().length;

		for (int i = 0; i < size >> 12; i++) {
			for (int j = 0; j < ((size >> 6) & 63); j++) {
				for (int k = 0; k < (size & 63); k++) {

				}
			}
		}

		return null;

	}

	private static class Voxel {
		private Vector4f[] vertecies;
		private boolean inside;
		public Voxel() {
			setVertecies(new Vector4f[8]);
		}
		public Vector4f[] getVertecies() {
			return vertecies;
		}
		public void setVertecies(Vector4f[] vertecies) {
			this.vertecies = vertecies;
		}
		public boolean isInside() {
			return inside;
		}
		public void setInside(boolean inside) {
			this.inside = inside;
		}

	}

	private static class gridPoint{
		private float value;
		private short material;
		public gridPoint() {

		}
		public float getValue() {
			return value;
		}
		public void setValue(float value) {
			this.value = value;
		}
		public short getMaterial() {
			return material;
		}
		public void setMaterial(short material) {
			this.material = material;
		}
	}
	private static class edgeData{
		private float[] normal;
		private float position;
		public edgeData() {
			normal = new float[3];
		}
		public float[] getNormal() {
			return normal;
		}
		public void setNormal(float[] normal) {
			this.normal = normal.clone();
		}
		public float getPosition() {
			return position;
		}
		public void setPosition(float position) {
			this.position = position;
		}
	}
	private static float delta = 0.001f;
	private static Vector3f calculateGradient(float x,float y, float z, float posx, float posy, float posz, float radius) {
		Vector3f result = new Vector3f();

		result.x = sphere(x+delta,y,z,posx,posy,posz,radius)-sphere(x,y,z,posx,posy,posz,radius);
		result.y = sphere(x,y+delta,z,posx,posy,posz,radius)-sphere(x,y,z,posx,posy,posz,radius);
		result.z = sphere(x,y,z+delta,posx,posy,posz,radius)-sphere(x,y,z,posx,posy,posz,radius);
		result.normalize();
		return result;
	}
	private static float lerpEdge(gridPoint a, gridPoint b) {
		return (0-a.value)/(b.value-a.value);
	}

	private static int[][][] edges = {{{0,0,0},{0,0,1},{0,1,1},{0,1,0}},{{0,0,0},{0,0,1},{1,0,1},{1,0,0}},{{0,0,0},{0,1,0},{1,1,0},{1,0,0}}};

	private static Vector4f genVertex(ArrayList<float[]> normals, ArrayList<Vector3f> positions,short material) {
		Matrix3f ATA = new Matrix3f();
		Vector3f ATb = new Vector3f();
		Vector3f tempVec = new Vector3f();
		GMatrix M = new GMatrix(3,normals.size());
		GVector V = new GVector(normals.size());
		GVector res = new GVector(3);
		float[] b = new float[normals.size()];

	//	M.
		float temp;
		for(int i = 0; i < 3;i++)
			for(int j = 0; j < 3; j++) {
				temp = 0;
				for(int k = 0; k < normals.size();k++) {
					temp+=normals.get(k)[i]*normals.get(k)[j];
				}
				ATA.setElement(i, j, temp);
			}
		for(int k = 0; k < normals.size();k++) {
			//positions.get(k).
			b[k]=normals.get(k)[0]*positions.get(k).x+normals.get(k)[1]*positions.get(k).y+normals.get(k)[2]*positions.get(k).z;
		}
		for(int k = 0; k < normals.size();k++) {
			ATb.x+=b[k]*normals.get(k)[0];
			ATb.y+=b[k]*normals.get(k)[1];
			ATb.z+=b[k]*normals.get(k)[2];
		}

		/*for(int i = 0; i < normals.size();i++) {
			tempVec.set(normals.get(i));
			for(int j = 0; j < 3; j++)
				M.setElement(j, i,normals.get(i)[j]);
			V.setElement(i, tempVec.dot(positions.get(i)) );
		}*/
		//M.transpose();
		/*try {
		res.mul(M, V);;
		}
		catch(Exception e) {
			System.out.println(M);
			System.out.println(V);
			System.exit(1);
		}*/
		Vector4f result;
		try {
		ATA.invert();
		}
		catch(Exception e) {
			//System.out.println(normals.size());
			System.out.println(ATA);
			System.exit(1);
		}
		ATA.transform(ATb);
		result = new Vector4f(ATb);
		//result.x = (float)res.getElement(0);
		/*result.y = (float)res.getElement(1);
		result.z = (float)res.getElement(2);*/
		result.w = (float) material;
		return result;
	}
	public static Mesh genMeshDualContour() {
		Mesher x = new Mesher();
		Vector3f temp = new Vector3f();
		Vector3f spherePos = new Vector3f();
		float radius = 4;
		int indexCounter = 0;
		boolean isSignChange = false;
		gridPoint[][][] gridPoints = new gridPoint[TerrainChunk.SIZE+1][TerrainChunk.SIZE+1][TerrainChunk.SIZE+1];
		//Calculate materials at grid points
		for(int i = 0; i <= TerrainChunk.SIZE;i++)
			for(int j = 0; j <= TerrainChunk.SIZE;j++)
				for(int k =0; k <= TerrainChunk.SIZE;k++) {
					temp.set((float)(i-TerrainChunk.SIZE/2)/TerrainChunk.NODEPERMETER,(float)(j-TerrainChunk.SIZE/2)/TerrainChunk.NODEPERMETER,(float)(k-TerrainChunk.SIZE/2)/TerrainChunk.NODEPERMETER);
					gridPoints[i][j][k] = new gridPoint();
					gridPoints[i][j][k].value =sphere(temp,spherePos,radius);
					if(gridPoints[i][j][k].value<0) {
						gridPoints[i][j][k].material = 1;
						System.out.println(gridPoints[i][j][k].value);
					}

				}
		//Calculate Hermite data
		edgeData[][][][] hermiteData;
		hermiteData = new edgeData[3][TerrainChunk.SIZE][TerrainChunk.SIZE][TerrainChunk.SIZE];
		short MaterialCheck;
		float tempPosition;
		float[] tempNormal = new float[3];
		int[] currentPos = new int[3];
		ArrayList<float[]> normals = new ArrayList<float[]>();
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		for(int dir = 0; dir < 3;dir++) {
			int[] currentPosNext = new int[3];
			currentPosNext[dir]=1;
			for(currentPos[0] = 0; currentPos[0] < TerrainChunk.SIZE;currentPos[0]++)
				for(currentPos[1] = 0; currentPos[1] < TerrainChunk.SIZE;currentPos[1]++)
					for(currentPos[2] = 0; currentPos[2] < TerrainChunk.SIZE;currentPos[2]++) {
						MaterialCheck = 0;
						isSignChange = false;
						//Check if this voxel should be processed
						for(int count = 0; count < 8; count++) {
							if(gridPoints[(int) (currentPos[0]+cubictranslationi[count].x)][(int) (currentPos[1]+cubictranslationi[count].y)][(int) (currentPos[2]+cubictranslationi[count].z)].material!=MaterialCheck) {
								isSignChange = true;
								break;
							}
							MaterialCheck = gridPoints[(int) (currentPos[0]+cubictranslationi[count].x)][(int) (currentPos[1]+cubictranslationi[count].y)][(int) (currentPos[2]+cubictranslationi[count].z)].material;
						}
						//If should, then for each edge with function sign change calculate relative point position and normal vector at this position
						if(isSignChange) {
//							System.out.println("Hermite data!");
							try {
								if(gridPoints[currentPos[0]][currentPos[1]][currentPos[2]].value*gridPoints[currentPos[0]+currentPosNext[0]][currentPos[1]+currentPosNext[1]][currentPos[2]+currentPosNext[2]].value>0)
									continue;
								hermiteData[dir][currentPos[0]][currentPos[1]][currentPos[2]] = new edgeData();
								tempPosition = lerpEdge(gridPoints[currentPos[0]][currentPos[1]][currentPos[2]],gridPoints[currentPos[0]+currentPosNext[0]][currentPos[1]+currentPosNext[1]][currentPos[2]+currentPosNext[2]]);
								float[] tempPos = new float[3];
								hermiteData[dir][currentPos[0]][currentPos[1]][currentPos[2]].position = tempPosition;
								tempPos[0] = (float) currentPos[0]+(float)currentPosNext[0]*tempPosition-TerrainChunk.SIZE/2;
								tempPos[0]/=TerrainChunk.NODEPERMETER;
								tempPos[1] = (float) currentPos[1]+(float)currentPosNext[1]*tempPosition-TerrainChunk.SIZE/2;
								tempPos[1]/=TerrainChunk.NODEPERMETER;
								tempPos[2] = (float) currentPos[2]+(float)currentPosNext[2]*tempPosition-TerrainChunk.SIZE/2;
								tempPos[2]/=TerrainChunk.NODEPERMETER;
								calculateGradient(tempPos[0],tempPos[1], tempPos[2], spherePos.x, spherePos.y, spherePos.z, radius).get(tempNormal);
								hermiteData[dir][currentPos[0]][currentPos[1]][currentPos[2]].setNormal(tempNormal);
							}
							catch(java.lang.ArrayIndexOutOfBoundsException e) {
								System.out.println(currentPos[0]+" "+currentPos[1]+" "+currentPos[2]+" "+currentPosNext[0]+" "+currentPosNext[1]+" "+currentPosNext[2]);
								System.exit(1);
							}
	//						System.out.println(hermiteData[dir][currentPos[0]][currentPos[1]][currentPos[2]]);
						}
					}
		}
		//For each voxel with sign change generate vertex at the minimizer of QEF (Quadratic error function
		Voxel[][][] VoxelData = new Voxel[TerrainChunk.SIZE][TerrainChunk.SIZE][TerrainChunk.SIZE];
		float tempPos;
		int[] edgePos = new int[3];
		Vector3f VertexPosition = new Vector3f();
		double[] normalCast = new double[3];
		for(currentPos[0] = 0; currentPos[0] < TerrainChunk.SIZE;currentPos[0]++)
			for(currentPos[1] = 0; currentPos[1] < TerrainChunk.SIZE;currentPos[1]++)
				for(currentPos[2] = 0; currentPos[2] < TerrainChunk.SIZE;currentPos[2]++) {
					MaterialCheck = 0;
					for(int count = 0; count < 8; count++) {
						if(gridPoints[(int) (currentPos[0]+cubictranslationi[count].x)][(int) (currentPos[1]+cubictranslationi[count].y)][(int) (currentPos[2]+cubictranslationi[count].z)].material!=MaterialCheck) {
							VoxelData[currentPos[0]][currentPos[1]][currentPos[2]] = new Voxel();
							VoxelData[currentPos[0]][currentPos[1]][currentPos[2]].setInside(true);
							normals.clear();
							positions.clear();
							for(int counter = 0; counter<3; counter++) {
								for(int edgeCounter = 0; edgeCounter<4;edgeCounter++) {
									if(hermiteData[counter][currentPos[0]+edges[counter][edgeCounter][0]][currentPos[1]+edges[counter][edgeCounter][1]][currentPos[2]+edges[counter][edgeCounter][2]]!=null) {
										tempPos = hermiteData[counter][currentPos[0]+edges[counter][edgeCounter][0]][currentPos[1]+edges[counter][edgeCounter][1]][currentPos[2]+edges[counter][edgeCounter][2]].position;
										//tempNormal =
										normals.add(hermiteData[counter][currentPos[0]+edges[counter][edgeCounter][0]][currentPos[1]+edges[counter][edgeCounter][1]][currentPos[2]+edges[counter][edgeCounter][2]].normal);
										positions.add(new Vector3f((float)(currentPos[0]+edges[counter][edgeCounter][0]*tempPos)/TerrainChunk.NODEPERMETER-6.4f,(float)(currentPos[1]+edges[counter][edgeCounter][1]*tempPos)/TerrainChunk.NODEPERMETER-6.4f,(float)(currentPos[2]+edges[counter][edgeCounter][2]*tempPos)/TerrainChunk.NODEPERMETER-6.4f));

									}
								}
							}
							//Generation
							if(!normals.isEmpty()) {
								System.out.println(currentPos[0]);
								System.out.println(currentPos[1]);
								System.out.println(currentPos[2]);
								VoxelData[currentPos[0]][currentPos[1]][currentPos[2]].vertecies[0] = new Vector4f();
							VoxelData[currentPos[0]][currentPos[1]][currentPos[2]].vertecies[0].set(genVertex(normals,positions,gridPoints[(int) (currentPos[0]+cubictranslationi[count].x)][(int) (currentPos[1]+cubictranslationi[count].y)][(int) (currentPos[2]+cubictranslationi[count].z)].material));
							break;
							}
						}
						MaterialCheck = gridPoints[(int) (currentPos[0]+cubictranslationi[count].x)][(int) (currentPos[1]+cubictranslationi[count].y)][(int) (currentPos[2]+cubictranslationi[count].z)].material;
					}
				}
		int size = TerrainChunk.SIZE;
		FloatBuffer Verts = memAllocFloat(size * size * size * 12 * 6);
		IntBuffer Inds = memAllocInt(size * size * size * 18 * 6);
		//For each edge with sign change generate quad with vertecies from nearest voxels
		for(int dir = 0; dir < 3; dir++) {
			int[] currentPosNext = new int[3];
			currentPosNext[dir]=1;

			for(currentPos[0] = 1; currentPos[0] < TerrainChunk.SIZE-1;currentPos[0]++)
				for(currentPos[1] = 1; currentPos[1] < TerrainChunk.SIZE-1;currentPos[1]++)
					for(currentPos[2] = 1; currentPos[2] < TerrainChunk.SIZE-1;currentPos[2]++) {
						if(hermiteData[dir][currentPos[0]][currentPos[1]][currentPos[2]]!=null) {
							if(gridPoints[currentPos[0]][currentPos[1]][currentPos[2]].material!=gridPoints[currentPos[0]+currentPosNext[0]][currentPos[1]+currentPosNext[1]][currentPos[2]+currentPosNext[2]].material) {
								try {
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][0][0]][currentPos[1]+nearestVoxels[dir][0][1]][currentPos[2]+nearestVoxels[dir][0][2]].vertecies[0].x/TerrainChunk.NODEPERMETER);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][0][0]][currentPos[1]+nearestVoxels[dir][0][1]][currentPos[2]+nearestVoxels[dir][0][2]].vertecies[0].y/TerrainChunk.NODEPERMETER);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][0][0]][currentPos[1]+nearestVoxels[dir][0][1]][currentPos[2]+nearestVoxels[dir][0][2]].vertecies[0].z/TerrainChunk.NODEPERMETER);
								Verts.put(0);
								Verts.put(0);
								Verts.put(2);
								Verts.put(0);
								Verts.put(0);
								Verts.put(0);
								//System.out.println("0");

								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][1][0]][currentPos[1]+nearestVoxels[dir][1][1]][currentPos[2]+nearestVoxels[dir][1][2]].vertecies[0].x);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][1][0]][currentPos[1]+nearestVoxels[dir][1][1]][currentPos[2]+nearestVoxels[dir][1][2]].vertecies[0].y);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][1][0]][currentPos[1]+nearestVoxels[dir][1][1]][currentPos[2]+nearestVoxels[dir][1][2]].vertecies[0].z);
								Verts.put(0);
								Verts.put(0);
								Verts.put(1);
								Verts.put(0);
								Verts.put(0);
								Verts.put(0);
								//System.out.println("1");

								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][2][0]][currentPos[1]+nearestVoxels[dir][2][1]][currentPos[2]+nearestVoxels[dir][2][2]].vertecies[0].x);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][2][0]][currentPos[1]+nearestVoxels[dir][2][1]][currentPos[2]+nearestVoxels[dir][2][2]].vertecies[0].y);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][2][0]][currentPos[1]+nearestVoxels[dir][2][1]][currentPos[2]+nearestVoxels[dir][2][2]].vertecies[0].z);
								Verts.put(0);
								Verts.put(0);
								Verts.put(1);
								Verts.put(0);
								Verts.put(0);
								Verts.put(0);
								//System.out.println("2");

								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][3][0]][currentPos[1]+nearestVoxels[dir][3][1]][currentPos[2]+nearestVoxels[dir][3][2]].vertecies[0].x);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][3][0]][currentPos[1]+nearestVoxels[dir][3][1]][currentPos[2]+nearestVoxels[dir][3][2]].vertecies[0].y);
								Verts.put(VoxelData[currentPos[0]+nearestVoxels[dir][3][0]][currentPos[1]+nearestVoxels[dir][3][1]][currentPos[2]+nearestVoxels[dir][3][2]].vertecies[0].z);
								Verts.put(0);
								Verts.put(0);
								Verts.put(1);
								Verts.put(0);
								Verts.put(0);
								Verts.put(0);
								//System.out.println("3");

								Inds.put(Verts.position()/Vertex.size-1);
								Inds.put(Verts.position()/Vertex.size-2);
								Inds.put(Verts.position()/Vertex.size-3);
								Inds.put(Verts.position()/Vertex.size-1);
								Inds.put(Verts.position()/Vertex.size-3);
								Inds.put(Verts.position()/Vertex.size-4);
								}
								catch(Exception e) {
									System.out.println(currentPos[0]+" "+currentPos[1]+" "+currentPos[2]+" "+dir);
									System.exit(1);
								}
							}
						}
					}
		}
		Verts.flip();
		Inds.flip();
		ByteBuffer vertecies = (memAlloc(Verts.limit() * 4));
		ByteBuffer indecies = (memAlloc(Inds.limit() * 4));
		// int temp = 0;
		while (vertecies.hasRemaining()) {
			vertecies.putFloat(Verts.get());
			// temp++;
		}
		// temp = 0;
		while (indecies.hasRemaining()) {
			indecies.putInt(Inds.get());
		}
		/*
		 * for(temp = 0; temp < 20;temp++)
		 * System.out.println((float)vertecies.get(temp*6));
		 */
		Mesh mesh = new Mesh(vertecies, indecies);
		memFree(Verts);
		memFree(Inds);

		// System.out.println(elapsed);
		// return new Mesh(vertecies,indecies);

		return mesh;

	}

	private static float sphere(Vector3f pos, Vector3f center, float radius) {
		Vector3f temp = new Vector3f();
		temp.sub(pos, center);
		return temp.length() - radius;
	}
	private static float sphere(float posx, float posy, float posz, float centerx,float centery,float centerz, float radius) {
		Vector3f temp = new Vector3f();
		return (float)Math.sqrt((posx-centerx)*(posx-centerx)+(posy-centery)*(posy-centery)+(posz-centerz)*(posz-centerz)) - radius;
	}

}
