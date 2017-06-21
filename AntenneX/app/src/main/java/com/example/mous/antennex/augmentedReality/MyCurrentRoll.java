package com.example.mous.antennex.augmentedReality;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;


/**
 * Created by Mous on 21/06/2017.
 */

public class MyCurrentRoll implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;
    private int rollFrom = 0;
    private int rollTo = 0;
    private OnRollChangedListener mRollListener;
    Context mContext;

    public MyCurrentRoll(OnRollChangedListener rollListener, Context context) {
        mRollListener = rollListener;
        mContext = context;
    }

    public void start(){
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //@TODO: CHECKER SI CE QUI SUIT EST BON : J'ai modifie la premiere condition à != null !!



        if(sensor == null) sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        if(sensor == null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            Toast.makeText(mContext, "Using Game Rotation Vector. Direction may not be accurate!", Toast.LENGTH_SHORT).show();
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnRollChangedListener listener) {
        mRollListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        rollFrom = rollTo;

        float[] orientation = new float[3];
        float[] rMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rMat, event.values);
        rollTo =  (- (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[2] ))) % 180;

        mRollListener.onRollChanged(rollFrom, rollTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        //@TODO: COMPLETER !!
        return;

    }
}




