package com.example.mous.antennex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.mous.antennex.augmentedReality.CoreActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.mous.antennex.GeoUtils.isVisible;


public class FetchDataActivity extends AppCompatActivity {

    private String TAG = FetchDataActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://data.anfr.fr/api/records/1.0/search/?dataset=observatoire_2g_3g_4g&q=nom_dept%3DParis&rows=100";
    private static  String urlhauteur="https://data.anfr.fr/api/records/1.0/search/?dataset=sup_support&q=dept%3D75&rows=1860";
    //modifier l'urlzone avec les positions gps du telephone
    private static String urlzone ;
    private static String urlzonemesure ="https://data.anfr.fr/api/records/1.0/search/?dataset=mesures-de-lexposition-aux-ondes&geofilter.distance=48.825804%2C2.346267%2C500";
    ArrayList<HashMap<String, String>> contactList;
    public static ArrayList<HashMap<String, String>> Antennezone;
    ArrayList<HashMap<String,String>> Mesures;
    ArrayList<HashMap<String,String>> Mesureszone;
    public static ArrayList<HashMap<String,String>> Mesureszone1;
    ArrayList<ArrayList<Polygon>> polygones;
    ArrayList<HashMap<String,String>> antennesafficher;

    /*CoreActivity core = new CoreActivity();
    public String myLatitude = core.locationInOnCreate().get(0)+""; ;
    public String myLongitude = core.locationInOnCreate().get(1)+""; ;*/

    Double myLatitude;
    Double myLongitude;




    HashMap<String, String> sup_support;
    ArrayList<ArrayList<ArrayList<HashMap<String,String>>>> batvisibles;
    ArrayList<ArrayList<ArrayList<Point>>> batvisiblesfloat;
    ArrayList<ArrayList<String>> Listefinale;

    // TEST
    LocationManager locManager;
    boolean network_enabled ;

    Location location;
    Double latitude ;
    Double longitude ;
    Double altitude =  0.0;

    //TEST



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLatitude=48.825945;
        myLongitude =  2.345271;


        /*locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

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
        }*/


        urlzone="https://data.anfr.fr/api/records/1.0/search/?dataset=observatoire_2g_3g_4g&rows=1000&geofilter.distance="+myLatitude+"%2C"+myLongitude+"%2C250";




        Mesures=new ArrayList<>();
        contactList = new ArrayList<>();
        Antennezone = new ArrayList<>();
        sup_support = new HashMap<>();
        Mesureszone=new ArrayList<>();
        Mesureszone1=new ArrayList<>();
        batvisibles=new ArrayList<ArrayList<ArrayList<HashMap<String,String>>>>();
        batvisiblesfloat=new ArrayList<ArrayList<ArrayList<Point>>>();
        polygones=new ArrayList<ArrayList<Polygon>>();
        antennesafficher=new ArrayList<HashMap<String,String>>();
        Listefinale=new ArrayList<ArrayList<String>>();



        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */


    /*// pour récupérer la postion gps à envoyer dans le oncreate et pour les points de mesure
    public  ArrayList<Double> locationInOnCreate()
    {
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;
        Double latitude = 0.0;
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
    }*/

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();




            // Showing progress dialog
            pDialog = new ProgressDialog(FetchDataActivity.this);
            pDialog.setMessage("Mise à jour de la base de données ... ");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String jsonmesure=null;
            try {
                InputStream is = getAssets().open("mesures_globales.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                jsonmesure = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
            }


            HttpHandler sh = new HttpHandler();
            HttpHandler sh1 = new HttpHandler();
            HttpHandler sh2 = new HttpHandler();
            HttpHandler sh3 = new HttpHandler();







            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            String jsonStrhauteur= sh2.makeServiceCall(urlhauteur);
            String jsonStrzone= sh1.makeServiceCall(urlzone);
            String jsonStrzonemesure=sh3.makeServiceCall(urlzonemesure);


            Log.e(TAG, "Response from url: " + jsonStrzone);

            if (jsonStrzone != null) {
                try {
                    //code pour les hauteurs
                    JSONObject jsonObjhauteur = new JSONObject(jsonStrhauteur);
                    JSONArray recordshauteur = jsonObjhauteur.getJSONArray("records");
                    for (int i = 0; i < recordshauteur.length(); i++) {
                        JSONObject c1 = recordshauteur.getJSONObject(i);
                        JSONObject fields1 = c1.getJSONObject("fields");
                        String idsupport = fields1.getString("sup_id");
                        String hauteur=fields1.getInt("sup_nm_haut")+"";

                        sup_support.put(idsupport,hauteur);

                    }
                    //code pour les mesures
                    JSONObject jsonObjmesure = new JSONObject(jsonmesure);
                    JSONArray recordsmesure = jsonObjmesure.getJSONArray("records1");
                    for (int i = 0; i < recordsmesure.length(); i++) {
                        JSONObject c1 = recordsmesure.getJSONObject(i);
                        String Longitude = c1.getString("FIELD2");
                        String Latitude =c1.getString("FIELD3");
                        String Niveauglobal = c1.getString("FIELD12");
                        HashMap<String, String> mesure = new HashMap<>();
                        mesure.put("Latitude",Latitude);
                        mesure.put("Longitude",Longitude);
                        mesure.put("Niveauglobal",Niveauglobal);
                        Mesures.add(mesure);
                    }
                    //code pour les mesures dans une zone de 250m
                    JSONObject jsonObjzonemesure = new JSONObject(jsonStrzonemesure);
                    JSONArray recordszonemesure = jsonObjzonemesure.getJSONArray("records");
                    for (int i = 0; i < recordszonemesure.length(); i++) {
                        JSONObject c1 = recordszonemesure.getJSONObject(i);
                        JSONObject fields = c1.getJSONObject("fields");
                        String Longitude = fields.getDouble("longitude")+"";
                        String Latitude =fields.getDouble("latitude")+"";
                        String Niveauglobal = fields.getDouble("valeur_globale_v_m")+"";
                        HashMap<String, String> mesures = new HashMap<>();
                        mesures.put("Latitude",Latitude);
                        mesures.put("Longitude",Longitude);
                        mesures.put("Niveauglobal",Niveauglobal);
                        Mesureszone.add(mesures);
                    }

                    for(int i=0;i<Mesures.size();i++){

                        //myLatitude et myLongitude sont des Double maintenant
                        double lat_a_degre=Float.parseFloat(myLatitude.toString());//ajouter ma latitude
                        double lon_a_degre=Float.parseFloat(myLongitude.toString());//ajouter ma longitude
                        double lat_b_degre=Float.parseFloat(Mesures.get(i).get("Latitude"));
                        double lon_b_degre=Float.parseFloat(Mesures.get(i).get("Longitude"));
                        double R = 6378000; //Rayon de la terre en mètre
                        double lat_a = (Math.PI * lat_a_degre)/180;
                        double lon_a = (Math.PI * lon_a_degre)/180;
                        double lat_b = (Math.PI * lat_b_degre)/180;
                        double lon_b = (Math.PI * lon_b_degre)/180;
                        double d = R * (Math.PI / 2 - Math.asin(Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));
                        HashMap<String, String> mesurezone = new HashMap<>();
                        if(d<250){
                            mesurezone.put("Latitude",Mesures.get(i).get("Latitude"));
                            mesurezone.put("Longitude",Mesures.get(i).get("Longitude"));
                            mesurezone.put("Niveauglobal",Mesures.get(i).get("Niveauglobal"));
                            Mesureszone1.add(mesurezone);
                        }



                    }



                    //code pour zone 250m des antennes
                    JSONObject jsonObjzone = new JSONObject(jsonStrzone);
                    JSONArray recordszone = jsonObjzone.getJSONArray("records");
                    for (int i = 0; i < recordszone.length(); i++) {
                        JSONObject c = recordszone.getJSONObject(i);
                        String datasetidzone = c.getString("datasetid");
                        JSONObject fields = c.getJSONObject("fields");
                        Log.i("tag", fields.toString());
                        String generationzone = fields.getString("generation");
                        String adm_lb_nomzone = fields.getString("adm_lb_nom");
                        String sup_id = fields.getString("sup_id");
                        JSONArray coordonnees = fields.getJSONArray("coordonnees");
                        String Latitude = coordonnees.getInt(0)+"";
                        String Longitude = coordonnees.getInt(1)+"";
                        HashMap<String, String> contactzone = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contactzone.put("Latitude", Latitude);
                        contactzone.put("Longitude", Longitude);
                        contactzone.put("adm_lb_nomzone", adm_lb_nomzone);
                        contactzone.put("generationzone", generationzone);
                        contactzone.put("hauteurzone",sup_support.get(sup_id));




                        // adding contact to contact list
                        Antennezone.add(contactzone);
                    }
                    //code pour les polygones

                    for(int i=0;i<Antennezone.size();i++){
                        String latAntenne=Antennezone.get(i).get("Latitude");
                        String longAntenne=Antennezone.get(i).get("Longitude");
                        String urlbatvis = "https://opendata.paris.fr/api/records/1.0/search/?dataset=bati_donnees_geographiques&rows=100&geofilter.polygon=("+"48.870156"+"%2C"+"2.301026"+")%2C("+latAntenne+"%2C"+longAntenne+")%2C("+latAntenne+"%2C"+"2.301026"+")";
                        HttpHandler sh4 = new HttpHandler();
                        String jsonbat = sh4.makeServiceCall(urlbatvis);;
                        JSONObject jsonObjbatvis = new JSONObject(jsonbat);
                        JSONArray recordsbatvis = jsonObjbatvis.getJSONArray("records");
                        ArrayList<ArrayList<HashMap<String,String>>> batsAntenne=new ArrayList<ArrayList<HashMap<String,String>>>();
                        for (int j = 0; j < recordsbatvis.length(); j++) {
                            JSONObject cbatvis = recordsbatvis.getJSONObject(i);
                            if (cbatvis.has("geom") ){


                                JSONObject geom = cbatvis.getJSONObject("geom");
                                JSONArray coordinates = geom.getJSONArray("coordinates");

                                ArrayList<HashMap<String, String>> noeudsbat = new ArrayList<HashMap<String, String>>();
                                for (int k = 0; k < coordinates.length(); k++) {
                                    JSONArray coordk = coordinates.getJSONArray(k);
                                    String latitude = Double.toString(coordk.getDouble(0));
                                    String longitude = Double.toString(coordk.getDouble(1));
                                    HashMap<String, String> noeud = new HashMap<>();
                                    noeud.put("latitude", latitude);
                                    noeud.put("longitude", longitude);
                                    noeudsbat.add(noeud);
                                }
                                batsAntenne.add(noeudsbat);
                            }

                        }
                        batvisibles.add(batsAntenne);



                    }
                    for(int i = 0; i< batvisibles.size();i++){
                        ArrayList<ArrayList<Point>> batsAntenne1=new ArrayList<ArrayList<Point>>();
                        for(int j=0;j<batvisibles.get(i).size();j++){
                            ArrayList<Point> noeudsbat1=new ArrayList<Point>();
                            for(int k=0;k<batvisibles.get(i).get(j).size();k++){
                                Point point=new Point(Float.parseFloat(batvisibles.get(i).get(j).get(k).get("latitude")),Float.parseFloat(batvisibles.get(i).get(j).get(k).get("longitude")));
                                noeudsbat1.add(point);
                            }
                            batsAntenne1.add(noeudsbat1);


                        }
                        batvisiblesfloat.add(batsAntenne1);
                    }
                    for(int i = 0; i< batvisibles.size();i++){
                        ArrayList<Polygon>polygon1=new ArrayList<Polygon>();
                        for(int j=0;j<batvisibles.get(i).size();j++){
                            Polygon polygon=Polygon.Builder().addVertex(batvisiblesfloat.get(i).get(j)).build();
                            polygon1.add(polygon);

                        }
                        polygones.add(polygon1);
                    }
                    //liste des antennes à afficher
                    for (int i = 0; i < Antennezone.size(); i++) {
                        if(isVisible(myLatitude.toString(),myLongitude.toString(),Antennezone.get(i).get("Latitude"),Antennezone.get(i).get("Longitude"),polygones.get(i)))
                            antennesafficher.add(Antennezone.get(i));
                    }
                /*
                for (int i = 0; i < antennesafficher.size(); i++) {
                    ArrayList<String> liste=new ArrayList<String>();
                    liste.add("antenne");
                    liste.add(antennesafficher.get(i).get(0));
                    liste.add(antennesafficher.get(i).get(1));
                    liste.add(antennesafficher.get(i).get(2));
                    liste.add(antennesafficher.get(i).get(3));
                    liste.add(antennesafficher.get(i).get(4));
                    Listefinale.add(liste);

                }
                for (int i = 0; i < Mesures.size(); i++) {
                    ArrayList<String> liste1=new ArrayList<String>();
                    liste1.add("mesure");
                    liste1.add(Mesures.get(i).get(0));
                    liste1.add(Mesures.get(i).get(1));
                    liste1.add(Mesures.get(i).get(2));
                    Listefinale.add(liste1);



                }*/



                    //code pour les antennes de paris
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray records = jsonObj.getJSONArray("records");
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject c = records.getJSONObject(i);
                        String datasetid = c.getString("datasetid");
                        JSONObject fields = c.getJSONObject("fields");
                        Log.i("tag",fields.toString());
                        String generation = fields.getString("generation");
                        String adm_lb_nom = fields.getString("adm_lb_nom");
                        String sup_id = fields.getString("sup_id");
                        JSONArray coordonnees = fields.getJSONArray("coordonnees");
                        int latitude=coordonnees.getInt(0);
                        int longitude=coordonnees.getInt(1);
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("datasetid", datasetid);
                        contact.put("generation", generation);
                        contact.put("adm_lb_nom", adm_lb_nom);


                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
                Intent intent = new Intent (getApplicationContext(),CoreActivity.class);
                startActivity(intent);
            /**
             * Updating parsed JSON data into ListView
             * */
            String[] test= new String[]{contactList.size()+"",Antennezone.size()+"",Mesures.size()+"",
                    sup_support.size()+"",Mesureszone.size()+"",Mesureszone1.size()+"",batvisibles.size()+"",batvisiblesfloat.size()+"", polygones.size()+"", antennesafficher.size()+"",
                    "Gerard", "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
                    "Mathieu", "Noemie", "Olivia", "Philippe", "Quentin", "Romain",
                    "Sophie", "Tristan", "Ulric", "Vincent", "Willy", "Xavier"
            };

            /*ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FetchDataActivity.this,
                    R.layout.list_item, test);*/
            /*ListAdapter adapter = new SimpleAdapter(
                    FetchDataActivity.this, contactListzone,
                    R.layout.list_item, new String[]{ "datasetidzone","generationzone","adm_lb_nomzone"}, new int[]{R.id.info1,
                    R.id.info2,R.id.info3});*/

            /*lv.setAdapter(adapter1);*/
        }

    }
}
