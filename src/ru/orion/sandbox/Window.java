package ru.orion.sandbox;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import com.sun.javafx.geom.Vec3f;

import ru.orion.sandbox.Renderer.RenderUtils;

public class Window {
	
	
// Зачем мне это? Перенести в Engine	
	
	
	
	private long WindowId;
	public Window(){
		if(!initGLCONTEXT()) {
			System.exit(1);
		};
		
	
	}
	
	public boolean initGLCONTEXT() {
		return glfwInit();
	}
	
	public void createWindow() {
		glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
		glfwWindowHint(GLFW_VERSION_MAJOR,4);
		glfwWindowHint(GLFW_VERSION_MINOR,5);
		glfwWindowHint(GL_FRONT_FACE,GL_CW);
		glfwWindowHint(GL_CULL_FACE,GL_BACK);
		
		WindowId = glfwCreateWindow(800,600,"LWJGL_TEST_2",NULL,NULL);
		EngineContext.windowID = WindowId;
		glfwMakeContextCurrent(WindowId);
		RenderUtils.initGraphics();
		glfwSwapInterval(1);
		GLFWFramebufferSizeCallback fsCallback = new GLFWFramebufferSizeCallback() {
			
			@Override
			public void invoke(long arg0, int arg1, int arg2) {
				ResizeCallback(arg0,arg1,arg2);
			}
		};
		glfwSetFramebufferSizeCallback(WindowId, fsCallback);
		
	}

	public boolean sholdClose() {
		return glfwWindowShouldClose(WindowId);
	}
	
	public void DisposeWindow() {
		
		glfwDestroyWindow(WindowId);
		glfwTerminate();
	}
	
	public void PollEvents() {
		glfwPollEvents();
	}
	public void UpdateScreen() {
		glfwSwapBuffers(WindowId);
	}
	
	public long getWindowId() {
		return WindowId;
	}
	
	private void ResizeCallback(long window,int width,int height) {
		EngineContext.SCREEN_WIDGTH = width;
		EngineContext.SCREEN_HEIGTH = height;
		glViewport(0, 0, width, height);
	}
}
