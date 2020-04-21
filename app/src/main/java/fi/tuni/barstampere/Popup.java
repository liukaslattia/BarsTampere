package fi.tuni.barstampere;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class Popup extends Activity {
    String barname;
    String monopening;
    String monclosing;
    String tueopening;
    String tueclosing;
    String wedopening;
    String wedclosing;
    String thuopening;
    String thuclosing;
    String friopening;
    String friclosing;
    String satopening;
    String satclosing;
    String sunopening;
    String sunclosing;
    TextView textView;
    TextView textViewName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barname = extras.getString("barname");
            monopening = extras.getString("monopening");
            monclosing = extras.getString("monclosing");
            tueopening = extras.getString("tueopening");
            tueclosing = extras.getString("tueclosing");
            wedopening = extras.getString("wedopening");
            wedclosing = extras.getString("wedclosing");
            thuopening = extras.getString("thuopening");
            thuclosing = extras.getString("thuclosing");
            friopening = extras.getString("friopening");
            friclosing = extras.getString("friclosing");
            satopening = extras.getString("satopening");
            satclosing = extras.getString("satclosing");
            sunopening = extras.getString("sunopening");
            sunclosing = extras.getString("sunclosing");
        } else if(extras == null) {

        }
        setContentView(R.layout.popupwindow);

        textView = (TextView) findViewById(R.id.popTextView);
        textViewName = (TextView) findViewById(R.id.popTextName);

        textViewName.setText(barname);

        textView.setText(" Mon " + monopening + " - " + monclosing
                +  "\n" +" Tue " + tueopening + " - " + tueclosing
                +  "\n" +" Wed " + wedopening + " - " + wedclosing
                +  "\n" +" Thu " + thuopening + " - " + thuclosing
                +  "\n" +" Fri " + friopening + " - " + friclosing
                +  "\n" +" Sat " + satopening + " - " + satclosing
                +  "\n" +" Sun " + sunopening + " - " + sunclosing);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;


        // Get orientation and set window size
        int display_mode = getResources().getConfiguration().orientation;
        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setLayout( (int) (width*.6),(int)(heigth*.4));
        } else {
            getWindow().setLayout( (int) (width*.4),(int)(heigth*.6));
        }
    }
}
