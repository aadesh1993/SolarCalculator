package com.example.solarcalculator.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.places.PlacesClient;
import com.algolia.search.saas.places.PlacesQuery;
import com.example.solarcalculator.Adapters.CustomDialogAdapter;
import com.example.solarcalculator.Models.LocationModels;
import com.example.solarcalculator.Models.PlacesModel;
import com.example.solarcalculator.R;
import com.example.solarcalculator.database.DataBaseHandler;
import com.example.solarcalculator.receiver.AlarmReceiver;
import com.example.solarcalculator.utilities.CalculateTime;
import com.example.solarcalculator.utilities.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private ImageView prev_date;
    private ImageView next_date;
    private ImageView current_date;
    private TextView date;
    private String dateString;
    private Date datee;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private final String URL = "https://atlas.mapmyindia.com/api/places/textsearch/json?query=";
    private AutoCompleteTextView search;
    private ArrayList<PlacesModel> placesModels;
    private int REQUEST_MULTIPLE_PERMISSIONS = 100;
    private GoogleApiClient mGoogleApiClient;
    private TextView sunrise_time;
    private TextView sunset_time;
    private ImageView save;
    private ImageView display;
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    long time;
    String notificationtime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkselfpermission();
        } else {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        mGoogleApiClient = getapiclient();
        getIds();


    }

    private boolean checkselfpermission() {


        int locationpermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (locationpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            }
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_MULTIPLE_PERMISSIONS) {

            if (grantResults.length > 0) {

                for (int i = 0; i < permissions.length; i++) {


                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {

                            Log.e("Granted", "");
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(this);

                            //                   mCurrLocationMarker = Utils.currentLocation(this, mMap);


                        } else {

                            Log.e("Denied", "");

                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {

                        } else {
                        }


                    }


                }


            }
        }
    }

    private void getIds() {

        prev_date = (ImageView) findViewById(R.id.prev_date);
        next_date = (ImageView) findViewById(R.id.next_date);
        current_date = (ImageView) findViewById(R.id.current_date);
        date = (TextView) findViewById(R.id.date);
        search = (AutoCompleteTextView) findViewById(R.id.search_loc);
        sunrise_time = (TextView) findViewById(R.id.sunrise_time);
        sunset_time = (TextView) findViewById(R.id.sunset_time);
        save = (ImageView) findViewById(R.id.save);
        display = (ImageView) findViewById(R.id.show_saved);
        datee = new Date();
        setDate(0);

    }

    private void initializeListeners() {

        prev_date.setOnClickListener(this);
        next_date.setOnClickListener(this);
        current_date.setOnClickListener(this);
        save.setOnClickListener(this);
        display.setOnClickListener(this);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (search.isPerformingCompletion()) {
                    return;
                } else {
                    if (charSequence != null) {
                        Log.e("Text ", "changed " + charSequence.toString());
                        getPlaces(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                    LatLng midLatLng = mMap.getCameraPosition().target;
                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions().title("I am here ").
                            position(midLatLng).icon(Utils.bitmapDescriptorFromVector(getApplicationContext())));
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                    LatLng position = mCurrLocationMarker.getPosition();
                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions().title("I am here ").
                            position(position).icon(Utils.bitmapDescriptorFromVector(getApplicationContext())));
                    calculateTime(datee, position);
                    Log.e("Lat " + position.latitude, "lng " + position.longitude);
                }
            }
        });



    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // mCurrLocationMarker = Utils.currentLocation(this, mMap);


        initializeListeners();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.prev_date:
                setDate(1);
                break;
            case R.id.current_date:
                setDate(0);
                break;
            case R.id.next_date:
                setDate(2);
                break;
            case R.id.save:
                saveinDatabase();
                break;
            case R.id.show_saved:
                displaySavedLocations();
                break;


        }


    }

    private void saveinDatabase() {

        DataBaseHandler handler = new DataBaseHandler(this);

        LocationModels models = new LocationModels();
        models.setLocation("Saved Location ");
        models.setLatitude(String.valueOf(mCurrLocationMarker.getPosition().latitude));
        models.setLongitude(String.valueOf(mCurrLocationMarker.getPosition().longitude));
        models.setSunrise(sunrise_time.getText().toString());
        models.setSunset(sunset_time.getText().toString());

        handler.saveLocation(models);

        Toast.makeText(this,"Location Saved", Toast.LENGTH_SHORT).show();

    }

    private void displaySavedLocations(){

        DataBaseHandler handler = new DataBaseHandler(this);

        ArrayList<LocationModels> savedList = handler.retrieveValues();

        Log.e("Retrieved Values", "Size ? "+savedList.size());

        displayList(savedList);

    }

    private void setDate(int flag) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd,yyyy");


        if (flag == 0) {
            datee = new Date();
            dateString = simpleDateFormat.format(datee);
            date.setText(dateString);
            if (mCurrLocationMarker != null)
                calculateTime(datee, mCurrLocationMarker.getPosition());

        } else if (flag == 1) {
            datee = new Date(datee.getTime() - 24 * 60 * 60 * 1000);
            dateString = simpleDateFormat.format(datee);
            date.setText(dateString);
            if (mCurrLocationMarker != null)
                calculateTime(datee, mCurrLocationMarker.getPosition());
        } else if (flag == 2) {
            datee = new Date(datee.getTime() + 24 * 60 * 60 * 1000);
            dateString = simpleDateFormat.format(datee);
            date.setText(dateString);
            if (mCurrLocationMarker != null)
                calculateTime(datee, mCurrLocationMarker.getPosition());

        }


    }


    private void getPlaces(String place) {

        PlacesClient places = new PlacesClient("plXBYY30TUOS", "e161bfdd7096eff16e67e720ed4ebeca");


        PlacesQuery query = new PlacesQuery();
        query.setQuery(place);
        //query.setType(PlacesQuery.Type.CITY);
        query.setHitsPerPage(10);
        //query.setAroundLatLngViaIP(false);
        //query.setAroundLatLng(new PlacesQuery.LatLng(32.7767, -96.7970)); // Dallas, TX, USA
        query.setLanguage("en");
        //query.setCountries("in");

        places.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {


                try {
                    JSONArray array = jsonObject.getJSONArray("hits");
                    JSONObject object = (JSONObject) array.get(0);
                    String id = object.getString("objectID");
                    parseLocationArray(array);
                    //           JSONObject object = array.getJSONObject(0);
                    Log.e("Json " + id, "Alg " + array.length());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        });


    }

    //showing 5 places in suggestions
    private void parseLocationArray(JSONArray array) {

        placesModels = new ArrayList<>();
        int totalSuggestions;
        if (array.length() > 5)
            totalSuggestions = 5;
        else
            totalSuggestions = array.length();

        for (int i = 0; i < totalSuggestions; i++) {

            try {
                JSONObject object = (JSONObject) array.get(i);
                JSONArray names = object.getJSONArray("locale_names");
                String place = names.getString(0);
                JSONObject loc = object.getJSONObject("_geoloc");
                double lat = loc.getDouble("lat");
                double lng = loc.getDouble("lng");
                Log.e("name", " " + place + " lat " + lat + " lng " + lng);

                PlacesModel model = new PlacesModel(place, lat, lng);
                placesModels.add(model);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        if (placesModels != null)
            setsuggestions(placesModels);


    }

    private void setsuggestions(final ArrayList<PlacesModel> placesModels) {

        final String[] suggeationsarray = new String[5];
        if (placesModels != null) {


            for (int i = 0; i < placesModels.size(); i++) {
                suggeationsarray[i] = placesModels.get(i).getPlace();
            }

            Log.e("Places ", "Model " + placesModels.size());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, suggeationsarray);

            search.setAdapter(adapter);

            search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e("On ", "Item Selected");

                    String selection = (String) adapterView.getItemAtPosition(i);
                    int pos = -1;

                    for (int j = 0; j < suggeationsarray.length; j++) {
                        if (suggeationsarray[j].equals(selection)) {
                            pos = j;
                            break;
                        }
                    }
                    Log.e("Position ", "item " + placesModels.get(pos).getPlace());

                    plotItemOnMap(pos, placesModels);

                }
            });
        }

    }

    private void plotItemOnMap(int pos, ArrayList<PlacesModel> placesModels) {

        PlacesModel model = placesModels.get(pos);

        Double lat = model.getLatitude();
        Double lng = model.getLongitude();
        String place = model.getPlace();

        if (mCurrLocationMarker != null)
            mCurrLocationMarker.remove();

        mCurrLocationMarker = mMap.addMarker(new MarkerOptions().icon(Utils.bitmapDescriptorFromVector(this))
                .position(new LatLng(lat, lng)).title(place));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));


    }



    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            Log.e("loc ", " " + mLastLocation);
            LatLng latLng = new LatLng(lat, lng);
            mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).
                    icon(Utils.bitmapDescriptorFromVector(this)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            calculateTime(datee, latLng);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public GoogleApiClient getapiclient() {

        GoogleApiClient mGoogleApiClient = null;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        return mGoogleApiClient;
    }

    private void calculateTime(Date date, LatLng latLng) {

        //Date date = new Date();
        //SimpleDateFormat format = new SimpleDateFormat("dd-MMM-YYYY");
        //format.format(date);

        CalculateTime calculateTime = new CalculateTime(date, latLng);

        calculateTime.timerequired();

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        double risingtime = calculateTime.getRisingtime();
        double settingtime = calculateTime.getSettingtime();
        double alarmtime = calculateTime.getalarmtime();


        long timeInMilliSeconds = (long) Math.floor(risingtime * 60 * 1000);
        Date daterising = new Date(timeInMilliSeconds);

        Log.e("Local time ", "  " + sdf.format(daterising));
        sunrise_time.setText(sdf.format(daterising));

        long timeInMilliSecondss = (long) Math.floor(settingtime * 60 * 1000);
        Date datesetting = new Date(timeInMilliSecondss);
        Log.e("minutes "+daterising.getTime(), "  " + sdf.format(datesetting));

        long timeInMilliSecondsss = (long) Math.floor(alarmtime * 60 * 1000);
        Date datealarm = new Date(timeInMilliSecondsss);
        Log.e("Local time "+datealarm.getHours(), "  " + sdf.format(datealarm));

        String s[] = sdf.format(datealarm).split(":");

        Log.e("Setting ", "Time "+ s[0]);


        //setAlarm(s[0],s[1]);

        setAlarm("14","56");


        sunset_time.setText(sdf.format(datesetting));

    }

    private void setAlarm(String s, String min) {

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s));
        calendar.set(Calendar.MINUTE, Integer.parseInt(min));


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,pendingIntent);

    }


    private void displayList(final ArrayList<LocationModels> models){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Saved Locations");

        final CustomDialogAdapter arrayAdapter = new CustomDialogAdapter(models,this);


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.e("Clicked", "Pos "+ which);

                plotItemOnMap(models.get(which));


            }
        });



        builderSingle.show();



    }

    private void plotItemOnMap(LocationModels models) {


        double lat = Double.parseDouble(models.getLatitude());
        double lng = Double.parseDouble(models.getLongitude());
        String place = models.getLocation();

        if (mCurrLocationMarker != null)
            mCurrLocationMarker.remove();

        mCurrLocationMarker = mMap.addMarker(new MarkerOptions().icon(Utils.bitmapDescriptorFromVector(this))
                .position(new LatLng(lat, lng)).title(place));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));


    }

}
