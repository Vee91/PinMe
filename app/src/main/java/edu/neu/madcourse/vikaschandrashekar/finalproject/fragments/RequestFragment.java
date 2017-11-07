package edu.neu.madcourse.vikaschandrashekar.finalproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.Requests;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.RequestListAdapter;

/**
 * Created by cvikas on 4/20/2017.
 */

public class RequestFragment extends Fragment{
    public static User thisUser = new User();
    private DatabaseReference database;
    private String token;
    private Map<String, User> allUsers = new HashMap<String, User>();
    private List<Requests> myRequests = new ArrayList<>();
    private static ValueEventListener requestsListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        //token = "fCnxPy_t5b8:APA91bG0yYLlKH9zBNwMf6PO1Vc4Ykl29nIREYWWyascfDiRUdPUE01dJP7q0HwjruL47bxao4NsEaYg662SEjQYb9qF7O_SfcEud6IshcwbUJTKeynItPMenF_U0umIBP9nIFuh1KNq";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onResume();
        final View rootView = inflater.inflate(R.layout.requests, container, false);
        loadAllUsers();
        getUserData();
        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
        database.child("Project").child("Requests").removeEventListener(requestsListener);
    }

    private void loadAllUsers(){
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
        database.child("Project").child("Users").addValueEventListener(allUserListener);
    }

    private void removeListener(Map<String, User> u, ValueEventListener v) {
        database.child("Project").child("Users").removeEventListener(v);
        allUsers.putAll(u);
    }

    private void getUserData() {
        database.child("Project").child("Users")
                .child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                setThisUser(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setThisUser(User u) {
        thisUser.setUserName(u.getUserName());
        thisUser.setAllPins(u.getAllPins());
        thisUser.setLastPin(u.getLastPin());
        thisUser.setFollowing(u.getFollowing());
        thisUser.setFirebaseToken(u.getFirebaseToken());
        getAllRequests();
    }

    private void getAllRequests(){
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Requests> out = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Requests temp = postSnapshot.getValue(Requests.class);
                    if(temp.getTo().containsKey(thisUser.getFirebaseToken()) && temp.getStatus().equals("Pending"))
                        out.add(temp);
                }
                removeListener(out);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("Project").child("Requests").addValueEventListener(requestsListener);
    }

    private void removeListener(List<Requests> out) {
        TextView c = (TextView) getView().findViewById(R.id.no_requests);
        myRequests.clear();
        myRequests.addAll(out);
        if(myRequests.size() != 0){
            c.setVisibility(View.INVISIBLE);
        }
        else{
            c.setVisibility(View.VISIBLE);
        }
        setRequestView(getView());
    }

    private void setRequestView(View view) {
        RequestListAdapter adapter = new RequestListAdapter(getActivity(), this, R.layout.requests_list, myRequests);
        ListView requestListView = (ListView) view.findViewById(R.id.requests);
        requestListView.setAdapter(adapter);
    }

    public DatabaseReference getDatabase(){
        return database;
    }

    public User getThisUser(){
        return thisUser;
    }

    public Map<String, User> getAllUsers(){
        return  allUsers;
    }
}
