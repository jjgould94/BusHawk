package com.example.jjgould94.bushawk;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSelectionActivity extends ListActivity {

    private String displayType;
    private List<Map<String, String>> numbersMapList;
    private List<Integer> numbersList;
    private String routesString = "routes";
    private String stopsString = "stops";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);

        Intent intent = getIntent();
        if (intent != null)
        {
            displayType = intent.getStringExtra("type");
        }
        else
        {
            Log.d("ListSelectionActivity", "ERROR: Failed to load the intent to retrieve the display type");
        }

        if (!displayType.equals(routesString) || !displayType.equals(stopsString))
        {
            //ERROR: we weren't able to get the type from the intent
            //TODO: Do something, like pop up an error message and return to the home screen?
            Log.d("ListSelectionActivity", "ERROR: The type to display wasn't set as routes or stops, but as "+displayType);
        }
        else
        {
            Log.d("ListSelectionActivity","The type to display is  "+displayType);
        }

        //Creating a temporary list of route/stop numbers
        //TODO: populate this with actual route/stop information
        List<String> numbers = new ArrayList();
        numbersMapList = new ArrayList<Map<String,String>>();
        numbers.add("1");
        numbers.add("2");

        //Taking the route/stop numbers and putting them in the map
        for (String num : numbers)
        {
            Map<String, String> numMap = new HashMap<String, String>();
            numMap.put(displayType, num);       //displayType will be set to either routes or stops
            numbersMapList.add(numMap);
        }

        ListAdapter adapter = new SimpleAdapter(
                this,
                numbersMapList,
                android.R.layout.simple_list_item_1,
                new String[] {displayType},
                new int[] {android.R.id.text1}
        );

        setListAdapter(adapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        //Get what was selected
        String selection = (String) ((HashMap) getListAdapter().getItem(position)).get(displayType);

        //Open the route or stop activity
        if (displayType.equals("routes".toString()))
        {
            Intent intent = new Intent(this, RouteView.class);
            intent.putExtra("routeNumber", Integer.parseInt(selection));
            Log.d("ListSelectionActivity", "Opening route view with route #"+selection);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, StopMapsActivity.class);
            intent.putExtra("stopNumber", Integer.parseInt(selection));
            startActivity(intent);
        }
    }

}
