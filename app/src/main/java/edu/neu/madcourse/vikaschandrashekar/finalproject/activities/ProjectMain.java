package edu.neu.madcourse.vikaschandrashekar.finalproject.activities;

/**
 * Created by cvikas on 4/18/2017.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.MainFragment;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.Following;
import edu.neu.madcourse.vikaschandrashekar.finalproject.fragments.RequestFragment;
import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;
import edu.neu.madcourse.vikaschandrashekar.finalproject.utils.UserData;

public class ProjectMain extends AppCompatActivity  {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.project_main);
        int position = 0;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = Integer.valueOf(extras.getString("Fragment"));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PinMe");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(position);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(int position) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "Pin");
        adapter.addFragment(new Following(), "Following");
        adapter.addFragment(new RequestFragment(), "Requests");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void pinLocationProject(View view) {
        MainFragment m = (MainFragment)getSupportFragmentManager().getFragments().get(0);
        m.pinLocation();
    }

    public void pinHelp(View view){
        final AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Pins");
        help.setMessage("Pin your current location by either choosing from options generated from map or entering in text field. Latest pin" +
                " added by you can be seen by your followers.");
        help.setCancelable(true);
        help.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        help.show();
    }

    public void followerHelp(View view){
        final AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Followers");
        help.setMessage("Here you can see users you are following or add new users to follow. Search users by their username." +
                " You need to have usernames with you before you can add them. Searching username is also case sensitive.");
        help.setCancelable(true);
        help.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        help.show();
    }

}
