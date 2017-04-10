package com.example.adidas.a326_5slidingtabtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothChatService;
import com.example.adidas.a326_5slidingtabtest.BlueTooth.Constants;

/**
 * Created by Adidas on 2017/3/28.
 */

public class FragmentSend extends BTFragment {
    private static final String ARG_POSITION = "position";
    public static StringBuffer MESSAGE_TEMP=new StringBuffer();
    private int position;
    public static FragmentSend newInstance(int position) {
        FragmentSend f = new FragmentSend();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    private Button btnSend;
    private EditText editText;
    private TextView textGetMassage;
    private ListView sendListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_send,container,false);
        editText= (EditText) view.findViewById(R.id.send_edit);
        btnSend= (Button) view.findViewById(R.id.btn_sendmessage);
        textGetMassage= (TextView) view.findViewById(R.id.send_textgetmessage);
        sendListView= (ListView) view.findViewById(R.id.send_listview);




        sendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos=position+1;
                String name=parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(),"你点击了第" + pos + "项      "+name,Toast.LENGTH_LONG).show();


            }
        });



        return view;
    }


    @Override
    protected void setupSend() {
        Log.d(TAG, "setupChat()");

        // Initialize the compose field with a listener for the return key
        editText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    String message = editText.getText().toString();
                    sendMessage(message);
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

    }

    @Override
    protected void sendMessage(String message) {
        super.sendMessage(message);
         if (message.length() > 0)   {
             // Reset out string buffer to zero and clear the edit text field
             mOutStringBuffer.setLength(0);
             editText.setText(mOutStringBuffer);

         }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            textGetMassage.setText("");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    /*mConversationArrayAdapter.add("Me:  " + writeMessage);*/
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    textGetMassage.setText(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
