package edu.neu.madcourse.vikaschandrashekar.trickiestpart.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.model.CoOrdinates;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.model.Pin;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.model.User;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.service.LocationService;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.utils.MobileUtils;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.utils.PlaceJson;

/**
 * Created by cvika on 4/9/2017.
 */

public class Trickiest extends Activity {

    DatabaseReference database;
    String token;
    static User thisUser = new User();
    private String pinName;
    private final float factor = 0.15f;
    LocationService l;
    List<String> nearbyPlaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trickiest_main);
        database = FirebaseDatabase.getInstance().getReference();
        token = "eYAXMaap0uc:APA91bFq-xBwcG6kvKs7kcHqW3jSglMLJGOK0nPDdS9Z21KcWgBHC0p9Ur63hGymCplioKtkhQsMul3JO5M_O70bzqY_Of80dfewyyaS1LKOwdw6Aki5UJJCV359Usq19FigCkN_IxTl";
        getUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public CoOrdinates getLocation() {
        return new CoOrdinates(l.getLat(), l.getLon());
    }

    public void pinLocation(View view) {
        l = new LocationService(this);
        nearbyPlaces.clear();
        List<Pin> allPins = thisUser.getAllPins();
        if(allPins != null){
            for(int i = 0; i < allPins.size(); i++){
                if(nearbyPlaces.size() >= 3)
                    break;
                if(MobileUtils.distanceInKm(allPins.get(i).getAvg(), getLocation()) < 1.0){
                    nearbyPlaces.add(allPins.get(i).getName());
                }
            }
        }
        if(nearbyPlaces.size() < 3) {
            StringBuilder sbValue = new StringBuilder(getQuery());
            PlacesTask placesTask = new PlacesTask();
            placesTask.execute(sbValue.toString());
        }
        else {
            nearbyPlaces.add("Somewhere else");
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            final CharSequence items[] = nearbyPlaces.toArray(new CharSequence[nearbyPlaces.size()]);
            adb.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    if(n == 3)
                        addTextDialog();
                    else
                        addPin(nearbyPlaces.get(n));
                }

            });
            adb.setNegativeButton("Cancel", null);
            adb.setTitle("Where are you?");
            adb.show();
        }
        /*User testUser = new User();
        testUser.setUserName("Test");
        testUser.setFirebaseToken(token);
        database.child("Project").child("Users").child(token).setValue(testUser);*/
    }

    private void addTextDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Pin Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pinName = input.getText().toString();
                addPin(pinName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void getUserData() {
        thisUser.setFirebaseToken(token);
        ValueEventListener userdataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                setThisUser(temp, this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("Project").child("Users")
                .child(token).addValueEventListener(userdataListener);
    }

    private void setThisUser(User u, ValueEventListener dataL) {
        thisUser.setUserName(u.getUserName());
        thisUser.setAllPins(u.getAllPins());
        thisUser.setLastPin(u.getLastPin());
        database.child("Project").child("Users")
                .child(token).removeEventListener(dataL);
    }

    private void addPin(String name) {
        boolean added = false;
        CoOrdinates current = getLocation();
        List<CoOrdinates> all = new ArrayList<>();
        all.add(current);
        List<Pin> allPins = thisUser.getAllPins();
        if (allPins != null)
            for (int i = 0; i < allPins.size(); i++) {
                if (allPins.get(i).getName().equalsIgnoreCase(name)) {
                    added = true;
                    updatePin(allPins.get(i), current);
                }
            }
        if(allPins == null){
            added = true;
            allPins = new ArrayList<>();
            Pin p = new Pin();
            TextView lt = (TextView) findViewById(R.id.lat);
            TextView ln = (TextView) findViewById(R.id.lon);
            TextView na = (TextView) findViewById(R.id.pin_name);
            lt.setText(String.valueOf(current.getLat()));
            ln.setText(String.valueOf(current.getLon()));
            na.setText(name);
            p.setAvg(current);
            p.setName(name);
            p.setAllCoOrdinates(all);
            allPins.add(p);
            thisUser.setAllPins(allPins);
        }
        if (!added) {
            Pin p = new Pin();
            TextView lt = (TextView) findViewById(R.id.lat);
            TextView ln = (TextView) findViewById(R.id.lon);
            TextView na = (TextView) findViewById(R.id.pin_name);
            lt.setText(String.valueOf(current.getLat()));
            ln.setText(String.valueOf(current.getLon()));
            na.setText("Last Pin :"+name);
            p.setAvg(current);
            p.setName(name);
            p.setAllCoOrdinates(all);
            allPins.add(p);
            thisUser.setAllPins(allPins);
        }

        database.child("Project").child("Users").child(token).setValue(thisUser);
    }

    private void updatePin(Pin p, CoOrdinates currentCoOrdinates) {
        List<CoOrdinates> output = lowPass(p.getAllCoOrdinates(), currentCoOrdinates);
        p.setAllCoOrdinates(output);
        CoOrdinates avg = average(output);
        p.setAvg(avg);
        TextView lt = (TextView) findViewById(R.id.lat);
        TextView ln = (TextView) findViewById(R.id.lon);
        TextView na = (TextView) findViewById(R.id.pin_name);
        lt.setText(String.valueOf(avg.getLat()));
        ln.setText(String.valueOf(avg.getLon()));
        na.setText(p.getName());
    }

    private List<CoOrdinates> lowPass(List<CoOrdinates> saved, CoOrdinates newCoOrdinates) {
        List<CoOrdinates> output = new ArrayList<>();
        if (saved == null) {
            output.add(newCoOrdinates);
        } else {
            for (int i = 0; i < saved.size(); i++) {
                CoOrdinates temp = new CoOrdinates(
                        (saved.get(i).getLat() + factor * (newCoOrdinates.getLat() - saved.get(i).getLat())),
                        (saved.get(i).getLon() + factor * (newCoOrdinates.getLon() - saved.get(i).getLon())));
                output.add(temp);
            }
            output.add(newCoOrdinates);
        }
        return output;
    }

    private CoOrdinates average(List<CoOrdinates> c) {
        double lat = 0;
        double lon = 0;
        for (int i = 0; i < c.size(); i++) {
            lat += c.get(i).getLat();
            lon += c.get(i).getLon();
        }
        lat = lat / c.size();
        lon = lon / c.size();
        return new CoOrdinates(lat, lon);
    }

    private StringBuilder getQuery() {
        CoOrdinates current = getLocation();
        double mLatitude = current.getLat();
        double mLongitude = current.getLon();

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=1000");
        sb.append("&types=" + "airport|bank|gym|library|restaurant");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyAaMcGc1Vdb--cAsQRI5qRTCqEwUAUzu4w");

        return sb;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;
        ParserTask parserTask = new ParserTask();

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                // Connecting to url
                urlConnection.connect();
                // Reading data from url
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();

            } catch (Exception e) {

            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;
        AlertDialog.Builder adb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adb = new AlertDialog.Builder(Trickiest.this);
        }

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            PlaceJson placeJson = new PlaceJson();

            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJson.parse(jObject);
            } catch (Exception e) {
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            // Clears all the existing markers;

            for (int i = 0; i < list.size(); i++) {
                if (nearbyPlaces.size() >= 3)
                    break;
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
                // Getting latitude of the place
                //double lat = Double.parseDouble(hmPlace.get("lat"));
                // Getting longitude of the place
                //double lng = Double.parseDouble(hmPlace.get("lng"));
                // Getting name
                nearbyPlaces.add(hmPlace.get("place_name"));
                // Getting vicinity
                //String vicinity = hmPlace.get("vicinity");
            }
            nearbyPlaces.add("Somewhere else");

            final CharSequence items[] = nearbyPlaces.toArray(new CharSequence[nearbyPlaces.size()]);
            adb.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    if(n == 3)
                        addTextDialog();
                    else
                        addPin(nearbyPlaces.get(n));
                }

            });
            adb.setNegativeButton("Cancel", null);
            adb.setTitle("Where are you?");
            adb.show();
        }
    }
}
