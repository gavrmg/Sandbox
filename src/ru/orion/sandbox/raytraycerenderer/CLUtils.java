package ru.orion.sandbox.raytraycerenderer;

import org.lwjgl.system.*;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL12.*;

import org.lwjgl.opencl.CL;

public class CLUtils {
	public static void initCL() {
		Configuration.OPENCL_EXPLICIT_INIT.set(true);
		CL.create();
	}
	public static void destroyCL() {
		CL.destroy();
	}

}
