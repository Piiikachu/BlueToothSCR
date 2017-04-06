package com.example.adidas.a326_5slidingtabtest.BlueTooth;

/**
 * Created by Adidas on 2017/3/27.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_TASK1= 6;

    public static final int MESSAGE_ACTION=10;

    public static final int ACTION_STATE_START=11;
    public static final int ACTION_STATE_STOP=12;
    public static final int ACTION_STATE_WRITE=13;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

}
