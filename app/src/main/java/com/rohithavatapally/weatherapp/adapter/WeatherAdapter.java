package com.rohithavatapally.weatherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rohithavatapally.weatherapp.R;
import com.rohithavatapally.weatherapp.network.WeatherResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class WeatherAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OTHER_WEATHER = 0;
    private static final int CURRENT_WEATHER = 1;

    private final List<WeatherResponse> list;
    private final Context context;

    public WeatherAdapter(final Context context,
                          final List<WeatherResponse> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // If viewType is CURRENT_WEATHER then inflate
        // current weather view
        if (viewType == CURRENT_WEATHER) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_weather_current, parent, false);
            return new CurrentWeatherViewHolder(view);
        } else {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_weather, parent, false);
            return new WeatherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {
        final WeatherResponse response = list.get(position);
        if (holder instanceof WeatherViewHolder) {
            final WeatherViewHolder weatherViewHolder =
                    (WeatherViewHolder) holder;
            weatherViewHolder.weatherTempMax
                    .setText(getMaxTemp(response.getTemperatureMax()));
            weatherViewHolder.weatherTempMin
                    .setText(getMinTemp(response.getTemperatureMin()));
            weatherViewHolder.weatherDesc
                    .setText(response.getWeatherDesc());
            weatherViewHolder.weatherDay
                    .setText(getDayString(response.getDay()));
            Picasso.with(context)
                    .load(response.getIcon())
                    .into(weatherViewHolder.weatherIcon);
        } else if (holder instanceof CurrentWeatherViewHolder) {
            final CurrentWeatherViewHolder currentWeatherViewHolder =
                    (CurrentWeatherViewHolder) holder;
            currentWeatherViewHolder.weatherTempMax
                    .setText(getMaxTemp(response.getTemperatureMax()));
            currentWeatherViewHolder.weatherDesc
                    .setText(response.getWeatherMain());
            currentWeatherViewHolder.weatherDay
                    .setText(getDayString(response.getDay()));
            Picasso.with(context)
                    .load(response.getIcon())
                    .into(currentWeatherViewHolder.weatherIcon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // If first element in the list,
        // then view type is current weather
        if (position == 0) {
            return CURRENT_WEATHER;
        } else {
            return OTHER_WEATHER;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private String getDayString(final int day) {
        return context.getResources().getStringArray(R.array.days)[day - 1];
    }

    private String getMinTemp(String temp) {
        return context.getString(R.string.temp_min, temp);
    }

    private String getMaxTemp(String temp) {
        return context.getString(R.string.temp_max, temp);
    }
}
