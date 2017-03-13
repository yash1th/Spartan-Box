package com.kirandama.spartanbox.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.Tasks.SearchTask;
import com.kirandama.spartanbox.parcels.IconMaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by harmit on 11/30/15.
 */
public class OptionsFragment extends Fragment{

    private View parentView;
    ArrayList <IconMaker> dropboxItems = new ArrayList<IconMaker>();
    private ListViewAdapter listViewAdapter;

    private EditText searchField;
    private static final int CONTEXTMENU_OPTION_VIEW = 1;
    private static final int CONTEXTMENU_OPTION_DELETE = 2;
    private static final int CONTEXTMENU_OPTION_SHARE = 3;
    private static boolean searchMode;


    private static final int CONTEXTMENU_OPTION_DOWNLOAD = 4;
    private static final int CONTEXTMENU_OPTION_RENAME = 7;
    private static final int CONTEXTMENU_OPTION_CANCEL = 8;

    ListViewFragmentProtocol listViewFragmentListener;
    public interface  ListViewFragmentProtocol {

        public void viewDropboxItem(IconMaker dropboxItem);
        public void deleteDropboxItems(ArrayList <IconMaker> toBeDeleted);
        public void shareFromDropbox(ArrayList <IconMaker> toBeShared);
        public void renameDropboxItem(IconMaker item);
        public void beginContextualActionMode(ArrayList <IconMaker> selectedItems);
        public void endContextualActionMode();
        public IconMaker getRootFolder();
        public void refreshRootFolder();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            listViewFragmentListener = (ListViewFragmentProtocol) context;
        } catch (ClassCastException exception) {

            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_list_view, container, false);

        ListView listView = (ListView)parentView.findViewById(R.id.list_view);
        registerForContextMenu(listView);

        searchField = (EditText) parentView.findViewById(R.id.searchField);
        addTextChangeListener();
        addClickListener();

        return parentView;
    }

    public boolean isSearchModeOn () {

        return searchMode;
    }
    public static void hideSoftKeyboard (Activity activity, View view) {

        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void addTextChangeListener() {

        final EditText searchEditText = (EditText) parentView.findViewById(R.id.searchField);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                hideSoftKeyboard(getActivity(), parentView);
                String searchQuery = searchEditText.getText().toString();

                if (searchQuery.length() > 0) {

                    searchMode = Boolean.TRUE;
                    SearchTask s = (SearchTask) new SearchTask(new SearchTask.AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                            ArrayList<IconMaker> result = new ArrayList<IconMaker>();

                            Log.d("search results", "reached here");
                            for (DropboxAPI.Entry e : output) {

                                IconMaker dropboxItem = new IconMaker(e);
                                result.add(dropboxItem);
                            }

                            reloadListView(result);
                        }
                    }, listViewFragmentListener.getRootFolder().getPath(), searchQuery).execute();
                } else {
                    searchMode = Boolean.FALSE;
                    listViewFragmentListener.refreshRootFolder();
                }
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_DONE) {

                    hideSoftKeyboard(getActivity(), parentView);

                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.dropboxItems.get(contextMenuInfo.position).getName());

        menu.add(Menu.NONE, CONTEXTMENU_OPTION_VIEW, 0, "View");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DELETE, 1, "Delete");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_SHARE, 2, "Email");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DOWNLOAD, 3, "Download");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_RENAME, 6, "Rename");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_CANCEL, 7, "Back");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {

            case CONTEXTMENU_OPTION_VIEW:

                listViewFragmentListener.viewDropboxItem(dropboxItems.get(contextMenuInfo.position));
                break;

            case CONTEXTMENU_OPTION_DELETE:

                ArrayList <IconMaker> itemsToDelete = new ArrayList<IconMaker>();
                itemsToDelete.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.deleteDropboxItems(itemsToDelete);
                break;

            case CONTEXTMENU_OPTION_SHARE:

                ArrayList <IconMaker> selectedItemToShare = new ArrayList<IconMaker>();
                selectedItemToShare.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.shareFromDropbox(selectedItemToShare);
                break;

            case CONTEXTMENU_OPTION_DOWNLOAD:

                break;

            case CONTEXTMENU_OPTION_RENAME:

                listViewFragmentListener.renameDropboxItem(this.dropboxItems.get(contextMenuInfo.position));
                break;

            case CONTEXTMENU_OPTION_CANCEL:

                break;
        }

        return true;
    }

    private void addClickListener() {

        ListView listView = (ListView) parentView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                listViewFragmentListener.viewDropboxItem(dropboxItems.get(pos));
            }
        });
    }

    private ArrayList<IconMaker> sortDropboxItems(ArrayList <IconMaker> unsortedArray) {

        ArrayList <IconMaker> sortedItems = new ArrayList<IconMaker>();

        sortedItems.addAll(getSortedFolders(unsortedArray));
        sortedItems.addAll(getSortedFiles(unsortedArray));

        return sortedItems;
    }

    private ArrayList<IconMaker> getSortedFolders(ArrayList <IconMaker> unsortedArray) {

        ArrayList <IconMaker> folders = new ArrayList<IconMaker>();

        for (IconMaker item : unsortedArray) {

            if (item.isDir()) {

                folders.add(item);
            }
        }

        Collections.sort(folders, new IconMaker());
        return folders;
    }

    private ArrayList<IconMaker> getSortedFiles(ArrayList <IconMaker> unsortedArray) {

        ArrayList <IconMaker> files = new ArrayList<IconMaker>();

        for (IconMaker item : unsortedArray) {

            if (!item.isDir()) {

                files.add(item);
            }
        }

        Collections.sort(files, new IconMaker());
        return files;
    }

    public void reloadListView (ArrayList<IconMaker> dropboxItems) {

        ArrayList sortedItems = sortDropboxItems(dropboxItems);
        this.dropboxItems.removeAll(this.dropboxItems);
        this.dropboxItems.addAll(sortedItems);

        reloadData(this.dropboxItems);
    }

    private void reloadData(ArrayList <IconMaker> dropboxItems) {

        listViewAdapter = new ListViewAdapter(getContext(),
                R.layout.dropbox_item, 0, dropboxItems);


        ListView listView = (ListView)parentView.findViewById(R.id.list_view);
        listView.setAdapter(listViewAdapter);
    }

    public ArrayList <IconMaker> selectedDropboxItems () {

        return listViewAdapter.selectedFiles;
    }

    public ArrayList <IconMaker> dropboxItems() {

        return this.dropboxItems;
    }

    private class ListViewAdapter extends ArrayAdapter<IconMaker> {

        ArrayList <IconMaker> selectedFiles = new ArrayList<IconMaker>();

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List<IconMaker> objects) {

            super(context, resource, textViewResourceId, objects);
        }

        public void setNewSelection(IconMaker dropboxItem) {

            selectedFiles.add(dropboxItem);
            notifyDataSetChanged();
        }

        public void removeSelection(IconMaker dropboxItem) {

            for (int i = 0; i < selectedFiles.size(); i++) {

                if (selectedFiles.get(i).getPath().equals(dropboxItem.getPath())) {

                    selectedFiles.remove(i);
                    notifyDataSetChanged();

                    break;
                }
            }
        }

        public ArrayList <IconMaker> getSelectedFiles () {

            return this.selectedFiles;
        }

        public boolean isSelected (IconMaker dropboxItem) {

            for (IconMaker dbItem : selectedFiles) {

                if (dropboxItem.getPath().equals(dbItem.getPath())) {

                    return true;
                }
            }

            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getActivity().getLayoutInflater().inflate(R.layout.dropbox_item, parent, false);
            }

            IconMaker dropboxItem = dropboxItems.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.icon);
            imageView.setImageResource(dropboxItem.getIcon());

            TextView title = (TextView)convertView.findViewById(R.id.title);
            title.setText(dropboxItem.getName());
            title.setTextColor(Color.parseColor("#666666"));

            TextView itemInfoToggle = (TextView)convertView.findViewById(R.id.modified);
            itemInfoToggle.setTextColor(Color.parseColor("#666666"));
            if(searchMode)
            {
                itemInfoToggle.setText("in ..." + dropboxItem.getParentPath());
                itemInfoToggle.setTypeface(null, Typeface.ITALIC);
            }
            else
            {
                itemInfoToggle.setText(dropboxItem.getModified());
                itemInfoToggle.setTypeface(null, Typeface.NORMAL);
            }

            TextView size = (TextView)convertView.findViewById(R.id.size);
            size.setText(dropboxItem.getSize());
            size.setTextColor(Color.parseColor("#666666"));
            if(searchMode) {

                size.setVisibility(View.INVISIBLE);
            } else {

                size.setVisibility(dropboxItem.isDir() ? View.INVISIBLE : View.VISIBLE);
            }

            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setChecked(listViewAdapter.isSelected(dropboxItems.get(position)));

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked) {

                        listViewAdapter.setNewSelection(dropboxItems.get(position));
                    } else {

                        listViewAdapter.removeSelection(dropboxItems.get(position));
                    }

                    if (listViewAdapter.getSelectedFiles().size() != 0) {

                        listViewFragmentListener.beginContextualActionMode(selectedFiles);
                    } else {

                        listViewFragmentListener.endContextualActionMode();
                    }
                }
            });

            return convertView;
        }
    }
}