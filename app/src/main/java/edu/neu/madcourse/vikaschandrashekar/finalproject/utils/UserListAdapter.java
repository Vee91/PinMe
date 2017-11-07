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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.Following;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.Requests;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;

/**
 * Created by cvikas on 4/18/2017.
 */

public class UserListAdapter extends ArrayAdapter<String> {
    private List<String> items;
    private int layoutResourceId;
    private FragmentActivity context;
    private Following frag;
    private DatabaseReference database;
    private static final String SERVER_KEY = "key=AAAAebHHBsI:APA91bEofuzHb8FWUZEKUT0_QdKG_Vi7mlzDqgKtBB962Qv6FImKT7yYWVDGUdaZujN5oNOI-R5hO_q1uLMaFEWj5LMcpzYF5euzPncxyOT8taRqGSrrgkSUn6z-b_Q0srYmATr2VW17";

    public UserListAdapter(FragmentActivity context, Following frag, int layoutResourceId, List<String> items, User u) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.frag = frag;
        this.database = frag.getDatabase();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        LayoutInflater inflater = context.getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        final UserListHolder holder = new UserListHolder();
        holder.name = items.get(position);
        holder.addButton = (Button) row.findViewById(R.id.addUser);

        holder.addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendFollowRequest(holder.name);
            }
        });


        holder.userName = (TextView) row.findViewById(R.id.found_username);
        setupItem(holder);
        return row;
    }

    private void setupItem(UserListHolder holder) {
        holder.userName.setText(holder.name);
    }

    private void sendFollowRequest(String name) {
        if (frag.thisUser.getUserName().equals(name)) {
            final AlertDialog.Builder help = new AlertDialog.Builder(frag.getActivity());
            help.setTitle("Request error");
            help.setMessage("You cannot add yourself as follower");
            help.setCancelable(true);
            help.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            help.show();
        }

        else{
            Requests make = new Requests();
            Map<String, User> allUsersMap = frag.getAllUsers();
            Map<String, String> from = new HashMap<>();
            from.put(frag.thisUser.getFirebaseToken(), frag.thisUser.getUserName());
            Map<String, String> to = new HashMap<>();
            for (Map.Entry e : allUsersMap.entrySet()) {
                User temp = (User) e.getValue();
                if (temp.getUserName().equals(name)) {
                    to.put(temp.getFirebaseToken(), temp.getUserName());
                }
            }
            sendNotification(frag.thisUser, to);
            make.setStatus("Pending");
            make.setRequestType("add_request");
            make.setFrom(from);
            make.setTo(to);
            sendToDB(make);
        }
    }

    private void sendNotification(final User thisUser, final Map<String, String> to) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(thisUser, to);
            }
        }).start();
    }

    private void pushNotification(User thisUser, Map<String, String> to) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Pin Me");
            jNotification.put("body", thisUser.getUserName() + " has sent a request to follow you");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "PROJECT_OPEN");

            Map.Entry<String, String> entry = to.entrySet().iterator().next();

            // If sending to a single client
            jPayload.put("to", entry.getKey());
            jData.put("Fragment", "2");
            jPayload.put("data", jData);

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


    private void sendToDB(final Requests r) {
        database.child("Project").child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Requests> temp = new HashMap<String, Requests>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Requests t = postSnapshot.getValue(Requests.class);
                            temp.put(t.getRequestNo(), t);
                        }
                        doPush(temp, r);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void doPush(Map<String, Requests> all, final Requests n) {
        int size = all.size();
        n.setRequestNo("R" + size + 1);
        all.put("R" + size + 1, n);
        database.child("Project").child("Requests").setValue(all);
        AlertDialog.Builder builder = new AlertDialog.Builder(frag.getActivity());
        builder.setMessage("A request has been sent")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static class UserListHolder {
        String name;
        TextView userName;
        Button addButton;
    }
}
