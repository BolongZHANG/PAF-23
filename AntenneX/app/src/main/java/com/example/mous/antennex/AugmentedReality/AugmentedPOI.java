package com.example.mous.antennex.augmentedReality;

/**
 * Created by Mous on 20/06/2017.
 */

public class AugmentedPOI {
        private int mId;
        private String mName;
        private String mDescription;
        private double mLatitude;
        private double mLongitude;
        private double mAltitude;
        private String mType;

        public AugmentedPOI(String newName, String newDescription, String mType,
                            double newLatitude, double newLongitude, double newAltitude) {

            this.mName = newName;
            this.mType=mType;
            this.mDescription = newDescription;
            this.mLatitude = newLatitude;
            this.mLongitude = newLongitude;
            this.mAltitude=newAltitude;

        }

    public String getPoiType() {
        return mType;
    }
    public void setPoiType(String poiType) {
        this.mType = poiType;
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

        public void setPoiAltitude(double poiAltitude) {
        this.mAltitude = poiAltitude ;}

        public double getPoiAltitude (){
            return mAltitude;
        }

    }

