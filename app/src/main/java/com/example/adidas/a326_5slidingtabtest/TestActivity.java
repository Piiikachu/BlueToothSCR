package com.example.adidas.a326_5slidingtabtest;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_test);

        setResult(Activity.RESULT_CANCELED);

        Button btnScan= (Button) findViewById(R.id.test_btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 2017/3/27 set discover
                Snackbar.make(view, "This is a TEST for BUTTON SCAN", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //String[] strs=new String[]{"test1","test2"};

        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this,R.layout.activity_test);
        findViewById(R.id.test_list_pairedDevices).setVisibility(View.VISIBLE);

        //pairedDevicesArrayAdapter.add("test1"+"/n11");
        //pairedDevicesArrayAdapter.add("test2"+"/n11");

        ArrayAdapter<String> newDevicesArrayAdapter =
                new ArrayAdapter<>(this,R.layout.activity_test);
        findViewById(R.id.test_list_newDevices).setVisibility(View.VISIBLE);
        //newDevicesArrayAdapter.add("new1"+"/n11");
        //newDevicesArrayAdapter.add("new2"+"/n11");

        //TODO SET onClickListener
        ListView pairedListView= (ListView) findViewById(R.id.test_list_pairedDevices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);


        ListView newDevicesListView= (ListView) findViewById(R.id.test_list_newDevices);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);





    }
}
