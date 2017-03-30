package com.example.adidas.a326_5slidingtabtest;

/**
 * Created by Adidas on 2017/3/26.
 */

        import android.app.Activity;
        import android.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.design.widget.Snackbar;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.w3c.dom.Text;

        import java.util.zip.Inflater;

/**
 * Created by Adidas on 2017/3/14.
 */

public class FragmentTest extends android.support.v4.app.Fragment {

    private Button button;
    private TextView text;
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
        text= (TextView) view.findViewById(R.id.text_pgcontent);
        text.setText("By Fire Be Purged");
        button= (Button) view.findViewById(R.id.btn_get);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentSend.MESSAGE_TEMP.length()>0)
                text.setText(FragmentSend.MESSAGE_TEMP.toString());
                else{
                    Snackbar.make(view,"No Message",Snackbar.LENGTH_LONG).setAction("Action",null).show();

                }
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode== Activity.RESULT_OK){
                    connectDevice();
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectDevice(){
        Toast.makeText(getActivity(),"假装connected",Toast.LENGTH_LONG);

    }

}

