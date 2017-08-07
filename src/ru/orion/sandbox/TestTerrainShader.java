package ru.orion.sandbox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestTerrainShader extends Shader{
	public TestTerrainShader() {
		super();
		addVertexShader(ResourceLoader.LoadShader("TestTerrainVertex.vs"));
		addFragmentShader(ResourceLoader.LoadShader("TestTerrainFragment.fs"));
		linkProgram();
		addUniform("transformationView");
		addUniform("Projection");
//		addUniform("View");
		addUniform("color");
		addUniform("MVIT"); //Model View Inverted Transposed, used for calculating normals;
		
	}
	
	public void updateUniforms(Matrix4f transformationView,Matrix4f Projection,Matrix4f ModelViewInvTransposed,Vector3f color) {
		setUniformMatrix4f("transformationView", transformationView);
		setUniformMatrix4f("Projection", Projection);
		setUniformMatrix4f("MVIT", ModelViewInvTransposed);
		setUniformVec3f("color", color);
	}
}