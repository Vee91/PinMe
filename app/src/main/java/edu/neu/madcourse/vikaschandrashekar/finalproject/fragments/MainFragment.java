package edu.neu.madcourse.vikaschandrashekar.finalproject.fragments;

/**
 * Created by cvikas on 4/18/2017.
 */


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.CoOrdinates;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.Pin;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;
import edu.neu.madcourse.vikaschandrashekar.finalproject.service.LocationService;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.MobileUtils;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.PlaceJson;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.UserData;

public class MainFragment extends Fragment {

    private DatabaseReference database;
    private String token;
    private static User thisUser = new User();
    private String pinName;
    private final float factor = 0.15f;
    private LocationService l;
    private List<String> nearbyPlaces = new ArrayList<>();
    private static final String SERVER_KEY = "key=AAAAebHHBsI:APA91bEofuzHb8FWUZEKUT0_QdKG_Vi7mlzDqgKtBB962Qv6FImKT7yYWVDGUdaZujN5oNOI-R5hO_q1uLMaFEWj5LMcpzYF5euzPncxyOT8taRqGSrrgkSUn6z-b_Q0srYmATr2VW17";

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        //token = "fCnxPy_t5b8:APA91bG0yYLlKH9zBNwMf6PO1Vc4Ykl29nIREYWWyascfDiRUdPUE01dJP7q0HwjruL47bxao4NsEaYg662SEjQYb9qF7O_SfcEud6IshcwbUJTKeynItPMenF_U0umIBP9nIFuh1KNq";
        l = new LocationService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.project_main_fragment, container, false);
        getUserData();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
//        pinLocation();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getUserData() {
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

    private void setThisUser(final User u, ValueEventListener dataL) {
        thisUser.setUserName(u.getUserName());
        thisUser.setAllPins(u.getAllPins());
        thisUser.setLastPin(u.getLastPin());
        thisUser.setFirebaseToken(u.getFirebaseToken());
        thisUser.setFollowing(u.getFollowing());
        thisUser.setFollowers(u.getFollowers());

        TextView t = (TextView) getView().findViewById(R.id.loggedIn);
        t.setText("Logged in as : "+ u.getUserName());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                if(thisUser.getLastPin() != null) {
                    LatLng pin = new LatLng(thisUser.getLastPin().getAvg().getLat(), thisUser.getLastPin().getAvg().getLon());
                    googleMap.addMarker(new MarkerOptions().position(pin).title("Last Pin").snippet(thisUser.getLastPin().getName()));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(pin).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                else {
                    CoOrdinates current = getLocation();
                    LatLng l = new LatLng(current.getLat(), current.getLon());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(l).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });


        database.child("Project").child("Users")
                .child(token).removeEventListener(dataL);
    }



    public CoOrdinates getLocation() {
        return new CoOrdinates(l.getLat(), l.getLon());
    }

    public void pinLocation(){
        l = new LocationService(getActivity());
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
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
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

    private void addPin(String name) {
        boolean added = false;
        Pin p = new Pin();
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
            p.setAvg(current);
            p.setName(name);
            p.setAllCoOrdinates(all);
            allPins.add(p);
            thisUser.setLastPin(p);
            thisUser.setAllPins(allPins);
        }
        if (!added) {
            p.setAvg(current);
            p.setName(name);
            p.setAllCoOrdinates(all);
            allPins.add(p);
            thisUser.setLastPin(p);
            thisUser.setAllPins(allPins);
        }
        LatLng pin = new LatLng(thisUser.getLastPin().getAvg().getLat(), thisUser.getLastPin().getAvg().getLon());
        if(thisUser.getFollowers() != null && thisUser.getFollowers().size() > 0)
            sendNotificationToAll();
        googleMap.addMarker(new MarkerOptions().position(pin).title("Last Pin").snippet(thisUser.getLastPin().getName()));
        database.child("Project").child("Users").child(token).setValue(thisUser);
    }

    private void sendNotificationToAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification();
            }
        }).start();
    }

    private void pushNotification() {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Pin Me");
            jNotification.put("body", thisUser.getUserName()+" added a new pin just now.");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "PROJECT_OPEN");

            if(thisUser.getFollowers().size() == 1)
                jPayload.put("to", thisUser.getFollowers().get(0));
            // If sending to multiple clients (must be more than 1 and less than 1000)
            else {
                JSONArray ja = new JSONArray();
                for (int i = 0; i < thisUser.getFollowers().size(); i++) {
                    ja.put(thisUser.getFollowers().get(i));
                }
                jPayload.put("registration_ids", ja);
            }


            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            jData.put("Fragment", "1");
            jPayload.put("data", jData);


            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
    private void updatePin(Pin p, CoOrdinates currentCoOrdinates) {
        List<CoOrdinates> output = lowPass(p.getAllCoOrdinates(), currentCoOrdinates);
        p.setAllCoOrdinates(output);
        CoOrdinates avg = average(output);
        p.setAvg(avg);
        thisUser.setLastPin(p);
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

    private void addTextDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Pin Name");

        // Set up the input
        final EditText input = new EditText(getActivity());
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
        // this is concurrent thread in android
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
            adb = new AlertDialog.Builder(getActivity());
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
