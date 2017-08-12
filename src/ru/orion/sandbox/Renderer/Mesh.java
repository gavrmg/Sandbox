package ru.orion.sandbox.Renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ru.orion.sandbox.Utils;

public class Mesh {
	private Vertex[] vertecies;
	private int[] indecies;
	private int vboId;
	private int iboId;
	private int vaoId;
	private int size;
	private boolean isEmpty;
	private Transform translation;
	
	public Mesh(Vertex[] vertecies, int[] indecies) {
		this.vertecies=vertecies;
		this.indecies=indecies;
		size = indecies.length;
		this.setTranslation(new Transform());
		this.isEmpty=false;
	}
	
	public void createMesh() {
		if(vertecies.length==0)
			isEmpty = true;
		else {
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
			//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
			
		}
	}
	
	public void disposeMesh() {
		glDeleteBuffers(vboId);
		//glDeleteBuffers(vaoId);
	}
	
	public void draw() {
		
		if(!isEmpty) {
		glBindBuffer(GL_ARRAY_BUFFER,vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.size*4, 0L);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.size*4, 12);
		glDrawElements(GL_TRIANGLES,size,GL_UNSIGNED_INT,0);
		//glBindBuffer(GL_VERTEX_ARRAY,0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(1);
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
