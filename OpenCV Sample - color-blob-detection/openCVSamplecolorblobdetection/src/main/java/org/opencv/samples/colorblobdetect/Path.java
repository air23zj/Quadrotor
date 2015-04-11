package org.opencv.samples.colorblobdetect;
import java.util.Vector;

public class Path {
	private Vector<PathNode> path;
	private int numPathNode;
	
	public Path() {
		this.path = new Vector<PathNode>();
		this.numPathNode=0;
	}
	
	public int getnumPathNode() {
		this.numPathNode=path.size();
		return this.numPathNode;
	}
	
	public PathNode getPathNode(int pos) {
		return this.path.get(pos);
	}
	
	public PathNode back() {
		return this.path.lastElement();
	}
	
	public PathNode front() {
		return this.path.firstElement();
	}
	
	public void addPathNode(double[] pin) 
	{
		PathNode PathNodenew=new PathNode(pin);
		this.path.add(PathNodenew);
	}	
	public Vector<PathNode> getpath() {
		return this.path;
	}
	
}