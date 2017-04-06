package com.example.adidas.a326_5slidingtabtest.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/29.
 */

public class BluetoothChatService {

    private static final String TAG = "BluetoothChatService";

    private static final UUID MY_UUID= UUID.fromString("42bc3394-2266-4e03-bbbc-5de7cef1a407");
    private final BluetoothAdapter mBTAdapter;
    private final Handler mHandler;

    private AcceptTask acceptTask;
    private ConnectTask connectTask;
    private ConnectedTask connectedTask;

    private int mState;
    private int mNewState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device




    public BluetoothChatService(Context context, Handler handler) {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }

    /**
     * Update UI title according to the current state of the chat connection
     */
    private synchronized void updateUserInterfaceTitle() {
        mState = getState();
        Log.d(TAG, "updateUserInterfaceTitle() " + mNewState + " -> " + mState);
        mNewState = mState;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewState, -1).sendToTarget();
    }
    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    public synchronized void start(){
        if(connectTask!=null){
            connectTask.cancel();
            connectTask=null;
        }

        if (connectedTask!=null){
            connectedTask.cancel();
            connectedTask=null;
        }

        if (acceptTask==null){
            acceptTask=new AcceptTask();
            acceptTask.start();
        }

        updateUserInterfaceTitle();

    }

    public synchronized void connect(BluetoothDevice device){
        if (mState==STATE_CONNECTING){
            if (connectTask!=null){
                connectTask.cancel();
                connectTask=null;
            }
        }

        if (connectedTask!=null){
            connectedTask.cancel();
            connectedTask=null;
        }

        connectTask=new ConnectTask(device);
        connectTask.start();

        updateUserInterfaceTitle();
    }

    public synchronized void connected(BluetoothSocket socket,BluetoothDevice device){
        if(connectTask!=null){
            connectTask.cancel();
            connectTask=null;
        }

        if (connectedTask!=null){
            connectedTask.cancel();
            connectedTask=null;
        }

        if (acceptTask!=null){
            acceptTask.cancel();
            acceptTask=null;
        }

        connectedTask=new ConnectedTask(socket);
        connectedTask.start();

        Message msg=mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle=new Bundle();
        bundle.putString(Constants.DEVICE_NAME,device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        updateUserInterfaceTitle();

    }

    public synchronized void stop(){
        if(connectTask!=null){
            connectTask.cancel();
            connectTask=null;
        }

        if (connectedTask!=null){
            connectedTask.cancel();
            connectedTask=null;
        }

        if (acceptTask!=null){
            acceptTask.cancel();
            acceptTask=null;
        }

        mState=STATE_NONE;

        updateUserInterfaceTitle();

    }

    public void write(byte[] out){
        ConnectedTask r;

        synchronized (this){
            if (mState!=STATE_CONNECTED) return;
            r=connectedTask;
        }

        r.write(out);

    }

    private void connectionFailed(){

        Message msg=mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle=new Bundle();
        bundle.putString(Constants.TOAST,"unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState=STATE_NONE;

        updateUserInterfaceTitle();

        BluetoothChatService.this.start();

    }

    private void connectionLost(){

        Message msg=mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle=new Bundle();
        bundle.putString(Constants.TOAST,"Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState=STATE_NONE;

        BluetoothChatService.this.start();

    }

    private class AcceptTask{

        private final BluetoothServerSocket mmServerSocket;


        public AcceptTask(){
            BluetoothServerSocket tmp=null;

            try{
                tmp=mBTAdapter.listenUsingRfcommWithServiceRecord("BluetoothService!",MY_UUID);

            } catch (IOException e) {
                Log.e(TAG,  "listen() failed", e);
            }
            mmServerSocket=tmp;
            mState=STATE_LISTEN;

        }

        public void start(){
            BluetoothSocket socket=null;

            if (mState !=STATE_CONNECTED){
                try{
                    socket=mmServerSocket.accept();

                } catch (IOException e) {
                    Log.e(TAG,  "accept() failed", e);
                }

                if (socket!=null){
                    synchronized (BluetoothChatService.this){
                        switch (mState){
                            case STATE_LISTEN:
                                break;
                            case STATE_CONNECTING:
                                connected(socket,socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                                break;
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");

        }

        public void cancel(){
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    private class ConnectTask{
        private final BluetoothSocket mmsocket;
        private final BluetoothDevice mmdevice;

        public ConnectTask(BluetoothDevice device){
            mmdevice=device;
            BluetoothSocket tmp=null;

            try{
                tmp=device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                Log.e(TAG,  "create() failed", e);
            }
            mmsocket=tmp;
            mState=STATE_CONNECTING;
        }

        public void start(){
            mBTAdapter.cancelDiscovery();

            try{
                mmsocket.connect();
            } catch (IOException e) {
                try{
                    mmsocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }
            synchronized(BluetoothChatService.this){
                connectTask=null;
            }
            connected(mmsocket,mmdevice);
        }

        public void cancel(){
            try {
                mmsocket.close();
            } catch (IOException e) {
                Log.e(TAG,  " socket failed", e);
            }
        }
    }

    private class ConnectedTask{
        private final BluetoothSocket mmsocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedTask(BluetoothSocket socket){
            mmsocket=socket;
            InputStream tmpIn=null;
            OutputStream tmpOut=null;

            try{
                tmpIn=socket.getInputStream();
                tmpOut=socket.getOutputStream();

            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream=tmpIn;
            mmOutStream=tmpOut;
            mState=STATE_CONNECTED;
        }

        public void write(byte[] buffer){
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE,-1,-1,buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void start(){
            byte[] buffer=new byte[1024];
            int bytes;

            if (mState==STATE_CONNECTED){
                try{
                    bytes=mmInStream.read(buffer);
                    mHandler.obtainMessage(Constants.MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                }
            }

        }

        public void cancel(){
            try {
                mmsocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
