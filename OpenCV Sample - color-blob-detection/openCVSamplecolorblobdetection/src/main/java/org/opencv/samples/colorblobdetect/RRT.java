package org.opencv.samples.colorblobdetect;
/*
* version 1.0.
* RRT planner Java implimentation
* licensed under the GPL.
* Jian Zhang, 02/29/2015, @ Purdue Biorobotics Lab, ME.
*/

import java.util.Vector;

public class RRT {
	private Vector<Node> tree;
	private int numnode;
	
	public RRT() {
		this.tree = new Vector<Node>();
		this.numnode=0;
	}
	
	public int getnumnode() {
		this.numnode=tree.size();
		return this.numnode;
	}
	
	public Node back() {
		return this.tree.lastElement();
	}
	
	public Node front() {
		return this.tree.firstElement();
	}
	
	public Node getNode(int pos) {
		return this.tree.get(pos);
	}
	
	public void addNode(double[] pin,int iPrevin) 
	{
		Node newnode=new Node(pin,iPrevin);
		this.tree.add(newnode);
	}
	public void addNode(Node newnode) // need newnode to be new for new memory space
	{
		this.tree.add(newnode);
	}	
	public Vector<Node> gettree() {
		return this.tree;
	}
	
}