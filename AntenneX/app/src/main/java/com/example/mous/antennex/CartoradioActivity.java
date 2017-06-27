package com.example.mous.antennex;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.io.File;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import static com.example.mous.antennex.R.id.webView;

public class CartoradioActivity extends AppCompatActivity {



    static File file1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoradio);


        WebView myWebView = (WebView) findViewById(webView);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
                Log.i("geo", "onGeolocationPermissionsHidePrompt");
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin,
                                                           final GeolocationPermissions.Callback callback) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartoradioActivity.this);
                builder.setMessage("Allow to access location information?");
                DialogInterface.OnClickListener dialogButtonOnClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int clickedButton) {
                        if (DialogInterface.BUTTON_POSITIVE == clickedButton) {
                            callback.invoke(origin, true, true);
                        } else if (DialogInterface.BUTTON_NEGATIVE == clickedButton) {
                            callback.invoke(origin, false, false);
                        }
                    }
                };
                builder.setPositiveButton("Allow", dialogButtonOnClickListener);
                builder.setNegativeButton("Deny", dialogButtonOnClickListener);
                builder.show();
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                Log.i("geo", "onGeolocationPermissionsShowPrompt");
            }
        });
        myWebView.loadUrl("file:///android_asset/Cartoradio.html");

    }








}
