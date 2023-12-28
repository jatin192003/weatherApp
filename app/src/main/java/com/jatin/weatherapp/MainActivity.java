package com.jatin.weatherapp;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    TextView location , mainTemp , maxTemp , minTemp , dayText , dateText;
    private LocationManager locationManager;
    private Location currentLocation;

    void fetchWeather(String city){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid=417dd444ad823b88f324737494eca99b";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    String name = response.getString("name");
                    String temp = main.getString("temp");
                    String maxtemp = main.getString("temp_max");
                    String mintemp = main.getString("temp_min");
                    double tempvaluedouble = Double.parseDouble(temp);
                    tempvaluedouble = tempvaluedouble - 273.15;
                    temp = Double.toString(tempvaluedouble).substring(0,5);
                    double tempmaxvaluedouble = Double.parseDouble(maxtemp);
                    tempmaxvaluedouble = tempmaxvaluedouble - 273.15;
                    maxtemp = Double.toString(tempmaxvaluedouble).substring(0,5);
                    double tempminvaluedouble = Double.parseDouble(mintemp);
                    tempminvaluedouble = tempminvaluedouble - 273.15;
                    mintemp = Double.toString(tempminvaluedouble).substring(0,5);
                    String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);
                    mainTemp.setText(temp+"° C");
                    maxTemp.setText("MAX TEMP: "+maxtemp+"° C");
                    minTemp.setText("MIN TEMP: "+mintemp+"° C");
                    location.setText(name);
                    dateText.setText(date);
                    dayText.setText(dayOfTheWeek);


                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "An error occured! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Invalid city name!", Toast.LENGTH_SHORT).show();
                mainTemp.setText("");
                maxTemp.setText("");
                minTemp.setText("");
            }
        });

        queue.add(jsonObjectRequest);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        searchView = findViewById(R.id.searchView);
        location = findViewById(R.id.location);
        mainTemp = findViewById(R.id.mainTemp);
        maxTemp = findViewById(R.id.maxTemp);
        minTemp = findViewById(R.id.minTemp);
        dayText = findViewById(R.id.dayText);
        dateText = findViewById(R.id.dateText);

        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener(){
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();


        String city ="noida";
        fetchWeather(city);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchWeather(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });




    }
    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //removeLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            return;
        }
    }}