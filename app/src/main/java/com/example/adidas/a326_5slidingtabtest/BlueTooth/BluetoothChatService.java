package com.example.adidas.a326_5slidingtabtest.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.nfc.Tag;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/29.
 */

public class BluetoothChatService {

    private static final UUID MY_UUID= UUID.fromString("42bc3394-2266-4e03-bbbc-5de7cef1a407");
    private final BluetoothAdapter mBTAdapter;
    private final Handler mHandler;

    private int mState;
    private int mNewState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    private ConnectThread mConnectThread;

    public BluetoothChatService(Context context, Handler handler) {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }

    private class ConnectThread extends Thread{
        private  final BluetoothSocket msocket;
        private  final BluetoothDevice mBTDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device){
            mBTDevice=device;
            BluetoothSocket tmp=null;
            try {
                tmp=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msocket=tmp;
            mState=STATE_CONNECTING;

        }

        @Override
        public void run() {
            setName("ConnectThread");

            mBTAdapter.cancelDiscovery();

            try {
                msocket.connect();
            } catch (IOException e) {
                try {
                    msocket.close();
                } catch (IOException e1) {
                    Log.e("connect Failed", String.valueOf(e1));
                }
                //// TODO: 2017/3/31 Add Method ConnectionFailed 
            }

            super.run();
        }
    }


}
