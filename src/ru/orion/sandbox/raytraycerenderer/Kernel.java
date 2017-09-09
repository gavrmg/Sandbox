package ru.orion.sandbox.raytraycerenderer;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL12.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
//import static org.lwjgl.opencl.CLPlat;

//Kernel class written for easy kernel creation
public class Kernel {
	private String name;
	private String source;
	private CLEnvironment environment;
	private long program;
	private long kernel;
	private IntBuffer errCode;
	public Kernel(CLEnvironment environment,String source,String name) {
		this.name = name;
		this.source = source;
		this.environment = environment;
		errCode = memAllocInt(1);
	}
	
	public void compileKernel() {
		program = clCreateProgramWithSource(environment.getContext(), source, errCode);
		int i;
		if ((i = clBuildProgram(program,environment.getDevices(),"",null,NULL))!=0) {
			System.out.println(i);
			System.out.println(clGetProgramBuildInfo(program, environment.getDevices().get(0), CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, null));
		}
	}
	public void compileKernel(String compilatorParams) {
		program = clCreateProgramWithSource(environment.getContext(), source, errCode);
		int i;
		if ((i = clBuildProgram(program,environment.getDevices(),compilatorParams,null,NULL))!=0) {
			System.out.println(i);
			System.out.println(clGetProgramBuildInfo(program, environment.getDevices().get(0), CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, null));
		}
	}
	
	public void createKernel() {
		kernel = clCreateKernel(program,name,errCode);
	//	System.out.println(errCode.get(0));
	}
	
	public void destroy() {
		clReleaseKernel(kernel);
		clReleaseProgram(program);
		//clKernel
		memFree(errCode);
	}

	public long getProgram() {
		return program;
	}

	public void setProgram(long program) {
		this.program = program;
	}
	//returns the compiled kernel;
	public long getKernel() {
		return kernel;
	}

	public void setKernel(long kernel) {
		this.kernel = kernel;
	}
}
