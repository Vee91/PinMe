package edu.neu.madcourse.vikaschandrashekar.finalproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.vikaschandrashekar.R;


public class Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_project);
        getSupportActionBar().setTitle("Project Description");

        Button startButton = (Button)findViewById(R.id.trickest_main_start);
        Button acknowledgementButton = (Button)findViewById(R.id.trickiest_main_acknowledgement);

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent =  new Intent(Description.this, Registration.class);
                startActivity(intent);
            }
        });


        acknowledgementButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Description.this, ProjectAcknowledgements.class);
                startActivity(intent);
            }
        });
    }
}
