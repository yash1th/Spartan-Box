package com.kirandama.spartanbox.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.kirandama.spartanbox.utils.Common;
import com.kirandama.spartanbox.parcels.UserProfile;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.fragments.OptionsFragment;
import com.kirandama.spartanbox.parcels.IconMaker;
import com.kirandama.spartanbox.Tasks.FileTask;
import com.kirandama.spartanbox.Tasks.ListTask;
import com.kirandama.spartanbox.Tasks.ShareTask;
import com.kirandama.spartanbox.Tasks.UploadTask;
import com.kirandama.spartanbox.utils.AppSettings;
import com.kirandama.spartanbox.utils.Common;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;
import com.parse.SendCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OptionsFragment.ListViewFragmentProtocol {

    private ActionMode actionMode;

    private boolean mLoggedIn, onResume;
    private int PICK_IMAGE = 0;
    private int PICK_PDF = 1;
    private IconMaker rootFolder = new IconMaker();
    private String selectedFolderPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4D8FCC")));

        if (!AppSettings.getSharedSettings().isAuthenticated()) {

            AndroidAuthSession session = buildSession();
            Common.setDropboxObj(new DropboxAPI<AndroidAuthSession>(session));
            Common.getDropboxObj().getSession().startOAuth2Authentication(MainActivity.this);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            rootFolder = extras.getParcelable("rootFolder");
        } else {

            rootFolder.setDir(true);
            rootFolder.setName("Spartan Box");
            rootFolder.setPath(Common.rootDIR);
        }

        try {

            setTitle(rootFolder.getName());


            FloatingActionButton actionEnable = (FloatingActionButton) findViewById(R.id.docs);
            actionEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFileChooser();
                }
            });
            final FloatingActionButton actionEnable1 = (FloatingActionButton) findViewById(R.id.image);
            actionEnable1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pickIntent = null;
                    pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentPicker("image",pickIntent);
                }
            });
            final FloatingActionButton actionEnable2 = (FloatingActionButton) findViewById(R.id.video);
            actionEnable2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pickIntent = null;
                    pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    intentPicker("video",pickIntent);
                }
            });
            final FloatingActionButton actionEnable3 = (FloatingActionButton) findViewById(R.id.item_create_folder);
            actionEnable3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlertDialogueForCreateFolder();
                }
            });

        } catch (Exception exc) {

            rootFolder = new IconMaker();

            rootFolder.setDir(true);
            rootFolder.setName("Spartan Box");
            rootFolder.setPath(Common.rootDIR);

            setTitle(rootFolder.getName());
        }


//        Parse.initialize(this, "I0jGUjsj3Je3ol4TPpHJ0rIhmKrMm8qms90uFPHf", "xsuKJV8tElnevfoDmc3Y64c50MCJGJ6qi5jG7vIl");
//        ParseInstallation.getCurrentInstallation().saveInBackground();



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        System.out.println("onWindowFocusChanged");
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        String obj = "";
        Intent pickIntent = null;

            switch (id)
            {
                case R.id.action_acc_info:
                    getAcctInfo();
                    return true;
//                case R.id.log_out:
//                    logOut();
//                    return true;
            }

        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogueForCreateFolder () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Folder name");
        builder.setMessage("This folder will be created under " + rootFolder.getName());

        final EditText folderNameInput = new EditText(this);

        folderNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(folderNameInput);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(folderNameInput.getWindowToken(), 0);

                MainActivity.this.createFolderWithName(folderNameInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(folderNameInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        folderNameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void createFolderWithName(String folderName) {

        FileTask f = (FileTask) new FileTask(MainActivity.this,
                new FileTask.AsyncResponse() {

                    @Override
                    public void processFinish(boolean result) {

                        if(result) {

                            refreshList(rootFolder.getPath());
                        }
                    }
                }).execute(Common.METHOD_CREATE_FOLDER, rootFolder.getPath()+ File.separator+folderName);

        ParsePush push = new ParsePush();
        push.setMessage("A new folder with name " + folderName + " was created under Spartan Box");
        push.sendInBackground();

    }

    public void intentPicker(String obj, Intent pickIntent)
    {
        String type = obj+"/*";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select " + obj);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);

    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if ((requestCode == PICK_IMAGE || requestCode == PICK_PDF) && resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    //Display an error
                    return;
                }
                else
                {
                    uploadFile(data, requestCode);
                }

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.d("intent_result","reached here");
        }

    }

    private void uploadFile(Intent data, int requestCode) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(data.getData());
        int size = inputStream.available();
        String fileName = getFileName(data, requestCode);
        Log.i("TEST", "File Size: " + inputStream.available());

        UploadTask u = new UploadTask(MainActivity.this, fileName, inputStream, size);
        u.execute();
    }

    private String getFileName(Intent data, int requestCode)
    {

        String path = data.getData().getPath();
        String[] pathArr = null;
        String fileName = "";
        String baseName = "";
        String type = getContentResolver().getType(data.getData());

        if(requestCode == 0)
        {
            pathArr = path.split(File.separator);

        }
        else if(requestCode == 1)
        {
            pathArr = path.split("=");
        }
        fileName = pathArr[pathArr.length-1];
        pathArr = type.split(File.separator);
        baseName = pathArr[0];
        type = pathArr[1];

        fileName = baseName + "_" + fileName + "." + type;
        fileName = rootFolder.getPath() + File.separator + fileName;

        return  fileName;
    }
    private AndroidAuthSession buildSession() {

        AppKeyPair appKeyPair = new AppKeyPair(Common.APP_KEY, Common.APP_SECRET);
        AndroidAuthSession session = null;
        String[] stored = getKeys();
        if (stored != null) {

            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair,accessToken);
        } else {

            session = new AndroidAuthSession(appKeyPair);
        }

        return session;
    }

    protected void onResume() {

        super.onResume();

        DropboxAPI<AndroidAuthSession> mApi = Common.getDropboxObj();
        if (Common.getDropboxObj().getSession().authenticationSuccessful()) {

            try {

                mApi.getSession().finishAuthentication();
                String accessToken = mApi.getSession().getOAuth2AccessToken();
                System.out.print(accessToken);
                storeKeys("oauth2:", accessToken);
                setLoggedIn(true);



            } catch (IllegalStateException e) {

                Log.i("DbAuthLog", "Error authenticating", e);
                showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());
            }
        }

        if(mLoggedIn)
        {
            try
            {
                createDIR();
            }
            catch (DropboxException e) {

                Log.i("DbAuthLog", "Error creating Spartan Box Folder..", e);
                showToast("Error creating Spartan Box Folder..Please contact Administrator");
            }
        }

    }

    private void showToast(String msg) {

        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    public void setLoggedIn(boolean loggedIn) {

        mLoggedIn = loggedIn;
        AppSettings.getSharedSettings().setAuthenticated(loggedIn);
        if (loggedIn) {

            onResume = false;
        }

    }

    public void refreshList(String path)
    {
        ListTask t = (ListTask) new ListTask(new ListTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {
                ArrayList<IconMaker> result = new ArrayList<IconMaker>();


                for(DropboxAPI.Entry e : output) {

                    IconMaker dropboxItem = new IconMaker(e);
                    result.add(dropboxItem);
                }
                OptionsFragment optionsFragment = (OptionsFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.list_view_fragment);
                optionsFragment.reloadListView(result);
            }
        }).execute(path);
    }


    private void storeKeys(String key, String secret) {

        SharedPreferences prefs = getSharedPreferences(Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Common.ACCESS_KEY_NAME, key);
        edit.putString(Common.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

//    private void logOut() {
//
//        mLoggedIn = false;
//        Common.getDropboxObj().getSession().unlink();
//        clearKeys();
//
//
//        Intent i = new Intent(getApplicationContext(), UserActivity.class);
//        startActivity(i);
//    }


    private void clearKeys() {

        SharedPreferences prefs = getSharedPreferences(
                Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private String[] getKeys() {

        SharedPreferences prefs = getSharedPreferences(Common.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(Common.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Common.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {

            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;

            return ret;
        } else {

            return null;
        }
    }

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            OptionsFragment optionsFragment = (OptionsFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.list_view_fragment);

            switch (item.getItemId()) {

                case R.id.item_delete:

                    deleteFromDropbox(optionsFragment.selectedDropboxItems());
                    mode.finish();
                    return true;

                case R.id.item_share:

                    shareFromDropbox(optionsFragment.selectedDropboxItems());
                    mode.finish();
                    return true;

                case R.id.item_download:

                    mode.finish();
                    return true;

               /* case R.id.item_move:

                    startFolderSelectionIntent(optionsFragment.selectedDropboxItems(), FOLDER_SELECT_ACTIVITY_RESULT_MOVE);
                    mode.finish();
                    return true;

                case R.id.item_copy:

                    startFolderSelectionIntent(optionsFragment.selectedDropboxItems(), FOLDER_SELECT_ACTIVITY_RESULT_COPY);
                    mode.finish();
                    return true;*/
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }
    }

    private void deleteFromDropbox(final ArrayList <IconMaker> dropboxItems) {

        for (IconMaker item : dropboxItems) {

            FileTask f = (FileTask) new FileTask(MainActivity.this,
                    new FileTask.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            if(result) {

                                refreshList(rootFolder.getPath());
                            }
                        }
                    }).execute(Common.METHOD_DELETE, item.getPath());
            ParsePush push = new ParsePush();
            push.setMessage("A file/folder was removed from Spartan Box");
            push.sendInBackground();
        }
    }

    public void getAcctInfo(){

        Intent i = new Intent(getApplicationContext(), AccountActivity.class);
        startActivity(i);

    }

    public void shareFromDropbox(final ArrayList<IconMaker> dropboxItems) {

        final ArrayList <String> shareUrls = new ArrayList<String>();
        for (IconMaker item : dropboxItems) {

            ShareTask f = (ShareTask) new ShareTask(MainActivity.this, new ShareTask.AsyncResponse() {

                @Override
                public void processFinish(String result) {

                    if (result != null) {

                        shareUrls.add(result);

                        if (shareUrls.size() == dropboxItems.size()) {

                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < shareUrls.size(); i++) {

                                if (i < shareUrls.size()-1) {

                                    builder.append(shareUrls.get(i)).append(", ");
                                } else {

                                    builder.append(shareUrls.get(i));
                                }
                            }

                            sendEmailDialogBox(builder);
                        }
                    }
                }
            }).execute(item);

        }
    }

    private void sendEmailDialogBox(final StringBuilder stringBuilder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share with");

        final EditText emailInput = new EditText(this);
        emailInput.setHint("Use comma to separate multiple emails");

        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(emailInput);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);
                //userProfile = new UserProfile(DropboxAPI.Account);
                MainActivity.this.openEmailIntent(stringBuilder, emailInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        emailInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void openEmailIntent (StringBuilder stringBuilder, String toEmail) {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+toEmail+"?subject=" +
                Uri.encode("Notification from Spartan Box") + "&body=" +
                Uri.encode("A new file was shared with you from Spartan Box \n" + stringBuilder.toString())));
        startActivity(intent);
        ParsePush push = new ParsePush();
        push.setMessage("A file was shared to an user from Spartan Box");
        push.sendInBackground();
    }

    public void createDIR() throws DropboxException {

        ListTask t = (ListTask) new ListTask(new ListTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                // SpartanBox Folder does not exist.. create it
                if(output.size()==0) {

                    FileTask f = (FileTask)new FileTask(MainActivity.this, new FileTask.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            //refreshList(Common.rootDIR);

                        }
                    }).execute(Common.METHOD_CREATE_FOLDER, rootFolder.getPath());
                } else {

                    OptionsFragment optionsFragment = (OptionsFragment) getSupportFragmentManager().
                            findFragmentById(R.id.list_view_fragment);
                    if (!optionsFragment.isSearchModeOn()) {

                        refreshList(rootFolder.getPath());
                    } else {

                        ArrayList temp = optionsFragment.dropboxItems();
                        if (temp.size() == 0) {

                            refreshList(rootFolder.getPath());
                        } else {

                            optionsFragment.reloadListView(temp);
                        }
                    }
                }
            }
        }).execute(Common.rootDIR);

    }

    public void viewFile(final IconMaker dropboxItem) {

        ShareTask f = (ShareTask) new ShareTask(MainActivity.this, new ShareTask.AsyncResponse() {

            @Override
            public void processFinish(String result) {

                if (result != null) {

                    dropboxItem.setShareLink(result);
                    Intent filePreviewIntent = new Intent(getApplicationContext(), FileActivity.class);
                    filePreviewIntent.putExtra("dropboxItem", dropboxItem);
                    startActivity(filePreviewIntent);
                }
            }
        }).execute(dropboxItem);
    }

    public void openFolder (IconMaker dropboxItem) {

        Intent folderNavigatorIntent = new Intent(getApplicationContext(), MainActivity.class);
        folderNavigatorIntent.putExtra("rootFolder", dropboxItem);
        startActivity(folderNavigatorIntent);
    }

    public void viewDropboxItem(final IconMaker dropboxItem) {

        if (dropboxItem.isDir()) {

            openFolder(dropboxItem);
        } else {

            viewFile(dropboxItem);
        }
    }

    public void beginContextualActionMode(ArrayList <IconMaker> selectedItems) {

        actionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
    }

    public void endContextualActionMode() {

        actionMode.finish();
    }

    public void deleteDropboxItems(ArrayList <IconMaker> toBeDeleted) {

        deleteFromDropbox(toBeDeleted);
    }



    IconMaker selectedItem = new IconMaker();
    public void renameDropboxItem(IconMaker item) {

        selectedItem = item;
        openAlertDialogueRenameFile();
    }

    private void openAlertDialogueRenameFile () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New title");
        builder.setMessage(selectedItem.getName()+" will be renamed");

        final EditText fileNameInput = new EditText(this);

        fileNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(fileNameInput);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileNameInput.getWindowToken(), 0);

                MainActivity.this.rename(fileNameInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileNameInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        fileNameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void rename(String newName) {

        String name = null;

        if (!selectedItem.isDir()) {

            name = selectedItem
                    .getParentPath()
                    +newName
                    +"."+selectedItem.getExtension();
        } else {

            name = selectedItem
                    .getParentPath()
                    +newName;
        }

        if (newName.trim().length() != 0) {

            FileTask f = (FileTask) new FileTask(MainActivity.this,
                    new FileTask.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            if(result) {

                                refreshList(rootFolder.getPath());
                            }
                        }
                    }).execute(Common.METHOD_RENAME, selectedItem.getPath(), name);
        } else {

            showToast("Enter a valid name for file!");
        }
    }

    @Override
    public IconMaker getRootFolder() {
        return rootFolder;
    }

    @Override
    public void refreshRootFolder() {
        refreshList(rootFolder.getPath());
    }
}