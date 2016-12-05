package com.rohithavatapally.weatherapp.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class WeatherRequest {

    private static final String TAG = WeatherRequest.class.getSimpleName();

    // App id for authenticating the request
    private static final String APP_ID = "f238710c9868be0e872fa5951b63fb8e";

    // Base urls
    private static final String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private static final String iconBaseUrl = "http://openweathermap.org/img/w/%1$s.png";

    // Url request keys
    private final static String queryLat = "lat";
    private final static String queryLon = "lon";
    private final static String queryCount = "cnt";
    private final static String queryMode = "mode";
    private final static String queryUnits = "units";
    private final static String queryAppId = "appid";

    // Url request values
    private final static String unit = "imperial";
    private final static String mode = "json";

    // Keys from JSON response
    private final static String KEY_LIST = "list";
    private final static String KEY_TEMP = "temp";
    private final static String KEY_WEATHER = "weather";
    private final static String KEY_DATE = "dt";
    private final static String KEY_TEMP_MAX = "max";
    private final static String KEY_TEMP_MIN = "min";
    private final static String KEY_MAIN = "main";
    private final static String KEY_DESCRIPTION = "description";
    private final static String KEY_ICON = "icon";

    private final static int ITEM_COUNT = 10;

    /**
     * Callback for listening to weather request response
     */
    public interface ResponseListener {

        /**
         * Callback invoked when weather information is available
         * @param response list of {@link WeatherResponse} objects
         */
        void onResponse(List<WeatherResponse> response);

        void onError(WeatherException weatherException);
    }

    private final RequestQueue requestQueue;
    private final ResponseListener listener;

    public WeatherRequest(final Context context,
                          final ResponseListener listener) {
        requestQueue = Volley.newRequestQueue(context);
        this.listener = listener;
    }

    public void request(final double latitude,
                        final double longitude) {
        final Uri uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter(queryLat, Double.toString(latitude))
                .appendQueryParameter(queryLon, Double.toString(longitude))
                .appendQueryParameter(queryUnits, unit)
                .appendQueryParameter(queryCount, Integer.toString(ITEM_COUNT))
                .appendQueryParameter(queryMode, mode)
                .appendQueryParameter(queryAppId, APP_ID)
                .build();
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                uri.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseWeatherResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Weather request error: " +
                                error.getCause()
                                        .toString()
                                        .concat(error.getMessage()));
                        listener.onError(new WeatherException(
                                error.getMessage(),
                                WeatherException.ErrorType.NETWORK_ERROR,
                                error.getCause()));
                    }
                }
        );
        requestQueue.add(request);
    }

    private void parseWeatherResponse(final JSONObject jsonObject) {
        final List<WeatherResponse> response = new ArrayList<>();
        try {
            final JSONArray array = jsonObject.getJSONArray(KEY_LIST);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject object = array.getJSONObject(i);
                    final JSONObject temp = object.getJSONObject(KEY_TEMP);
                    final JSONObject weather = object
                            .getJSONArray(KEY_WEATHER)
                            .getJSONObject(0);
                    response.add(new WeatherResponse.Builder(object.getLong(KEY_DATE))
                            .withTemperatureMax(temp.getDouble(KEY_TEMP_MAX))
                            .withTemperatureMin(temp.getDouble(KEY_TEMP_MIN))
                            .withWeatherMain(weather.getString(KEY_MAIN))
                            .withWeatherDesc(weather.getString(KEY_DESCRIPTION))
                            // build icon url and set it in WeatherResponse object
                            .withIcon(String.format(
                                    iconBaseUrl,
                                    weather.getString(KEY_ICON)))
                            .build());
                }
            }
            listener.onResponse(response);
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON, message: " + e.getMessage());
            listener.onError(new WeatherException(
                    e.getMessage(),
                    WeatherException.ErrorType.JSON_PARSE));
        }
    }
}
