package ru.orion.sandbox;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.system.MemoryUtil.*;

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

	private class quad {

	}

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
					current = new Vertex(
							new Vector3f(x, y, z).mul(1f / TerrainChunk.NODEPERMETER).add(cubictranslation[k]));
					vertecies.add(current);
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
	
	//public static 
	public static Mesh3texcoord genGreedyCubesBufferedFromChunk3tex(TerrainChunk chunk,short[] id) {
		int size = TerrainChunk.SIZE;
		float coordShift = (1f/TerrainChunk.NODEPERMETER)/2;
		float dimCoord;
		short[] terrain = chunk.getTerrain();
		short[] mask = new short[size*size];
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
		int r,b;
		int count = 1;
		float U1;
		float V1;
		float U2;
		float V2;
		long start,stop;
		long elapsed = 0;
		int current,nextPlane;
		int shiftV;
		boolean done;
		FloatBuffer Verts = memAllocFloat(size*size*size*12*6);
		IntBuffer Inds = memAllocInt(size*size*size*18*6);
		int vertexCounter = 0;
		start = System.currentTimeMillis();
		for(int dim = 0; dim < 3; dim++) {
			x[0] = 0;
			x[1] = 0;
			x[2] = 0;
			u = (dim+2)%3;
			v = (dim+1)%3;
			shiftV = (int)Math.pow(size, v);
			//for each 2d plane
			for(x[dim]=-1; x[dim] < size;x[dim]++) {
				n = 0;
				dimCoord = (float)x[dim]/TerrainChunk.NODEPERMETER;
				if((x[dim]==-1)||(x[dim]==size-1)) {
					if(x[dim]==-1) {
						x[dim]+=1;
						for(x[u] = 0; x[u]<size;x[u]++) {
							x[v] = 0;
							current = (int) ((x[0])+size*((x[1]) +size*(x[2])));
							for(x[v] = 0; x[v]<size;x[v]++) {
								mask[n] = (terrain[current]!=0)?(short)-terrain[current]:0;
								current+=shiftV;
								n++;
							}
						}
						x[dim]-=1;
					}
					else if(x[dim]==size-1) {
						for(x[u] = 0; x[u]<size;x[u]++) {
							x[v] = 0;
							current = (int) ((x[0])+size*((x[1]) +size*(x[2])));
							for(x[v] = 0; x[v]<size;x[v]++) {
								mask[n] = (terrain[current]!=0)?terrain[current]:0;
								current+=shiftV;
								n++;
							}
						}
					}
				}
				else {
					q[dim]=1;
					for(x[u] = 0; x[u]<size;x[u]++) {
						x[v] = 0;
						current = (int) ((x[0])+size*((x[1]) +size*(x[2])));
						for(x[v] = 0; x[v]<size;x[v]++) {
							nextPlane = current + count;
							if(terrain[current]!=terrain[nextPlane]) {
								if(terrain[nextPlane]==0)
									mask[n] = terrain[current];
								else if(terrain[current]==0)
									mask[n] = (short)-terrain[nextPlane];
								else
									mask[n] = 0;
								
								}
							else mask[n]=0;
							current+=shiftV;
							n++;
						}
					}
				}
				//get quads;
				n = 0;
				for(r = 0; r < size;r++) {
					for( b = 0; b < size;) {
					done = false;
					if(mask[n]!=0) {
						// compute width
						for(w = 1;w+(b)<size && mask[n+w]==mask[n];w++);
						// compute height;
						for(h = 1; h+(r) < size && mask[n+(h<<6)]==mask[n];) {
							for(k = 0; k < w;k++) {
								if(mask[n+(h<<6)+k]!=mask[n]) {
									done = true;
									break;
								}
							}
							if(done)
								break;
							h++;
						}
						done = false;
						//add a quad
						U1 = (float)r/TerrainChunk.NODEPERMETER;
						V1 = (float)b/TerrainChunk.NODEPERMETER;
						U2 = (float)(r+h-1)/TerrainChunk.NODEPERMETER;
						V2 = (float)(b+w-1)/TerrainChunk.NODEPERMETER;
						//TODO: Manually set position
						vertexCounter = Verts.position();
						Verts.put(vertexCounter+dim,dimCoord+coordShift).put(vertexCounter+u, U1-coordShift).put(vertexCounter+v, V1-coordShift).put(vertexCounter+3,0).put(vertexCounter+4,0).put(vertexCounter+5,Math.abs(mask[n])).put(vertexCounter+dim+6,Math.signum(mask[n]));
						Verts.position(vertexCounter+Vertex3texcoord.size);
						vertexCounter = Verts.position();
						Verts.put(vertexCounter+dim,dimCoord+coordShift).put(vertexCounter+u, U2+coordShift).put(vertexCounter+v, V1-coordShift).put(vertexCounter+3,0).put(vertexCounter+4,w).put(vertexCounter+5,Math.abs(mask[n])).put(vertexCounter+dim+6,Math.signum(mask[n]));
						Verts.position(vertexCounter+Vertex3texcoord.size);
						vertexCounter = Verts.position();
						Verts.put(vertexCounter+dim,dimCoord+coordShift).put(vertexCounter+u, U2+coordShift).put(vertexCounter+v, V2+coordShift).put(vertexCounter+3,h).put(vertexCounter+4,w).put(vertexCounter+5,Math.abs(mask[n])).put(vertexCounter+dim+6,Math.signum(mask[n]));
						Verts.position(vertexCounter+Vertex3texcoord.size);
						vertexCounter = Verts.position();
						Verts.put(vertexCounter+dim,dimCoord+coordShift).put(vertexCounter+u, U1-coordShift).put(vertexCounter+v, V2+coordShift).put(vertexCounter+3,h).put(vertexCounter+4,0).put(vertexCounter+5,Math.abs(mask[n])).put(vertexCounter+dim+6,Math.signum(mask[n]));
						Verts.position(vertexCounter+Vertex3texcoord.size);
						
						
						
						Inds.put(vertexCounter/Vertex3texcoord.size );
						Inds.put(vertexCounter/Vertex3texcoord.size - 1);
						Inds.put(vertexCounter/Vertex3texcoord.size - 2);
						Inds.put(vertexCounter/Vertex3texcoord.size);
						Inds.put(vertexCounter/Vertex3texcoord.size - 3);
						Inds.put(vertexCounter/Vertex3texcoord.size - 2);
						
						//zero the quad in mask
						for(i = 0; i < h; i++)
							for(j = 0; j < w; j++) {
								mask[n+(i<<6)+j]=(short)0;
							}
						n+=w;
						b+=w;
					}
					else {
						n++;
						b++;
					}
					}
				}
				
			}
			count*=size;
		}
		//Verts.ca
		stop = System.currentTimeMillis();
		Verts.flip();
		Inds.flip();
		FloatBuffer vertecies = (memAllocFloat(Verts.limit()));
		IntBuffer indecies = (memAllocInt(Inds.limit()));
//		int temp = 0;
		while(vertecies.hasRemaining()) {
			vertecies.put(Verts.get());
//			temp++;
		}
//		temp = 0;
		while(indecies.hasRemaining()) {
			indecies.put(Inds.get());
		}
		/*for(temp = 0; temp < 20;temp++)
			System.out.println((float)vertecies.get(temp*6));*/
		Mesh3texcoord mesh = new Mesh3texcoord(vertecies,indecies);
		memFree(Verts);
		memFree(Inds);
		elapsed +=(stop-start);
	
		System.out.println(elapsed);
		//return new Mesh3texcoord(vertecies,indecies);
		
		return mesh;
	}
	
	public static Mesh3texcoord genCulledMesh(TerrainChunk chunk, short[] id ) {
		
		int size = chunk.getTerrain().length;
		
		for(int i = 0; i < size >> 12; i++) {
			for(int j = 0; j < ((size >> 6)&63);j++) {
				for(int k = 0; k <(size & 63);k++) {
					
				}
			}
		}
		
		return null;
		
	}
}
