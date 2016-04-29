package com.example.jjgould94.bushawk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class RouteView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Handler UI_HANDLER = new Handler();
    private Map<Integer, Marker> markerMap;
    private Map<Integer, Marker> stopMap;
    boolean firstRefreshFlag;
    private FileOutputStream fos;

    int thisRouteNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.routeMap);
        mapFragment.getMapAsync(this);
        markerMap = new HashMap<Integer, Marker>();
        stopMap = new HashMap<Integer, Marker>();
        firstRefreshFlag = true;

        //The intent contains the route number sent from the main activity
        Intent intent = getIntent();
        if (intent != null)
        {
            thisRouteNum = intent.getIntExtra("routeNumber", 0);
        }
        else
        {
            Log.d("RouteView", "ERROR: Failed to load the intent to retrieve the route number");
        }

        if (thisRouteNum == 0)
        {
            //ERROR: we weren't able to get the route number from the intent
            //TODO: Do something, like pop up an error message and return to the home screen?
            Log.d("RouteView", "ERROR: The route number is zero");
        }
        else
        {
            Log.d("RouteView","The route number is "+thisRouteNum);
        }


        try
        {
            saveRoutePointsToFile("BushawkPoints");
        }
        catch (FileNotFoundException fnfExcep)
        {
            Log.d("RouteView","File not found exception encountered when running saveRoutePointsToFile");
        }
        catch (IOException ioExcep)
        {
            Log.d("RouteView","IOExceception encountered when running saveRoutePointsToFile");
        }

    }

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
                            int route = object.getInt("route");
                            double lat = object.getDouble("latitude");
                            double lon = object.getDouble("longitude");
                            int name = object.getInt("busID");
                            String stringName = Integer.toString(name);
                            Log.d("Objects", "Name: " + stringName + " Lat: " + lat + "Lng: " + lon);
                            newBus = new LatLng(lat, lon);
                            if (route == thisRouteNum) {
                                if (markerMap.containsKey(name)) {
                                    markerMap.get(name).setPosition(newBus);
                                } else {
                                    Marker marker = mMap.addMarker(new MarkerOptions().position(newBus).title(stringName));
                                    markerMap.put(name, marker);
                                }
                            }
                            //TODO: clear all markers and then add them back? Need list
                            //newBus= new LatLng(35.1,34.0);
                            //marker.setPosition(newBus);
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newBus, 15)); //15 corresponds to street level

                            //TODO addMarker returns a Marker, can add them to a list so that we can update easier
                        }

                        //Only change camera location if this is our first view of the map
                        if (firstRefreshFlag) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newBus, 15)); //15 corresponds to street level
                            firstRefreshFlag = false;
                        }

                    } else {
                        //Error occurred when querying the database
                        Log.d("Objects", "Error: " + e.getMessage());
                    }
                }
            });
            UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 1000);
        }
    };

    /*
    public String[] parsePointString(String thePointString)
    {

    }
    */

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

        //This below sets the default map area to Lawrence, KS coordinates
        LatLng Lawrence = new LatLng(38.971332, -95.236166);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lawrence, 13));

        //The route object. We will add the points to this object then add it to the map
        PolylineOptions route = new PolylineOptions();
        route.color(Color.BLUE);

        Resources routePoints = getResources();
        TypedArray pointsArray = routePoints.obtainTypedArray(R.array.pointsArray);


        //Go through all of the points listed in the points.xml file
        //Points are in format: (point#), lat, lon, (stop boolean), (list of route #s this point is for)
        for (int i = 0; i<pointsArray.length(); i++)
        {
            //Get the point item out of the array
            String newPoint = pointsArray.getString(i);

            //Parse the point into an array of strings, broken up by the comma
            String[] pointParts = newPoint.split(",");

            LatLng routePoint = new LatLng(Float.parseFloat(pointParts[1]), Float.parseFloat(pointParts[2]));

            //The boolean indicates whether this point is also a stop
            //If it is a stop, we add it to the hash map of stops
            if (Boolean.parseBoolean(pointParts[3]))
            {
                Marker marker = mMap.addMarker(new MarkerOptions().position(routePoint).title("Stop #"+pointParts[0].substring(1)));
                stopMap.put(Integer.parseInt(pointParts[0].substring(1)), marker);
            }

            //Check to see if the point is for this route
            for (int j = 4; j<pointParts.length; j++)
            {
                if (Integer.parseInt(pointParts[j]) == thisRouteNum)
                {
                    //The point is on this route, so add it to the route
                    route.add(routePoint);
                }
            }
        }

        //Now that we have added all the points to our route, we can add the route to the map
        mMap.addPolyline(route);
        UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 1000);


    }

    void saveRoutePointsToFile(String filename) throws FileNotFoundException, IOException {
        fos = openFileOutput(filename, Context.MODE_PRIVATE);
        Log.d("saveRoutePointsToFile",fos.getFD().toString());
        Log.d("saveRoutePointsToFile", Boolean.toString(fos.getFD().valid()));
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutePoint");

        try {
            List<ParseObject> objectList = query.find();

            // Sort the results by point name
            ListIterator<ParseObject> pointIterator = objectList.listIterator();
            // Use a map to store the objects according to their int name
            Map<Integer, ParseObject> objectMap = new HashMap<Integer, ParseObject>();
            while (pointIterator.hasNext()) {
                ParseObject object = pointIterator.next();
                String name = object.getString("name");
                // Get int value from name
                String numberString = name.substring(1);
                int number = Integer.parseInt(numberString);
                objectMap.put(number, object);
            }
            // Sort the keys of the map
            List<Integer> pointNumList = new ArrayList<Integer>(objectMap.keySet());
            Collections.sort(pointNumList);
            // Add the elements to the file in order
            for (int key : pointNumList) {
                ParseObject object = objectMap.get(key);
                String name = object.getString("name");
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                boolean stopBool = object.getBoolean("stop");
                List<Integer> routeList = object.getList("routes");
                StringBuilder sb = new StringBuilder();
                sb.append(name + ',');
                sb.append(Boolean.toString(stopBool)+',');
                sb.append(latitude);
                sb.append(',');
                sb.append(longitude);
                for (int num : routeList) {
                    sb.append(',');
                    sb.append(num);
                }
                sb.append('\n');
                try {
                    //TODO add these to a global list of LatLng objects instead of writing to file
                    fos.write(sb.toString().getBytes());
                    Log.d("saveRoutePointsToFile", "Written to file: "+sb.toString());
                } catch (IOException ioe) {
                    // What to do on exception?
                    Log.d("saveRoutePointsToFile", "IOException caught: "+ioe.getMessage());
                    //Log.d("saveRoutePointsToFile", Boolean.toString(fos.getFD().valid()));
                }
            }
        }
        catch (ParseException pe)
        {

        }

        fos.close();
    }
}


        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // Sort the results by point name
                    ListIterator<ParseObject> pointIterator = objects.listIterator();
                    // Use a map to store the objects according to their int name
                    Map<Integer, ParseObject> objectMap = new HashMap<Integer, ParseObject>();
                    while (pointIterator.hasNext()) {
                        ParseObject object = pointIterator.next();
                        String name = object.getString("name");
                        // Get int value from name
                        String numberString = name.substring(1);
                        int number = Integer.parseInt(numberString);
                        objectMap.put(number, object);
                    }
                    // Sort the keys of the map
                    List<Integer> pointNumList = new ArrayList<Integer>(objectMap.keySet());
                    Collections.sort(pointNumList);
                    // Add the elements to the file in order
                    for (int key : pointNumList) {
                        ParseObject object = objectMap.get(key);
                        String name = object.getString("name");
                        double latitude = object.getDouble("latitude");
                        double longitude = object.getDouble("longitude");
                        List<Integer> routeList = object.getList("routes");
                        StringBuilder sb = new StringBuilder();
                        sb.append(name + ',');
                        sb.append(latitude);
                        sb.append(',');
                        sb.append(longitude);
                        for (int num : routeList) {
                            sb.append(',');
                            sb.append(num);
                        }
                        sb.append('\n');
                        try {
                            fos.write(sb.toString().getBytes());
                            Log.d("saveRoutePointsToFile", "Written to file: "+sb.toString());
                        } catch (IOException ioe) {
                            // What to do on exception?
                            Log.d("saveRoutePointsToFile", "IOException caught: "+ioe.getMessage());
                            //Log.d("saveRoutePointsToFile", Boolean.toString(fos.getFD().valid()));
                        }
                    }
                } else {
                    //Error occurred when querying the database
                    Log.d("Objects", "Error: " + e.getMessage());
                }
            }
        });

        */