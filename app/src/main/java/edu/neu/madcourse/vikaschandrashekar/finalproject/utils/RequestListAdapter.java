package edu.neu.madcourse.vikaschandrashekar.finalproject.utils;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.RequestFragment;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.Requests;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;

/**
 * Created by cvikas on 4/20/2017.
 */

public class RequestListAdapter extends ArrayAdapter<Requests> {
    private List<Requests> items;
    private int layoutResourceId;
    private FragmentActivity context;
    private RequestFragment frag;
    private DatabaseReference database;
    private static final String SERVER_KEY = "key=AAAAebHHBsI:APA91bEofuzHb8FWUZEKUT0_QdKG_Vi7mlzDqgKtBB962Qv6FImKT7yYWVDGUdaZujN5oNOI-R5hO_q1uLMaFEWj5LMcpzYF5euzPncxyOT8taRqGSrrgkSUn6z-b_Q0srYmATr2VW17";

    public RequestListAdapter(FragmentActivity context, RequestFragment frag, int layoutResourceId, List<Requests> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.frag = frag;
        this.database = frag.getDatabase();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);
        final RequestsHolder holder = new RequestsHolder();
        Requests r = items.get(position);
        Map.Entry<String,String> entry = r.getFrom().entrySet().iterator().next();
        String value=entry.getValue();
        holder.content = value + " wants to follow you and check your pins.";
        holder.accept = (Button) row.findViewById(R.id.accept_request);
        holder.reject = (Button) row.findViewById(R.id.reject_request);
        holder.contentView = (TextView) row.findViewById(R.id.request_content);
        holder.reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final AlertDialog.Builder rejectdialog = new AlertDialog.Builder(context);
                rejectdialog.setTitle("Follower request");
                rejectdialog.setMessage("Are you sure you want to reject?");
                rejectdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                rejectdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        rejectRequest(items.get(position));
                    }
                });
                rejectdialog.show();
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final AlertDialog.Builder acceptdialog = new AlertDialog.Builder(context);
                acceptdialog.setTitle("Follower request");
                acceptdialog.setMessage("Are you sure you want to accept?");
                acceptdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                acceptdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        acceptRequest(items.get(position));
                    }
                });
                acceptdialog.show();
            }
        });
        setupItem(holder);
        return row;
    }

    private void rejectRequest(Requests r){
        r.setStatus("Rejected");
        database.child("Project").child("Requests").child(r.getRequestNo()).setValue(r);
    }

    private void acceptRequest(Requests r){
        sendNotification(r);
        Map<String, User> allUsers = frag.getAllUsers();
        Map.Entry<String,String> fromMap = r.getFrom().entrySet().iterator().next();
        Map.Entry<String,String> toMap = r.getTo().entrySet().iterator().next();

        User from = allUsers.get(fromMap.getKey());
        User to = allUsers.get(toMap.getKey());
        if(from.getFollowing() != null)
            from.getFollowing().add(toMap.getKey());
        else {
            List<String> follo = new ArrayList<>();
            follo.add(toMap.getKey());
            from.setFollowing(follo);
        }

        if(to.getFollowers() != null){
            to.getFollowers().add(fromMap.getKey());
        }
        else{
            List<String> follo = new ArrayList<>();
            follo.add(fromMap.getKey());
            to.setFollowers(follo);
        }
        database.child("Project").child("Users").child(from.getFirebaseToken()).setValue(from);
        database.child("Project").child("Users").child(to.getFirebaseToken()).setValue(to);
        r.setStatus("Accepted");
        database.child("Project").child("Requests").child(r.getRequestNo()).setValue(r);
    }

    private void sendNotification(final Requests r) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(r);
            }
        }).start();
    }

    private void pushNotification(Requests r) {


        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Pin Me");
            jNotification.put("body", r.getTo().entrySet().iterator().next().getValue()+" has accepted your request");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "PROJECT_OPEN");

            // If sending to a single client
            jPayload.put("to", r.getFrom().entrySet().iterator().next().getKey());

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

            /*Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FCMActivity.this,resp,Toast.LENGTH_LONG);
                }
            });*/
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private void setupItem(RequestsHolder holder) {
        holder.contentView.setText(holder.content);
    }


    public static class RequestsHolder {
        String content;
        TextView contentView;
        Button accept;
        Button reject;
    }
}
