package com.example.jjgould94.bushawk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;

import com.parse.Parse;

public class MainActivity extends AppCompatActivity {
    private Context context;    //NOTE: added, because the 'this' keyword inside the listeners
                                //      wasn't getting the context correctly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.initialize(this);
        context = this;         //Setting the context to be able to use the Intents
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button myStopsButton = (Button) findViewById(R.id.myStopsButton);
        final Button myRoutesButton = (Button) findViewById(R.id.myRoutesButton);
        final Button tempBusButton = (Button) findViewById(R.id.tempBusButton);

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


}
