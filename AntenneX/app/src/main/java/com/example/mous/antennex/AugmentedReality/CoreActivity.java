package com.example.mous.antennex.augmentedReality;




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

import com.example.mous.antennex.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoreActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, OnRollChangedListener {

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraViewOn = false;
    private AugmentedPOI mPoi;

    private double mRollReal = 0;
    private double mRollTheoretical = 0;
    private static double ROLL_ACCURACY =17;
    private double mAzimuthReal = 0;
    private double mAzimuthTheoretical = 0;
    private static double AZIMUTH_ACCURACY = 25;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;
    private double mMyAltitude = 0;
    private MyCurrentRoll myCurrentRoll;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    ImageView pointerIcon;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setupListeners();
        setupLayout();
        setAugmentedRealityPoint();

    }

    // POUR CALCULER LE ROLL ANGLE THETA :


    public double calculateTheoreticalRoll(AugmentedPOI poi) {
        // Calculates Roll angle  of POI
        double dy = poi.getPoiLatitude() - mMyLatitude;
        double dx = poi.getPoiLongitude() - mMyLongitude;
        double dz = poi.getPoiAltitude() - mMyAltitude;

        double tanTheta;
        double theta;

        tanTheta= Math.abs(dz/ Math.sqrt(dx*dx+dy*dy));
        theta = Math.atan(tanTheta);

        return theta;
    }


    private void setAugmentedRealityPoint() {

        // @TODO : METTRE DES VALEURS FONCTIONNELES
        mPoi = new AugmentedPOI(
                "NITK",
                "Surathkal",
                2.27917,
                48.84694, 60.4
        );
    }

    public double calculateTheoreticalAzimuth(AugmentedPOI poi) {
        // Calculates azimuth angle (phi) of POI
        double dy = poi.getPoiLatitude() - mMyLatitude;
        double dx = poi.getPoiLongitude() - mMyLongitude;

        double phiAngle;
        double tanPhi;

        tanPhi = Math.abs(dx / dy);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        // phiAngle = [0,90], check quadrant and return correct phiAngle
        if (dy > 0 && dx > 0) { // I quadrant
            return phiAngle;
        } else if (dy < 0 && dx > 0) { // II
            return 180 - phiAngle;
        } else if (dy < 0 && dx < 0) { // III
            return 180 + phiAngle;
        } else if (dy > 0 && dx < 0) { // IV
            return 360 - phiAngle;
        }

        return phiAngle;
    }





    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        // Returns the Camera View Sector
        List<Double> minMax = new ArrayList<Double>();
        double minAngle = (azimuth - AZIMUTH_ACCURACY + 360) % 360;
        double maxAngle = (azimuth + AZIMUTH_ACCURACY) % 360;
        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);
        return minMax;
    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
        // Checks if the azimuth angle lies in minAngle and maxAngle of Camera View Sector
        if (minAngle > maxAngle) {
            if (isBetween(0, maxAngle, azimuth) || isBetween(minAngle, 360, azimuth))
                return true;
        } else if (azimuth > minAngle && azimuth < maxAngle)
            return true;
        return false;
    }

    private void updateDescription() {
        descriptionTextView.setText("Le POI est ici : " + mPoi.getPoiName() + " azimuthTheoretical "
                + mAzimuthTheoretical + " azimuthReal " + mAzimuthReal + " latitude: "
                + mMyLatitude + " longitude: " + mMyLongitude  + "altitude: "+ mMyAltitude + "camera angle: " +ROLL_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Function to handle Change in Location
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        mMyAltitude = location.getAltitude();
        mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);
        updateDescription();
    }
/*

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        mAzimuthReal = azimuthChangedTo;
        mAzimuthTheoretical = calculateTheoreticalAzimuth();

        // Since Camera View is perpendicular to device plane
        mAzimuthReal = (mAzimuthReal+90)%360;

        pointerIcon = (ImageView) findViewById(R.id.icon);

        double minAngle = calculateAzimuthAccuracy(mAzimuthReal).get(0);
        double maxAngle = calculateAzimuthAccuracy(mAzimuthReal).get(1);

        if (isBetween(minAngle, maxAngle, mAzimuthTheoretical)) {
            float ratio = ((float) (mAzimuthTheoretical - minAngle + 360.0) % 360) / ((float) (maxAngle - minAngle + 360.0) % 360);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = (int) (display.getHeight() * ratio);
            lp.leftMargin = display.getWidth()/2 - pointerIcon.getWidth();
            pointerIcon.setLayoutParams(lp);
            pointerIcon.setVisibility(View.VISIBLE);
        } else {
            pointerIcon.setVisibility(View.GONE);
        }

        updateDescription();
    }
*/

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        mAzimuthReal = azimuthChangedTo;
        mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);


        // Since Camera View is perpendicular to device plane
        mAzimuthReal = (mAzimuthReal+90)%360;

        pointerIcon = (ImageView) findViewById(R.id.icon);

        consequenceIsBetween(mAzimuthTheoretical,pointerIcon);
         updateDescription();
    }



    // To handle Change in azimuth angle
    public void onRollChanged(float rollChangedFrom, float rollChangedTo) {

        mRollReal = rollChangedTo;
        mRollTheoretical = calculateTheoreticalAzimuth(mPoi);

        // parce que sinon 0° vers le sol
        mRollReal = (mRollReal-90)%180;

        pointerIcon = (ImageView) findViewById(R.id.icon);
        consequenceIsBetween(mRollTheoretical,pointerIcon);


        updateDescription();
    }



    public void consequenceIsBetween(double azimuthTheoretical, ImageView pointer){

        double minAngle = calculateAzimuthAccuracy(mAzimuthReal).get(0);
        double maxAngle = calculateAzimuthAccuracy(mAzimuthReal).get(1);

        if (isBetween(minAngle, maxAngle, azimuthTheoretical)) {
            float ratio = ((float) (azimuthTheoretical - minAngle + 360.0) % 360) / ((float) (maxAngle - minAngle + 360.0) % 360);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = (int) (display.getHeight() * ratio);
            lp.leftMargin = display.getWidth()/2 - pointer.getWidth();
            pointer.setLayoutParams(lp);
            pointer.setVisibility(View.VISIBLE);
        } else {
            pointer.setVisibility(View.GONE);
        }
    }



    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        myCurrentRoll.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
        myCurrentRoll.start();
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();

        myCurrentRoll= new MyCurrentRoll(this, this);
        myCurrentRoll.start();
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
