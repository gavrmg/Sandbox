package ru.orion.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.zip.*;
import javax.vecmath.Point3i;

public class World {
	private HashMap<Point3i,TerrainChunk> ActiveChunks;
	private String name;
	public World(String name) {
		this.setName(name);
		String path = new String("./Saves/"+name);
		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs();
		
		setActiveChunks(new HashMap<Point3i,TerrainChunk>());
	}
	public HashMap<Point3i,TerrainChunk> getActiveChunks() {
		return ActiveChunks;
	}
	public void setActiveChunks(HashMap<Point3i,TerrainChunk> activeChunks) {
		ActiveChunks = activeChunks;
	}
	
	public String getActiveChunkName(TerrainChunk chunk){
		return new String(chunk.getPosition().x+"."+chunk.getPosition().y+"."+chunk.getPosition().z+"."+"chk");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TerrainChunk loadChunk(Point3i pos) {
		long start,stop;
		start = System.currentTimeMillis();
		TerrainChunk chunk = new TerrainChunk(pos);
		String chunkFile = new String("./Saves/"+name+"/"+pos.x+"."+pos.y+"."+pos.z+".chk");
		File file = new File(chunkFile);
		file.setReadable(true);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			GZIPInputStream input = new GZIPInputStream(inputStream);
			
			ByteBuffer flatBuffer = ByteBuffer.allocate(4*TerrainChunk.SIZE*TerrainChunk.SIZE*TerrainChunk.SIZE);
			short[] flatArray = new short[TerrainChunk.SIZE*TerrainChunk.SIZE*TerrainChunk.SIZE];
			
			input.read(flatBuffer.array());
			ShortBuffer buf = flatBuffer.asShortBuffer();
			chunk.setPosition(pos);
			buf.get(flatArray);
			input.close();
			inputStream.close();
			chunk.setTerrain(flatArray);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop = System.currentTimeMillis();
		System.out.println(stop-start);
		
		return chunk;
	}
	
	public void writeChunk(TerrainChunk chunk) {
		long start,stop;
		start = System.currentTimeMillis();
		String chunkFile = new String("./Saves/"+name+"/"+chunk.getPosition().x+"."+chunk.getPosition().y+"."+chunk.getPosition().z+".chk");
		File file = new File(chunkFile);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		file.setReadable(true);
		file.setWritable(true);
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			GZIPOutputStream output = new GZIPOutputStream(outputStream);
			ByteBuffer flatBuffer = ByteBuffer.allocate(4*TerrainChunk.SIZE*TerrainChunk.SIZE*TerrainChunk.SIZE);
			/*short[] temp = Utils.runLengthCode(chunk.getTerrain());
			byte[] flatArray = new byte[4*TerrainChunk.SIZE*TerrainChunk.SIZE*TerrainChunk.SIZE];
			//for(int i = 0; i< temp.length;)
			int count = 0;
			while(!(temp[count]==0 && temp[count+1]==0)) {
				flatBuffer.putShort(temp[count]).putShort(temp[count+1]);
				count+=2;
			}*/
			for(int i = 0; i < TerrainChunk.SIZE; i++)
				for(int j = 0; j < TerrainChunk.SIZE; j++)
					for(int k = 0; k < TerrainChunk.SIZE; k++)
						flatBuffer.putShort(chunk.getTerrain()[i +TerrainChunk.SIZE*(j+TerrainChunk.SIZE*k)]);
			//byte[] flatArray = flatBuffer.array();
			flatBuffer.flip();
			ByteBuffer tempBuf = ByteBuffer.allocateDirect(flatBuffer.limit());
			tempBuf.put(flatBuffer);
			output.write(flatBuffer.array());
			output.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop = System.currentTimeMillis();
		System.out.println(stop-start);
	}
}
