package ru.orion.sandbox;

public class Time {
	
	private static long delta;
	
	public static long SECOND = 1000000000L;//1 second in nanoseconds;
	
	public static long getTime() {
		return System.nanoTime();
	}
	
	public static long getDelta() {
		return delta;
	}
	public static void setDelta(long delta) {
		Time.delta = delta;
	}
}
