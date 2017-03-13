package com.kirandama.spartanbox.parcels;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.client2.DropboxAPI;
import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.utils.DateFormat;

import java.util.Comparator;

/**
 * Created by kiran on 11/28/15.
 */
public class IconMaker implements Parcelable, Comparator<IconMaker> {

    String name;
    String path;
    String shareLink;
    String isDir;
    String modified;
    String parentPath;
    String size;
    String extension;


    public IconMaker() {

    }

    public IconMaker(DropboxAPI.Entry entry) {

        this.setDir(entry.isDir);
        this.setName(entry.fileName());
        this.setPath(entry.path);
        this.setShareLink("");
        this.setModified(entry.modified);
        this.setParentPath(entry.parentPath());
        this.setSize(entry.size);
    }

    public void setName(String name) {

        this.name = name;

        try {

            if(!this.isDir()) {

                int dotIndex = name.lastIndexOf(".");
                String fileExt = name.substring(dotIndex+1, name.length());

                this.setExtension(fileExt);
            }
        } catch (Exception exc) {

            exc.printStackTrace();
        }
    }

    public String getName() {

        return this.name;
    }

    public void setExtension(String extension) {

        this.extension = extension;
    }

    public String getExtension() {

        return this.extension;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public String getPath() {

        return this.path;
    }

    public void setShareLink(String shareLink) {

        this.shareLink = shareLink;
    }

    public String getShareLink() {

        return this.shareLink;
    }

    public void setDir(boolean isDir) {

        this.isDir = String.valueOf(isDir);
    }

    public boolean isDir() {

        return Boolean.valueOf(this.isDir);
    }

    public void setModified(String modified) {

        this.modified = modified;
    }

    public String getModified() {

        return DateFormat.convertDate(this.modified);
    }

    public String getParentPath() {

        return this.parentPath;
    }

    public void setParentPath(String parentPath) {

        this.parentPath = parentPath;
    }

    public void setSize(String size) {

        this.size = size;
    }

    public String getSize() {

        return this.size;
    }

    public int getIcon() {

        try {

            if (!this.isDir()) {

                int dotIndex = this.getPath().lastIndexOf(".");
                String fileExt = this.getPath().substring(dotIndex, this.getPath().length());

                if (fileExt.toLowerCase().contains("doc".toLowerCase())
                        || fileExt.toLowerCase().contains("txt".toLowerCase())
                        || fileExt.toLowerCase().contains("rtf".toLowerCase())) {

                    return R.drawable.ic_file;
                } else if (fileExt.toLowerCase().contains("xls".toLowerCase())) {

                    return R.drawable.ic_xls;
                } else if (fileExt.toLowerCase().contains("pdf".toLowerCase())) {

                    return R.drawable.ic_pdf;
                } else if (fileExt.toLowerCase().contains("ppt".toLowerCase())) {

                    return R.drawable.ic_ppt;
                } else if (fileExt.toLowerCase().contains("png".toLowerCase())
                        || fileExt.toLowerCase().contains("jpg".toLowerCase())
                        || fileExt.toLowerCase().contains("jpeg".toLowerCase())) {

                    return R.drawable.ic_photo;
                } else if (fileExt.toLowerCase().contains("mp3".toLowerCase())
                        || fileExt.toLowerCase().contains("wav".toLowerCase())) {

                    return R.drawable.ic_mp3;
                } else if (fileExt.toLowerCase().contains("avi".toLowerCase())
                        || fileExt.toLowerCase().contains("flv".toLowerCase())
                        || fileExt.toLowerCase().contains("mp4".toLowerCase())
                        || fileExt.toLowerCase().contains("mkv".toLowerCase())
                        || fileExt.toLowerCase().contains("wmv".toLowerCase())
                        || fileExt.toLowerCase().contains("3gp".toLowerCase())) {

                    return R.drawable.ic_mp3;
                } else {

                    return R.drawable.ic_file;
                }
            } else {

                return R.drawable.ic_folder;
            }
        } catch (Exception exc) {

            return R.drawable.ic_folder;
        }
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(isDir);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(shareLink);
        parcel.writeString(modified);
        parcel.writeString(parentPath);
        parcel.writeString(size);
        parcel.writeString(extension);
    }

    public static final Parcelable.Creator<IconMaker> CREATOR = new Creator<IconMaker>() {

        public IconMaker createFromParcel(Parcel source) {

            IconMaker dropboxItem = new IconMaker();

            dropboxItem.setDir(Boolean.valueOf(source.readString()));
            dropboxItem.setName(source.readString());
            dropboxItem.setPath(source.readString());
            dropboxItem.setShareLink(source.readString());
            dropboxItem.setModified(source.readString());
            dropboxItem.setParentPath(source.readString());
            dropboxItem.setSize(source.readString());
            dropboxItem.setExtension(source.readString());

            return dropboxItem;
        }

        public IconMaker[] newArray(int size) {

            return new IconMaker[size];
        }
    };

    @Override
    public int compare(IconMaker lhs, IconMaker rhs) {

        return lhs.getName().compareTo(rhs.getName());
    }
}