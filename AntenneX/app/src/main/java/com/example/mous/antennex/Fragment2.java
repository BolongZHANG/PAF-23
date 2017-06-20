package com.example.mous.antennex;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment2 extends Fragment implements View.OnClickListener{

    Button gallery_btn ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        View myView = inflater.inflate(R.layout.fragment2_layout, container, false);
        gallery_btn = (Button) myView.findViewById(R.id.gallery);
        gallery_btn.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent (getActivity(), GalleryActivity.class);
        getActivity().startActivity(intent);

    }


    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}