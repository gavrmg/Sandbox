package ru.orion.EngineTest;

import jdk.nashorn.internal.runtime.linker.JavaAdapterFactory;

public class Engine {

	private boolean isRunning;

	private Window window;
	
	private Game game;
	
	private long startTime;
	private long pastTime;
	private long elapsed;
	private double remainingtoupdate;
	private double updateRate;
	private static double FRAMECAP = 10000;
	private static double loopSlot = 1f/120;
	private double endTime;
	public Engine() {
		isRunning = false;
		window = new Window();
		window.createWindow();
		game = new Game();
	//	window.setMesh();
		updateRate = 0.01;
		remainingtoupdate = 0;
		endTime = 0;
		//game.input().setWindowID(window.getID());
	}
	
	public void start() {
		if(isRunning) {
			return;
		}
		run();
	}
	
	public void stop() {
		if(!isRunning)
			return;
		isRunning = false;
	}
	
	public void run() {
		isRunning = true;
		pastTime = Time.getTime();
		while(isRunning) {
			startTime = Time.getTime();
			elapsed = startTime - pastTime;
			pastTime = Time.getTime();
			remainingtoupdate+= elapsed/(double)Time.SECOND;
			while(remainingtoupdate>=updateRate) {
				remainingtoupdate-=updateRate;
				game.update();
			}
			if(window.sholdClose())
				stop();
			window.PollEvents();
			RenderUtils.clearScreen();

			game.render();
			window.UpdateScreen();
			/*endTime = startTime+loopSlot;
			while(Time.getTime() < endTime) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//to free the resources;
			}*/
		}
		
		stop();
		cleanUp();
	}
	
	private void cleanUp() {
		window.DisposeWindow();
	}
}
