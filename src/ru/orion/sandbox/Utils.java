package ru.orion.sandbox;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;
public class Utils {
	public static FloatBuffer createFlippedBuffer(Vertex[] data) {
		FloatBuffer buffer = memAllocFloat(data.length*Vertex.size);
		for(int i = 0; i < data.length;i++) {
			if(data[i] == null)
				break;
			buffer.put(data[i].getPos().x);
			buffer.put(data[i].getPos().y);
			buffer.put(data[i].getPos().z);
			buffer.put(data[i].getTexCoord().x);
			buffer.put(data[i].getTexCoord().y);
		}
		buffer.flip();
		return buffer;
	}
	public static FloatBuffer createFlippedBuffer(Vertex3texcoord[] data) {
		FloatBuffer buffer = memAllocFloat(data.length*Vertex3texcoord.size);
		for(int i = 0; i < data.length;i++) {
			if(data[i] == null)
				break;
			buffer.put(data[i].getPos().x);
			buffer.put(data[i].getPos().y);
			buffer.put(data[i].getPos().z);
			buffer.put(data[i].getTexCoord().x);
			buffer.put(data[i].getTexCoord().y);
			buffer.put(data[i].getTexCoord().z);
		}
		buffer.flip();
		return buffer;
	}
	public static IntBuffer createFlippedBuffer(int[] data) {
		IntBuffer buffer = memAllocInt(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static FloatBuffer craeteFlippedBuffer(Matrix4f data) {
		FloatBuffer buffer = memAllocFloat(16);
		/*buffer.put(data.m00()).put(data.m01()).put(data.m02()).put(data.m03());
		buffer.put(data.m10()).put(data.m11()).put(data.m12()).put(data.m23());
		buffer.put(data.m20()).put(data.m21()).put(data.m22()).put(data.m23());
		buffer.put(data.m30()).put(data.m31()).put(data.m32()).put(data.m33());*/
		data.get(buffer);
		buffer.flip();
		return buffer;
	}
	
	public static void disposeBuffer(Buffer buffer) {
		memFree(buffer);
	}
}
