package com.hrily.artutorial;

import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bolong on 2017/6/20.
 */

public class AntenneModel {

    private static double AZIMUTH_ACCURACY = 25;

    private int mId;
    private String mName;
    private String mDescription;
    private double mLatitude;
    private double mLongitude;
    private double mAzimuthTheoretical = 0;
    private ImageView imageView;

    public AntenneModel(String newName, String newDescription,
                        double newLatitude, double newLongitude) {
        this.mName = newName;
        this.mDescription = newDescription;
        this.mLatitude = newLatitude;
        this.mLongitude = newLongitude;
    }

    public int getPoiId() {
        return mId;
    }
    public void setPoiId(int poiId) {
        this.mId = poiId;
    }
    public String getPoiName() {
        return mName;
    }
    public void setPoiName(String poiName) {
        this.mName = poiName;
    }
    public String getPoiDescription() {
        return mDescription;
    }
    public void setPoiDescription(String poiDescription) {
        this.mDescription = poiDescription;
    }
    public double getPoiLatitude() {
        return mLatitude;
    }
    public void setPoiLatitude(double poiLatitude) {
        this.mLatitude = poiLatitude;
    }
    public double getPoiLongitude() {
        return mLongitude;
    }
    public void setPoiLongitude(double poiLongitude) {
        this.mLongitude = poiLongitude;
    }
    public void setImageView(ImageView imageView) { this.imageView = imageView;}
    public ImageView getImageView() { return this.imageView; }
    public void distory(){
        this.mName = null;
        this.mDescription = null;
        this.imageView = null;
    }

    public void calculateTheoreticalAzimuth(double mMyLatitude, double mMyLongitude){

        double dy = this.mLatitude - mMyLatitude;
        double dx = this.mLongitude - mMyLongitude;

        double phiAngle;
        double tanPhi;

        tanPhi = Math.abs(dx / dy);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        // phiAngle = [0,90], check quadrant and return correct phiAngle
        if (dy > 0 && dx > 0) { // I quadrant
            this.mAzimuthTheoretical =  phiAngle;
        } else if (dy < 0 && dx > 0) { // II
            this.mAzimuthTheoretical =  180 - phiAngle;
        } else if (dy < 0 && dx < 0) { // III
            this.mAzimuthTheoretical =  180 + phiAngle;
        } else if (dy > 0 && dx < 0) { // IV
            this.mAzimuthTheoretical = 360 - phiAngle;
        }else {
            this.mAzimuthTheoretical = phiAngle;
        }

    }

    public List<Double> calculateAzimuthAccuracy(double azimuth) {
        // Returns the Camera View Sector
        List<Double> minMax = new ArrayList<Double>();
        double minAngle = (azimuth - AZIMUTH_ACCURACY + 360) % 360;
        double maxAngle = (azimuth + AZIMUTH_ACCURACY) % 360;
        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);
        return minMax;
    }

    public boolean isBetween(double minAngle, double maxAngle, double azimuth) {
        // Checks if the azimuth angle lies in minAngle and maxAngle of Camera View Sector
        if (minAngle > maxAngle) {
            if (isBetween(0, maxAngle, azimuth) || isBetween(minAngle, 360, azimuth))
                return true;
        } else if (azimuth > minAngle && azimuth < maxAngle)
            return true;
        return false;
    }

}
