package ru.orion.sandbox;

public class Attenuation {
	private float linear;
	private float exponential;
	private float constant;
	public Attenuation(float constant,float linear, float exponential) {
		this.setConstant(constant);
		this.setLinear(linear);
		this.setExponential(exponential);
		
	}
	public Attenuation(Attenuation atten) {
		this.constant = atten.constant;
		this.linear = atten.linear;
		this.exponential = atten.exponential;
	}
	public float getLinear() {
		return linear;
	}
	public void setLinear(float linear) {
		this.linear = linear;
	}
	public float getExponential() {
		return exponential;
	}
	public void setExponential(float exponential) {
		this.exponential = exponential;
	}
	public float getConstant() {
		return constant;
	}
	public void setConstant(float constant) {
		this.constant = constant;
	}

}
