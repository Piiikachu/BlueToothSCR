package com.example.adidas.a326_5slidingtabtest;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothChatService;
import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothTask;
import com.example.adidas.a326_5slidingtabtest.BlueTooth.Constants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Adidas on 2017/4/5.
 */

public abstract class FragmentBase extends Fragment {
    public abstract void freshText();
    private Timer timerMain;

    protected static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    protected static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    protected static final int REQUEST_ENABLE_BT = 3;

    protected static int state=0;


    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothChatService mBluetoothService;

    protected StringBuffer mOutStringBuffer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerMain=new Timer();
        setTimerTask();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService!=null){
            mBluetoothService.stop();
        }

        timerMain.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            Toast.makeText(getActivity(), "Not supported", Toast.LENGTH_LONG).show();
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
    public void onResume() {
        super.onResume();
        if (mBluetoothService.getState()== BluetoothTask.STATE_NONE){
            mBluetoothService.start();
        }
    }

    protected void setSend() {

        mBluetoothService=new BluetoothChatService(getActivity(),mhandler);
        mOutStringBuffer = new StringBuffer("");


    }
    private void setTimerTask(){

        TimerTask task= new TimerTask(){

            @Override
            public void run() {
                Message msg=new Message();
                Bundle bundle=new Bundle();
                switch (state){
                    case Constants.ACTION_STATE_STOP:
                        break;
                    case Constants.ACTION_STATE_START:
                        msg=mhandler.obtainMessage(Constants.MESSAGE_ACTION);
                        bundle.putString(String.valueOf(Constants.ACTION_STATE_START),"start");
                        msg.setData(bundle);
                        mhandler.sendMessage(msg);
                        break;
                    case Constants.ACTION_STATE_WRITE:
                        break;
                }
                msg.what=Constants.MESSAGE_STATE_CHANGE;
                mhandler.sendMessage(msg);

            }
        };
        timerMain.schedule(task,1000,1000);/* 表示1000毫秒之後，每隔1000毫秒執行一次 */

    }

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothService.write(send);
        }


    }

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int messageID=msg.what;
            switch(messageID){
                case Constants.MESSAGE_ACTION:


                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    break;
                case Constants.MESSAGE_READ:
                    break;
                case Constants.MESSAGE_STATE_CHANGE:
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
