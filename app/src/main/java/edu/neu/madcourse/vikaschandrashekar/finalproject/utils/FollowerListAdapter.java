package edu.neu.madcourse.vikaschandrashekar.finalproject.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.Following;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;

/**
 * Created by cvikas on 4/18/2017.
 */

public class FollowerListAdapter extends ArrayAdapter<User> {

    private List<User> items;
    private int layoutResourceId;
    private FragmentActivity context;
    private User thisUser;
    private static final String SERVER_KEY = "key=AAAAebHHBsI:APA91bEofuzHb8FWUZEKUT0_QdKG_Vi7mlzDqgKtBB962Qv6FImKT7yYWVDGUdaZujN5oNOI-R5hO_q1uLMaFEWj5LMcpzYF5euzPncxyOT8taRqGSrrgkSUn6z-b_Q0srYmATr2VW17";

    public FollowerListAdapter(FragmentActivity context, Following f, int layoutResourceId, List<User> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        thisUser = f.getThisUser();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);

        final FollowerListHolder holder = new FollowerListHolder();
        holder.name = "Name : "+items.get(position).getUserName();
        if (items.get(position).getLastPin() != null)
            holder.pinName = "Last Pin : "+items.get(position).getLastPin().getName();
        else
            holder.pinName = "Last Pin : No Pins Yet";
        holder.userName = (TextView) row.findViewById(R.id.follower_username);
        holder.pin = (TextView) row.findViewById(R.id.follower_last_pin);

        holder.requestButton = (Button) row.findViewById(R.id.request_pin);
        holder.requestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushNotification(items.get(position).getFirebaseToken());
                    }
                }).start();
            }
        });
        setupItem(holder);
        return row;
    }

    private void setupItem(FollowerListHolder holder) {
        holder.userName.setText(holder.name);
        holder.pin.setText(holder.pinName);
    }

    public static class FollowerListHolder {
        String name;
        String pinName;
        TextView userName;
        TextView pin;
        Button requestButton;
    }

    private void pushNotification(String firebaseToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Pin Me");
            jNotification.put("body", thisUser.getUserName()+" wants to know where you are");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "PROJECT_OPEN");

            // If sending to a single client
            jPayload.put("to", firebaseToken);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            jData.put("Fragment", "0");
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
}
