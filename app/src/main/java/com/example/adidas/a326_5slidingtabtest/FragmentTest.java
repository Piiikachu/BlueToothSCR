package com.example.adidas.a326_5slidingtabtest;

/**
 * Created by Adidas on 2017/3/26.
 */

        import android.app.Activity;
        import android.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.design.widget.Snackbar;
        import android.support.v4.app.FragmentActivity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.adidas.a326_5slidingtabtest.BlueTooth.BluetoothChatService;
        import com.example.adidas.a326_5slidingtabtest.BlueTooth.Constants;

        import org.w3c.dom.Text;

        import java.util.zip.Inflater;

/**
 * Created by Adidas on 2017/3/14.
 */

public class FragmentTest extends BTFragment {

    private Button button;
    private TextView textGet;
    private static final String ARG_POSITION = "position";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    private int position;


    public static FragmentTest newInstance(int position) {
        FragmentTest f = new FragmentTest();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_content,container,false);
        textGet= (TextView) view.findViewById(R.id.text_pgcontent);
        textGet.setText("By Fire Be Purged");
        button= (Button) view.findViewById(R.id.btn_get);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentSend.MESSAGE_TEMP.length()>0)
                textGet.setText(FragmentSend.MESSAGE_TEMP.toString());
                else{
                    Snackbar.make(view,"No Message",Snackbar.LENGTH_LONG).setAction("Action",null).show();

                }
            }
        });

        return view;

    }



    @Override
    protected void setupSend() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        mOutStringBuffer = new StringBuffer("");

    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            textGet.setText("");
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
                    textGet.setText(mConnectedDeviceName + ":  " + readMessage);
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

