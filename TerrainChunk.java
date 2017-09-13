package ru.orion.sandbox;
import java.util.Random;

import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;
import javax.vecmath.Vector3f;

/*
 * Class representing terrain data
 * terrain is a flat array representation
 * terrain3 is 3-dimensional array representation
 * terrain is most likely to be deprecated
 */

public class TerrainChunk {
	public static final int SIZE = 64;
	public static final int NODEPERMETER = 5;
	
	private short[] terrain;
	private short[][][] terrain3;
	private Point3i position;
	
	public TerrainChunk(Point3i pos) {
		this.position = pos;
//		terrain = new short[262144];
//		terrain3 = new short[64][64][64];
	}
	public TerrainChunk(Point3i pos,boolean allocateArray) {
		this.position = pos;
		if(allocateArray)
		terrain = new short[262144];
//		terrain3 = new short[64][64][64];
	}
	public TerrainChunk(int x,int y,int z) {
		this.position = new Point3i(x,y,z);
		terrain = new short[64*64*64];
		terrain3 = new short[64][64][64];
	}
	
	public TerrainChunk() {
		Random rand = new Random();
		terrain = new short[SIZE*SIZE*SIZE];
		rand.setSeed(System.currentTimeMillis());
		for(int i = 0; i < SIZE*SIZE*SIZE;i++) {
			if(Math.sin((float)i/10)>0.5)
				terrain[i] = 1;
			else if(Math.sin((float)i/10)>0) 
				terrain[i]=2;
			else if(Math.sin((float)i/10)>-0.5) 
				terrain[i]=3;
			else
				terrain[i]=4;
		}
		for(int i = 0; i < SIZE*SIZE*SIZE;i++) {
			if(Math.cos((float)i/10)>0.5)
				terrain[i] = 0;
		}
		
		/*
		for(int i = 0; i < SIZE*SIZE*SIZE;i++)
			terrain[i] = (short)rand.nextInt(10);
		*/	
		/*for(int i = 0; i < SIZE*SIZE*SIZE;i++)
			terrain[i] = (short)1;*/
			
	}


	public short[] getTerrain() {
		return terrain;
	}

	public void setTerrain(short[] terrain) {
		this.terrain = terrain;
	}


	public short[][][] getTerrain3() {
		return terrain3;
	}


	public void setTerrain3(short[][][] terrain3) {
		this.terrain3 = terrain3;
	}


	public Tuple3i getPosition() {
		return position;
	}


	public void setPosition(Point3i position) {
		this.position = position;
	}
	
}
