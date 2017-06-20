package com.example.mous.antennex;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
<<<<<<< a936adad094b9a98c657c255d58bee8739fb4309

public class Fragment2 extends Fragment implements View.OnClickListener{

    Button gallery_btn ;

    Button resume_btn;

    Button carte_btn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        View myView = inflater.inflate(R.layout.fragment2_layout, container, false);
        gallery_btn = (Button) myView.findViewById(R.id.gallery);
        gallery_btn.setOnClickListener(this);
        resume_btn = (Button) myView.findViewById(R.id.resume_btn);
        resume_btn.setOnClickListener(this);
        carte_btn = (Button) myView.findViewById(R.id.carte_btn);
        carte_btn.setOnClickListener(this);


        return myView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.gallery:
                Intent intent = new Intent (getActivity(), GalleryActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.resume_btn:
                Intent intent2 = new Intent (getActivity(), ResumeActivity.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.carte_btn:
                Intent intent3 = new Intent (getActivity(), ResumeActivity.class);
                getActivity().startActivity(intent3);
                break;
        }


    }


=======

public class Fragment2 extends Fragment {

    Button gallery_btn ;




>>>>>>> 22cf2e6af8734ec60fb560868be50075b1812d8b
    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}