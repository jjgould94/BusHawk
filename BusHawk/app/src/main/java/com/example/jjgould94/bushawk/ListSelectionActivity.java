package com.example.jjgould94.bushawk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListSelectionActivity extends Activity {

    String displayType;

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

        if (displayType != "routes" || displayType != "stops")
        {
            //ERROR: we weren't able to get the type from the intent
            //TODO: Do something, like pop up an error message and return to the home screen?
            Log.d("ListSelectionActivity", "ERROR: The type to display wasn't set as routes or stops");
        }
        else
        {
            Log.d("ListSelectionActivity","The type to display is  "+displayType);
        }

        final ListView listView = (ListView) findViewById(R.id.listSelectionListView);

        String[] values = new String[] {"1", "2"};
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i<values.length; ++i)
        {
            list.add(values[i]);
        }



        //final ListAdapter adapter = new ListAdapter();
        //listView.setAdapter(adapter);
        //final ListAdaptor adaptor = new ListAdaptor(this, android.R.layout.simple_list_item_1, list);


    }
}
