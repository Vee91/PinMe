package edu.neu.madcourse.vikaschandrashekar.assignment1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 1/16/2017.
 */

public class AboutActivity extends Activity {

    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.about);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if(appTitle != null){
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.assignment1_about_label);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

            }
        }
        else {
            final TextView imei = (TextView) findViewById(R.id.imei);
            TelephonyManager tm = (TelephonyManager) getSystemService(android.content.Context.TELEPHONY_SERVICE);
            imei.setText(tm.getDeviceId());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final TextView imei = (TextView) findViewById(R.id.imei);
                    TelephonyManager tm = (TelephonyManager) getSystemService(android.content.Context.TELEPHONY_SERVICE);
                    imei.setText(tm.getDeviceId());

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
