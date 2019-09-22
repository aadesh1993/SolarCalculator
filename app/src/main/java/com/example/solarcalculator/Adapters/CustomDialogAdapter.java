package com.example.solarcalculator.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.solarcalculator.Models.LocationModels;
import com.example.solarcalculator.R;

import java.util.ArrayList;

public class CustomDialogAdapter extends ArrayAdapter<LocationModels> implements View.OnClickListener{

    private ArrayList<LocationModels> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView location;
        TextView sunrise;
        TextView sumset;
    }

    public CustomDialogAdapter(ArrayList<LocationModels> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        LocationModels dataModel=(LocationModels) object;



    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocationModels dataModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.sunrise = (TextView) convertView.findViewById(R.id.sunrise_time);
            viewHolder.sumset = (TextView) convertView.findViewById(R.id.sunset_time);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        viewHolder.location.setText(dataModel.getLocation());
        viewHolder.sunrise.setText(dataModel.getSunrise());
        viewHolder.sumset.setText(dataModel.getSunset());
        return convertView;
    }
}