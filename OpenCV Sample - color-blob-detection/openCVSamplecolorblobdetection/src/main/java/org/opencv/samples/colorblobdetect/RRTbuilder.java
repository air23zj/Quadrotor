package org.opencv.samples.colorblobdetect;
/*
* version 1.0.
* RRT planner Java implimentation
* licensed under the GPL.
* Jian Zhang, 02/29/2015, @ Purdue Biorobotics Lab, ME.
*/
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.io.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class RRTbuilder {

	public Path path;
	public Robot rob;
	public Parameters param;
	
	
    public RRTbuilder()
    {
    	this.path=new Path();
    	this.rob=new Robot();
    	this.param= new Parameters();
    }
    
    public void MyLine( Mat img, Point start, Point end , char color)
    {
      int thickness = 2;
      switch(color)
      {
      	  case 'b':
      		Core.line(img,start,end,new Scalar( 255, 0, 0 ),thickness );
      		  break;
      	  case 'r':
      		Core.line(img,start,end,new Scalar( 0, 0, 255 ),thickness );
      		  break;
      	  case 'g':
      		Core.line(img,start,end,new Scalar( 0, 255, 0 ),thickness );
      		  break;
      	  default :
      		Core.line(img,start,end,new Scalar( 255, 0, 0 ),thickness );
      		  break;
      }
    }
    
    public void MyFilledCircle( Mat img, Point center, char color)
    {
      int thickness = 2;
      switch(color)
      {
      	  case 'b':
      		Core.circle( img,center,param.widthplot/100,new Scalar( 255, 0, 0 ),thickness);
      		  break;
      	  case 'r':
      		Core.circle( img,center,param.widthplot/100,new Scalar( 0, 0, 255 ),thickness);
      		  break;
      	  case 'g':
      		Core.circle( img,center,param.widthplot/100,new Scalar( 0, 255, 0 ),thickness);
      		  break;
      	  default :
      		Core.circle( img,center,param.widthplot/100,new Scalar( 255, 0, 0 ),thickness );
      		  break;
      }

    }
    
    public double randcpp(double lmin, double lmax)
    {
    	Random random = new Random();
    	double r;
    	r=random.nextDouble(); //uniformly distributed double value between 0.0 and 1.0
    	r=r*(lmax-lmin)+lmin;
    	return r;
    }
    
    public double[] linspace(double min, double max, int n)
    {
    	double[] result;
    	result=new double[n];
    	double step;
    	step=(max-min)/((double)(n-1));
    	
    	for (int i = 0; i < n; i++)
    	{
    		result[i]=min+step*((double)i);
    	}
    	return result;
    }
    
    public boolean InCollision_Node(Robot rob, Mat obst)
    {
    	boolean col=false;
	    int pixelx;
	    int pixely;
	    double pixel;	    
    	for(int j=0; j< 10; j++)
    	{
    		pixelx=(int)(rob.getp()[0]+rob.getballradius()*randcpp(-1.0, 1.0));
    		pixely=(int)(rob.getp()[1]+rob.getballradius()*randcpp(-1.0, 1.0));
    		if(pixelx>obst.cols()-1)
    		{
    			pixelx=obst.cols()-1;
    		}
    		if(pixely>obst.rows()-1)
    		{
    			pixely=obst.rows()-1;
    		}
    		pixel=obst.get(pixely, pixelx)[0];
    		if(pixel>100)
    		{
    			col=true;
    			return col;
    		}
    	}
    	return col;
    }
    public boolean InCollision_Edge(Robot rob,Mat obst,double[] p1, double[] p2, double res)
    {
    	boolean col=false;
    	double d;
    	int m;
    	
    	d=Math.sqrt(Math.pow((p1[0]-p2[0]),2)+Math.pow((p1[1]-p2[1]),2));
    	m=(int)Math.ceil(d/res);  	
    	double[] t;
    	t=linspace(0.0, 1.0, m);
    	for(int i=1;i<(m-1);i++)
    	{
    		rob.setp((1-t[i])*p1[0] + t[i]*p2[0],(1-t[i])*p1[1] + t[i]*p2[1]);
    		col = InCollision_Node(rob,obst);
    		if(col)
    		{
    			return col;
    		}
    	}
    	return col;
    }
    
    public int PlanPathRRT(Path path,Robot rob, Mat obst,Parameters param, double[] p_start,double[] p_goal, Mat map)
    {
    	int iterations=0;
    	RRT rrt=new RRT(); // define the tree
    	rrt.addNode(p_start,0);  // add the start point to the tree's first node to initialize the tree

    	int iter=1;
    	boolean col=false;
    	double dist=0.0;
    	double mindist=0.0;
    	Node rrts= new Node(); // define one node of the tree
    	int imin=0;
    	double[] l= new double[2];

    	while(iter<=param.maxiters)
    	{

    		double[] p= new double[2];
    		p[0] = (randcpp(0, 1.0))*(param.randxh-param.randxl)+param.randxl; // the next random point x is
    		p[1] = (randcpp(0, 1.0))*(param.randyh-param.randyl)+param.randyl; // the next random point y is 
    		
    		if(p[0]>obst.cols())
    		{
    			p[0]=obst.cols();
    		}
    		if(p[1]>obst.rows())
    		{
    			p[1]=obst.rows();
    		}
    		rob.setp(p);
    		
    		col = InCollision_Node(rob,obst);

    		if(col) // skip to next iteration
    		{
    			iter++;
    			continue;
    		}
    		//if new random point is valid
    		for(int i=0;i< (rrt.getnumnode());i++)
    		{
 
    			rrts=rrt.getNode(i);
    			dist=Math.sqrt(Math.pow((p[0]-rrts.getp()[0]),2.0)+Math.pow((p[1]-rrts.getp()[1]),2.0));

    			if( (i==0) || (dist < mindist))
    			{           mindist = dist;   //find minimum distance between the tree nodes and new random point
    			            imin = i+1; //find which tree node gives the minimum distance between the tree nodes and new random point
    			            l[0] = rrts.getp()[0];
    			            l[1] = rrts.getp()[1];
    			            //find which tree node gives the minimum distance between the tree nodes and new random point
    			}
    		}

    		col = InCollision_Edge(rob,obst,p,l,param.res); //check for valid edge between the shortest distance tree node and the new random point

    		if(col) // skip to next iteration
    		{
    			iter++;
    			continue;
    		}
    		// if edge is valid
    		rrt.addNode(p,imin);//add p to Tree with parent l

    		dist=Math.sqrt(Math.pow((p[0]-p_goal[0]),2.0)+Math.pow((p[1]-p_goal[1]),2.0));

    		MyLine(map,new Point(p[0]*param.scaleplot,p[1]*param.scaleplot ), new Point(rrt.getNode(imin-1).getp()[0]*param.scaleplot,rrt.getNode(imin-1).getp()[1]*param.scaleplot), 'g' );

    		if (dist < param.thresh)
    		{
    			col = InCollision_Edge(rob,obst,p,p_goal,param.res); //check for valid edge

    			if(col) // skip to next iteration
    			{
    				iter++;
    				continue;
    			}

    			iterations = iter;
    			// add goal to T and exit with success
    			rrt.addNode(p_goal,(rrt.getnumnode()));
    		
    			System.out.println( "Reach Goal !!");

    			//construct Q here:
    			int i=rrt.getnumnode();

    			path.addPathNode(rrt.back().getp());

    		    while (true)
    		    {
    		    	i = rrt.getNode(i-1).getiPrev();
    		        if (i == 0)
    		        {
    		        	return iterations;
    		        }
    		        path.addPathNode(rrt.getNode(i-1).getp());
    		    	//order reverse from Matlab implementation
    			}
    		}
    		iter++;
    	}
    	iterations = iter - 1;
    	return iterations;
    	
    }
	
    
    public Mat mapprocessor(Mat src)
    {
 	   /*
 	    * Erosion and dilation depends on the feature size and thus the size of the map
 	    * */
 	    int mapsizescaling=1; //gmap1 is 100ft zoom
 	    //int mapsizescaling=2; // gmap2 is 200ft zoom

 	    Mat src_gaussblur= new Mat(src.rows(),src.cols(),src.type());
 	    Imgproc.GaussianBlur(src, src_gaussblur, new Size(0,0), 2);
 	    
 	    Mat src_dilate= new Mat(src.rows(),src.cols(),src.type()); 
 	    Mat src_dilate_erode= new Mat(src.rows(),src.cols(),src.type()); 
         int erosion_size = 20/mapsizescaling;
         int dilation_size = erosion_size;
         Mat element0 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
         Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
         Imgproc.dilate(src_gaussblur, src_dilate, element0);
         Imgproc.erode(src_dilate, src_dilate_erode, element1);	
         
 	    Mat src_gaussblur2= new Mat(src.rows(),src.cols(),src.type());
 	    Imgproc.GaussianBlur(src_dilate_erode, src_gaussblur2, new Size(5,5), 0);
 	    Mat src_sharp= new Mat(src.rows(),src.cols(),src.type()); 
 	    Core.addWeighted(src_dilate_erode, 1.5, src_gaussblur2, -0.5, 0, src_sharp);
         
 	    Mat src_gray = new Mat(src.rows(),src.cols(),src.type()); 
 	    Imgproc.cvtColor( src_sharp, src_gray, Imgproc.COLOR_RGB2GRAY);
 	    
 	    Mat src_thresh= new Mat(src.rows(),src.cols(),src.type());
 	    Mat src_gaussblur3= new Mat(src.rows(),src.cols(),src.type());
 	    Imgproc.GaussianBlur(src_gray, src_gaussblur3, new Size(5,5), 0);
 	    Imgproc.threshold(src_gaussblur3,src_thresh,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
         int erosion_size1 = 10/mapsizescaling;
         int dilation_size1 = erosion_size1;
         Mat element01 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size1 + 1, 2*erosion_size1+1));
         Mat element11 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size1 + 1, 2*dilation_size1+1));
         Imgproc.dilate(src_thresh, src_thresh, element01);
         Imgproc.erode(src_thresh, src_thresh, element11);
         
         
 	    
 	   return src_thresh;

 	    
    }
    
}
