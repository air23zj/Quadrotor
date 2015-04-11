package org.opencv.samples.colorblobdetect;
/*
* version 1.0.
* RRT planner Java implimentation
* licensed under the GPL.
* Jian Zhang, 02/29/2015, @ Purdue Biorobotics Lab, ME.
*/


public class Node {
	private double[] p;
	private int iPrev;
	
	public Node() {
		this.p = new double[2];
		this.p[0]=0.0;
		this.p[1]=0.0;
		this.iPrev=0;
	}
	
	public Node(double[] pin,int newiPrev) {
		this.p = new double[2];
		this.p[0]=pin[0];
		this.p[1]=pin[1];
		this.iPrev = newiPrev;
	}

	public Node(double[] pin) {
		this.p = new double[2];
		this.p[0]=pin[0];
		this.p[1]=pin[1];
		this.iPrev = 0;
	}
	
	public void setiPrev(int newiPrev) {
		this.iPrev = newiPrev;
	}
	
	public int getiPrev() {
		return this.iPrev;
	}
	
	public void setp(double[] pin) {
		this.p[0]=pin[0];
		this.p[1]=pin[1];
	}
	
	public double[] getp() {
		return this.p;
	}
	
}