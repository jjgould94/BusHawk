package com.example.jjgould94.bushawk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;

import com.parse.Parse;
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

public class MainActivity extends AppCompatActivity {
    private Context context;    //NOTE: added, because the 'this' keyword inside the listeners
                                //      wasn't getting the context correctly

    private FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.initialize(this);
        context = this;         //Setting the context to be able to use the Intents
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button myStopsButton = (Button) findViewById(R.id.myStopsButton);
        final Button myRoutesButton = (Button) findViewById(R.id.myRoutesButton);
        final Button tempBusButton = (Button) findViewById(R.id.tempBusButton);
        final Button searchStopsButton = (Button) findViewById(R.id.searchStopButton);
        final Button searchRoutesButton = (Button) findViewById(R.id.searchRouteButton);

        try {
            saveRoutePointsToFile("BushawkPoints");
        }
        catch (IOException e)
        {
            Log.d("MainActivity", "SaveRoutePointsToFile threw IOException: "+e);
        }

        searchStopsButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Open the list selection with stops displayed
                 Intent intent = new Intent(context, ListSelectionActivity.class);
                 intent.putExtra("type", "stops".toString());
                 startActivity(intent);

                 /*
                 //Open the stop view
                 Intent intent = new Intent(context, StopMapsActivity.class);
                 intent.putExtra("stopNumber", 1);
                 startActivity(intent);
                 //TODO: determine which stop we want to open from the dialog box
                 */
             }

         }
        );


        searchRoutesButton.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick (View v) {
                 //Open the list selection with routes displayed
                 Intent intent = new Intent(context, ListSelectionActivity.class);
                 intent.putExtra("type", "routes".toString());
                 startActivity(intent);

                 //Open the route view
                 //Intent intent = new Intent(context, StopMapsActivity.class);
                 //intent.putExtra("routeNumber", 1);
                 //startActivity(intent);
                 //TODO: determine which route we want to open from the dialog box
             }

         }
        );


        myStopsButton.setOnClickListener(new OnClickListener(){
            @Override
             public void onClick (View v) {
                //Open the stop view
                Intent intent = new Intent (context, StopMapsActivity.class);
                intent.putExtra("stopNumber", 1);
                startActivity(intent);
                //TODO: determine which stop we want to open
            }

        }
        );


        myRoutesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open the routes view
                Intent intent = new Intent(context, RouteView.class);
                intent.putExtra("routeNumber", 1);
                startActivity(intent);
                //TODO: determine which route we want to open
            }
        });


        tempBusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Open the bus view
                Intent intent = new Intent(context, BusView.class);
                intent.putExtra("busNumber", 1);
                startActivity(intent);

                //TODO: remove this once we get bus access from the other screens
            }
        });


    }

    //Jeff's saveRoutePointsToFile function, moved here so that every other activity can
    // simply load from the file rather than making a database call 
    //TODO: determine if this should be opened before or after the UI is initially loaded
    //Would improve loading performance to be run before UI is loaded, but could result
    // in bugs and have edge cases to handle if it is running when the user is trying to
    // click on UI elements and we haven't finished running this yet
    void saveRoutePointsToFile(String filename) throws FileNotFoundException, IOException {
        fos = openFileOutput(filename, Context.MODE_PRIVATE);
        Log.d("saveRoutePointsToFile", fos.getFD().toString());
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
