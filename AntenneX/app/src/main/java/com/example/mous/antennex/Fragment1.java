package com.example.mous.antennex;



import android.view.View;

import android.content.Intent;
        import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;
        import android.widget.Button;

import com.example.mous.antennex.augmentedReality.CoreActivity;
import com.example.mous.antennex.augmentedReality.JaugeActivity;


public class Fragment1 extends android.support.v4.app.Fragment implements View.OnClickListener {
    private Button btn_camera ;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        View myView = inflater.inflate(R.layout.fragment1_layout, container, false);
        btn_camera = (Button) myView.findViewById(R.id.camera);
        btn_camera.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent (getActivity(), FetchDataActivity.class);
        getActivity().startActivity(intent);

    }

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }}
