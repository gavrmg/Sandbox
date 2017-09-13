package ru.orion.sandbox;

import static org.lwjgl.glfw.GLFW.*;

import javax.vecmath.Vector3f;

/*
 * This class is responsible for every input in game, and should be made into a state machine, based on whether the player is currently searching inventory, idling in the main menu, etc.
 */

public class Input {
	private long WindowID;
	private PlayerCharacter character;
	private InputState currentState,previousState;
	public Input(long WindowId,PlayerCharacter Character) {
		this.WindowID = WindowId;
		this.character = Character;
		temp = new Vector3f();
		Velocity = new Vector3f();
		AngularVelocity = new Vector3f();
		currentState = new InputState(WindowID);
		previousState = new InputState(WindowID);
		glfwSetInputMode(WindowID, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	private Vector3f temp;
	private Vector3f Velocity;
	private Vector3f AngularVelocity;
	public void update() {
		currentState.update();
		//TODO: Input basics
		AngularVelocity.set(0,0,0);
		temp.set(0,0,0);
		if(currentState.getW_KEY()==GLFW_PRESS) {
			//character.getForward().scaleAdd(2, temp);
			temp.add(character.getForward());
			//System.out.println("I should move forward!!!");
		}
		/*else if(currentState.getW_KEY()==GLFW_RELEASE&&previousState.getW_KEY()==GLFW_PRESS) {
			temp.sub(character.getForward());
			//System.out.println("I should stop!!!");
				}*/
		
		if(currentState.getA_KEY()==GLFW_PRESS) {
			//character.getForward().scaleAdd(2, temp);
			temp.sub(character.getRight());
			
		}
		/*else if(currentState.getA_KEY()==GLFW_RELEASE&&previousState.getA_KEY()==GLFW_PRESS) {
			temp.sub(character.getRight());
		}*/
		
		if(currentState.getS_KEY()==GLFW_PRESS) {
			//character.getForward().scaleAdd(2, temp);
			temp.sub(character.getForward());
			
		}
		/*else if(currentState.getS_KEY()==GLFW_RELEASE&&previousState.getS_KEY()==GLFW_PRESS) {
			temp.add(character.getForward());
		}*/
		
		if(currentState.getD_KEY()==GLFW_PRESS) {
			//character.getForward().scaleAdd(2, temp);
			temp.add(character.getRight());
			
		}
		/*else if(currentState.getD_KEY()==GLFW_RELEASE&&previousState.getD_KEY()==GLFW_PRESS) {
			temp.add(character.getRight());
		}*/
		//temp.normalize();
		if(currentState.getSPACE_KEY()==GLFW_PRESS&&(!character.isInAir())) {
			//character.getForward().scaleAdd(2, temp);
			character.getBody().applyCentralImpulse(new Vector3f(0,1,0));
		}
		double deltaX = currentState.getMouseX()[0]-previousState.getMouseX()[0];
		//if(deltaX!=0)
		//	System.out.println(deltaX);
		//AngularVelocity.scaleAdd((float)(currentState.getMouseX()[0]-previousState.getMouseX()[0]),character.getRight());
		AngularVelocity.scaleAdd((float)(currentState.getMouseX()[0]-previousState.getMouseX()[0]),character.getUp());
		character.getCamera().rotateY((float)(currentState.getMouseX()[0]-previousState.getMouseX()[0])/1000);
		character.getCamera().rotateX((float)(currentState.getMouseY()[0]-previousState.getMouseY()[0])/1000);
		character.getBody().setAngularFactor(0);
		//character.getBody().setAngularVelocity(AngularVelocity);
		//character.getBody().setA
		//character.getBody().getLinearVelocity(Velocity);
		character.getBody().setActivationState(4);
		if(temp.lengthSquared()!=0) {
			temp.normalize();
			//temp.scale(2);
			character.getBody().getLinearVelocity(Velocity);
			Velocity.x = temp.x*2;
			Velocity.z = temp.z*2;
		//if(temp.lengthSquared()!=0)a
		//Velocity.normalize();
			//Velocity.scale(2);
		//Velocity.y = temp.y;
		//temp.normalize();
		//temp.scale(6);
		//character.getBody().applyCentralForce(Velocity);
			character.getBody().setLinearVelocity(Velocity);
		}
		//character.getBody().;
		//System.out.println(temp);
		
		previousState.setState(currentState);
	
	}
	
	

	public PlayerCharacter getCharacter() {
		return character;
	}

	public void setCharacter(PlayerCharacter character) {
		this.character = character;
	}

	public void keyPressedCallback(long window, int key, int scancode, int action, int mods) {
		
	}
}
