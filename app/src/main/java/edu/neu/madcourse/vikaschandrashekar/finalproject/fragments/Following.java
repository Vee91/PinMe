package edu.neu.madcourse.vikaschandrashekar.finalproject.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.FollowerListAdapter;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.UserListAdapter;

/**
 * Created by cvikas on 4/18/2017.
 */

public class Following extends Fragment {

    private List<String> foundUserList = new ArrayList<>();
    public static User thisUser = new User();
    private DatabaseReference database;
    private String token;
    private Map<String, User> allUsers = new HashMap<String, User>();
    private static List<User> followingUsers = new ArrayList<>();

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
        final View rootView = inflater.inflate(R.layout.following, container, false);
        loadUserMap();
        getUserData();
        EditText enterWord = (EditText) rootView.findViewById(R.id.searchUser);
        enterWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() >= 3) {
                    foundUserList.clear();
                    String out = findUser(s.toString());
                    if(out != null) {
                        setFollowerListView(new ArrayList<User>(), rootView);
                        foundUserList.add(out);
                        setSearchListView(s.toString().toLowerCase(), rootView);
                    }
                }
                else if(s.length() == 0){
                    foundUserList.clear();
                    setSearchListView(s.toString().toLowerCase(), rootView);
                    setFollowerListView(followingUsers, rootView);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void setSearchListView(String s, View view) {
        UserListAdapter adapter = new UserListAdapter(getActivity(), this, R.layout.user_list, foundUserList, thisUser);
        ListView userListView = (ListView) view.findViewById(R.id.foundUsers);
        userListView.setAdapter(adapter);
    }

    private void setFollowerListView(List<User> s, View view) {
        FollowerListAdapter adapter = new FollowerListAdapter(getActivity(), this, R.layout.followers_list, s);
        ListView userListView = (ListView) view.findViewById(R.id.followers);
        userListView.setAdapter(adapter);
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
        if (thisUser.getFollowing() != null && thisUser.getFollowing().size() > 0)
            getFollowingUsers(thisUser.getFollowing());
    }

    private String findUser(String s) {
        for(Map.Entry e : allUsers.entrySet()){
            User temp = (User)e.getValue();
            if(temp.getUserName().equals(s))
                return s;
        }
        return null;
    }

    private void loadUserMap() {
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
        database.child("Project").child("UserMap").addValueEventListener(allUserListener);
    }

    private void getFollowingUsers(final List<String> tokens) {
        final List<User> out = new ArrayList<>();
        ValueEventListener allUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User temp = postSnapshot.getValue(User.class);
                    if (tokens.contains(temp.getFirebaseToken())) {
                        out.add(temp);
                    }
                }
                removeListener(out, this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("Project").child("Users").addValueEventListener(allUserListener);
    }

    private void removeListener(Map<String, User> u, ValueEventListener v) {
        database.child("Project").child("UserMap").removeEventListener(v);
        allUsers.putAll(u);
    }

    private void removeListener(List<User> out, ValueEventListener v) {
        followingUsers.clear();
        followingUsers.addAll(out);
        if (followingUsers.size() > 0)
            setFollowerListView(followingUsers, getView());
        database.child("Project").child("Users").removeEventListener(v);
    }

    public List<String> getFoundUserList() {
        return foundUserList;
    }

    public Map<String, User> getAllUsers() {
        return allUsers;
    }

    public static List<User> getFollowingUsers() {
        return followingUsers;
    }

    public DatabaseReference getDatabase(){
        return database;
    }

    public User getThisUser(){
        return  thisUser;
    }
}
