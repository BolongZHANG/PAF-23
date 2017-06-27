package com.paf.bolong.client_server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class AppRequest {

    public static JSONObject jsonAnfr(String longitude, String latitude, String distance) {
        String requete = "https://data.anfr.fr/api/records/1.0/search/?"
                + "dataset=sup_support&geofilter.distance="
                + longitude + "," + latitude + "," + distance;

        try {
            URL url = new URL(requete);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                JSONObject j = new JSONObject(strTemp);
                return j;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static JSONObject jsonVolumes(double MyLongitude, double MyLatitude, double PoiLongitude, double PoiLatitude) {
        String requete = "https://opendata.paris.fr/api/records/1.0/search/?"
                + "dataset=volumesbatisparis2011&geofilter.polygon="
                + "(" + MyLongitude + "%2C" + MyLatitude + ")" + "%2C"
                + "(" + PoiLongitude + "%2C" +PoiLatitude + ")" + "%2C"
                + "(" + MyLongitude + "%2C" + (MyLatitude+0.000001) + ")";

        try {
            URL url = new URL(requete);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(),"utf-8"));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                JSONObject j = new JSONObject(strTemp);
                return j;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}