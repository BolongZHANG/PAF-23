package com.example.mous.antennex.augmentedReality;




import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;


import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mous.antennex.CartoradioActivity;
import com.example.mous.antennex.FetchDataActivity;
import com.example.mous.antennex.ProximityActivity;
import com.example.mous.antennex.R;
import com.example.mous.antennex.ResumeActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mous.antennex.FetchDataActivity.Antennes;
import static com.example.mous.antennex.FetchDataActivity.Mesures;

public class CoreActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, OnRollChangedListener {

    /*private ActionMenuView amvMenu;*/

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
    //Fin initialisation variables jauge


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
    private double mMyLatitude = 0;
    private int maxRayon = 300 ; //mètres
    private ArrayList<Double> myInitiateLocation;
    private double mMyLongitude = 0;
    private double mMyAltitude = 0;

    // TEST
    private ArrayList<ArrayList<String>> testArray = Antennes;
    // TEST

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentRoll myCurrentRoll;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    TextView debug;
    ArrayList<ImageView> pointerIconList = new ArrayList<ImageView>(listSize);
    ArrayList<ImageView> textIconList = new ArrayList<ImageView>(listSize);

    ImageView pointerIcon;
    ImageView mapButton;
    ImageView proximityButton;

    Display display;

    //POUR PRENDRE UN SCREEN DE L'ACTIVITE ET L'ENREGISTRER ENSUITE
    private Button share ;
    private File imagePath;
    // FIN DE CETTE PARTIE ( SCREEN + SHARE )

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        myInitiateLocation = locationInOnCreate(); // récupère la position dès l'ouverture, ensuite elle est actualisée avec les listeners
        mMyLatitude = myInitiateLocation.get(0);
        mMyLatitude = myInitiateLocation.get(1);
        mMyAltitude = myInitiateLocation.get(2);

        gaugeView = (GaugeView) findViewById(R.id.gaugeView);
        mapButton= (ImageView) findViewById(R.id.buttonMaps);
        proximityButton=(ImageView) findViewById(R.id.buttonProximity);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartoradioActivity.class);
                startActivity(intent);
            }
        });

        proximityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(),ProximityActivity.class);
                startActivity(intent);
            }
        });

        gaugeView.setRotateDegree(degree);
        if (!isInProgress) {
            isInProgress = true;
            startRunning();
        }

        //Pour maintenir l'activité toujours allumée
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        share = (Button)findViewById(R.id.cameraButton);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });



        /*Toolbar coreToolbar = (Toolbar) findViewById(R.id.tToolbar);
        amvMenu = (ActionMenuView) findViewById(R.id.amvM);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });*/

        /*LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), ProximityActivity.class);
                startActivity(intent);
            }
        });
*/
        /*setSupportActionBar(coreToolbar);
        getSupportActionBar().setTitle(null);*/
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/



        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setupListeners();
        setupLayout();
        initializeArrays();
        Log.d("Test", "Fin du start");
        setAugmentedRealityPoint();
        /*ImageView wallpaper = (ImageView) findViewById(R.id.wallpaper);
        fadeOutAndHideImage(wallpaper);*/




    }


   /* @Override
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

    }*/


    // DEBUT DU CODE SCREEN ET SHARE
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "In Tweecher, My highest score with screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Tweecher score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    // FIN DU CODE POUR SCREENSHOT ET SHARE



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




    private void setAugmentedRealityPoint() {
        ArrayList<ArrayList<String>> Listefinale=new ArrayList<ArrayList<String>>();
        for (int i=0; i<Antennes.size();i++){

            ArrayList<String> liste=new ArrayList<String>();
            liste.add("antenne");// Type
            liste.add(Antennes.get(i).get(3));// génération
            liste.add(Antennes.get(i).get(4)); // opérateur
            liste.add(Antennes.get(i).get(2));//latitude
            liste.add(Antennes.get(i).get(1));//longitude
            liste.add(Antennes.get(i).get(5));//hauteur
            Listefinale.add(liste);

        }
        for (int i=0; i<Mesures.size();i++)
        {
            ArrayList<String> liste1=new ArrayList<String>();
            liste1.add("mesures");//Type
            liste1.add(Mesures.get(i).get(0));//latitude
            liste1.add(Mesures.get(i).get(1));//longitude
            liste1.add(Mesures.get(i).get(2));//niveau global
            Listefinale.add(liste1);
        }
        for(int i=0;i<Listefinale.size();i++){
            if(Listefinale.get(i).get(0).equals("antenne")){
                mPoiList.add(new AugmentedPOI(Listefinale.get(i).get(1), Listefinale.get(i).get(2), "antenne",Float.parseFloat(Listefinale.get(i).get(3)),Float.parseFloat(Listefinale.get(i).get(4)),10 ));
            }
            else if(Listefinale.get(i).get(0).equals("mesure")){
                mPoiList.add(new AugmentedPOI("Mesure(V/m)", Listefinale.get(i).get(2), "mesure",Float.parseFloat(Listefinale.get(i).get(0)),Float.parseFloat(Listefinale.get(i).get(1)),mMyAltitude ));

            }
            else
                Log.d("","Error sur le type");
        }

        Integer test1 = Listefinale.size();
        Integer test2 = mPoiList.size();
        Log.d("tailleliste", test1.toString()+" "+test2.toString());



       /* mPoiList.add(new AugmentedPOI("Sacré-Coeur", "Haut du dôme du Sacré-Coeur", "antenne", 48.886705, 2.343104, 207));
        mPoiList.add(new AugmentedPOI("Eiffel Tower","Antenne de la tour Eiffel","antenne",48.85837009999999,2.2944813000000295,358));
        mPoiList.add(new AugmentedPOI("Strasbourg","Strasbourg mesure","mesure",48.573405,7.752111,mMyAltitude));*/
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
                    Double distance = getDistance(mPoiList.get(j).getPoiLatitude(), mPoiList.get(j).getPoiLongitude(), mMyLatitude,mMyLongitude) ;
                    final String details = mPoiList.get(j).getPoiDescription() + " Distance:  " +
                            df.format(distance)+" mètres";
                    TextView descriptionTextView = (TextView)findViewById(R.id.cameraTextView);
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

    private void initializeArrays(){
        mAzimuthTheoreticalList = new ArrayList<Double>(Collections.nCopies(listSize, 0.));
        mRollTheoreticalList = new ArrayList<Double>(Collections.nCopies(listSize, 0.));
        pointerIconList = new ArrayList<ImageView>(Collections.nCopies(listSize,new ImageView(this)));
        mPoiList = new ArrayList<AugmentedPOI>(listSize);

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
        // Returns the Camera View Sector horizontally
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


    // Calculer la distance
    public double getDistance( double lat_a_degre,  double lon_a_degre, double lat_b_degre,  double lon_b_degre) {

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


        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);
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
