package com.example.adidas.a326_5slidingtabtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Adidas on 2017/3/28.
 */

public class FragmentSend extends FragmentBase {
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
                MESSAGE_TEMP.replace(0,MESSAGE_TEMP.length(),name);

                Toast.makeText(getActivity(),"你点击了第" + pos + "项      "+name,Toast.LENGTH_LONG).show();


            }
        });



        return view;
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);
        // Reset out string buffer to zero and clear the edit text field
        if (message.length() > 0){
        mOutStringBuffer.setLength(0);
        editText.setText(mOutStringBuffer);
        }
    }

    @Override
    protected void setSend() {
        super.setSend();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.length()>0){
                    textGetMassage.setText(editText.getText());
                    MESSAGE_TEMP.replace(0,MESSAGE_TEMP.length(),editText.getText().toString());
                    sendMessage(MESSAGE_TEMP.toString());
                }
                else{
                    Snackbar.make(view,"No Message",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
            }
        });
    }

    @Override
    public void freshText() {

    }
}
