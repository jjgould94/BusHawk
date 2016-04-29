package com.example.jjgould94.bushawk;

<<<<<<< Updated upstream
import android.content.Context;
import android.content.Intent;
=======
import android.media.Image;
>>>>>>> Stashed changes
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ListIterator;

public class BusView extends FragmentActivity implements OnMapReadyCallback {

    Handler UI_HANDLER = new Handler();
    private GoogleMap mMap;
    private Map<Integer, Marker> markerMap;
    private Context context;
    int routeNumber;
    int stopNumber;

    Runnable UI_UPDATE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FieldMouse");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {


                    if (e == null) {
                        //Object will be filled out with latitude and longitudes
                        Log.d("Objects", "Retrieved " + objects.size() + "buses");
                        ListIterator<ParseObject> busIterator = objects.listIterator();
                        LatLng newBus = null;
                        while (busIterator.hasNext()) {
                            ParseObject object = busIterator.next();
                            double lat = object.getDouble("latitude");
                            double lon = object.getDouble("longitude");
                            int name = object.getInt("busID");
                            String stringName = Integer.toString(name);
                            Log.d("Objects", "Name: " + stringName + " Lat: " + lat + "Lng: " + lon);
                            newBus = new LatLng(lat, lon);
                            if (markerMap.containsKey(name)) {
                                markerMap.get(name).setPosition(newBus);
                            } else {

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(newBus)
                                        .title(stringName)
                                        );
                                markerMap.put(name, marker);
                            }
                            //TODO: clear all markers and then add them back? Need list
                            //newBus= new LatLng(35.1,34.0);
                            //marker.setPosition(newBus);
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newBus, 15)); //15 corresponds to street level

                            //TODO addMarker returns a Marker, can add them to a list so that we can update easier
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newBus, 15)); //15 corresponds to street level

                    } else {
                        //Error occurred when querying the database
                        Log.d("Objects", "Error: " + e.getMessage());
                    }
                }
            });
            UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent myIntent = getIntent();
        routeNumber = myIntent.getIntExtra("routeNumber", 0);
        stopNumber = myIntent.getIntExtra("stopNumber", 0);

        //TODO: change this so that we are actually getting the stop and route num from the intent
        routeNumber = 1;
        stopNumber = 1;

        if (routeNumber == 0)
        {
            //TODO log an error message

        }
        if (stopNumber == 0)
        {
            //TODO log an error message
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.busMap);
        mapFragment.getMapAsync(this);
        markerMap = new HashMap<Integer, Marker>();

        final Button routeButton = (Button) findViewById(R.id.busViewRouteNum);
        final Button stopButton = (Button) findViewById(R.id.busViewStopNum);

        routeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Open the route activity
                Intent intent = new Intent(context, RouteView.class);
                intent.putExtra("routeNumber", routeNumber);
                startActivity(intent);

            }

        });

        stopButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Open the stop activity
                Intent intent = new Intent(context, StopView.class);
                intent.putExtra("stopNumber", stopNumber);
                startActivity(intent);
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI_HANDLER.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UI_HANDLER.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 1000);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 1000);

    }
}
