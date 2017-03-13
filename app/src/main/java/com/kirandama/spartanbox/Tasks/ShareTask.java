package com.kirandama.spartanbox.Tasks;

/**
 * Created by sasank on 11/30/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.kirandama.spartanbox.utils.Common;
import com.kirandama.spartanbox.parcels.IconMaker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ShareTask extends AsyncTask<IconMaker, Void, String> {

    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;
    public String shareAddress=null;

    public ShareTask(Context context, AsyncResponse respObj) {

        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();
    }

    public interface AsyncResponse {

        void processFinish(String result);
    }

    private static String getShareURL(String strURL) {

        URLConnection conn = null;
        String redirectedUrl = null;

        try {

            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            System.out.println("Redirected URL: " + conn.getURL());
            redirectedUrl = conn.getURL().toString();
            is.close();

        } catch (MalformedURLException e) {

            Log.d("xxxxx", "Please input a valid URL");
        } catch (IOException ioe) {

            Log.d("xxx", "Can not connect to the URL");
        }

        return redirectedUrl;
    }

    @Override
    protected String doInBackground(IconMaker... params) {

        try {

            dropbox = Common.getDropboxObj();
            DropboxAPI.DropboxLink shareLink = dropbox.share(params[0].getPath());
            shareAddress = getShareURL(shareLink.url);
            if (params[0].isDir() == false) {

                shareAddress = shareAddress.replaceFirst("https://www", "https://dl");
            }

            Log.d("XXXXXX", "dropbox share link " + shareAddress);
        } catch (DropboxException e) {

            Log.i("File Dropbox Operation", "Exception: ", e);
            e.printStackTrace();
            message = e.getCause().getMessage();
        }

        return shareAddress;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result == null) {

            Toast.makeText(context, "File operation failed: " + message, Toast.LENGTH_LONG)
                    .show();
        }

        respObj.processFinish(result);
    }
}