package com.example.adidas.a326_5slidingtabtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class TestActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;

    private static int REQUEST_ENABLE_BT=1;

    private ArrayAdapter<String> newDevicesArrayAdapter;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region Description
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_test);
        setResult(Activity.RESULT_CANCELED);
        //endregion

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            Toast.makeText(this, "Not supported", Toast.LENGTH_LONG).show();
        }

        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent,REQUEST_ENABLE_BT);
        }




        Button btnScan= (Button) findViewById(R.id.test_btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 2017/3/27 set discover
                doDiscovery();
                view.setVisibility(View.GONE);
            }
        });

        String[] strs=new String[]{"test1","test2"};

        ArrayAdapter<String> pairedDevicesArrayAdapter =
              new ArrayAdapter<>(this,R.layout.device_name);
        findViewById(R.id.test_list_pairedDevices).setVisibility(View.VISIBLE);



        newDevicesArrayAdapter =new ArrayAdapter<String>(this,R.layout.device_name);
        findViewById(R.id.test_list_newDevices).setVisibility(View.VISIBLE);

        Set<BluetoothDevice> pairedDevices=mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size()>0){
            for (BluetoothDevice device:pairedDevices){
                pairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }else{
            String noDevice=getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevice);
        }



        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver,filter);

        filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver,filter);

        //TODO SET onClickListener
        ListView pairedListView= (ListView) findViewById(R.id.test_list_pairedDevices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);


        ListView newDevicesListView= (ListView) findViewById(R.id.test_list_newDevices);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);





    }

    private void doDiscovery(){
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        Log.w("TestACT","StartDiscovery");
    }

    private final BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action =intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState()!=BluetoothDevice.BOND_BONDED){
                   newDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
                }

            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDevicesArrayAdapter.getCount()==0) {
                    String noDevice=getResources().getText(R.string.none_found).toString();
                    newDevicesArrayAdapter.add(noDevice);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter!=null)
        {
            mBluetoothAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }
}
