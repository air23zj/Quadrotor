package org.opencv.samples.colorblobdetect;
public class PathNode{
	private double[] p;
	
	public PathNode() {
		this.p = new double[2];
		this.p[0]=0.0;
		this.p[1]=0.0;
	}
	
	public PathNode(double[] pin) {
		this.p = new double[2];
		this.p[0]=pin[0];
		this.p[1]=pin[1];
	}
	
	public void setp(double[] pin) {
		this.p[0]=pin[0];
		this.p[1]=pin[1];
	}
	
	public double[] getp() {
		return this.p;
	}
	
}