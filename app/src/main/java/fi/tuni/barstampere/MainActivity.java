package fi.tuni.barstampere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    private PermissionsManager permissionsManager;
    ProgressDialog pd;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;
    private static final String ID_ICON_MARKER = "marker";
    private static final String ID_ICON_MARKER1 = "marker1";
    private SymbolManager symbolManager;
    Date currentTime;
    String day;
    String hour;
    Integer hourint;

    public Style style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        // Init device time
        currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("EEE", Locale.ENGLISH);
        DateFormat df1 = new SimpleDateFormat("HH", Locale.ENGLISH);
        day = df.format(Calendar.getInstance().getTime());
        hour = df1.format(Calendar.getInstance().getTime());
        hourint = Integer.parseInt(hour);

        // Create the map

        Mapbox.getInstance(this, "pk.eyJ1IjoibGl1a2FzbGF0dGlhIiwiYSI6ImNrN3JvbHhhejBlZWEzbnFkbHk1eTRwM3QifQ.LObJOtBVkfvff_ovnb1xew");

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    // TO DO: Check internet access
    /*
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
*/

    // Actionbar info button
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem m1 = menu.findItem(R.id.info);
        m1.setEnabled(true);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.info):
                Intent intent = new Intent(MainActivity.this, InfoPopup.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    // Set style and markers to map
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.getStyle(this::addMarkerToStyle);
        mapboxMap.getStyle(this::addMarkerToStyle1);
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/liukaslattia/ck8m75k2z1d6i1ilkfahtspfm"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                symbolManager.setIconAllowOverlap(true);
                symbolManager.setIconIgnorePlacement(true);

                // Starts JsonTask to get json from backend
                StringBuilder url = new StringBuilder(
                        "https://bars-tampere-backend.herokuapp.com/bars"
                );

                try {
                    String serverResponse =  new JsonTask().execute(url.toString()).get();
                    JSONArray jsonArray = new JSONArray(serverResponse);
                    Gson gson = new Gson();


                    // Breakdown jsonArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject bar = (JSONObject) jsonArray.get(i);
                        JsonElement element = gson.fromJson(bar.toString(), JsonElement.class);

                        String barname = bar.getString("name");
                        String lat = bar.getString("lat");
                        double latDouble = Double.parseDouble(lat);
                        String lon = bar.getString("lon");
                        double lonDouble = Double.parseDouble(lon);
                        String monopening = bar.getString("monopening");
                        Integer monopeningint = Integer.parseInt(monopening);
                        String monclosing = bar.getString("monclosing");
                        Integer monclosingint = Integer.parseInt(monclosing);
                        String tueopening = bar.getString("tueopening");
                        Integer tueopeningint = Integer.parseInt(tueopening);
                        String tueclosing = bar.getString("tueclosing");
                        Integer tueclosingint = Integer.parseInt(tueclosing);
                        String wedopening = bar.getString("wedopening");
                        Integer wedopeningint = Integer.parseInt(wedopening);
                        String wedclosing = bar.getString("wedclosing");
                        Integer wedclosingint = Integer.parseInt(wedclosing);
                        String thuopening = bar.getString("thuopening");
                        Integer thuopeningint = Integer.parseInt(thuopening);
                        String thuclosing = bar.getString("thuclosing");
                        Integer thuclosingint = Integer.parseInt(thuclosing);
                        String friopening = bar.getString("friopening");
                        Integer friopeningint = Integer.parseInt(friopening);
                        String friclosing = bar.getString("friclosing");
                        Integer friclosingint = Integer.parseInt(friclosing);
                        String satopening = bar.getString("satopening");
                        Integer satopeningint = Integer.parseInt(satopening);
                        String satclosing = bar.getString("satclosing");
                        Integer satclosingint = Integer.parseInt(satclosing);
                        String sunopening = bar.getString("sunopening");
                        Integer sunopeningint = Integer.parseInt(sunopening);
                        String sunclosing = bar.getString("sunclosing");
                        Integer sunclosingint = Integer.parseInt(sunclosing);


                        // Create markers based on time NOTE: this could have been done better with more time

                        if(day.equals("Mon")) {
                            if (hourint >= monopeningint && hourint < monclosingint || hourint < sunclosingint && sunclosingint < monopeningint ||hourint >= monopeningint && monclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                 //       .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                 //       .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }

                        } else if (day.equals("Tue")) {
                            if (hourint >= tueopeningint && hourint < tueclosingint || hourint < monclosingint && monclosingint < tueopeningint ||hourint >= tueopeningint && tueclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                //        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                 //       .withTextField(barname)
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        } else if (day.equals("Wed")) {
                            if (hourint >= wedopeningint && hourint < wedclosingint|| hourint < tueclosingint && tueclosingint < wedopeningint ||hourint >= wedopeningint && wedclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                 //       .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                //        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));
                            }

                            } else if (day.equals("Thu")) {
                                if (hourint >= thuopeningint && hourint < thuclosingint || hourint < wedclosingint && wedclosingint < thuopeningint || hourint >= thuopeningint && thuclosingint < 8) {

                                    Symbol symbol = symbolManager.create(new SymbolOptions()
                                          //  .withTextField(barname)
                                            .withTextAnchor("top")
                                            .withTextOffset(new Float[] {0f, 0.8f})
                                            .withTextColor("black")
                                            .withTextHaloColor("white")
                                            .withTextHaloWidth((float)1.5)
                                            .withData(element)
                                            .withLatLng(new LatLng(latDouble, lonDouble))
                                            .withIconImage(ID_ICON_MARKER)
                                            .withIconSize(1.0f));


                                } else {
                                    Symbol symbol = symbolManager.create(new SymbolOptions()
                                     //       .withTextField(barname)
                                            .withTextAnchor("top")
                                            .withTextOffset(new Float[] {0f, 0.8f})
                                            .withTextColor("black")
                                            .withTextHaloColor("white")
                                            .withTextHaloWidth((float)1.5)
                                            .withData(element)
                                            .withLatLng(new LatLng(latDouble, lonDouble))
                                            .withIconImage(ID_ICON_MARKER1)
                                            .withIconSize(1.0f));

                                }
                            } else if (day.equals("Fri")) {
                            if (hourint >= friopeningint && hourint < friclosingint || hourint < thuclosingint && thuclosingint < friopeningint || hourint >= friopeningint && friclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                               //         .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                //        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }else if (day.equals("Sat")) {
                            if (hourint >= satopeningint && hourint < satclosingint || hourint < friclosingint && friclosingint < satopeningint || hourint >= satopeningint && satclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                //        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                //        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }
                        else if (day.equals("Sun")) {
                            if (hourint >= sunopeningint && hourint < sunclosingint || hourint < satclosingint && satclosingint < sunopeningint || hourint >= sunopeningint && sunclosingint < 8) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                    //    .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                 //       .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withTextOffset(new Float[] {0f, 0.8f})
                                        .withTextColor("black")
                                        .withTextHaloColor("white")
                                        .withTextHaloWidth((float)1.5)
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }

                    }



                } catch (ExecutionException | JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                // Send data from the marker to the Popup window

                symbolManager.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(Symbol symbol) {

                        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(50);


                        JsonObject jsonObject = symbol.getData().getAsJsonObject();
                        String barname = jsonObject.get("name").getAsString();
                        String monopening = jsonObject.get("monopening").getAsString();
                        String monclosing = jsonObject.get("monclosing").getAsString();
                        String tueopening = jsonObject.get("tueopening").getAsString();
                        String tueclosing = jsonObject.get("tueclosing").getAsString();
                        String wedopening = jsonObject.get("wedopening").getAsString();
                        String wedclosing = jsonObject.get("wedclosing").getAsString();
                        String thuopening = jsonObject.get("thuopening").getAsString();
                        String thuclosing = jsonObject.get("thuclosing").getAsString();
                        String friopening = jsonObject.get("friopening").getAsString();
                        String friclosing = jsonObject.get("friclosing").getAsString();
                        String satopening = jsonObject.get("satopening").getAsString();
                        String satclosing = jsonObject.get("satclosing").getAsString();
                        String sunopening = jsonObject.get("sunopening").getAsString();
                        String sunclosing = jsonObject.get("sunclosing").getAsString();



                        Log.d("asd1", String.valueOf(symbol.getData()));
                        Log.d("asd1", String.valueOf(currentTime));
                        Intent intent = new Intent(MainActivity.this, Popup.class);
                        intent.putExtra("barname", barname);
                        intent.putExtra("monopening", monopening);
                        intent.putExtra("monclosing", monclosing);
                        intent.putExtra("tueopening", tueopening);
                        intent.putExtra("tueclosing", tueclosing);
                        intent.putExtra("wedopening", wedopening);
                        intent.putExtra("wedclosing", wedclosing);
                        intent.putExtra("thuopening", thuopening);
                        intent.putExtra("thuclosing", thuclosing);
                        intent.putExtra("friopening", friopening);
                        intent.putExtra("friclosing", friclosing);
                        intent.putExtra("satopening", satopening);
                        intent.putExtra("satclosing", satclosing);
                        intent.putExtra("sunopening", sunopening);
                        intent.putExtra("sunclosing", sunclosing);

                        startActivity(intent);


                    }
                });
            }
        });
    }


    // Open marker
    private void addMarkerToStyle(Style style) {
        style.addImage(ID_ICON_MARKER,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.marker)),
                false);
    }

    // Closed marker
    private void addMarkerToStyle1(Style style) {
        style.addImage(ID_ICON_MARKER1,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.marker1)),
                false);
    }
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.LTGRAY)
                    .build();

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Add the location icon click listener
            locationComponent.addOnLocationClickListener(this);

            // Add the camera tracking listener. Fires if the map camera is manually moved.
            locationComponent.addOnCameraTrackingChangedListener(this);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    // Gets json from backend
    public class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            try {


                JSONArray jsonArray = new JSONArray(result);
                JSONObject jObject = jsonArray.getJSONObject(0);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format("Your Location",
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
        // Empty on purpose
    }

    // Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "User Permission Explanation needed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Location Denied", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}