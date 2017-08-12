package ru.orion.sandbox.Renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryUtil.*;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ru.orion.sandbox.Utils;

public class Mesh3texcoord {
	private static enum creationType{
		Buffer, verteciesArray,floatArray};
	private Vertex3texcoord[] vertecies;
	private int[] indecies;
	private float[] VertData;
	
	private FloatBuffer VertexDataBuffer;
	private IntBuffer IndeciesDataBuffer;
	private int vboId;
	private int iboId;
	private int vaoId;
	private int size;
	private boolean isEmpty;
	private Transform translation;
	private creationType type;
	
	public Mesh3texcoord(Vertex3texcoord[] vertecies, int[] indecies) {
		this.vertecies=vertecies;
		this.indecies=indecies;
		size = indecies.length;
		this.translation = new Transform();
		this.isEmpty=false;
		type = creationType.verteciesArray;
	}
	public Mesh3texcoord(float[] vertecies, int[] indecies) {
		this.VertData=vertecies;
		this.indecies=indecies;
		size = indecies.length;
		this.translation = new Transform();
		this.isEmpty=false;
		type = creationType.floatArray;
	}
	public Mesh3texcoord(FloatBuffer vertecies, IntBuffer indecies) {
		this.VertexDataBuffer=vertecies;
		this.IndeciesDataBuffer=indecies;
		size = IndeciesDataBuffer.limit();
//		VertexDataBuffer.flip();
//		IndeciesDataBuffer.flip();
		this.translation = new Transform();
		this.isEmpty=false;
		type = creationType.Buffer;
	}
	
	public void createMesh() {
		switch(type) {
		case Buffer:{
			VertexDataBuffer.flip();
			IndeciesDataBuffer.flip();
			vboId = glGenBuffers();
			iboId = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER,vboId);
			glBufferData(GL_ARRAY_BUFFER,VertexDataBuffer,GL_STATIC_DRAW);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,IndeciesDataBuffer,GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER,0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
			break;
		}
		case floatArray:{
			vboId = glGenBuffers();
			iboId = glGenBuffers();
			break;
		}
		case verteciesArray:{
			vboId = glGenBuffers();
			iboId = glGenBuffers();
			FloatBuffer buffer = Utils.createFlippedBuffer(vertecies);
			glBindBuffer(GL_ARRAY_BUFFER,vboId);
			glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
			IntBuffer intBuffer = Utils.createFlippedBuffer(indecies);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,intBuffer,GL_STATIC_DRAW);
			Utils.disposeBuffer(buffer);
			Utils.disposeBuffer(intBuffer);
			glBindBuffer(GL_ARRAY_BUFFER,0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
			break;
		}
		}
	}
	
	public void disposeMesh() {
		glDeleteBuffers(vboId);
		if(VertexDataBuffer!=null)
			memFree(VertexDataBuffer);
		if(IndeciesDataBuffer!=null)
			memFree(IndeciesDataBuffer);
		//glDeleteBuffers(vaoId);
		//memFree()
	}
	
	public void draw() {
		
		if(!isEmpty) {
		glBindBuffer(GL_ARRAY_BUFFER,vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex3texcoord.size*4, 0L);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, Vertex3texcoord.size*4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex3texcoord.size*4, 24);
		glDrawElements(GL_TRIANGLES,size,GL_UNSIGNED_INT,0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		}
	}

	public Transform getTranslation() {
		return translation;
	}

	public void setTranslation(Transform translation) {
		this.translation = translation;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
}