package com.scape2004.mobile;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    
    private WebView webView;
    private ProgressBar progressBar;
    private String serverUrl;
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_game);
        
        // Get server URL from intent
        serverUrl = getIntent().getStringExtra("SERVER_URL");
        if (serverUrl == null) {
            serverUrl = "http://154.38.175.246";
        }
        
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        
        setupWebView();
        
        // Load the game
        webView.loadUrl(serverUrl + "/rs2.cgi");
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);
        
        // Enable database
        webSettings.setDatabaseEnabled(true);
        
        // Enable app cache
        webSettings.setAppCacheEnabled(true);
        
        // Set cache mode
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable zooming
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        // Fit content to screen
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        // Allow file access
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        
        // Set user agent to identify as mobile
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + " 2004scapeAndroid/1.0");
        
        // Handle page loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        
        // Handle JavaScript dialogs
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
        
        // Keep screen on while playing
        webView.setKeepScreenOn(true);
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }
    
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}