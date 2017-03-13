package com.kirandama.spartanbox.parcels;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.client2.DropboxAPI;

/**
 * Created by sasank on 12/3/15.
 */
public class UserProfile implements Parcelable {

    String displayName;
    String email;
    String totalSpace;
    String usedSpace;
    String freeSpace;
    String country;
    String locale;
    //String accountType;

    public UserProfile() {

    }

    public UserProfile(DropboxAPI.Account account) {

        this.setDisplayName(account.displayName);
        this.setEmail(account.email);
        this.setCountry(account.country);
        this.setLocale(account.locale);
        this.setTotalSpace(String.valueOf(account.quota / 1024 / 1024));
        this.setUsedSpace(String.valueOf(account.quotaNormal / 1024 / 1024));
        this.setFreeSpace(String.valueOf((account.quota - (account.quotaNormal + account.quotaShared))/1024/1024));
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }


    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
    public String getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(String usedSpace) {
        this.usedSpace = usedSpace;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String getEmail() {

        return email;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(displayName);
        parcel.writeString(email);
        parcel.writeString(country);
        parcel.writeString(locale);
        parcel.writeString(totalSpace);
        parcel.writeString(usedSpace);
        parcel.writeString(freeSpace);
    }

    public static final Parcelable.Creator<UserProfile> CREATOR = new Creator<UserProfile>() {

        public UserProfile createFromParcel(Parcel source) {

            UserProfile userProfile = new UserProfile();

            userProfile.setDisplayName(source.readString());
            userProfile.setEmail(source.readString());
            userProfile.setCountry(source.readString());
            userProfile.setLocale(source.readString());
            userProfile.setTotalSpace(source.readString());
            userProfile.setUsedSpace(source.readString());
            userProfile.setFreeSpace(source.readString());

            return userProfile;
        }

        public UserProfile[] newArray(int size) {

            return new UserProfile[size];
        }
    };
}