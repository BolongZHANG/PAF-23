package com.example.mous.antennex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mous.antennex.augmentedReality.CoreActivity;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FetchDataActivity extends AppCompatActivity {

    private String TAG = FetchDataActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://data.anfr.fr/api/records/1.0/search/?dataset=observatoire_2g_3g_4g&q=nom_dept%3DParis&rows=100";
    private static String url1 ="https://data.anfr.fr/api/records/1.0/search/?dataset=sup_support&q=dept%3D75&rows=1936";
    public static ArrayList<ArrayList<String>> Mesures;
    public static ArrayList<ArrayList<String>> Antennes;
    public static ArrayList<ArrayList<String>> ZoneAntenne;
    ArrayList<ArrayList<ArrayList<ArrayList<String>>>> batvisibles;
    ArrayList<ArrayList<String>> antennesafficher;


    HashMap<String, String> hashhauteur ;
    ArrayList<ArrayList<String>> Listefinale;
    private ListView mListView;
    static JSONArray testx=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_data);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
                startActivity(intent);
            }
        });
        Mesures=new ArrayList<ArrayList<String>>();
        Antennes=new ArrayList<ArrayList<String>>();
        ZoneAntenne=new ArrayList<ArrayList<String>>();
        batvisibles=new ArrayList<ArrayList<ArrayList<ArrayList<String>>>>();
        antennesafficher=new ArrayList<ArrayList<String>>();

        hashhauteur=new HashMap<String, String>();
        Listefinale=new ArrayList<ArrayList<String>>();


        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog = new ProgressDialog(FetchDataActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }



        /*public String zoneantenne(String myLatitude,String myLongitude,String distance){
            String json=null;
            String urlzone = "https://data.anfr.fr/api/records/1.0/search/?dataset=observatoire_2g_3g_4g&geofilter.distance=48.853%2C2.35%2C250";
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(urlzone);
            return jsonStr;

        }*/
        /*public String batvis(String myLatitude,String myLongitude,String latAntenne,String longAntenne){
            String json=null;
            String urlbatvis = "https://opendata.paris.fr/api/records/1.0/search/?dataset=volumesbatisparis2011&rows=10&geofilter.polygon=("+myLatitude+"%2C"+myLongitude+")%2C("+latAntenne+"%2C"+longAntenne+")%2C("+latAntenne+"%2C"+myLongitude+")";
           *//* HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(urlbatvis);*//*
            return jsonStr;

        }*/


        public String readJSONFromAsset1() {
            String json = null;
            try {
                InputStream is = getAssets().open("mesures_globales.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
        public String readJSONFromAssetantenneprox() {
            String json = null;
            try {
                InputStream is = getAssets().open("antennestelecom.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
        public String readJSONFromAssetsup_support() {
            String json = null;
            try {
                InputStream is = getAssets().open("sup_supportbis.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /*HttpHandler sh = new HttpHandler();*/

            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url);
            //String jsonStr1=sh.makeServiceCall(url1);
            //String jsonStrzone=sh.makeServiceCall("https://data.anfr.fr/api/records/1.0/search/?dataset=observatoire_2g_3g_4g&geofilter.distance=48.853%2C2.35%2C250");
            // Log.e(TAG, "Response from url: " + jsonStr);



            try {
                JSONObject jsonObj = new JSONObject(readJSONFromAssetsup_support());//donnÃ©es sup_support
                JSONObject jsonObj1 = new JSONObject(readJSONFromAsset1());//donnÃ©es mesures
                JSONObject jsonObj2 = new JSONObject(readJSONFromAssetantenneprox());//donnÃ©es observatoire2g_3g_4g
                JSONObject jsonObjzone = new JSONObject(readJSONFromAssetantenneprox());//donnÃ©es observatoire2g_3g_4g zone de 250m autour de l'utilisateur




                // Getting JSON Array node
                JSONArray records = jsonObj.getJSONArray("records");
                JSONArray records1 = jsonObj1.getJSONArray("records1");



                // Getting JSON Array node
                JSONArray records2 = jsonObj2.getJSONArray("records");
                JSONArray recordszone = jsonObjzone.getJSONArray("records");

                //donnÃ©es sup_support
                for (int i = 0; i < records.length(); i++) {
                    JSONObject c = records.getJSONObject(i);
                    JSONObject fields=c.getJSONObject("fields");

                    String Numérodesupport = fields.getString("sup_id");
                    String hauteur =fields.getString("sup_nm_haut");

                    hashhauteur.put(Numérodesupport,"hauteur");



                }
                //donnÃ©es observatoire2g_3g_4g
                for (int i = 0; i < records2.length(); i++) {
                    JSONObject c2 = records2.getJSONObject(i);

                    String datasetid = c2.getString("datasetid");
                    String recordid = c2.getString("recordid");


                    // Phone node is JSON Object
                    JSONObject fields = c2.getJSONObject("fields");
                    Log.i("tag", fields.toString());
                    //String nom_reg = fields.getString("nom_reg");
                    String emr_lb_systeme = fields.getString("emr_lb_systeme");

//                       String nom_dept = fields.getString("nom_dept");
                    String sta_nm_dpt = fields.getString("sta_nm_dpt");
                    String mutualisation = fields.getString("mutualisation");
                    String generation = fields.getString("generation");
                    //String adr_lb_add2 = fields.getString("adr_lb_add2");
//                        String adr_lb_add1 = fields.getString("adr_lb_add1");
                    String adm_lb_nom = fields.getString("adm_lb_nom");
                    String emr_dt_service = fields.getString("emr_dt_service");
                    String coord = fields.getString("coord");
                    String date_maj = fields.getString("date_maj");
                    int dept = fields.getInt("dept");
                    String code_insee = fields.getString("code_insee");
                    String en_service = fields.getString("en_service");
                    String sup_id = fields.getString("sup_id");

                    JSONArray coordonnees = fields.getJSONArray("coordonnees");
                    String latitude = Double.toString(coordonnees.getDouble(0));
                    String longitude = Double.toString(coordonnees.getDouble(1));
                    int id = fields.getInt("id");
                    int total_de_adm_lb_nom = fields.getInt("total_de_adm_lb_nom");
                    String mutualisation_public = fields.getString("mutualisation_public");
                    JSONObject geometry = c2.getJSONObject("geometry");
                    String type = geometry.getString("type");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    int latitude1 = coordinates.getInt(0);
                    int longitude1 = coordinates.getInt(1);
                    ArrayList<String> antenne=new ArrayList<String>();

                    antenne.add(sup_id);
                    antenne.add(longitude);
                    antenne.add(latitude);
                    antenne.add(generation);
                    antenne.add(adm_lb_nom);
                    antenne.add(hashhauteur.get(sup_id));
                    Antennes.add(antenne);
                }

                //donnÃ©es observatoire2g_3g_4g zone de 250m autour de l'utilisateur
                for (int i = 0; i < recordszone.length(); i++) {
                    JSONObject czone = recordszone.getJSONObject(i);
                    JSONObject fields = czone.getJSONObject("fields");
                    String sup_id = fields.getString("sup_id");
                    String generation = fields.getString("generation");
                    String adm_lb_nom = fields.getString("adm_lb_nom");
                    JSONArray coordonnees = fields.getJSONArray("coordonnees");
                    String latitude = Double.toString(coordonnees.getDouble(0));
                    String longitude = Double.toString(coordonnees.getDouble(1));
                    ArrayList<String> mesurezone=new ArrayList<String>();
                    mesurezone.add(latitude);
                    mesurezone.add(longitude);
                    mesurezone.add(generation);
                    mesurezone.add(adm_lb_nom);
                    //mesurezone.add(hashhauteur.get(sup_id));
                    ZoneAntenne.add(mesurezone);
                }
                //code pour polygones
                /*for(int i=0;i<ZoneAntenne.size();i++){
                    String jsonStrbatvis=batvis("48.853","2.35",ZoneAntenne.get(i).get(0),ZoneAntenne.get(i).get(1));
                    JSONObject jsonObjbatvis = new JSONObject(jsonStrbatvis);
                    JSONArray recordsbatvis = jsonObjbatvis.getJSONArray("records");
                    ArrayList<ArrayList<ArrayList<String>>> batsAntenne=new ArrayList<ArrayList<ArrayList<String>>>();
                    for (int j = 0; j < recordsbatvis.length(); j++) {
                        JSONObject cbatvis = recordsbatvis.getJSONObject(i);
                        JSONObject geom = cbatvis.getJSONObject("geom");
                        JSONArray coordinates = geom.getJSONArray("coordinates");

                        ArrayList<ArrayList<String>> noeudsbat=new ArrayList<ArrayList<String>>();
                        for (int k = 0; k < coordinates.length(); k++) {
                            JSONArray coordk = coordinates.getJSONArray(k);
                            String latitude = Double.toString(coordk.getDouble(0));
                            String longitude = Double.toString(coordk.getDouble(1));
                            ArrayList<String> noeud=new ArrayList<String>();
                            noeud.add(latitude);
                            noeud.add(longitude);
                            noeudsbat.add(noeud);
                        }
                        batsAntenne.add(noeudsbat);

                    }
                    batvisibles.add(batsAntenne);



                }

                for(int i = 0; i< batvisibles.size();i++){
                    ArrayList<ArrayList<Point>> batsAntenne1=new ArrayList<ArrayList<Point>>();
                    for(int j=0;j<batvisibles.get(i).size();j++){
                        ArrayList<Point> noeudsbat1=new ArrayList<Point>();
                        for(int k=0;k<batvisibles.get(i).get(j).size();k++){
                            Point point=new Point(Float.parseFloat(batvisibles.get(i).get(j).get(k).get(0)),Float.parseFloat(batvisibles.get(i).get(j).get(k).get(1)));
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
                }*/






                //donnÃ©es mesures
                for (int i = 0; i < records1.length(); i++) {
                    JSONObject c1 = records1.getJSONObject(i);
                    String Longitude = c1.getString("FIELD2");
                    String Latitude =c1.getString("FIELD3");
                    String Niveauglobal = c1.getString("FIELD12");
                    HashMap<String, String> contact1 = new HashMap<>();
                    ArrayList<String> mesure=new ArrayList<String>();
                    mesure.add(Latitude);
                    mesure.add(Longitude);
                    mesure.add(Niveauglobal);
                    Mesures.add(mesure);
                }
                //liste des antennes Ã  afficher
                /*for (int i = 0; i < ZoneAntenne.size(); i++) {
                    if(isVisible("48.853","2.35",ZoneAntenne.get(i).get(0),ZoneAntenne.get(i).get(1),polygones.get(i)))
                        antennesafficher.add(ZoneAntenne.get(i));
                }
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



            } catch (final Exception e) {
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


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            String[] test= new String[]{ZoneAntenne.size()+"",Mesures.size()+"",hashhauteur.size()+"",Antennes.size()+"",
                    "Benoit", "Cyril", "David", "Eloise", "Florent",
                    "Gerard", "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
                    "Mathieu", "Noemie", "Olivia", "Philippe", "Quentin", "Romain",
                    "Sophie", "Tristan", "Ulric", "Vincent", "Willy", "Xavier"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FetchDataActivity.this,
                    android.R.layout.simple_list_item_1, test);
            mListView.setAdapter(adapter);


        }


    }
}
