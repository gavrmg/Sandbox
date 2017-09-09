package ru.orion.sandbox.raytraycerenderer;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.opencl.CL12.*;
public class CLEnvironment {
	private PointerBuffer platforms;
	private PointerBuffer devices;
	private IntBuffer numPlatforms;
	private IntBuffer numDevices;
	private CLCapabilities platformCapabilities;
	private CLCapabilities capabilities;
	private PointerBuffer properties;
	private long context;
	private long commandQueue;
	public CLEnvironment() {
		int[] errorRet = new int[1];
		numPlatforms = memAllocInt(1);
		clGetPlatformIDs(null, numPlatforms);
		platforms = memAllocPointer(numPlatforms.get(0));
		clGetPlatformIDs(platforms,(IntBuffer)null);
		numDevices = memAllocInt(1);
		errorRet[0] = clGetDeviceIDs(platforms.get(0),CL_DEVICE_TYPE_GPU,null,numDevices);
		devices = memAllocPointer(numDevices.get(0));
		errorRet[0] =clGetDeviceIDs(platforms.get(0),CL_DEVICE_TYPE_GPU,devices,(IntBuffer)null);
		platformCapabilities = CL.createPlatformCapabilities(platforms.get(0));
		properties = memAllocPointer(3);
		properties.put(0,CL_CONTEXT_PLATFORM);
		properties.put(1,platforms.get(0));
		properties.put(2,0);
		capabilities = CL.createDeviceCapabilities(devices.get(0), platformCapabilities);
		CLContextCallback callback = CLContextCallback.create((errinfo, private_info, cb, user_data) -> {
            System.err.println("[LWJGL] cl_context_callback");
            System.err.println("\tInfo: " + memUTF8(errinfo));
        });
		try {
			context = clCreateContext(properties,devices,null,NULL,errorRet);
			commandQueue = clCreateCommandQueue(context, devices.get(0), NULL, errorRet);
		}
		catch(java.lang.IllegalArgumentException e) {
			//System.out.println(e.getMessage());
			e.printStackTrace();
			this.destroy();
		}
		
	}
	public PointerBuffer getPlatforms() {
		return platforms;
	}
	public void setPlatforms(PointerBuffer platforms) {
		this.platforms = platforms;
	}
	public PointerBuffer getDevices() {
		return devices;
	}
	public void setDevices(PointerBuffer devices) {
		this.devices = devices;
	}
	public IntBuffer getNumPlatforms() {
		return numPlatforms;
	}
	public void setNumPlatforms(IntBuffer numPlatforms) {
		this.numPlatforms = numPlatforms;
	}
	public IntBuffer getNumDevices() {
		return numDevices;
	}
	public void setNumDevices(IntBuffer numDevices) {
		this.numDevices = numDevices;
	}
	public long getContext() {
		return context;
	}
	public void setContext(long context) {
		this.context = context;
	}
	public long getCommandQueue() {
		return commandQueue;
	}
	public void setCommandQueue(long commandQueue) {
		this.commandQueue = commandQueue;
	}
	public void destroy() {
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
		clReleaseDevice(devices.get(0));
		
		memFree(devices);
		memFree(platforms);
		memFree(numPlatforms);
		memFree(numDevices);
		memFree(properties);
	}
}
