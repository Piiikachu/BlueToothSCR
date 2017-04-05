package com.example.adidas.a326_5slidingtabtest.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Adidas on 2017/4/4.
 */

public class BluetoothTask {

    private static final String TAG = "BluetoothTask";

    private int mState;
    private int mNewState;

    private static final UUID MY_UUID= UUID.fromString("42bc3394-2266-4e03-bbbc-5de7cef1a407");

    private final BluetoothAdapter mBTAdapter;
    private final Handler mHandler;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private AcceptTask acceptTask;
    private ConnectTask connectTask;
    private ConnectedTask connectedTask;

    public void runAcceptTask(){
        acceptTask.run();
    }

    public ConnectedTask getConnectedTask(){
        return connectedTask;
    }

    public ConnectTask getConnectTask(){
        return connectTask;
    }


    public BluetoothTask(Context context, Handler handler) {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
        acceptTask=new AcceptTask();

    }

    public synchronized int getState(){
        return mState;
    }

    private synchronized void updateUITitle(){
        mState=getState();
        Log.d(TAG, "updateUserInterfaceTitle() " + mNewState + " -> " + mState);
        mNewState = mState;
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE,mNewState,-1).sendToTarget();
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        //region Description
        // Cancel any thread attempting to make a connection
        if (connectTask != null) {
            connectTask.cancel();
            connectTask = null;
        }

        // Cancel any thread currently running a connection
        if (connectedTask != null) {
            connectedTask.cancel();
            connectedTask = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (acceptTask == null) {
            acceptTask = new AcceptTask();
            acceptTask.run();
        }

        // Update UI title
        //endregion

        updateUITitle();
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        //region Description
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (connectTask != null) {
                connectTask.cancel();
                connectTask = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedTask != null) {
            connectedTask.cancel();
            connectedTask = null;
        }

        // Start the thread to connect with the given device
        connectTask = new ConnectTask(device);
        connectTask.run();
        //endregion
        // Update UI title
        updateUITitle();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.d(TAG, "connected");

        //region Description
        // Cancel the thread that completed the connection
        if (connectTask != null) {
            connectTask.cancel();
            connectTask = null;
        }

        // Cancel any thread currently running a connection
        if (connectedTask != null) {
            connectedTask.cancel();
            connectedTask = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (acceptTask != null) {
            acceptTask.cancel();
            acceptTask = null;
        }

        //endregion

        //// TODO: 2017/4/4 connect method
        // Start the thread to manage the connection and perform transmissions
        connectedTask = new ConnectedTask(socket);
        connectedTask.run();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        // Update UI title
        updateUITitle();
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");

        //region Description
        if (connectTask != null) {
            connectTask.cancel();
            connectTask = null;
        }

        if (connectedTask != null) {
            connectedTask.cancel();
            connectedTask = null;
        }

        if (acceptTask != null) {
            acceptTask.cancel();
            acceptTask = null;
        }


        //endregion
        mState = STATE_NONE;
        // Update UI title
        updateUITitle();
    }

    public void write(byte[] out) {
        // Create temporary object
        //// TODO: 2017/4/4  method write
/*        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);*/


    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title
        updateUITitle();

        //// TODO: 2017/4/4 WTF is this
        // Start the service over to restart listening mode
        BluetoothTask.this.start();
    }

    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title
        updateUITitle();

        // Start the service over to restart listening mode
        BluetoothTask.this.start();
    }

    private  class AcceptTask{
        private final BluetoothServerSocket mmBluetoothServerSocket;

        public AcceptTask(){
            BluetoothServerSocket tmp =null;

            try{
                tmp=mBTAdapter.listenUsingRfcommWithServiceRecord("BluetoothChatSecure",MY_UUID);
            } catch (IOException e) {
                Log.e(TAG,  "listen() failed", e);
            }
            mmBluetoothServerSocket=tmp;
            mState=STATE_LISTEN;
        }

        public void run(){
            Log.d(TAG,"BEGIN mAcceptTask" + this);

            BluetoothSocket socket=null;

            if (mState!=STATE_CONNECTED){
                try {
                    socket=mmBluetoothServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                }

                if (socket!=null){
                    synchronized (BluetoothTask.this){
                        switch (mState){
                            case STATE_LISTEN:Log.e(TAG, "State Listen");
                                break;
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:Log.e(TAG, "State None");break;
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
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
            Log.i(TAG, "END mAcceptTask");
        }

        public void cancel(){
            Log.d(TAG,"cancel " + this);
            try {
                mmBluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG,  "close() of server failed", e);
            }
        }


    }
    private class ConnectTask{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectTask(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;


            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {

                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID);


            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread ");

            // Always cancel discovery because it will slow down a connection
            mBTAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() "  +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothTask.this) {
                connectTask = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect "+" socket failed", e);
            }
        }


    }
    private class ConnectedTask{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedTask(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread" );
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            if (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();

                }
            }

        }
        private void cancel(){

        }
    }

}
