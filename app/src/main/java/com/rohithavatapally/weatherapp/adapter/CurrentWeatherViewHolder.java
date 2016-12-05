package com.rohithavatapally.weatherapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohithavatapally.weatherapp.R;

/**
 * Created by RohithAvatapally on 12/3/16.
 */
class CurrentWeatherViewHolder extends RecyclerView.ViewHolder {

    ImageView weatherIcon;
    TextView weatherDesc;
    TextView weatherTempMax;
    TextView weatherDay;

    CurrentWeatherViewHolder(View itemView) {
        super(itemView);
        weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
        weatherDesc = (TextView) itemView.findViewById(R.id.weather_desc);
        weatherTempMax =
                (TextView) itemView.findViewById(R.id.weather_temp_max);
        weatherDay = (TextView) itemView.findViewById(R.id.weather_day);
    }

}
