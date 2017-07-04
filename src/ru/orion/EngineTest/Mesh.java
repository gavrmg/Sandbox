package ru.orion.EngineTest;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
	private Vertex[] vertecies;
	private int[] indecies;
	private int vboId;
	private int iboId;
	private int vaoId;
	private int size;
	private Transform translation;
	
	public Mesh(Vertex[] vertecies, int[] indecies) {
		this.vertecies=vertecies;
		this.indecies=indecies;
		size = indecies.length;
		this.setTranslation(new Transform());
	}
	
	public void createMesh() {
		vboId = glGenBuffers();
		iboId = glGenBuffers();
		FloatBuffer buffer = Utils.createFlippedBuffer(vertecies);
		glBindBuffer(GL_ARRAY_BUFFER,vboId);
		glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
		Utils.disposeBuffer(buffer);
		IntBuffer intBuffer = Utils.createFlippedBuffer(indecies);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,intBuffer,GL_STATIC_DRAW);
		Utils.disposeBuffer(intBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.size*4, 0L);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.size*4, 12);
	}
	
	public void disposeMesh() {
		glDeleteBuffers(vboId);
		glDeleteBuffers(vaoId);
	}
	
	public void draw() {
		
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES,size,GL_UNSIGNED_INT,0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
	}

	public Transform getTranslation() {
		return translation;
	}

	public void setTranslation(Transform translation) {
		this.translation = translation;
	}
}
