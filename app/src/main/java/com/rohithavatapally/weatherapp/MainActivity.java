package com.rohithavatapally.weatherapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rohithavatapally.weatherapp.fragment.LocationFragment;
import com.rohithavatapally.weatherapp.fragment.WeatherFragment;

public class MainActivity extends AppCompatActivity implements
        LocationFragment.OnLocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, new LocationFragment());
        ft.commit();
    }

    @Override
    public void onLocation(Location location) {
        if (location != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, WeatherFragment.newInstance(location));
            ft.commit();
        }
    }
}
