package com.hrily.artutorial;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener {
    private ArrayList<AntenneModel> antenneList;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraViewOn = false;
    private AugmentedPOI mPoi;
    private RelativeLayout imageLayout;
    private double mAzimuthReal = 0;
//  private double mAzimuthTheoretical = 0;
//  private static double AZIMUTH_ACCURACY = 25;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;
    private Location oldLocation;

    TextView descriptionTextView;
//  ImageView pointerIcon;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        oldLocation = new Location("0,0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLayout = (RelativeLayout)findViewById(R.id.image_layout) ;
        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setupListeners();
        setupLayout();
        setAugmentedRealityPoint();

    }


    private void setAugmentedRealityPoint() {
        this.antenneList = new ArrayList<>();

        antenneList.add(new AntenneModel(
                "NITK",
                "Surathkal",
                48.827839, 2.346874
        ));
        antenneList.add(new AntenneModel(
                "NITK1",
                "Surathkal",
                48.827006, 2.347727
        ));
        antenneList.add(new AntenneModel(
                "NITK2",
                "Surathkal",
                48.826441, 2.346708
        ));
        antenneList.add(new AntenneModel(
                "NITK3",
                "Surathkal",
                48.827041, 2.345780
        ));

        ImageView imageView;
        for(AntenneModel antenne : this.antenneList){
            imageView = new ImageView(this);
            antenne.setImageView(imageView);

            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(R.mipmap.ic_launcher); //图片资源drawable/sym_def_app_icon
            this.imageLayout.addView(imageView); //动态添加图片

        }

    }

//    public double calculateTheoreticalAzimuth() {
//        // Calculates azimuth angle (phi) of POI
//        double dy = mPoi.getPoiLatitude() - mMyLatitude;
//        double dx = mPoi.getPoiLongitude() - mMyLongitude;
//
//        double phiAngle;
//        double tanPhi;
//
//        tanPhi = Math.abs(dx / dy);
//        phiAngle = Math.atan(tanPhi);
//        phiAngle = Math.toDegrees(phiAngle);
//
//        // phiAngle = [0,90], check quadrant and return correct phiAngle
//        if (dy > 0 && dx > 0) { // I quadrant
//            return phiAngle;
//        } else if (dy < 0 && dx > 0) { // II
//            return 180 - phiAngle;
//        } else if (dy < 0 && dx < 0) { // III
//            return 180 + phiAngle;
//        } else if (dy > 0 && dx < 0) { // IV
//            return 360 - phiAngle;
//        }
//
//        return phiAngle;
//    }

//    private List<Double> calculateAzimuthAccuracy(double azimuth) {
//        // Returns the Camera View Sector
//        List<Double> minMax = new ArrayList<Double>();
//        double minAngle = (azimuth - AZIMUTH_ACCURACY + 360) % 360;
//        double maxAngle = (azimuth + AZIMUTH_ACCURACY) % 360;
//        minMax.clear();
//        minMax.add(minAngle);
//        minMax.add(maxAngle);
//        return minMax;
//    }

//    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
//        // Checks if the azimuth angle lies in minAngle and maxAngle of Camera View Sector
//        if (minAngle > maxAngle) {
//            if (isBetween(0, maxAngle, azimuth) || isBetween(minAngle, 360, azimuth))
//                return true;
//        } else if (azimuth > minAngle && azimuth < maxAngle)
//            return true;
//        return false;
//    }

    private void updateDescription(Location location) {
        descriptionTextView.setText("Location:" + location.toString() + "\n"
                + "oldLocation:" + oldLocation.toString()
                + " azimuthReal " + mAzimuthReal );
        Toast.makeText(this,"update descripiton",Toast.LENGTH_SHORT);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Function to handle Change in Location
       // if(oldLocation.distanceTo(location) > 100) {
            mMyLatitude = location.getLatitude();
            mMyLongitude = location.getLongitude();
            for(AntenneModel antenne : antenneList) {
                antenne.calculateTheoreticalAzimuth(mMyLatitude, mMyLongitude);
            }
            oldLocation = location;
        //}

        updateDescription(location);
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        for(AntenneModel antenne : antenneList) {
            mAzimuthReal = azimuthChangedTo;
            antenne.calculateTheoreticalAzimuth(mMyLatitude, mMyLongitude);
            mAzimuthReal = (mAzimuthReal+90)%360;
            double minAngle = antenne.calculateAzimuthAccuracy(mAzimuthReal).get(0);
            double maxAngle = antenne.calculateAzimuthAccuracy(mAzimuthReal).get(1);
            if (antenne.isBetween(minAngle, maxAngle)) {
                float ratio = ((float) (antenne.getmAzimuthTheoretical() - minAngle + 360.0) % 360) / ((float) (maxAngle - minAngle + 360.0) % 360);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) (display.getHeight() * ratio);
                lp.leftMargin = display.getWidth()/2 - antenne.getImageView().getWidth();
                antenne.getImageView().setLayoutParams(lp);
                antenne.getImageView().setVisibility(View.VISIBLE);
            } else {
                antenne.getImageView().setVisibility(View.GONE);
            }

        }

        // Since Camera View is perpendicular to device plane

    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();
    }

    private void setupLayout() {
        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (isCameraViewOn) {
            mCamera.stopPreview();
            isCameraViewOn = false;
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                isCameraViewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraViewOn = false;
    }

}
