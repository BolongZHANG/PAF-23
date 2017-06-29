package com.example.mous.antennex;





import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
        import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
        import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.mous.antennex.FetchDataActivity.Antennezone;
import static com.example.mous.antennex.FetchDataActivity.Mesureszone1;

public class MesureActivity extends AppCompatActivity {

    TextView textTargetUri;
    ImageView targetImage;
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ListAdapterGallery adapter;
    private ArrayList<String> distanceList;
    private ArrayList<String> hauteurList;

    private ArrayList<HashMap<String, String>> listeAntenneZone;

    private Double myLatitude;
    private Double myLongitude;


    private ArrayList<Integer> profilePictures;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        listeAntenneZone=FetchDataActivity.Mesureszone1 ;

        ArrayList<Double> myInitialLocation = locationInOnCreate();
        myLatitude = myInitialLocation.get(0);
        myLongitude = myInitialLocation.get(1);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mesures à proximité...");

        setData();
        adapter = new ListAdapterGallery(this, R.layout.item_listview, stringArrayList, distanceList,hauteurList, profilePictures);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), (String)parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.homeItem:
                                Intent intent = new Intent(MesureActivity.this,MainActivity.class);

                                /*intent.putExtra("Check",1);*/
                                startActivity(intent);
                                break;
                            case R.id.historique:
                                finish();
                                break;
                            case R.id.parameter:
                                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);

                                intent2.putExtra("Check","2");
                                startActivity(intent2);

                                break;

                        }

                        return true;
                    }
                });

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.commit();*/

    }
    //POUR REVENIR AUX FRAGMENTS
    @Override
    public void onBackPressed()
    {

        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);

        intent2.putExtra("Check","2");
        startActivity(intent2);
    }

    // FIN POUR REVENIR AUX FRAGMENTS

    private void setData() {
        stringArrayList = new ArrayList<>();
        distanceList=new ArrayList<>();
        profilePictures=new ArrayList<>();
        hauteurList=new ArrayList<>();

        Double distance ;
        int distanceEntiere ;

        for (int i=0; i<Mesureszone1.size();i++){

            Double latitudeB = Double.parseDouble(Antennezone.get(i).get("Latitude"));
            Double longitudeB = Double.parseDouble(Antennezone.get(i).get("Longitude"));



            distance = getDistance(myLatitude,myLongitude,latitudeB,longitudeB);
            distanceEntiere=distance.intValue();

            stringArrayList.add("Niveau global d'exposition: " + Mesureszone1.get(i).get("Niveauglobal") + " V/m" );
            distanceList.add(distanceEntiere+"");// A CHANGER AVEC GET DISTANCE
            profilePictures.add(5); // Mettre 5 ou n'importe quel int




        }



    }


    public  double getDistance( double lat_a_degre,  double lon_a_degre, double lat_b_degre,  double lon_b_degre) {

        double R = 6378000; //Rayon de la terre en mètre

        double lat_a = convertToRad(lat_a_degre);
        double lon_a = convertToRad(lon_a_degre);
        double lat_b = convertToRad(lat_b_degre);
        double lon_b = convertToRad(lon_b_degre);

        double d = R * (Math.PI / 2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));

        return d;
    }

    //Conversion des degrés en radian
    public double convertToRad(double degrees){
        return (Math.PI * degrees)/180;
    }



    // To deal with the search View
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else{
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }




    public  ArrayList<Double> locationInOnCreate()
    {
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
