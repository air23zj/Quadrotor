package org.opencv.samples.colorblobdetect;
public class Robot {
	private double[] p;
	private double ballradius;
	
	public Robot() {
		this.p = new double[2];
		this.p[0]=0.0;
		this.p[1]=0.0;
		this.ballradius=0.0;
	}
	
	public Robot(double[] pin,double i) {
		this.p = new double[2];
		this.p[0]=pin[0];
		this.p[1]=pin[1];
		this.ballradius = i;
	}
	
	public void setballradius(double i) {
		this.ballradius = i;
	}
	
	public double getballradius() {
		return this.ballradius;
	}
	
	public void setp(double[] pin) {
		this.p[0]=pin[0];
		this.p[1]=pin[1];
	}

	public void setp(double pin0, double pin1) {
		this.p[0]=pin0;
		this.p[1]=pin1;
	}
	
	public double[] getp() {
		return this.p;
	}
	
}