package edu.neu.madcourse.vikaschandrashekar.finalproject.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;

/**
 * Created by cvikas on 4/21/2017.
 */

public class Registration extends AppCompatActivity {

    String token;
    private String username;
    static User thisUser = new User();
    private ArrayList<User> userList = new ArrayList<>();
    private Map<String, User> allUsers = new HashMap<String, User>();
    ProgressDialog progress;
    private final int ACCESS_FINE_LOCATION_PERMISSION = 0;
    private final int ACCESS_COARSE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        setContentView(R.layout.trickiestpart_registration);
        token = FirebaseInstanceId.getInstance().getToken();
        //token = "fCnxPy_t5b8:APA91bG0yYLlKH9zBNwMf6PO1Vc4Ykl29nIREYWWyascfDiRUdPUE01dJP7q0HwjruL47bxao4NsEaYg662SEjQYb9qF7O_SfcEud6IshcwbUJTKeynItPMenF_U0umIBP9nIFuh1KNq";
        loadAllUsers();

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION);
            return;
        }

        if(ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION);
            return;
        }

        //getUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    progress.dismiss();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Create a unique username and share it with other users who you want as followers. Do not share it with everyone as this is a private location sharing app")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createUsername();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Sorry this app needs location permission.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case ACCESS_COARSE_LOCATION_PERMISSION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    progress.dismiss();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Create a unique username and share it with other users who you want as followers. Do not share it with everyone as this is a private location sharing app")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createUsername();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Sorry this app needs location permission.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void getUserData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        thisUser.setFirebaseToken(token);
        ValueEventListener userdataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                // setThisUser(temp, this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //database.child("Project").child("Users").addValueEventListener(userdataListener);
        database.child("Project").child("Users").child(token).addValueEventListener(userdataListener);
    }


    public void createUsername() {
        //second dialog
        final AlertDialog.Builder recreateDialog = new AlertDialog.Builder(this);
        recreateDialog.setTitle("Username already exists");
        recreateDialog.setMessage("The username you selected already exists. Please enter another username");
        recreateDialog.setCancelable(false);
        recreateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createUsername();
            }
        });
        final AlertDialog recreateDialogSuccess = recreateDialog.create();

        //first dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create a username:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                username = input.getText().toString();
                if (!checkName(username)) {
                    recreateDialogSuccess.show();
                } else {
                    addUser();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        progress.dismiss();
        builder.show();

    }

    private boolean checkName(String name) {
        for (Map.Entry<String, User> e : allUsers.entrySet()) {
            User temp = e.getValue();
            if (temp.getUserName().equals(name))
                return false;
        }
        return true;
    }

    private void addUser() {
        User testUser = new User();
        testUser.setUserName(username);
        testUser.setFirebaseToken(token);
        allUsers.put(token, testUser);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Project").child("UserMap").setValue(allUsers);

        database.child("Project").child("Users").child(testUser.getFirebaseToken()).setValue(testUser);
        finish();
        Intent intent = new Intent(this, ProjectMain.class);
        startActivity(intent);
    }

    private void loadAllUsers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ValueEventListener allUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, User> t = new HashMap<String, User>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User temp = postSnapshot.getValue(User.class);
                    t.put(temp.getFirebaseToken(), temp);
                }
                removeListener(t, this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //database.child("Project").child("Users").addValueEventListener(userdataListener);
        database.child("Project").child("UserMap").addValueEventListener(allUserListener);
    }

    private void removeListener(Map<String, User> u, ValueEventListener v) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Project").child("UserMap").removeEventListener(v);
        allUsers.putAll(u);
        if (allUsers.containsKey(token)) {
            finish();
            Intent intent = new Intent(this, ProjectMain.class);
            progress.dismiss();
            startActivity(intent);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Create a unique username and share it with other users who you want as followers. Do not share it with everyone as this is a private location sharing app")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            createUsername();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

    }

}
