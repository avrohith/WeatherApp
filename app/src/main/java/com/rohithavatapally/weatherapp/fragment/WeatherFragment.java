package com.rohithavatapally.weatherapp.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rohithavatapally.weatherapp.R;
import com.rohithavatapally.weatherapp.adapter.WeatherAdapter;
import com.rohithavatapally.weatherapp.network.WeatherException;
import com.rohithavatapally.weatherapp.network.WeatherRequest;
import com.rohithavatapally.weatherapp.network.WeatherResponse;

import java.util.List;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class WeatherFragment extends Fragment implements
        WeatherRequest.ResponseListener {

    private static final String TAG = WeatherFragment.class.getSimpleName();

    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";

    public static WeatherFragment newInstance(Location location) {
        final WeatherFragment fragment = new WeatherFragment();
        final Bundle bundle = new Bundle();
        bundle.putDouble(LATITUDE, location.getLatitude());
        bundle.putDouble(LONGITUDE, location.getLongitude());
        fragment.setArguments(bundle);
        return fragment;
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView message;
    private ProgressBar progressBar;

    private double latitude;
    private double longitude;

    private WeatherRequest weatherRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        latitude = bundle.getDouble(LATITUDE);
        longitude = bundle.getDouble(LONGITUDE);
        weatherRequest = new WeatherRequest(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_weather, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        message = (TextView) view.findViewById(R.id.message);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                weatherRequest.request(latitude, longitude);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupInitialUI();
        // Get the updated weather information
        // whenever user comes back to this screen
        weatherRequest.request(latitude, longitude);
    }

    private void setupInitialUI() {
        progressBar.setVisibility(View.VISIBLE);
        message.setText(getString(R.string.loading_weather));
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(List<WeatherResponse> response) {
        message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setAdapter(new WeatherAdapter(getContext(), response));
    }

    @Override
    public void onError(WeatherException e) {
        Log.d(TAG, "Error occurred fetching weather, message: " + e.getMessage());
        swipeRefreshLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(e.getMessage());
    }
}
