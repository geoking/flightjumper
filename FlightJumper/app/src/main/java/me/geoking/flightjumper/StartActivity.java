package me.geoking.flightjumper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    EditText yourEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        yourEditText = (EditText) findViewById(R.id.originLocation);

        Button enterButton = (Button) findViewById(R.id.button2);
        enterButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("nameOfPlace", yourEditText.getText().toString());
        startActivity(i);
    }
}
