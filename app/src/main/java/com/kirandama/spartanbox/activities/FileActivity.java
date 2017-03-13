package com.kirandama.spartanbox.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kirandama.spartanbox.R;
import com.kirandama.spartanbox.parcels.IconMaker;

public class FileActivity extends AppCompatActivity {

    IconMaker dropboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4D8FCC")));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            dropboxItem = extras.getParcelable("dropboxItem");
        }

        setTitle(dropboxItem.getName());
    }

    @Override
    protected void onStart() {

        super.onStart();

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

            }
        });

        webView.loadUrl(dropboxItem.getShareLink());
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
}
