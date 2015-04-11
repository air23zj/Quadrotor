package org.opencv.samples.colorblobdetect;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG  = "OCVSample::Activity";

    public static double[] p_start=new double[2]; //Set Start locations
	public static double[] p_goal=new double[2]; //Set  Goal locations
    
	private static final long ONE_MIN = 1000 * 60;
	private static final long TWO_MIN = ONE_MIN * 2;
	private static final long FIVE_MIN = ONE_MIN * 5;
	private static final long MEASURE_TIME = 1000 * 30;
	private static final long POLLING_FREQ = 1000 * 10;
	private static final float MIN_ACCURACY = 25.0f;
	private static final float MIN_LAST_READ_ACCURACY = 500.0f;
	private static final float MIN_DISTANCE = 10.0f;
	
	// Views for display location information
    GridView grid;
    static String[] letters=new String[4];
    
	// Current best location estimate
	private Location mBestReading;

	// Reference to the LocationManager and LocationListener
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	ImageView mImage;
    TextView mText;
    
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    private CameraBridgeViewBase mOpenCvCameraView;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                 // now we can call opencv code !
                    plantraj();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
		mImage = (ImageView)findViewById(R.id.imageView1);
		loadFigureFromAsset();
		grid = (GridView) findViewById(R.id.gridView1);
		
		// Acquire reference to the LocationManager
		if (null == (mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE)))
			finish();

		// Get best last location measurement
		mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

		// Display last reading information
		if (null != mBestReading) {

			updateDisplay(mBestReading);

		} else {

			letters[0]="No Initial Reading Available";
			letters[1]="No Initial Reading Available";
			letters[2]="No Initial Reading Available";
			letters[3]="No Initial Reading Available";
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letters);
			grid.setAdapter(adapter);
		}

		mLocationListener = new LocationListener() {

			// Called back when location changes

			public void onLocationChanged(Location location) {


				// Determine whether new location is better than current best
				// estimate

				if (null == mBestReading
						|| location.getAccuracy() < mBestReading.getAccuracy()) {

					// Update best estimate
					mBestReading = location;

					// Update display
					updateDisplay(location);

					if (mBestReading.getAccuracy() < MIN_ACCURACY)
						mLocationManager.removeUpdates(mLocationListener);

				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// NA
			}

			public void onProviderEnabled(String provider) {
				// NA
			}

			public void onProviderDisabled(String provider) {
				// NA
			}
		};
		
    }
    public void loadFigureFromAsset()
    {
   	 
        // load image
        try {
            // get input stream
            InputStream ims = getAssets().open("gmap1.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            mImage.setImageDrawable(d);
        }
        catch(IOException ex) {
        	return;
        }        
    }
    public void loadTextFromAsset() 
	{
	        // load text
	        try {
	            // get input stream for text
	            InputStream is = getAssets().open("text.txt");
	            // check size
	            int size = is.available();
	            // create buffer for IO
	            byte[] buffer = new byte[size];
	            // get data to buffer
	            is.read(buffer);
	            // close stream
	            is.close();
	            // set result to TextView
	            mText.setText(new String(buffer));
	        }
	        catch (IOException ex) {
	            return;
	        }
	}
    public Mat mapprocessor(Mat map)
    {
 	   /*
 	    * Erosion and dilation depends on the feature size and thus the size of the map
 	    * */
 	    int mapsizescaling=1; //gmap1 is 100ft zoom
 	    //int mapsizescaling=2; // gmap2 is 200ft zoom

 	    Mat map_gaussblur= new Mat(map.rows(),map.cols(),map.type());
 	    Imgproc.GaussianBlur(map, map_gaussblur, new Size(0,0), 2);
 	    
 	    Mat map_dilate= new Mat(map.rows(),map.cols(),map.type()); 
 	    Mat map_dilate_erode= new Mat(map.rows(),map.cols(),map.type()); 
        int erosion_size = 20/mapsizescaling;
        int dilation_size = erosion_size;
        Mat element0 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
        Imgproc.dilate(map_gaussblur, map_dilate, element0);
        Imgproc.erode(map_dilate, map_dilate_erode, element1);	
         
 	    Mat map_gaussblur2= new Mat(map.rows(),map.cols(),map.type());
 	    Imgproc.GaussianBlur(map_dilate_erode, map_gaussblur2, new Size(5,5), 0);
 	    Mat map_sharp= new Mat(map.rows(),map.cols(),map.type()); 
 	    Core.addWeighted(map_dilate_erode, 1.5, map_gaussblur2, -0.5, 0, map_sharp);
         
 	    Mat map_gray = new Mat(map.rows(),map.cols(),map.type()); 
 	    Imgproc.cvtColor( map_sharp, map_gray, Imgproc.COLOR_RGB2GRAY);
 	    
 	    Mat map_thresh= new Mat(map.rows(),map.cols(),map.type());
 	    Mat map_gaussblur3= new Mat(map.rows(),map.cols(),map.type());
 	    Imgproc.GaussianBlur(map_gray, map_gaussblur3, new Size(5,5), 0);
 	    Imgproc.threshold(map_gaussblur3,map_thresh,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        int erosion_size1 = 10/mapsizescaling;
        int dilation_size1 = erosion_size1;
        Mat element01 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size1 + 1, 2*erosion_size1+1));
        Mat element11 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size1 + 1, 2*dilation_size1+1));
        Imgproc.dilate(map_thresh, map_thresh, element01);
        Imgproc.erode(map_thresh, map_thresh, element11);
 	    return map_thresh;	    
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        // you may be tempted, to do something here, but it's *async*, and may take some time,
        // so any opencv call here will lead to unresolved native errors.
		// Determine whether initial reading is
		// "good enough"

		if (mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
				|| mBestReading.getTime() < System.currentTimeMillis()
						- TWO_MIN) {

			// Register for network location updates
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, POLLING_FREQ, MIN_DISTANCE,
					mLocationListener);

			// Register for GPS location updates
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE,
					mLocationListener);

			// Schedule a runnable to unregister location listeners

			Executors.newScheduledThreadPool(1).schedule(new Runnable() {

				@Override
				public void run() {

					Log.i(TAG, "location updates cancelled");

					mLocationManager.removeUpdates(mLocationListener);

				}
			}, MEASURE_TIME, TimeUnit.MILLISECONDS);
		}
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
        
    }

    public void displaythemap(Mat map, ImageView iv){
        // convert to bitmap:
        Bitmap mapbm = Bitmap.createBitmap(map.cols(), map.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(map, mapbm);

        // find the imageview and draw it!
        iv.setImageBitmap(mapbm);
    }
    
    public void plantraj()
    {
        
        mImage = (ImageView)findViewById(R.id.imageView1);
        p_start[0] = 100;
        p_start[1] = 500; //Set Start locations
    	p_goal[0] = 1000;
    	p_goal[1] = 700; //Set  Goal location
      
        RRTbuilder newrrtbuilder=new RRTbuilder();
        Mat map=new Mat();
		try {
			map = Utils.loadResource(this, R.drawable.gmap1, Highgui.CV_LOAD_IMAGE_COLOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    displaythemap(map, mImage);
		
	    Mat obst= new Mat(map.rows(),map.cols(),map.type());
	    obst=newrrtbuilder.mapprocessor(map);

	    displaythemap(obst, mImage);
	    
	    newrrtbuilder.param.setwidthplot(map.rows());
	    newrrtbuilder.param.setscaleplot(1);
	    newrrtbuilder.param.setrandxh(map.cols());
	    newrrtbuilder.param.setrandyh(map.rows());
	    newrrtbuilder.param.setres(10);
	    newrrtbuilder.param.setthresh(80); //distance to goal
	    
	    newrrtbuilder.rob.setp(p_start[0], p_start[1]);
	    newrrtbuilder.rob.setballradius(20);

	    newrrtbuilder.MyFilledCircle( map, new Point( newrrtbuilder.rob.getp()[0]*newrrtbuilder.param.scaleplot, newrrtbuilder.rob.getp()[1]*newrrtbuilder.param.scaleplot),'b');
	    newrrtbuilder.MyFilledCircle( map, new Point( p_goal[0]*newrrtbuilder.param.scaleplot, p_goal[1]*newrrtbuilder.param.scaleplot ),'r' );
	    
	    displaythemap(map, mImage);
	    
	    
	    int iterations=0;

	    //Plan the path
	    iterations=newrrtbuilder.PlanPathRRT(newrrtbuilder.path,newrrtbuilder.rob,obst,newrrtbuilder.param,p_start,p_goal,map);
	    
	    
	    //Plot the unsmoothed path
	    for(int i1=1;i1<newrrtbuilder.path.getnumPathNode();i1++)
	    {
	    	newrrtbuilder.MyLine(map,new Point(newrrtbuilder.path.getPathNode(i1).getp()[0]*newrrtbuilder.param.scaleplot,newrrtbuilder.path.getPathNode(i1).getp()[1]*newrrtbuilder.param.scaleplot), new Point(newrrtbuilder.path.getPathNode(i1-1).getp()[0]*newrrtbuilder.param.scaleplot,newrrtbuilder.path.getPathNode(i1-1).getp()[1]*newrrtbuilder.param.scaleplot), 'r' );
	    }
		
	    //Smooth the path
//	    P = SmoothPath(rob,obst,param,P);
	    /// Display final results!
	    
 	    //find contour
 	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();  
 	    Imgproc.findContours(obst, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
 	    Imgproc.drawContours(map, contours, -1, new Scalar(0,0,255));
		
		displaythemap(map, mImage);
    }
    
    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

	// Get the last known location from all providers
	// return best reading is as accurate as minAccuracy and
	// was taken no longer then minTime milliseconds ago

	private Location bestLastKnownLocation(float minAccuracy, long minTime) {

		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;

		List<String> matchingProviders = mLocationManager.getAllProviders();

		for (String provider : matchingProviders) {

			Location location = mLocationManager.getLastKnownLocation(provider);

			if (location != null) {

				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if (accuracy < bestAccuracy) {

					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;

				}
			}
		}

		// Return best reading or null
		if (bestAccuracy > minAccuracy || bestTime < minTime) {
			return null;
		} else {
			return bestResult;
		}
	}

	// Update display
	private void updateDisplay(Location location) {


		letters[0]="Accuracy:" + location.getAccuracy();
		letters[1]="Time:"
				+ new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale
						.getDefault()).format(new Date(location.getTime()));
		letters[2]="Longitude:" + location.getLongitude();
		letters[3]="Latitude:" + location.getLatitude();
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letters);
		grid.setAdapter(adapter);
	}

}


