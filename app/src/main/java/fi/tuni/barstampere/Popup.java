package fi.tuni.barstampere;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class Popup extends Activity {
    String barname;
    String opening;
    String closing;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barname = extras.getString("barname");
            opening = extras.getString("opening");
            closing = extras.getString("closing");
        } else if(extras == null) {
            barname = "barname missing";
            opening = "opening missing";
            closing = "closing missing";
        }
        setContentView(R.layout.popupwindow);

        textView = (TextView) findViewById(R.id.popTextView);

        textView.setText(barname +  "\n" + opening + " - " + closing);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout( (int) (width*.8),(int)(heigth*.06));
    }
}
