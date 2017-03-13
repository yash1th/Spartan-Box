package com.kirandama.spartanbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.kirandama.spartanbox.R;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class UserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Parse.initialize(this, "I0jGUjsj3Je3ol4TPpHJ0rIhmKrMm8qms90uFPHf", "xsuKJV8tElnevfoDmc3Y64c50MCJGJ6qi5jG7vIl");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        Button login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Auth.startOAuth2Authentication(UserActivity.this, getString(R.string.app_key));


                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);


            }
        });
    }

}
