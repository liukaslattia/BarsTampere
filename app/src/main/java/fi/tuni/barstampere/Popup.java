package fi.tuni.barstampere;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Popup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout( (int) (width*.8),(int)(heigth*.06));
    }
}
