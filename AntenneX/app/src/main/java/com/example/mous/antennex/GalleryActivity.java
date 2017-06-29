package com.example.mous.antennex;





import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
        import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity {

    TextView textTargetUri;
    ImageView targetImage;
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ListAdapterGallery adapter;
    private ArrayList<String> distanceList;
    private ArrayList<String> hauteurList;


    private ArrayList<Integer> profilePictures;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gallerie ...");

        setData();
        adapter = new ListAdapterGallery(this, R.layout.item_listview, stringArrayList, distanceList,hauteurList, profilePictures);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), (String)parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.homeItem:
                                Intent intent = new Intent(GalleryActivity.this,MainActivity.class);

                                /*intent.putExtra("Check",1);*/
                                startActivity(intent);
                                break;
                            case R.id.historique:
                                finish();
                                break;
                            case R.id.parameter:
                                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);

                                intent2.putExtra("Check","2");
                                startActivity(intent2);

                                break;

                        }

                        return true;
                    }
                });

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.commit();*/

    }
    //POUR REVENIR AUX FRAGMENTS
    @Override
    public void onBackPressed()
    {

        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);

        intent2.putExtra("Check","2");
        startActivity(intent2);
    }

    // FIN POUR REVENIR AUX FRAGMENTS

    private void setData() {
        stringArrayList = new ArrayList<>();
        distanceList=new ArrayList<>();
        profilePictures=new ArrayList<>();
        hauteurList=new ArrayList<>();


        stringArrayList.add("ORANGE, 3G");
        stringArrayList.add("BOUYGUES TELECOM, 3G");
        stringArrayList.add("SFR, 3G");

        distanceList.add("3");
        distanceList.add("100");
        distanceList.add("300");

        hauteurList.add("hauteur: 5 mètres");
        hauteurList.add("hauteur: 70 mètres");
        hauteurList.add("hauteur: 200 mètres");






        profilePictures.add(5);
        profilePictures.add(5);
        profilePictures.add(5);


    }

    // To deal with the search View
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else{
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }



}
