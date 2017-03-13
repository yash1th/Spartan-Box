package com.kirandama.spartanbox.Tasks;

import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.kirandama.spartanbox.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiran on 12/2/2015.
 */
public class SearchTask extends AsyncTask<Void, Void, ArrayList<DropboxAPI.Entry>> {

private DropboxAPI<?> dropbox = Common.getDropboxObj();
private String path;
private String query;
    public AsyncResponse respObj = null;



// #########################Interface code ############################

public interface AsyncResponse {

    void processFinish(ArrayList<DropboxAPI.Entry> output);
}

    public SearchTask(AsyncResponse respObj, String path, String query){

        this.respObj = respObj;
        this.path = path;
        this.query = query;
    }


    //#######################################################################

    @Override
    protected ArrayList<DropboxAPI.Entry> doInBackground(Void... params) {

        List<DropboxAPI.Entry> fileList = new ArrayList<DropboxAPI.Entry>();
        try {

            fileList = dropbox.search(path,query,0,false);

        } catch (DropboxException e) {

            e.printStackTrace();
        }

        return (ArrayList<DropboxAPI.Entry>)fileList;
    }

    @Override
    protected void onPostExecute(ArrayList<DropboxAPI.Entry> result) {

        respObj.processFinish(result);
    }
}
