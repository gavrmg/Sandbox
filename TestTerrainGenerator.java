package ru.orion.sandbox;

import javax.vecmath.Point3i;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.Random;
public class TestTerrainGenerator {
	
	private static Vector2f[] preset = {new Vector2f(1,0),new Vector2f(0,1),new Vector2f(-1,0),new Vector2f(0,-1)};
	public TestTerrainGenerator() {
		
	}
	
	public TerrainChunk GenerateFlatTerrain(Point3i pos) {
		TerrainChunk chunk = new TerrainChunk(pos,true);
		for(int i = 0; i < TerrainChunk.SIZE;i++)//x
			for(int j = 0; j < TerrainChunk.SIZE;j++)//y
				for(int k = 0; k < TerrainChunk.SIZE;k++) {//z
					if((float)j/TerrainChunk.NODEPERMETER-6.4+pos.y*12.8<=0) {
						chunk.getTerrain()[i + TerrainChunk.SIZE*j + TerrainChunk.SIZE*TerrainChunk.SIZE*k] = 1;
//						chunk.getTerrain3()[i][j][k] = 1;
					}
				}
		return chunk;
	}
	
	private float[][] genPerlinNoise(Point3i pos,long seed){
		float[][] res = new float[TerrainChunk.SIZE][TerrainChunk.SIZE];
		int gridSize = Math.round((float)TerrainChunk.SIZE/TerrainChunk.NODEPERMETER);
		Vector2f[][] grid = new Vector2f[gridSize][gridSize];
		Random random = new Random();
		random.setSeed(seed);
		for(int i = 0; i < gridSize; i++)
			for(int j = 0; j < gridSize;j++)
				grid[i][j] = preset[random.nextInt(4)];
		int x0,x1,y0,y1;
		float tx,ty;
		for(int i = 0; i < TerrainChunk.SIZE; i++)
			for(int j = 0; j < TerrainChunk.SIZE;j++) {
				x0 = i/TerrainChunk.NODEPERMETER;
				x1 = x0+1;
				tx = (float)i/TerrainChunk.NODEPERMETER-x0;
				y0 = j/TerrainChunk.NODEPERMETER;
				y1 = y0+1;
				ty = (float)j/TerrainChunk.NODEPERMETER-y0;
			}
		
		return res;
	}
	
	public float lerp(float a,float b,float t) {
		return a + (b-a)*t;
	}
}

