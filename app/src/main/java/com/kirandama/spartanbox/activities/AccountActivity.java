package com.kirandama.spartanbox.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.Tasks.UserProfileInfoTask;
import com.kirandama.spartanbox.parcels.UserProfile;

public class AccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        UserProfileInfoTask userProfileInfoTask = (UserProfileInfoTask) new UserProfileInfoTask(AccountActivity.this, new UserProfileInfoTask.AsyncResponse() {

            @Override
            public void processFinish(UserProfile accountInfo) {

                acctInfoDialogBox(accountInfo);

            }
        }).execute();
    }



    private void acctInfoDialogBox(final UserProfile userProfile){


        final TextView accountName;
        final TextView email;
        final TextView country;
        final TextView locale;
        final TextView totalSpace;
        final TextView usedSpace;
        final TextView freeSpace;

        accountName = (TextView) findViewById(R.id.name_text);
        email = (TextView) findViewById(R.id.email_text);
        country = (TextView) findViewById(R.id.country);
        locale = (TextView) findViewById(R.id.locale);
        totalSpace = (TextView) findViewById(R.id.totalSpace);
        usedSpace = (TextView) findViewById(R.id.usedSpace);
        freeSpace = (TextView) findViewById(R.id.freeSpace);

        accountName.setText(userProfile.getDisplayName());
        email.setText(userProfile.getEmail());
        country.setText("Country Code: " + userProfile.getCountry());
        locale.setText("Language: " + userProfile.getLocale());

        totalSpace.setText("Total space: " + userProfile.getTotalSpace() + " MB");
        usedSpace.setText("Used space: " + userProfile.getUsedSpace() + " MB");
        freeSpace.setText("Free space: " + userProfile.getFreeSpace() + " MB");

    }

}
