package com.kirandama.spartanbox.utils;

/**
 * Created by sasank on 10/13/15.
 */
public class AppSettings {

    private static AppSettings sharedSettings = null;

    private String accessToken;
    private boolean authenticated;

    private AppSettings() {

        accessToken = "";
        authenticated = false;
    }

    public static AppSettings getSharedSettings() {

        if (sharedSettings == null) {

            sharedSettings = new AppSettings();
        }

        return sharedSettings;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setAuthenticated (boolean authenticated) {

        this.authenticated = authenticated;
    }

    public boolean isAuthenticated () {

        return this.authenticated;
    }
}