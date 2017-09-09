package ru.orion.sandbox;
import static org.lwjgl.glfw.GLFW.*;
public class InputState {
	private int W_KEY;
	private int A_KEY;
	private int S_KEY;
	private int D_KEY;
	private int SPACE_KEY;
	private double[] MouseX,MouseY;

	private long window;
	//private
	public InputState(long window) {
		W_KEY = GLFW_RELEASE;
		A_KEY = GLFW_RELEASE;
		S_KEY = GLFW_RELEASE;
		D_KEY = GLFW_RELEASE;
		SPACE_KEY = GLFW_RELEASE;
		this.window = window;
		MouseX = new double[1];
		MouseY = new double[1];
		glfwGetCursorPos(window, MouseX, MouseY);
	}
	public int getW_KEY() {
		return W_KEY;
	}
	public void setW_KEY(int w_KEY) {
		W_KEY = w_KEY;
	}
	public int getA_KEY() {
		return A_KEY;
	}
	public void setA_KEY(int a_KEY) {
		A_KEY = a_KEY;
	}
	public int getS_KEY() {
		return S_KEY;
	}
	public void setS_KEY(int s_KEY) {
		S_KEY = s_KEY;
	}
	public int getD_KEY() {
		return D_KEY;
	}
	public void setD_KEY(int d_KEY) {
		D_KEY = d_KEY;
	}
	public void setState(InputState state) {
		this.W_KEY = state.getW_KEY();
		this.A_KEY = state.getA_KEY();
		this.S_KEY = state.getS_KEY();
		this.D_KEY = state.getD_KEY();
		this.SPACE_KEY = state.getSPACE_KEY();
		this.MouseX[0] = state.getMouseX()[0];
		this.MouseY[0] = state.getMouseY()[0];
	}
	public double[] getMouseX() {
		return MouseX;
	}
	public void setMouseX(double[] mouseX) {
		MouseX = mouseX;
	}
	public double[] getMouseY() {
		return MouseY;
	}
	public void setMouseY(double[] mouseY) {
		MouseY = mouseY;
	}
	public void update() {
		W_KEY = glfwGetKey(window, GLFW_KEY_W);
		A_KEY = glfwGetKey(window, GLFW_KEY_A);
		S_KEY = glfwGetKey(window, GLFW_KEY_S);
		D_KEY = glfwGetKey(window, GLFW_KEY_D);
		SPACE_KEY = glfwGetKey(window, GLFW_KEY_SPACE);
		glfwGetCursorPos(window, MouseX, MouseY);
	}
	public int getSPACE_KEY() {
		return SPACE_KEY;
	}
	public void setSPACE_KEY(int sPACE_KEY) {
		SPACE_KEY = sPACE_KEY;
	}
}
