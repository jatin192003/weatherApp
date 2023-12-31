package com.jatin.weatherapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    TextView location, mainTemp, maxTemp, minTemp, dayText, dateText;

    Button getCL;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        location = findViewById(R.id.location);
        mainTemp = findViewById(R.id.mainTemp);
        maxTemp = findViewById(R.id.maxTemp);
        minTemp = findViewById(R.id.minTemp);
        dayText = findViewById(R.id.dayText);
        dateText = findViewById(R.id.dateText);
        getCL = findViewById(R.id.getCL);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        getCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                Toast.makeText(MainActivity.this, city, Toast.LENGTH_SHORT).show();
                fetchWeather(city);
            }
        });

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
    void fetchWeather(String city) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=417dd444ad823b88f324737494eca99b";


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
                    temp = Double.toString(tempvaluedouble).substring(0, 5);
                    double tempmaxvaluedouble = Double.parseDouble(maxtemp);
                    tempmaxvaluedouble = tempmaxvaluedouble - 273.15;
                    maxtemp = Double.toString(tempmaxvaluedouble).substring(0, 5);
                    double tempminvaluedouble = Double.parseDouble(mintemp);
                    tempminvaluedouble = tempminvaluedouble - 273.15;
                    mintemp = Double.toString(tempminvaluedouble).substring(0, 5);
                    String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);
                    mainTemp.setText(temp + "° C");
                    maxTemp.setText("MAX TEMP: " + maxtemp + "° C");
                    minTemp.setText("MIN TEMP: " + mintemp + "° C");
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
                Toast.makeText(MainActivity.this, "city not found", Toast.LENGTH_SHORT).show();
                mainTemp.setText("");
                maxTemp.setText("");
                minTemp.setText("");
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void getLastLocation() {


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {


                                try {
                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    city = addresses.get(0).getLocality();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    });


        }else {

            askPermission();

        }

    }

    private void askPermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                getLastLocation();

            }else {


                Toast.makeText(MainActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();

            }



        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


