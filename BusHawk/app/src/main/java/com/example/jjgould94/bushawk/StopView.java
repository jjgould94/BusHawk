package com.example.jjgould94.bushawk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StopView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_view);
        String[] values;                                                    //Holds items for the list view
        values = new String[]{""};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        ListView listView = (ListView) findViewById(R.id.stopsListView);
        listView.setAdapter(adapter);
    }
}