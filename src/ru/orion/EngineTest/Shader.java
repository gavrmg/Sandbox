package ru.orion.sandbox;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.*;

public class Shader {
	private Vector3f tempVec;
	private int ShaderProgram;
	private HashMap<String,Integer> uniforms;
	
	public Shader() {
		tempVec = new Vector3f();
		ShaderProgram = glCreateProgram();
		if (ShaderProgram == 0) {
			System.err.println("Failed to create program");
			System.exit(1);
		}
		uniforms = new HashMap<String,Integer>();
	}
	
	private void createShaderProgram() {
		ShaderProgram = glCreateProgram();
		if (ShaderProgram == 0) {
			System.err.println("Failed to create program");
			System.exit(1);
		}
	};
	
	public void addVertexShader(String ShaderCode) {
		addShaderProgram(ShaderCode, GL_VERTEX_SHADER);
	}
	public void addGeometryShader(String ShaderCode) {
		addShaderProgram(ShaderCode, GL_GEOMETRY_SHADER);
	}
	public void addFragmentShader(String ShaderCode) {
		addShaderProgram(ShaderCode, GL_FRAGMENT_SHADER);
	}
	private void addShaderProgram(String program,int type) {
		int shader;
		shader = glCreateShader(type);
		if (shader == 0) {
			System.err.println("Failed to create shader");
			System.exit(1);
		}
		glShaderSource(shader, program);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS)==0) {
			System.err.println(glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		glAttachShader(ShaderProgram, shader);
	}
	public void linkProgram() {
		glLinkProgram(ShaderProgram);
		if(glGetProgrami(ShaderProgram, GL_LINK_STATUS)==0) {
			System.err.println(glGetShaderInfoLog(ShaderProgram, 1024));
			System.exit(1);
		}
		glValidateProgram(ShaderProgram);
		if(glGetProgrami(ShaderProgram, GL_VALIDATE_STATUS)==0) {
			System.err.println(glGetShaderInfoLog(ShaderProgram, 1024));
			System.exit(1);
		}

	}
	
	public void addUniform(String uniform) {
		int uniformLocation = glGetUniformLocation(ShaderProgram, uniform);
		uniforms.put(uniform, uniformLocation);
	}
	
	public void setUniformi(String uniform, int value) {
		glUniform1i(uniforms.get(uniform), value);
	}
	public void setUniformf(String uniform, float value) {
		glUniform1f(uniforms.get(uniform), value);
	}
	public void setUniformVec3f(String uniform, Vector3f value) {
		glUniform3f(uniforms.get(uniform), value.x, value.y, value.z);
	}
	public void setUniformMatrix4f(String uniform, Matrix4f value) {
		try(MemoryStack stack = stackPush()){
			FloatBuffer buffer = stack.mallocFloat(16);
			value.get(buffer);
			//buffer.flip();
			glUniformMatrix4fv(uniforms.get(uniform), false, buffer);
		}
	}
	
	public void addDiffLight(String light) {
		addUniform(light + ".color");
		addUniform(light + ".coeff");
		
	}
	public void setDiffLight(String lightName, DiffuseLight light) {
//		setUniformVec3f(lightName + ".color",light.getColor());
		setUniformf(lightName + ".color",1f);
		setUniformf(lightName + ".coeff",light.getCoeff());
		
	}
	public void addPointLight(String light) {
		addUniform(light + ".position");
		addUniform(light + ".color");
		addUniform(light + ".intensity");
		addUniform(light + ".attenuation.constant");
		addUniform(light + ".attenuation.linear");
		addUniform(light + ".attenuation.exponential");
		
	}
	public void setPointLight(String lightName, PointLight light,Matrix4f tv) {
		light.getPosition().mulPosition(tv, tempVec);
		setUniformVec3f(lightName + ".position",tempVec);
		setUniformVec3f(lightName + ".color",light.getColor());
		setUniformf(lightName + ".intensity",light.getIntensity());
//		PointLight
		setUniformf(lightName + ".attenuation.linear",light.getAttenuation().getLinear());
		setUniformf(lightName + ".attenuation.exponential",light.getAttenuation().getExponential());
		setUniformf(lightName + ".attenuation.constant",light.getAttenuation().getConstant());
	}
	
	public void updateUniforms() {
		
	};
		
	
	
	public void bind() {
		glUseProgram(ShaderProgram);
	}
	public void unbind() {
		glUseProgram(0);
	}
	private int getProgram() {
		return ShaderProgram;
	}
}
