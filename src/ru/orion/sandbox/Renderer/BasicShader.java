package ru.orion.sandbox.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import ru.orion.sandbox.ResourceLoader;

public class BasicShader extends Shader{
	public BasicShader() {
		super();
		addVertexShader(ResourceLoader.LoadShader("basicVertex.vs"));
		addFragmentShader(ResourceLoader.LoadShader("basicFragment.fs"));
		linkProgram();
		addUniform("transformation");
		addUniform("Projection");
		addUniform("View");
		addUniform("MVIT"); //Model View Inverted Transposed, used for calculating normals;
		addUniform("color");
		addUniform("id");
		
	}
	
	public void updateUniforms(Matrix4f transformation,Matrix4f Projection,Matrix4f View,Matrix4f ModelViewInvTransposed, Vector3f color) {
		setUniformMatrix4f("transformation", transformation);
		setUniformMatrix4f("Projection", Projection);
		setUniformMatrix4f("View", View);
		setUniformVec3f("color", color);
	}
}
