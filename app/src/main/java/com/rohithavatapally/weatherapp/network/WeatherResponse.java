package com.rohithavatapally.weatherapp.network;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class WeatherResponse {

    private final int day;
    private final String temperatureMin;
    private final String temperatureMax;
    private final String weatherMain;
    private final String weatherDesc;
    private final String icon;

    private WeatherResponse(Builder builder) {
        this.temperatureMin = builder.temperatureMin;
        this.temperatureMax = builder.temperatureMax;
        this.weatherMain = builder.weatherMain;
        this.weatherDesc = builder.weatherDesc;
        this.icon = builder.icon;
        this.day = builder.date;
    }

    public int getDay() {
        return day;
    }

    public String getTemperatureMin() {
        return temperatureMin;
    }

    public String getTemperatureMax() {
        return temperatureMax;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public String getIcon() {
        return icon;
    }

    static class Builder {

        private int date;
        private String temperatureMin;
        private String temperatureMax;
        private String weatherMain;
        private String weatherDesc;
        private String icon;

        private final DecimalFormat df;

        Builder(long date) {
            df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) date*1000);
            this.date = calendar.get(Calendar.DAY_OF_WEEK);
        }

        Builder withTemperatureMin(double temperatureMin) {
            this.temperatureMin = df.format(temperatureMin);
            return this;
        }

        Builder withTemperatureMax(double temperatureMax) {
            this.temperatureMax = df.format(temperatureMax);
            return this;
        }

        Builder withWeatherMain(String weatherMain) {
            this.weatherMain = weatherMain;
            return this;
        }

        Builder withWeatherDesc(String weatherDesc) {
            this.weatherDesc = weatherDesc;
            return this;
        }

        Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        WeatherResponse build() {
            return new WeatherResponse(this);
        }
    }

}
