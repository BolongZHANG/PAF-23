package com.paf23.antennex;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, OnRollChangedListener {

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraViewOn = false;
    private int nb_poi = 0;
    private int listSize = 100;
    ArrayList<AugmentedPOI> mPoiList = new ArrayList<AugmentedPOI>(listSize);

    private double mAzimuthReal = 0;
    private double mRollReal = 0;
    ArrayList<Double> mAzimuthTheoreticalList = new ArrayList<Double>(listSize);
    ArrayList<Double> mRollTheoreticalList = new ArrayList<Double>(listSize);

    private static double AZIMUTH_ACCURACY = 25;
    private static double ROLL_ACCURACY =20;
    private int maxRayon = 300 ; //mètres
    private ArrayList<Double> myInitiateLocation;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;
    private double mMyAltitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentRoll myCurrentRoll;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    TextView debug;
    ArrayList<ImageView> pointerIconList = new ArrayList<ImageView>(listSize);
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeArrays();
        setContentView(R.layout.activity_main);
        myInitiateLocation = locationInOnCreate(); // récupère la position dès l'ouverture, ensuite elle est actualisée avec les listeners
        mMyLatitude = myInitiateLocation.get(0); mMyLatitude = myInitiateLocation.get(1); mMyAltitude = myInitiateLocation.get(2);
        setAugmentedRealityPoint();
        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        setupListeners();
        setupLayout();
        ImageView wallpaper = (ImageView) findViewById(R.id.wallpaper);
        fadeOutAndHideImage(wallpaper);



    }


    private void setAugmentedRealityPoint() {

        mPoiList.add(new AugmentedPOI("Sacré-Coeur", "Haut du dôme du Sacré-Coeur", "antenne", 48.886705, 2.343104, 354));
        mPoiList.add(new AugmentedPOI("Eiffel Tower","Antenne de la tour Eiffel","antenne",48.85837009999999,2.2944813000000295,358));
        mPoiList.add(new AugmentedPOI("Strasbourg","Strasbourg mesure","mesure",48.573405,7.752111,mMyAltitude));
        nb_poi= mPoiList.size();

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.imageView_layout);

        for (int i = 0; i < nb_poi; i++) {

            final int j= i; // pour pouvoir le passer dans le on click

            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80, 60));


            String type = mPoiList.get(i).getPoiType(); // type : antenne ou mesure

            if(type.equals("antenne"))
                image.setImageResource(R.drawable.antenne_rotated_big);
            else if (type.equals("mesure")) {
                image.setImageResource(R.drawable.mesure_rotated_big);
            }
            else
                image.setImageResource(R.drawable.question_mark_rotated); //  au cas où


            image.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    descriptionTextView.setVisibility(View.VISIBLE);



                    DecimalFormat df = new DecimalFormat("#"); // Pour tronquer le nombre
                    df.setRoundingMode(RoundingMode.CEILING);
                    Double distance = getDistance(mPoiList.get(j).getPoiLatitude(), mPoiList.get(j).getPoiLongitude(), mMyLatitude,mMyLongitude)/1000 ;
                    final String details = mPoiList.get(j).getPoiDescription() + " Distance:  " +
                            df.format(distance)+" km";
                    TextView descriptionTextView = (TextView)findViewById(R.id.descriptionTextView);
                    descriptionTextView.setText(details);
                    disappear(descriptionTextView); // fais un effet toast

                }
            });

            //image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // Adds the view to the layout
            layout.addView(image);
            pointerIconList.set(i,image);

            //to be continued...
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
        double d = getDistance(poi.getPoiLatitude(), poi.getPoiLongitude(), mMyLatitude,mMyLongitude);
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

        debug.setText(mPoiList.get(2).getPoiName() + " azimuthTheoretical2 " // faudrait le faire pour les autres poi aussi
                + mAzimuthTheoreticalList.get(2) + " azimuthReal " + mAzimuthReal + " rollTheoretical2 "+ mRollTheoreticalList.get(2) + " RollReal " + mRollReal + " latitude "
                + mMyLatitude + " longitude " + mMyLongitude + " altitude " + mMyAltitude +  " angle caméra " + ROLL_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Function to handle Change in Location

        //Commentaire ci-dessous pour actualisation BDD
        /*initializeArrays();
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.imageView_layout);
        if(((RelativeLayout) layout).getChildCount() > 0)
            ((RelativeLayout) layout).removeAllViews();
        setAugmentedRealityPoint();*/

        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        mMyAltitude = location.getAltitude();

        for (int i = 0; i < nb_poi; i++) {
            mAzimuthTheoreticalList.set(i, calculateTheoreticalAzimuth(mPoiList.get(i)));
            mRollTheoreticalList.set(i, calculateTheoreticalRoll(mPoiList.get(i)));
        }
            updateDescription();
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        // Function to handle Change in azimuth angle
        mAzimuthReal = azimuthChangedTo;

        for (int i = 0; i < nb_poi; i++) {
            mAzimuthTheoreticalList.set(i, calculateTheoreticalAzimuth(mPoiList.get(i)));
        }

        // Since Camera View is perpendicular to device plane
        mAzimuthReal = (mAzimuthReal+90)%360;


        for (int i =0; i<nb_poi;i++){
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),pointerIconList.get(i),i);
        }


        updateDescription();
    }

    public void onRollChanged(float rollChangedFrom, float rollChangedTo) {
        // Function to handle Change in azimuth angle
        mRollReal = rollChangedTo;

        for (int i = 0; i < nb_poi; i++) {
            mRollTheoreticalList.set(i, calculateTheoreticalRoll(mPoiList.get(i)));
        }

        // parce que sinon 0° vers le sol
        mRollReal = (mRollReal-90)%180; // problème à régler pour ceux qui regardent la tête à l'envers



        for (int i =0; i<nb_poi;i++){
            consequenceIsBetween(mAzimuthTheoreticalList.get(i),mRollTheoreticalList.get(i),pointerIconList.get(i),i);
        }

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
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        debug = (TextView) findViewById(R.id.debug);
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

    public void consequenceIsBetween(double azimuthTheoretical, double rollTheoretical, ImageView pointer, int index){

        double minAngleA = calculateAzimuthAccuracy(mAzimuthReal).get(0);
        double maxAngleA = calculateAzimuthAccuracy(mAzimuthReal).get(1);
        double minAngleR = calculateRollAccuracy(mRollReal).get(0);
        double maxAngleR = calculateRollAccuracy(mRollReal).get(1);




        if (azimuthIsBetween(minAngleA, maxAngleA, azimuthTheoretical)&& rollIsBetween(minAngleR, maxAngleR, rollTheoretical)) {

            float ratioAzimuth = ((float) (azimuthTheoretical - minAngleA + 360.0) % 360) / ((float) (maxAngleA - minAngleA + 360.0) % 360); // où afficher sur le tel
            float ratioRoll = ((float) (rollTheoretical - minAngleR) % 180) / ((float) (maxAngleR - minAngleR) % 180); // ou afficher sur le tel
            int imageDP = imageSizeDP(index);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dpToPx(imageDP,this.getApplicationContext()), dpToPx(imageDP,this.getApplicationContext())); // taille image
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

    public double getDistance( double lat_a_degre,  double lon_a_degre, double lat_b_degre,  double lon_b_degre) {

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
        pointerIconList = new ArrayList<ImageView>(Collections.nCopies(listSize,new ImageView(this)));
        mPoiList = new ArrayList<AugmentedPOI>(listSize);



    }

    private void disappear(final View view){
        // fade out view nicely after 5 seconds
        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
        alphaAnim.setStartOffset(3000);                        // start in 5 seconds
        alphaAnim.setDuration(400);
        alphaAnim.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                // make invisible when animation completes, you could also remove the view from the layout
                view.setVisibility(View.INVISIBLE);
            }

            public void onAnimationStart(Animation a) { }
            public void onAnimationRepeat(Animation a) { }

        });

        descriptionTextView.setAnimation(alphaAnim);
    }

    public static int dpToPx(int dp, Context context) {
        //transforme dp en pixels
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }



    private void fadeOutAndHideImage(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(3000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }

    private int imageSizeDP(int index){
        int imageDP;
        double distance = getDistance(mPoiList.get(index).getPoiLatitude(),mPoiList.get(index).getPoiLongitude(), mMyLatitude,mMyLongitude);
        double size =  (100 - distance * 70 / maxRayon); // Diminue la taille de l'icon avec l'éloignement affinement
        if (size<0)
            imageDP = 30;
        else imageDP = (int) size;

        return imageDP;
    }

    private ArrayList<Double> locationInOnCreate(){ // pour récupérer la postion gps à envoyer dans le oncreate et pour les points de mesure
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;
        Double latitude =  new Double(0);
        Double longitude =  new Double(0);
        Double altitude =  new Double(0);

        if(network_enabled){
            try {
                location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (SecurityException e) {
                Log.d("Sylvainn","Network exception");
                location = null;
            }

            if(location!=null){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                altitude = location.getAltitude();
            }
        }
        ArrayList<Double> position = new ArrayList<Double>();
        position.add(latitude);
        position.add(longitude);
        position.add(altitude);
        return (position);
    }
}
