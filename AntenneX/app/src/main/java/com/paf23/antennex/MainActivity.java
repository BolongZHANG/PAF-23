package com.paf23.antennex;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Location;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, OnRollChangedListener {

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraViewOn = false;
    private int nb_antennes = 0;
    private int listSize = 100;
    //private AugmentedPOI mPoi, mPoi2;
    ArrayList<AugmentedPOI> mPoiList = new ArrayList<AugmentedPOI>(listSize);

    private double mAzimuthReal = 0;
    private double mRollReal = 0;
    //private double mAzimuthTheoretical = 0;
    //private double mAzimuthTheoretical2 = 0;
    ArrayList<Double> mAzimuthTheoreticalList = new ArrayList<Double>(listSize);
    //private double mRollTheoretical = 0;
    //private double mRollTheoretical2 = 0;
    ArrayList<Double> mRollTheoreticalList = new ArrayList<Double>(listSize);

    private static double AZIMUTH_ACCURACY = 25;
    private static double ROLL_ACCURACY =20;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;
    private double mMyAltitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentRoll myCurrentRoll;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    //ImageView pointerIcon;
    //ImageView pointerIcon2;
    ArrayList<ImageView> pointerIconList = new ArrayList<ImageView>(listSize);
    ArrayList<Integer> viewId = new ArrayList<Integer>(listSize);
     private ImageView imageView;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeArrays();
        setContentView(R.layout.activity_main);
        setAugmentedRealityPoint();
        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        setupListeners();
        setupLayout();



    }


    private void setAugmentedRealityPoint() {
        /*mPoi = new AugmentedPOI(
                "Sacré-Coeur",
                "Romantisme à  l'état pur",
                48.886705,
                2.343104, 207
        );
        mPoi2 = new AugmentedPOI("Eiffel Tower","symbol of Paris",48.85837009999999,2.2944813000000295,358);*/

        mPoiList.add(new AugmentedPOI(
                "Sacré-Coeur",
                "Romantisme à  l'état pur",
                48.886705,
                2.343104, 207
        ));
        mPoiList.add(new AugmentedPOI("Eiffel Tower","symbol of Paris",48.85837009999999,2.2944813000000295,358));
        mPoiList.add(new AugmentedPOI("Strasbourg","sausages&beer",48.573405,7.752111,147));

        nb_antennes= mPoiList.size();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.imageView_layout);
        for (int i = 0; i < nb_antennes; i++) {

            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80, 60));
            image.setImageResource(R.drawable.antenna_rotated);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // Adds the view to the layout
            layout.addView(image);
            pointerIconList.set(i,image);
        }
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

    public double calculateTheoreticalRoll(AugmentedPOI poi) {
        // Calculates Roll angle  of POI
        double d = distance(poi.getPoiLatitude(), poi.getPoiLongitude(), mMyLatitude,mMyLongitude);
        Double dz = poi.getPoiAltitude() - mMyAltitude;
        double tanTheta;
        double theta;

        tanTheta= Math.abs(dz/d);
        theta = Math.atan(tanTheta);
        theta = theta *180/Math.PI;
        Double thheta = (Double) theta;
        Log.d("Sylvaintheta","theta "+ thheta.toString());
        return theta;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        // Returns the Camera View Sector horizontally
        List<Double> minMax = new ArrayList<Double>();
        double minAngle = (azimuth - AZIMUTH_ACCURACY + 360) % 360;
        double maxAngle = (azimuth + AZIMUTH_ACCURACY) % 360;
        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);
        return minMax;
    }

    private List<Double> calculateRollAccuracy(double roll) {
        // Returns the Camera View Sector vertically


        List<Double> minMax = new ArrayList<Double>();
        double minAngle = (roll - ROLL_ACCURACY);// % 180;
        double maxAngle = (roll + ROLL_ACCURACY);// % 180;
        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);
        return minMax;
    }

    private boolean azimuthIsBetween(double minAngle, double maxAngle, double azimuth) {
        // Checks if the azimuth angle lies in minAngle and maxAngle of Camera View Sector
        if (minAngle > maxAngle) {
            if (azimuthIsBetween(0, maxAngle, azimuth) || azimuthIsBetween(minAngle, 360, azimuth))
                return true;
        } else if (azimuth > minAngle && azimuth < maxAngle)
            return true;
        return false;
    }

    private boolean rollIsBetween(double minAngle, double maxAngle, double roll) {
        // Checks if the azimuth angle lies in minAngle and maxAngle of Camera View Sector
        /*if (minAngle > maxAngle) {
            if (rollIsBetween(0, maxAngle, roll) || rollIsBetween(minAngle, 360, roll))
                return true;
        } else if (roll > minAngle && roll < maxAngle)*/
        Boolean test = roll> minAngle && roll< maxAngle;
        Double minangle = minAngle;
        Double maxangle = maxAngle;
        Log.d("Sylvain", "AHLALALALALALA   " + minangle.toString()+ "  " + maxangle.toString() + " " + roll +" " + test.toString());
        if (roll > minAngle && roll < maxAngle)
            return true;
        else
            return false;
    }

    private void updateDescription() {
        /*descriptionTextView.setText(mPoi2.getPoiName() + " azimuthTheoretical2 " // faudrait le faire pour les autres poi aussi
                + mAzimuthTheoretical2 + " azimuthReal " + mAzimuthReal + " rollTheoretical2 "+ mRollTheoretical2 + " RollReal " + mRollReal + " latitude "
                + mMyLatitude + " longitude " + mMyLongitude + " altitude " + mMyAltitude +  " angle caméra " + ROLL_ACCURACY);*/
        descriptionTextView.setText(mPoiList.get(2).getPoiName() + " azimuthTheoretical2 " // faudrait le faire pour les autres poi aussi
                + mAzimuthTheoreticalList.get(2) + " azimuthReal " + mAzimuthReal + " rollTheoretical2 "+ mRollTheoreticalList.get(2) + " RollReal " + mRollReal + " latitude "
                + mMyLatitude + " longitude " + mMyLongitude + " altitude " + mMyAltitude +  " angle caméra " + ROLL_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Function to handle Change in Location
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        mMyAltitude = location.getAltitude();
        /*mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);
        mRollTheoretical = calculateTheoreticalRoll(mPoi);
        mAzimuthTheoretical2 = calculateTheoreticalAzimuth(mPoi2);//faudrait gÃ©nÃ©raliser pour une liste
        mRollTheoretical2 = calculateTheoreticalRoll(mPoi2);*/
        for (int i = 0; i < nb_antennes; i++) {
            mAzimuthTheoreticalList.set(i, calculateTheoreticalAzimuth(mPoiList.get(i)));
            mRollTheoreticalList.set(i, calculateTheoreticalRoll(mPoiList.get(i)));
        }
            updateDescription();
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        mAzimuthReal = azimuthChangedTo;
        //mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);
        //mAzimuthTheoretical2 = calculateTheoreticalAzimuth(mPoi2);
        for (int i = 0; i < nb_antennes; i++) {
            mAzimuthTheoreticalList.set(i, calculateTheoreticalAzimuth(mPoiList.get(i)));
        }

        // Since Camera View is perpendicular to device plane
        mAzimuthReal = (mAzimuthReal+90)%360;

        //pointerIcon = (ImageView) findViewById(R.id.icon);
        //pointerIcon2 = (ImageView) findViewById(R.id.icon2);

        /*RelativeLayout layout = (RelativeLayout) findViewById(R.id.imageView_layout);
        for (int i = 0; i < nb_antennes; i++) {

            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setImageResource(R.drawable.antenna_rotated);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewId.set(i,image.generateViewId()); // generate a valid id
            // Adds the view to the layout
            layout.addView(image);
            pointerIconList.set(i,(ImageView) findViewById(viewId.get(i)));
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),image);
        }*/

        for (int i =0; i<nb_antennes;i++){
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),pointerIconList.get(i));
        }

        //consequenceIsBetween(mAzimuthTheoretical,mRollTheoretical,pointerIcon);
        //consequenceIsBetween(mAzimuthTheoretical2,mRollTheoretical2,pointerIcon2);

        updateDescription();
    }

    public void onRollChanged(float rollChangedFrom, float rollChangedTo) {
        // Function to handle Change in azimuth angle
        mRollReal = rollChangedTo;
        //mRollTheoretical = calculateTheoreticalRoll(mPoi);
        //mRollTheoretical2 = calculateTheoreticalRoll(mPoi2);
        for (int i = 0; i < nb_antennes; i++) {
            mRollTheoreticalList.set(i, calculateTheoreticalRoll(mPoiList.get(i)));
        }

        // parce que sinon 0° vers le sol
        mRollReal = (mRollReal-90)%180; // problème à régler pour ceux qui regardent la tête à l'envers

        //pointerIcon = (ImageView) findViewById(R.id.icon);
        //pointerIcon2 = (ImageView) findViewById(R.id.icon2);

        /*RelativeLayout layout = (RelativeLayout) findViewById(R.id.imageView_layout);
        for (int i = 0; i < nb_antennes; i++) {

            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setImageResource(R.drawable.antenna_rotated);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewId.set(i,image.generateViewId()); // generate a valid id
            // Adds the view to the layout
            layout.addView(image);
            pointerIconList.set(i,(ImageView) findViewById(viewId.get(i)));
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),image );
        }*/

        for (int i =0; i<nb_antennes;i++){
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),pointerIconList.get(i));
        }

        //consequenceIsBetween(mAzimuthTheoretical,mRollTheoretical,pointerIcon);
        //consequenceIsBetween(mAzimuthTheoretical2,mRollTheoretical2,pointerIcon2);

        updateDescription();
    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentRoll.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentRoll.start();
        myCurrentLocation.start();
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentRoll= new MyCurrentRoll(this, this);
        myCurrentAzimuth.start();
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
        Camera.Parameters p = mCamera.getParameters();
        ROLL_ACCURACY = (p.getVerticalViewAngle()%360/2);
        AZIMUTH_ACCURACY = (p.getHorizontalViewAngle()%360)/2;
        Double r = ROLL_ACCURACY; Double a = AZIMUTH_ACCURACY;
        Log.d("Sylvainn", r.toString() + "  "+ a.toString());
        mCamera.setDisplayOrientation(90);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraViewOn = false;
    }

    public void consequenceIsBetween(double azimuthTheoretical, double rollTheoretical, ImageView pointer){

        double minAngleA = calculateAzimuthAccuracy(mAzimuthReal).get(0);
        double maxAngleA = calculateAzimuthAccuracy(mAzimuthReal).get(1);
        double minAngleR = calculateRollAccuracy(mRollReal).get(0);
        double maxAngleR = calculateRollAccuracy(mRollReal).get(1);


        if (azimuthIsBetween(minAngleA, maxAngleA, azimuthTheoretical)&& rollIsBetween(minAngleR, maxAngleR, rollTheoretical)) {
            float ratioAzimuth = ((float) (azimuthTheoretical - minAngleA + 360.0) % 360) / ((float) (maxAngleA - minAngleA + 360.0) % 360);
            float ratioRoll = ((float) (rollTheoretical - minAngleR) % 180) / ((float) (maxAngleR - minAngleR) % 180);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = (int) (display.getHeight() * ratioAzimuth);
            lp.leftMargin = (int) (display.getWidth() * ratioRoll ) ;
            pointer.setLayoutParams(lp);
            pointer.setVisibility(View.VISIBLE);
        } else {
            pointer.setVisibility(View.GONE);
        }
    }

    //Conversion des degrés en radian
    public double convertToRad(double degrees){
        return (Math.PI * degrees)/180;
    }

    public double distance( double lat_a_degre,  double lon_a_degre, double lat_b_degre,  double lon_b_degre) {

        double R = 6378000; //Rayon de la terre en mètre

        double lat_a = convertToRad(lat_a_degre);
        double lon_a = convertToRad(lon_a_degre);
        double lat_b = convertToRad(lat_b_degre);
        double lon_b = convertToRad(lon_b_degre);

        double d = R * (Math.PI / 2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));

        return d;
    }

    private void initializeArrays(){
        mAzimuthTheoreticalList = new ArrayList<Double>(Collections.nCopies(listSize, 0.));
        mRollTheoreticalList = new ArrayList<Double>(Collections.nCopies(listSize, 0.));
        viewId = new ArrayList<Integer>(Collections.nCopies(listSize, 0));
        pointerIconList = new ArrayList<ImageView>(Collections.nCopies(listSize,new ImageView(this)));



    }

}
