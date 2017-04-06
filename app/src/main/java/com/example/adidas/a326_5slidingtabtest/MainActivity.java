package com.example.adidas.a326_5slidingtabtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothChatService;
import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothTask;
import  com.example.adidas.a326_5slidingtabtest.BlueTooth.Constants;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Timer timerMain;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager vpager;
    private String[] titles=new String[]{"GET","SEND"};
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPagerAdapter vpagerAdater;

    private StringBuffer mOutStringBuffer;


    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerMain=new Timer();
        setTimerTask();




        

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingTabLayout= (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        vpager= (ViewPager) findViewById(R.id.viewpager);

        vpagerAdater=new ViewPagerAdapter(getSupportFragmentManager(),titles);
        vpager.setAdapter(vpagerAdater);
        slidingTabLayout.setViewPager(vpager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menu_secure_connect_scan:
                Intent intent1=new Intent(this,TestActivity.class);
                startActivityForResult(intent1,REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.menu_discoverable:
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            //getWindow().setTitle(item.getTitle().toString());
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ensureDiscoverable(){
        if(mBluetoothAdapter.getScanMode()!=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,120);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode== Activity.RESULT_OK){
                    connectDevice(data);
                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService!=null){
            mBluetoothService.stop();
        }

        timerMain.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            Toast.makeText(this, "Not supported", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent,REQUEST_ENABLE_BT);
        }else if (mBluetoothService==null){
            setSend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothService.getState()==BluetoothTask.STATE_NONE){
            mBluetoothService.start();
        }
    }

    private void setSend() {

        mBluetoothService=new BluetoothChatService(this,mhandler);
        mOutStringBuffer = new StringBuffer("");


    }

    private void connectDevice(Intent data){
        String address=data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothService.connect(device);

    }




    private void setTimerTask(){

        TimerTask task= new TimerTask(){

            @Override
            public void run() {
                Message message=new Message();
  //// TODO: 2017/4/4  set action

                message.what=Constants.MESSAGE_STATE_CHANGE;
                mhandler.sendMessage(message);

            }
        };
        timerMain.schedule(task,1000,1000);/* 表示1000毫秒之後，每隔1000毫秒執行一次 */

    }

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int messageID=msg.what;
            TextView textGet= (TextView) findViewById(R.id.text_pgcontent);
            switch(messageID){
                case Constants.MESSAGE_DEVICE_NAME:
                    break;
                case Constants.MESSAGE_READ:
                    break;
                case Constants.MESSAGE_STATE_CHANGE:
                    if (FragmentSend.MESSAGE_TEMP.length()>0)
                    textGet.setText(FragmentSend.MESSAGE_TEMP.toString());
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_TOAST:
                    break;
                case Constants.MESSAGE_TASK1:
                    break;
                default:
                    break;

            }
        }
    };


}
