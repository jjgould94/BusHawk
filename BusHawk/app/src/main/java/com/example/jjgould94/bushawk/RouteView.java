package com.example.jjgould94.bushawk;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.ListIterator;

public class RouteView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int thisRouteNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.routeMap);
        mapFragment.getMapAsync(this);
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

        //This below sets the default map area to Lawrence, KS coordinates
        double lat = 38.971332;
        double lon = -95.236166;
        LatLng Lawrence = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lawrence, 13));

        LatLng point1 = new LatLng(38.957048, -95.252098);
        LatLng point2 = new LatLng(38.956865, -95.260466);
        LatLng point3 = new LatLng(38.949990, -95.260488);
        PolylineOptions route1 = new PolylineOptions()
                .add(point1)
                .add(point2)
                .add(point3);
        route1.color(Color.BLUE);
        //route.width(4);
        mMap.addPolyline(route1);

        //TODO: move everything below into a separate method, call the method here but also call it in the scheduler/timer task
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FieldMouse");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //Object will be filled out with latitude and longitudes
                    Log.d("Objects", "Retrieved " + objects.size() + " buses");
                    ListIterator<ParseObject> busIterator = objects.listIterator();
                    while (busIterator.hasNext()) {
                        ParseObject object = busIterator.next();
                        int route = object.getInt("route");
                        double lat = object.getDouble("latitude");
                        double lon = object.getDouble("longitude");
                        int name = object.getInt("busID");
                        String stringName = Integer.toString(name);
                        Log.d("Objects", "Name: " + stringName + " Lat: " + lat + "Lng: " + lon + "Route: "+ route);

                        //Only putting the buses that are on this route onto the map
                        if (route == thisRouteNum) {
                            LatLng newBus = new LatLng(lat, lon);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(newBus).title(stringName));
                            //TODO: clear all markers and then add them back? Need list
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newBus, 15)); //15 corresponds to street level

                            //TODO addMarker returns a Marker, can add them to a list so that we can update easier
                        }
                    }

                } else {
                    //Error occurred when querying the database
                    Log.d("Objects", "Error: " + e.getMessage());
                }
            }
        });
    }
}

