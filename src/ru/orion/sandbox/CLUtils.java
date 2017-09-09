package ru.orion.sandbox;

import org.lwjgl.*;
import org.lwjgl.opencl.*;
import org.lwjgl.system.Configuration;
public class CLUtils {
	public static void initCL() {
		//CL.destroy();
		Configuration.OPENCL_EXPLICIT_INIT.set(true);
		CL.create();
		
	}
	public static void destroyCL() {
		//CL.destroy();
		CL.destroy();
	}

}
