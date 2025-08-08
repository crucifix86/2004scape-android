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
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class GameActivity extends AppCompatActivity {
    
    private WebView webView;
    private ProgressBar progressBar;
    private Button fullscreenButton;
    private String serverUrl;
    private SharedPreferences prefs;
    private boolean isFullscreen = true;
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen mode with immersive sticky
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Hide system UI for true full screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_game);
        
        // Get server URL from intent
        serverUrl = getIntent().getStringExtra("SERVER_URL");
        if (serverUrl == null) {
            serverUrl = "https://crucifixpwi.net";
        }
        
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        fullscreenButton = findViewById(R.id.fullscreenButton);
        
        prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        
        setupWebView();
        
        // Setup fullscreen button
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });
        
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
        
        // App cache is deprecated in newer Android versions
        
        // Set cache mode
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Initial zoom settings (will be overridden by user settings)
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        
        // Use viewport settings from the web page
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setUseWideViewPort(false);
        
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
                
                // Apply zoom settings
                int zoomLevel = prefs.getInt("zoomLevel", 100);
                view.setInitialScale(zoomLevel);
                
                // Inject JavaScript to handle canvas display
                String js = "javascript:(function() {" +
                    "var meta = document.querySelector('meta[name=viewport]');" +
                    "if (!meta) {" +
                    "  meta = document.createElement('meta');" +
                    "  meta.name = 'viewport';" +
                    "  document.head.appendChild(meta);" +
                    "}" +
                    "meta.content = 'width=device-width, initial-scale=" + (zoomLevel/100.0) + ", user-scalable=yes';" +
                    "document.body.style.margin = '0';" +
                    "document.body.style.padding = '0';" +
                    "document.body.style.overflow = 'hidden';" +
                    "})();";
                view.loadUrl(js);
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
        
        // Enable pinch zoom
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
    }
    
    private void toggleFullscreen() {
        View decorView = getWindow().getDecorView();
        if (isFullscreen) {
            // Exit fullscreen
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            fullscreenButton.setText("FULLSCREEN");
        } else {
            // Enter fullscreen
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
            fullscreenButton.setText("EXIT FULL");
        }
        isFullscreen = !isFullscreen;
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