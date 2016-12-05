package com.rohithavatapally.weatherapp.adapter;

import android.view.View;
import android.widget.TextView;

import com.rohithavatapally.weatherapp.R;

/**
 * Created by RohithAvatapally on 12/3/16.
 */
class WeatherViewHolder extends CurrentWeatherViewHolder {

    TextView weatherTempMin;

    WeatherViewHolder(View itemView) {
        super(itemView);
        weatherTempMin =
                (TextView) itemView.findViewById(R.id.weather_temp_min);
    }

}
