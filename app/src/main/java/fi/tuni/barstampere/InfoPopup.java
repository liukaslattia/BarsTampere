package fi.tuni.barstampere;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class InfoPopup extends Activity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.infopopup);

        textView = (TextView) findViewById(R.id.popTextView);



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;
        int display_mode = getResources().getConfiguration().orientation;
        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setLayout( (int) (width*.8),(int)(heigth*.1));
        } else {
            getWindow().setLayout( (int) (width*.5),(int)(heigth*.2));
        }
    }
}
