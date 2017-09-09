package ru.orion.sandbox;

import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

public class TerrainGenerator {
	private TestTerrainGenerator testGenerator;
	
	public TerrainGenerator() {
		testGenerator = new TestTerrainGenerator();
	}
	public TerrainChunk genTerrainChunk(Point3i pos) {
		return testGenerator.GenerateFlatTerrain(pos);
	}
}
