package fi.tuni.barstampere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.util.Log;
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

/**
 * Use the LocationLayerOptions class to customize the LocationComponent's device location icon.
 */
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

   // private Symbol symbol;
    public Style style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTime = Calendar.getInstance().getTime();

        DateFormat df = new SimpleDateFormat("EEE", Locale.ENGLISH);
        DateFormat df1 = new SimpleDateFormat("HH", Locale.ENGLISH);
        day = df.format(Calendar.getInstance().getTime());
        hour = df1.format(Calendar.getInstance().getTime());
        hourint = Integer.parseInt(hour);
        Log.d("asd1", String.valueOf(day));
        Log.d("asd1", String.valueOf(hour));
        Log.d("asd1", String.valueOf(hourint));

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "pk.eyJ1IjoibGl1a2FzbGF0dGlhIiwiYSI6ImNrN3JvbHhhejBlZWEzbnFkbHk1eTRwM3QifQ.LObJOtBVkfvff_ovnb1xew");

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


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

                StringBuilder url = new StringBuilder(
                        "https://bars-tampere-backend.herokuapp.com/bars"
                       // "http://localhost:64289/bars"
                );

                try {
                    String serverResponse =  new JsonTask().execute(url.toString()).get();
                    JSONArray jsonArray = new JSONArray(serverResponse);
                    Log.d("asd1", String.valueOf(jsonArray));
                    Gson gson = new Gson();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject bar = (JSONObject) jsonArray.get(i);
                        JsonElement element = gson.fromJson(bar.toString(), JsonElement.class);

                        String barname = bar.getString("name");
                        Log.d("asd1", String.valueOf(barname));
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


                        Log.d("asd1", String.valueOf(day));
                        if(day.equals("Mon")) {
                            if (hourint >= monopeningint && hourint < monclosingint || hourint < sunclosingint && sunclosingint < monopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }

                        } else if (day.equals("Tue")) {
                            if (hourint >= tueopeningint && hourint < tueclosingint || hourint < monclosingint && monclosingint < tueopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        } else if (day.equals("Wed")) {
                            if (hourint >= wedopeningint && hourint < wedclosingint|| hourint < tueclosingint && tueclosingint < wedopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));
                            }

                            } else if (day.equals("Thu")) {
                                if (hourint >= thuopeningint && hourint < thuclosingint || hourint < wedclosingint && wedclosingint < thuopeningint) {

                                    Symbol symbol = symbolManager.create(new SymbolOptions()
                                            .withTextField(barname)
                                            .withTextAnchor("top")
                                            .withData(element)
                                            .withLatLng(new LatLng(latDouble, lonDouble))
                                            .withIconImage(ID_ICON_MARKER)
                                            .withIconSize(1.0f));


                                } else {
                                    Symbol symbol = symbolManager.create(new SymbolOptions()
                                            .withTextField(barname)
                                            .withTextAnchor("top")
                                            .withData(element)
                                            .withLatLng(new LatLng(latDouble, lonDouble))
                                            .withIconImage(ID_ICON_MARKER1)
                                            .withIconSize(1.0f));

                                }
                            } else if (day.equals("Fri")) {
                            if (hourint >= friopeningint && hourint < friclosingint || hourint < thuclosingint && thuclosingint < friopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }else if (day.equals("Sat")) {
                            if (hourint >= satopeningint && hourint < satclosingint || hourint < friclosingint && friclosingint < satopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }
                        else if (day.equals("Sun")) {
                            if (hourint >= sunopeningint && hourint < sunclosingint || hourint < satclosingint && satclosingint < sunopeningint) {

                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER)
                                        .withIconSize(1.0f));


                            } else {
                                Symbol symbol = symbolManager.create(new SymbolOptions()
                                        .withTextField(barname)
                                        .withTextAnchor("top")
                                        .withData(element)
                                        .withLatLng(new LatLng(latDouble, lonDouble))
                                        .withIconImage(ID_ICON_MARKER1)
                                        .withIconSize(1.0f));

                            }
                        }

                        Log.d("asd1", String.valueOf(latDouble));
                        Log.d("asd1", String.valueOf(element));
                    }



                  //  Log.d("asd1", jObject.toString());
                } catch (ExecutionException | JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




                symbolManager.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(Symbol symbol) {


                        JsonObject jsonObject = symbol.getData().getAsJsonObject();
                        String barname = jsonObject.get("name").getAsString();
                        String opening = jsonObject.get("monopening").getAsString();
                        String closing = jsonObject.get("monclosing").getAsString();


                        Log.d("asd1", String.valueOf(symbol.getData()));
                        Log.d("asd1", String.valueOf(currentTime));
                        Intent intent = new Intent(MainActivity.this, Popup.class);
                        intent.putExtra("barname", barname);
                        intent.putExtra("opening", opening);
                        intent.putExtra("closing", closing);

                        startActivity(intent);


                    }
                });
            }
        });
    }


    private void addMarkerToStyle(Style style) {
        style.addImage(ID_ICON_MARKER,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.marker)),
                false);
    }

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

    public class JsonTask extends AsyncTask<String, String, String> {
        MainActivity parent;
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
                String barname = jObject.getString("name");


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format("Location",
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