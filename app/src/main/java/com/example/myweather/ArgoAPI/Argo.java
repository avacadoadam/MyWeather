package com.example.myweather.ArgoAPI;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.myweather.ArgoAPI.ArgoExceptions.ArgoConnectionException;
import com.example.myweather.ArgoAPI.ArgoExceptions.ArgoParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Handles API request to Argo API
 */
public class Argo {
    private static final String TAG = "Argo";
    private final String appID = "fea3bab1ce66c97c9ede450b9ce42fe5";
    private UnitsOfMeasurements unit;

    public Argo(UnitsOfMeasurements unit) {
        this.unit = unit;
    }

    public StringRequest getCityWeather(String cityName, ArgoCallback callback) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s";
        String urlFormatted = String.format(url, cityName, appID, this.unit.getUnitName());
        Log.d(TAG, "getCityWeather: url = " + url);
        return new StringRequest(Request.Method.GET,
                urlFormatted,
                response -> {
                    Weather weather = parseJSON(response);
                    if (weather == null)
                        callback.unsuccessful(new ArgoParseException("Could Not Parse JSON"));
                    else callback.success(weather);
                },
                error -> {
                    Log.e(TAG, "getCityWeather: ", error);
                    callback.unsuccessful(new ArgoConnectionException("Could not connect to API"));
                });
    }

    /**
     * Querys API based on coordinates of user
     *
     * @param lat      Latitude coordinates of user
     * @param lon      Longitude coordinates of user
     * @param callback interface
     * @return A StringRequest format with URl of API for Volley API
     */
    public StringRequest getCoordWeather(double lat, double lon, ArgoCallback callback) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=%3.6f&lon=%3.6f&appid=%s&units=%s";
        String urlFormatted = String.format(Locale.US,url, lat, lon, appID, unit.getUnitName());
        Log.d(TAG, "getCityWeather: url = " + url);
        return new StringRequest(Request.Method.GET,
                urlFormatted,
                response -> {
                    Weather weather = parseJSON(response);
                    if (weather == null)
                        callback.unsuccessful(new ArgoParseException("Could Not Parse JSON"));
                    else callback.success(weather);
                },
                error -> {
                    Log.e(TAG, "getCityWeather: ", error);
                    callback.unsuccessful(new ArgoConnectionException("Could not connect to API"));
                });
    }

    /**
     * Parses JSON into Weather Object
     *
     * @param response from API should be valid JSON
     * @return Weather Object or null if a JSONException was raised
     */
    private Weather parseJSON(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            return new Weather(obj.getString("name"),
                    obj.getJSONObject("main").getDouble("temp")
                    , obj.getJSONObject("main").getDouble("humidity")
                    , obj.getJSONObject("wind").getDouble("speed")
                    , obj.getJSONObject("main").getDouble("pressure")
                    , unit);
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: Failed to parse JSON from API", e);
            return null;
        }
    }

    public void setUnit(UnitsOfMeasurements unit) {
        this.unit = unit;
    }
}
