package com.example.mous.antennex;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProximityActivity extends AppCompatActivity {


    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ListAdapter adapter;
    private ArrayList<String> distanceList;
    private ArrayList<String> hauteurList;


    private ArrayList<Integer> profilePictures;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);
        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setData();
        adapter = new ListAdapter(this, R.layout.item_listview, stringArrayList, distanceList,hauteurList, profilePictures);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ProximityActivity.this, (String)parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setData() {
        stringArrayList = new ArrayList<>();
        distanceList=new ArrayList<>();
        profilePictures=new ArrayList<>();
        hauteurList=new ArrayList<>();


        stringArrayList.add("ORANGE, 3G");
        stringArrayList.add("BOUYGUES TELECOM, 3G");
        stringArrayList.add("SFR, 3G");

        distanceList.add("3 mètres");
        distanceList.add("3 mètres");
        distanceList.add("3 mètres");

        hauteurList.add("hauteur: 5mètres");
        hauteurList.add("hauteur: 5mètres");
        hauteurList.add("hauteur: 5mètres");




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
