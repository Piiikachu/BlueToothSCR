package com.example.adidas.a326_5slidingtabtest;

/**
 * Created by Adidas on 2017/3/26.
 */

        import android.app.Fragment;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import org.w3c.dom.Text;

        import java.util.zip.Inflater;

/**
 * Created by Adidas on 2017/3/14.
 */

public class FragmentTest extends android.support.v4.app.Fragment {

    private static final String ARG_POSITION = "position";
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
        TextView text= (TextView) view.findViewById(R.id.text_pgcontent);
        text.setText("hehehehehe");
        return view;

    }
}

