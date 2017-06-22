package com.example.mous.antennex.augmentedReality;




import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;


import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mous.antennex.R;
import com.example.mous.antennex.ResumeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoreActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, OnRollChangedListener {

    private ActionMenuView amvMenu;

    //Partie dédiée à la jauge :: A VOIR OPTIMISATION !!
    private GaugeView gaugeView;
    private float degree = 1;
    private float sweepAngleControl = 0;
    private float sweepAngleFirstChart = 1;
    private float sweepAngleSecondChart = 1;
    private float sweepAngleThirdChart = 1;
    private boolean isInProgress = false;
    private boolean resetMode = false;
    private boolean canReset = false;



    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraViewOn = false;
    private AugmentedPOI mPoi;

    private double mRollReal = 0;
    private double mRollTheoretical = 0;
    private static double ROLL_ACCURACY =20;
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
        gaugeView = (GaugeView) findViewById(R.id.gaugeView);

        gaugeView.setRotateDegree(degree);
        if (!isInProgress) {
            isInProgress = true;
            startRunning();
        }

        //Pour maintenir l'activité toujours allumée
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);



        Toolbar coreToolbar = (Toolbar) findViewById(R.id.tToolbar);
        amvMenu = (ActionMenuView) findViewById(R.id.amvM);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), ResumeActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(coreToolbar);
        getSupportActionBar().setTitle(null);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/



        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setupListeners();
        setupLayout();
        Log.d("Test", "Fin du start");
        setAugmentedRealityPoint();




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.core_menu, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do your actions here
        switch (item.getItemId()){
            case R.id.action_back:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Passer de la valeur de la BDD à la valeur utile en degrés
    private double toDegreExpostion ( double valueExposition)
    {
        return ( valueExposition*Math.floor(270/20)+135);
    }

    // POUR LA JAUGE :
    private void startRunning() {



        new Thread() {
            public void run() {
                for (int i = 0; i < 405; i++) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                degree++;
                                sweepAngleControl++;
                                double valueExposition = 10;
                                double degreExposition = toDegreExpostion(valueExposition);
                                if (degree < degreExposition )  // ICI IL FAUT METTRE LA VALEUR D'INTERET : ici je veux 90 degrés donc je mets - 90 : checker le cercle du cahier !!! ATTENTION AU -
                                {
                                    gaugeView.setRotateDegree(degree);

                                }

                                if (sweepAngleControl <= 90) {
                                    sweepAngleFirstChart++;
                                    gaugeView.setSweepAngleFirstChart(sweepAngleFirstChart);

                                } else if (sweepAngleControl <= 180) {
                                    sweepAngleSecondChart++;
                                    gaugeView.setSweepAngleSecondChart(sweepAngleSecondChart);


                                } else if (sweepAngleControl <= 270) {
                                    sweepAngleThirdChart++;
                                    gaugeView.setSweepAngleThirdChart(sweepAngleThirdChart);

                                }

                            }
                        });
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (i == 299) {
                        isInProgress = false;
                        canReset = true;
                    }

                }
            }
        }.start();

    }






    // POUR CALCULER LE ROLL ANGLE THETA :
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


    private void setAugmentedRealityPoint() {

        // @TODO : METTRE DES VALEURS FONCTIONNELES
        mPoi = new AugmentedPOI(
                "Sacre-Coeur",
                "Toto",
                48.49,
                2.20, 10
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





    //Returns the Camera View Sector horizontally
    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        List<Double> minMax = new ArrayList<Double>();
        double minAngle = (azimuth - AZIMUTH_ACCURACY + 360) % 360;
        double maxAngle = (azimuth + AZIMUTH_ACCURACY) % 360;
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
        mRollTheoretical=calculateTheoreticalRoll(mPoi);
        mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);


        updateDescription();
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



    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        mAzimuthReal = azimuthChangedTo;
        mAzimuthTheoretical = calculateTheoreticalAzimuth(mPoi);


        // Since Camera View is perpendicular to device plane
        mAzimuthReal = (mAzimuthReal+90)%360;

        pointerIcon = (ImageView) findViewById(R.id.icon);





        consequenceIsBetween(mAzimuthTheoretical,mRollTheoretical,pointerIcon);

        updateDescription();
    }

    /*@Override
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
*/


    // To handle Change in azimuth angle
    public void onRollChanged(float rollChangedFrom, float rollChangedTo) {
        // Function to handle Change in azimuth angle
        mRollReal = rollChangedTo;
        mRollTheoretical = calculateTheoreticalRoll(mPoi);


        // parce que sinon 0° vers le sol
        mRollReal = (mRollReal-90)%180; // problème à régler pour ceux qui regardent la tête à l'envers

        pointerIcon = (ImageView) findViewById(R.id.icon);




        consequenceIsBetween(mAzimuthTheoretical,mRollTheoretical,pointerIcon);


        updateDescription();
    }



    public void consequenceIsBetween(double azimuthTheoretical, double rollTheoretical, ImageView pointer)
    {

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


    // Calculer la distance
    public double distance( double lat_a_degre,  double lon_a_degre, double lat_b_degre,  double lon_b_degre) {

        double R = 6378000; //Rayon de la terre en mètre

        double lat_a = convertToRad(lat_a_degre);
        double lon_a = convertToRad(lon_a_degre);
        double lat_b = convertToRad(lat_b_degre);
        double lon_b = convertToRad(lon_b_degre);

        double d = R * (Math.PI / 2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));

        return d;
    }

    

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        myCurrentRoll.stop();
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
