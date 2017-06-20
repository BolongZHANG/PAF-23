package com.example.mous.antennex;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fragment1 extends android.support.v4.app.Fragment{
    private Button btn_camera ;

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1_layout, container, false);
    }
}