package com.example.mous.antennex;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class Fragment3 extends Fragment {
    public static Fragment3 newInstance() {
        Fragment3 fragment = new Fragment3();
        return fragment;
    }


    public static class MainActivity extends AppCompatActivity {

        static {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_test);

            if (savedInstanceState == null) {
                Fragment preferenceFragment = new PreferenceFragmentCustom();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.pref_container, preferenceFragment);
                ft.commit();
            }

        }


    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_test, container, false);
    }
}



