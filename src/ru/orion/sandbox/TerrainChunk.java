package ru.orion.sandbox;
import java.util.Random;
public class TerrainChunk {
	public static final int SIZE = 64;
	public static final int NODEPERMETER = 5;
	
	private short[] terrain;
	
	public TerrainChunk() {
		Random rand = new Random();
		terrain = new short[SIZE*SIZE*SIZE];
		rand.setSeed(System.currentTimeMillis());
		/*for(int i = 0; i < SIZE*SIZE*SIZE;i++) {
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
		}*/
		
		for(int i = 0; i < SIZE*SIZE*SIZE;i++)
			terrain[i] = (short)rand.nextInt(10);
			
	}


	public short[] getTerrain() {
		return terrain;
	}

	public void setTerrain(short[] terrain) {
		this.terrain = terrain;
	}
}
