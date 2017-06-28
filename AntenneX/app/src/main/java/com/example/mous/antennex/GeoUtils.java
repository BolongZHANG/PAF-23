package com.example.mous.antennex ;

/**
 * Created by Salim on 25/06/2017.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GeoUtils{

    public static double pas = 0.00001;

    public static boolean isVisible(String myLatitude,
                                    String myLongitude,
                                    String poiLatitude,
                                    String poiLongitude,
                                    ArrayList<Polygon> polyList) {
        Polygon poiPoly = null;

        Set<Polygon> polySet = new HashSet<>();
        ArrayList<Point> pointList = divideLine(Float.parseFloat(myLatitude), Float.parseFloat(myLongitude), Float.parseFloat(poiLatitude), Float.parseFloat(poiLongitude), pas);
        Point p=new Point(Float.parseFloat(poiLatitude),Float.parseFloat(poiLongitude));
        for(Polygon poly : polyList){
            if(poly.contains(p)) {
                poiPoly = poly;
            }

            for(Point point : pointList){
                if(poly.contains(point)){
                    polySet.add(poly);
                    break;
                }
            }
        }

        polySet.remove(poiPoly);
        return polySet.isEmpty();
    }

    private static ArrayList<Point> divideLine(float startX, float startY, float endX, float endY, double pas){
        ArrayList<Point> pointList = new ArrayList<>();
        double hypotenuse = Math.sqrt((endY - startY) * (endY - startY) + (endX - startX) * (endX - startX));
        double x_pas = pas * Math.abs(endX - startX) / hypotenuse;
        double y_pas = pas * Math.abs(endY - startY) / hypotenuse;
        float x = startX;
        float y = startY;
        double process = 0;

        while(process < hypotenuse){
            pointList.add(new Point(x, y));
            x += x_pas;
            y += y_pas;
            process += pas;
        }

        return pointList;
    }

}
