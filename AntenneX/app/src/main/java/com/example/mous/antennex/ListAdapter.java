package com.example.mous.antennex;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends ArrayAdapter<String> {

    private ProximityActivity activity;
    private List<String> operatorList;
    private List<String> searchList;
    private List<String> hauteursList; // Pour l'affichage de l'état de connexion
    private ArrayList<Integer> profilePictures;
    private List<String> hauteurList;


    public ListAdapter(ProximityActivity context, int resource, List<String> objects, List<String> hauteurs, List<String> hauteurList, ArrayList<Integer> profilePictures) {
        super(context, resource, objects);
        this.activity = context;
        this.operatorList = objects;
        this.searchList = new ArrayList<>();
        this.searchList.addAll(operatorList);
        this.hauteursList = hauteurs;
        this.profilePictures = profilePictures;
        this.hauteurList=hauteurList;
    }

    @Override
    public int getCount() {

        return operatorList.size();
    }

    @Override
    public String getItem(int position) {

        return operatorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.operatorName.setText(getItem(position));

        if (hauteursList.get(position).equals("Connecté")) {
            holder.operatorConnected.setTextColor(Color.parseColor("#1E824C"));
        }
        else {
            holder.operatorConnected.setTextColor(Color.parseColor("#bc060b"));
        }
        holder.operatorConnected.setText(hauteursList.get(position));

        int identifiant = profilePictures.get(position);

        if (operatorList.get(position).equals("SFR, 4G") || operatorList.get(position).equals("SFR, 3G") || operatorList.get(position).equals("SFR, 2G") ){
            holder.imageView.setImageDrawable(activity.getDrawable(R.drawable.sfr));
        }
        if (operatorList.get(position).equals("ORANGE, 4G") || operatorList.get(position).equals("ORANGE, 3G") || operatorList.get(position).equals("ORANGE, 2G") ){
            holder.imageView.setImageDrawable(activity.getDrawable(R.drawable.orange));
        }

        if (operatorList.get(position).equals("BOUYGUES TELECOM, 4G") || operatorList.get(position).equals("BOUYGUES TELECOM, 3G") || operatorList.get(position).equals("BOUYGUES TELECOM, 2G")){
            holder.imageView.setImageDrawable(activity.getDrawable(R.drawable.bouygues));
        }





        holder.operatorHauteur.setText(hauteurList.get(position));






        return convertView;
    }

    private class ViewHolder {
        private CircleImageView imageView;
        private TextView operatorName;
        private TextView operatorConnected;
        private TextView operatorHauteur;



        public ViewHolder(View v) {
            imageView = (CircleImageView) v.findViewById(R.id.profile_image);
            operatorName = (TextView) v.findViewById(R.id.text);
            operatorConnected = (TextView) v.findViewById(R.id.text2);
            operatorHauteur=(TextView) v.findViewById(R.id.text3);
        }
    }

    // Filter class :
    public void filter (String charText){
        charText=charText.toLowerCase(Locale.getDefault());
        operatorList.clear();
        if (charText.length()==0){
            operatorList.addAll(searchList);
        }
        else{
            for (String s: searchList){
                if (s.toLowerCase(Locale.getDefault()).contains(charText)){
                    operatorList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
}
