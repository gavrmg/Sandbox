package ru.orion.EngineTest;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BasicShader extends Shader{
	public BasicShader() {
		super();
		addVertexShader(ResourceLoader.LoadShader("basicVertex.vs"));
		addFragmentShader(ResourceLoader.LoadShader("basicFragment.fs"));
		linkProgram();
		addUniform("transformation");
		addUniform("ProjectionView");
		addUniform("color");
		
	}
	
	public void updateUniforms(Matrix4f transformation,Matrix4f ProjectionView,Vector3f color) {
		setUniformMatrix4f("transformation", transformation);
		setUniformMatrix4f("ProjectionView", ProjectionView);
		setUniformVec3f("color", color);

	}
}
