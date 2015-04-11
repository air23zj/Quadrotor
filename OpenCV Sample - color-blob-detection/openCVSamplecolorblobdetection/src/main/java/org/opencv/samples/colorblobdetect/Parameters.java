package org.opencv.samples.colorblobdetect;
/*
* version 1.0.
* RRT planner Java implimentation
* licensed under the GPL.
* Jian Zhang, 02/29/2015, @ Purdue Biorobotics Lab, ME.
*/



public class Parameters {
	public double res;
	public double thresh;
	public int maxiters;
	public double smoothiters;
	public double randxl;
	public double randxh;
	public double randyl;
	public double randyh;
	public  int widthplot;
	public  int scaleplot;
	
	public Parameters() {
	    this.res = 10.0;
	    this.thresh = 20.0;
	    this.maxiters = 100000;
	    this.smoothiters = 150;
	    this.randxl=0;
	    this.randxh=2000;
	    this.randyl=0;
	    this.randyh=2000;
	    this.widthplot=1000;
	    this.scaleplot=1;
	}
	
	public void setwidthplot(int newwidthplot)
	{
		this.widthplot=newwidthplot;
	}
	public void setscaleplot(int newscaleplot)
	{
		this.scaleplot=newscaleplot;
	}	
	public void setres(double newres)
	{
		this.res=newres;
	}	
	public void setthresh(double newthresh)
	{
		this.thresh=newthresh;
	}	
	public void setmaxiters(int newmaxiters)
	{
		this.maxiters=newmaxiters;
	}
	public void setsmoothiters(int newsmoothiters)
	{
		this.smoothiters=newsmoothiters;
	}
	public void setrandxl(double newrandxl)
	{
		this.randxl=newrandxl;
	}
	public void setrandxh(double newrandxh)
	{
		this.randxh=newrandxh;
	}
	public void setrandyl(double newrandyl)
	{
		this.randyl=newrandyl;
	}
	public void setrandyh(double newrandyh)
	{
		this.randyh=newrandyh;
	}
}

