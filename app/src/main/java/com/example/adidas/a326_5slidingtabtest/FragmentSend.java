package com.example.adidas.a326_5slidingtabtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Adidas on 2017/3/28.
 */

public class FragmentSend extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_send,container,false);
        editText= (EditText) view.findViewById(R.id.send_edit);
        btnSend= (Button) view.findViewById(R.id.btn_sendmessage);
        textGetMassage= (TextView) view.findViewById(R.id.send_textgetmessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.length()>0){
                textGetMassage.setText(editText.getText());
                Toast.makeText(getActivity(), editText.getText(), Toast.LENGTH_SHORT).show();
                MESSAGE_TEMP.replace(0,MESSAGE_TEMP.length(),editText.getText().toString());}
                else{
                    Snackbar.make(view,"No Message",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
            }
        });
        return view;
    }
}
