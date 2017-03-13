package com.kirandama.spartanbox.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.kirandama.spartanbox.utils.Common;
import com.kirandama.spartanbox.parcels.UserProfile;


public class UserProfileInfoTask extends AsyncTask <String, Void, UserProfile> {

    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;

    public UserProfileInfoTask(Context context, AsyncResponse respObj) {

        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();
    }

    public interface AsyncResponse {

        void processFinish(UserProfile userProfile);
    }

    @Override
    protected UserProfile doInBackground(String... params) {

        UserProfile userProfile = null;

        try {

            userProfile = new UserProfile(dropbox.accountInfo());
        } catch(DropboxException ex) {

            ex.printStackTrace();
        }

        return userProfile;
    }

    @Override
    protected void onPostExecute(UserProfile userProfile) {

        respObj.processFinish(userProfile);
    }
}