package com.imi.dolphin.bella;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    /**
     * Properties to enable Record Audio Permission
     * <p>
     * the permissionRequest property is used to accommodate requests from the web view that will be granted access later
     */
    private static final int RECORD_AUDIO_REQUEST_CODE = 123;
    private PermissionRequest permissionRequest;

    /**
     * Initial Component Web View and Progress Bar
     */
    private WebView webview;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        loadLiveChat();
    }

    /**
     * Configure Web View
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void loadLiveChat() {
        webview.loadUrl("<Server URL>");

        /* Configure WebView To set Enable Javascript True
         * And To Activate DomStorage True For LocalStorage
         */
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        enableVoiceToText();
    }

    /**
     * Binding Item
     */
    private void init() {
        webview = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Catch permission request from web view
     */
    private void enableVoiceToText() {
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (request != null) {
                    permissionRequest = request;
                    askPermission();
                }
            }
        });
    }

    /**
     * ask permission for Record Audio
     */
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                permissionRequest.grant(permissionRequest.getResources());
            }
        }
    }

    /**
     * Catch permission from user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    permissionRequest.grant(permissionRequest.getResources());
                }
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}