package com.example.myweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.myweather.ArgoAPI.Argo;
import com.example.myweather.ArgoAPI.ArgoCallback;
import com.example.myweather.ArgoAPI.ArgoExceptions.ArgoConnectionException;
import com.example.myweather.ArgoAPI.ArgoExceptions.ArgoParseException;
import com.example.myweather.ArgoAPI.UnitsOfMeasurements;
import com.example.myweather.ArgoAPI.Weather;


public class MainActivity extends AppCompatActivity implements ArgoCallback, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final int LOCATION_PERMISSION_CODE = 4;
    private final int INTERNET_PERMISSION_CODE = 3;

    private TextView errorField;
    private ImageView errorIcon;
    private TextView pressureField, humidityField, windSpeedField, tempField;
    private TextView pressureValue, humidityValue, windSpeedValue, tempValue;
    private ImageView pressureIcon, tempIcon, humidityIcon, windIcon;
    private TextView cityValue;
    private RequestQueue queue;
    private Argo argo;
    private LocationManager locationManager;
    private SharedPreferences sharedPreferences;
    private TextView unitField;
    private ProgressBar progressBar;
    private TextView loadingState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: init PreferenceManager");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String theme = getThemeFromSettings();
        Log.i(TAG, "onCreate: theme setting = " + theme);
        applyTheme(theme);

        Log.i(TAG, "onCreate: creating view");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        loadingState = findViewById(R.id.loading_state);
        showProgressBar(getString(R.string.show_progress_getting_user_Settings));


        Log.i(TAG, "onCreate: initing Volley");
        queue = Volley.newRequestQueue(MainActivity.this);

        Log.i(TAG, "onCreate: initing views");
        pressureField = findViewById(R.id.pressure);
        humidityField = findViewById(R.id.humidity);
        windSpeedField = findViewById(R.id.wind_speed);
        tempField = findViewById(R.id.temp);

        pressureValue = findViewById(R.id.pressure_value);
        windSpeedValue = findViewById(R.id.wind_speed_value);
        tempValue = findViewById(R.id.temp_value);
        humidityValue = findViewById(R.id.humidity_value);

        tempIcon = findViewById(R.id.temp_image);
        pressureIcon = findViewById(R.id.pressure_image);
        humidityIcon = findViewById(R.id.humidity_image);
        windIcon = findViewById(R.id.wind_image);
        cityValue = findViewById(R.id.city_name_field);
        unitField = findViewById(R.id.unit_field);

        errorField = findViewById(R.id.error_message);
        errorIcon = findViewById(R.id.error_img);


        Log.i(TAG, "onCreate: Calling Setting helpers");
        String unit = getUnitFromSettings();
        Log.i(TAG, "onCreate: unit setting = " + unit);


        Log.i(TAG, "onCreate: creating Argo API");
        argo = new Argo(UnitsOfMeasurements.valueOf(unit));

        askForPermission();
        if (!checkInternetPermission()) {
            Log.i(TAG, "onCreate: displaying no internet error message");
            displayErrorMessage("Must have internet to get weather ");
            displayErrorMessage(getString(R.string.display_error_must_have_internet));
            hideProgressBar();
        } else {
            Log.i(TAG, "onCreate: fetching API weather");
            updateWeather();
        }
    }

    private void hideProgressBar() {
        loadingState.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar(String task) {
        loadingState.setText(task);
        progressBar.setVisibility(View.VISIBLE);
        loadingState.setVisibility(View.VISIBLE);
    }

    /**
     * Asks for permission if SDK is under API level 23
     */
    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkInternetPermission()) {
                requestInternetPermission();
                Log.i(TAG, "onCreate: Internet not granted");
            }
            //if Location permission is not granted ask
            if (!checkLocationPermission()) {
                requestLocationPermission();
                Log.i(TAG, "onCreate: Location not granted");
            }
        }
    }

    private boolean checkInternetPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Asks user if we can have Location Coarse permission through a alertDialog box if user accepts a permission dialog is started for system.
     */
    private void requestInternetPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_needed)
                    .setMessage(getString(R.string.why_permission_is_needed))
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        Log.i(TAG, "onClick: User wont grant Internet permission");
                        dialog.cancel();
                    }).show();
        }
    }

    /**
     * Asks user if we can have Location Coarse permission through a alertDialog box if user accepts a permission dialog is started for system.
     */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i(TAG, "requestLocationPermission: shouldShowRequestPermissionRationale = true");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_needed)
                    .setMessage(getString(R.string.why_permission_is_needed))
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        Log.i(TAG, "onClick: User wont grant permission");
                        dialog.cancel();
                    }).show();
        }
    }

    /**
     * Is called when a user accepts or denies a Permission reques
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  String: The requested permissions. Never null.
     * @param grantResults int: The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: location permission code grantResults[0] = " + grantResults[0]);
                Toast.makeText(this, "Permission Location granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Internet permission code grantResults[0] = " + grantResults[0]);
                Toast.makeText(this, "Permission Internet granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Gets Unit of measurement from settings
     *
     * @return UnitsOfMeasurements from sharedPreferences if a ClassCastException is raised a default will be returned
     */
    private String getUnitFromSettings() {
        Log.i(TAG, "getUnitFromSettings: getting measurement unit from settings");
        String unit;
        try {
            unit = sharedPreferences.getString("unit", UnitsOfMeasurements.Imperial.getUnitName());
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: unit was not set to string", e);
            unit = UnitsOfMeasurements.Imperial.getUnitName();
        }
        return unit;
    }

    /**
     * Gets Theme of measurement from settings
     *
     * @return UnitsOfMeasurements from sharedPreferences if if a ClassCastException is raised a default will be returned
     */
    private String getThemeFromSettings() {
        Log.i(TAG, "getThemeFromSettings: getting theme for settings");
        String theme;
        try {
            theme = sharedPreferences.getString("theme", "Light Theme");
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: theme was not set to string", e);
            theme = "Light Theme";
        }
        return theme;
    }

    /**
     * A helper class to change the theme of the App
     */
    private void applyTheme(String theme) {
        Log.i(TAG, "applyTheme: theme");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Starts a UI thread to fade out all views that are used in showing weather values
     */
    public void hideElements() {
        Log.i(TAG, "hideElements: about to hide elements");
        this.runOnUiThread(() -> {
            pressureField.setVisibility(View.INVISIBLE);
            humidityField.setVisibility(View.INVISIBLE);
            windSpeedField.setVisibility(View.INVISIBLE);
            tempField.setVisibility(View.INVISIBLE);

            cityValue.setVisibility(View.INVISIBLE);
            pressureValue.setVisibility(View.INVISIBLE);
            tempValue.setVisibility(View.INVISIBLE);
            humidityValue.setVisibility(View.INVISIBLE);
            windSpeedValue.setVisibility(View.INVISIBLE);

            tempIcon.setVisibility(View.INVISIBLE);
            pressureIcon.setVisibility(View.INVISIBLE);
            humidityIcon.setVisibility(View.INVISIBLE);
            windIcon.setVisibility(View.INVISIBLE);
        });

    }


    /**
     * Gets called from the Argo API when a API request is successful parse into a weather object
     *
     * @param weather weather object representing information for Agro API
     */
    @Override
    public void success(Weather weather) {
        hideProgressBar();
        hideErrorMessage();
        Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "success: from Argo -> city name = " + weather.getCity());
        Runnable runnable = () -> {
            cityValue.setText(weather.getCity());
            Log.i(TAG, "success: ");
            pressureValue.setText(String.valueOf(weather.getPressure()));
            tempValue.setText(String.valueOf(weather.getTemperature()));
            humidityValue.setText(String.valueOf(weather.getHumidity()));
            windSpeedValue.setText(String.valueOf(weather.getWind()));
            unitField.setText(weather.getMeasurementType());

            // set color for wind based on value
            windSpeedValue.setTextColor(weather.getColorForWind());
            tempValue.setTextColor(weather.getTempColor());

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);
            fadeIn.setFillAfter(true);

            pressureField.startAnimation(fadeIn);
            humidityField.startAnimation(fadeIn);
            windSpeedField.startAnimation(fadeIn);
            tempField.startAnimation(fadeIn);

            cityValue.startAnimation(fadeIn);
            pressureValue.startAnimation(fadeIn);
            tempValue.startAnimation(fadeIn);
            humidityValue.startAnimation(fadeIn);
            windSpeedValue.startAnimation(fadeIn);

            tempIcon.startAnimation(fadeIn);
            pressureIcon.startAnimation(fadeIn);
            humidityIcon.startAnimation(fadeIn);
            windIcon.startAnimation(fadeIn);
        };

        this.runOnUiThread(runnable);
    }

    /**
     * Call Argo API based on weather a Location Permission is granted or not
     */
    private void updateWeather() {
        showProgressBar(getString(R.string.show_progress_getting_weather));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "updateWeather: is locationManager is null = " + (locationManager == null));
            if (locationManager == null)
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Log.i(TAG, "updateWeather: about to send Location request");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.ACCURACY_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
            String provider = locationManager.getBestProvider(criteria, true);
            Log.i(TAG, "updateWeather: provider =" + provider);
            locationManager.requestLocationUpdates(provider, 1000, 1, this);
        } else {
            Log.i(TAG, "updateWeather: location permission not granted using zagreb");
            queue.add(argo.getCityWeather("Zagreb", this));
        }
    }

    /**
     * Gets Called from Agro API if a error has taken place
     *
     * @param e may be of instance ArgoConnectionException or ArgoParseException
     */
    @Override
    public void unsuccessful(Exception e) {
        hideElements();
        hideProgressBar();
        Log.i(TAG, "unsuccessful: ");
        if (e instanceof ArgoConnectionException) {
            displayErrorMessage("Could Not Connect to API");
            Log.e(TAG, "unsuccessful: ", e);
        } else if (e instanceof ArgoParseException) {
            displayErrorMessage("API returned unexpected result");
            Log.e(TAG, "unsuccessful: ", e);
        }
    }
    //TODO create enum for settings

//TODO             applyTheme(get);

    public void displayErrorMessage(String error) {
        errorIcon.setVisibility(View.VISIBLE);
        errorField.setText(error);
        errorField.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage() {
        errorIcon.setVisibility(View.INVISIBLE);
        errorField.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String unit = getUnitFromSettings();
        String theme = getThemeFromSettings();
        applyTheme(theme);
        argo.setUnit(UnitsOfMeasurements.valueOf(unit));
        updateWeather();
    }

    /**
     * When the user clicks the refresh floating button this method will be called
     *
     * @param view given for OnClickListener
     */
    public void floatingButton(View view) {
        updateWeather();
    }

    /**
     * Will be called from LocationManager when network determines users location
     *
     * @param location Location of User
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: coords received");
        Log.i(TAG, "onLocationChanged: calling argo getCoordWeather");
        showProgressBar(getString(R.string.show_progress_getting_weather));
        queue.add(argo.getCoordWeather(location.getLatitude(), location.getLongitude(), this));
        locationManager.removeUpdates(this);
    }

    @Override //!deprecated in Q
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onProviderDisabled: called");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
