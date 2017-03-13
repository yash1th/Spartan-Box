package com.kirandama.spartanbox.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.Tasks.ListTask;
import com.kirandama.spartanbox.utils.Common;
import com.kirandama.spartanbox.parcels.IconMaker;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends AppCompatActivity {

    IconMaker selectedItem = new IconMaker();
    String selectedPath = new String();
    ArrayList<IconMaker> dropboxItems = new ArrayList<IconMaker>();
    private ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_selection);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1565C0")));


        addClickListener();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            selectedItem = extras.getParcelable("parentFolder");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {

        refreshList(Common.rootDIR);
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
    public void onBackPressed() {

    }

    private void addClickListener() {

        final ListView listView = (ListView)findViewById(R.id.select_folder_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                deselectPreviousCheckbox();
                selectCheckbox(v, pos);
            }
        });
    }

    private void deselectPreviousCheckbox () {

        ListView listView = (ListView) findViewById(R.id.select_folder_list_view);
        int childCount = listView.getChildCount();
        for (int i = 0; i < childCount; ++i) {

            View view = listView.getChildAt(i);

            CheckBox checkBox = (CheckBox)view.findViewById(R.id.select_folder_checkBox);
            checkBox.setChecked(false);
        }
    }

    private void selectCheckbox (View view, int position) {

        CheckBox checkBox = (CheckBox)view.findViewById(R.id.select_folder_checkBox);
        checkBox.setChecked(true);

        selectedPath = dropboxItems.get(position).getPath();
    }

    public void didTouchCancelButton (View cancelButton) {

        Intent cancelIntent = new Intent();
        setResult(MainActivity.RESULT_CANCELED, cancelIntent);
        finish();
    }

    public void didTouchSelectButton (View cancelButton) {

        if (selectedPath.length() != 0) {

            Intent selectIntent = new Intent();
            selectIntent.putExtra("folderPath", selectedPath);
            setResult(MainActivity.RESULT_OK, selectIntent);
            finish();
        } else {

            Toast error = Toast.makeText(this, "No folder selected!", Toast.LENGTH_LONG);
            error.show();
        }
    }

    final ArrayList<IconMaker> result = new ArrayList<IconMaker>();

    public void refreshList(String path) {

        ListTask listTask = (ListTask) new ListTask(new ListTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                for(DropboxAPI.Entry e : output) {

                    IconMaker dropboxItem = new IconMaker(e);
                    if (e.isDir) {

                        refreshList(dropboxItem.getPath());
                        result.add(dropboxItem);
                    }
                }

                if (result.size() != 0) {

                    reloadListView(result);
                }
            }
        }).execute(path);
    }

    public void reloadListView (ArrayList<IconMaker> dropboxItems) {

        ArrayList <IconMaker> temp = new ArrayList<>(dropboxItems);
        if (selectedItem.isDir()) {

            /** remove self and children dirs from this.dropboxItems    **/
            temp = removeChilren(dropboxItems);
        } else {

            /** remove self dir this.dropboxItems   **/
            temp = removeParent(dropboxItems);
        }
        this.dropboxItems.removeAll(this.dropboxItems);
        this.dropboxItems.addAll(temp);

        reloadData(this.dropboxItems);
    }

    private ArrayList<IconMaker> removeChilren(ArrayList<IconMaker> items) {

        for (int i = 0; i < items.size(); i++) {

            IconMaker item = items.get(i);

            if (item.getPath().startsWith(selectedItem.getPath())) {

                items.remove(i);
            }
        }

        return items;
    }

    private ArrayList<IconMaker> removeParent(ArrayList<IconMaker> items) {

        for (int i = 0; i < items.size(); i++) {

            IconMaker item = items.get(i);

            if (selectedItem.getParentPath().equals(item.getPath()+"/")) {

                items.remove(i);
            }
        }

        return items;
    }

    private void reloadData(ArrayList <IconMaker> dropboxItems) {

        listViewAdapter = new ListViewAdapter(getApplicationContext(),
                R.layout.dropbox_folder_item, 0, this.dropboxItems);

        ListView listView = (ListView)findViewById(R.id.select_folder_list_view);
        listView.setAdapter(listViewAdapter);
    }

    private class ListViewAdapter extends ArrayAdapter<IconMaker> {

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List<IconMaker> objects) {

            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.dropbox_folder_item, parent, false);
            }

            IconMaker dropboxItem = dropboxItems.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.select_folder_icon);
            imageView.setImageResource(dropboxItem.getIcon());

            TextView title = (TextView)convertView.findViewById(R.id.select_folder_title);
            title.setText(dropboxItem.getPath());
            title.setTextColor(Color.parseColor("#424242"));

            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.select_folder_checkBox);

            final View finalConvertView = convertView;
            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    deselectPreviousCheckbox();
                    selectCheckbox(finalConvertView, position);
                }
            });

            return convertView;
        }
    }
}