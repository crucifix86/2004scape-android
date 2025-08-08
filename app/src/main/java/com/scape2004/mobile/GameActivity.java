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
import android.webkit.JavascriptInterface;

public class GameActivity extends AppCompatActivity {
    
    private WebView webView;
    private ProgressBar progressBar;
    private Button fullscreenButton;
    private Button resizeButton;
    private String serverUrl;
    private SharedPreferences prefs;
    private boolean isFullscreen = true;
    private int currentMode = 0; // 0=fit, 1=fill, 2=zoom
    
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
        resizeButton = findViewById(R.id.resizeButton);
        
        prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        
        // Load saved display mode
        currentMode = prefs.getInt("displayMode", 0);
        String[] modes = {"FIT", "FILL", "150%"};
        resizeButton.setText(modes[currentMode]);
        
        setupWebView();
        
        // Setup fullscreen button
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });
        
        // Setup display mode button
        resizeButton.setText("FIT");
        resizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleDisplayMode();
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
        
        // Add JavaScript interface
        webView.addJavascriptInterface(new WebInterface(), "Android");
        
        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);
        
        // Enable database
        webSettings.setDatabaseEnabled(true);
        
        // App cache is deprecated in newer Android versions
        
        // Set cache mode
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable zooming
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        
        // Enable viewport
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        // Set initial scale
        webSettings.setMinimumFontSize(1);
        webSettings.setTextZoom(100);
        
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
                
                // Apply initial display mode
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        applyDisplayMode();
                    }
                }, 1000);
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
    
    private void cycleDisplayMode() {
        currentMode = (currentMode + 1) % 3;
        String[] modes = {"FIT", "FILL", "150%"};
        resizeButton.setText(modes[currentMode]);
        applyDisplayMode();
    }
    
    private void applyDisplayMode() {
        int scale = 100;
        
        switch (currentMode) {
            case 0: // FIT mode
                scale = 100;
                break;
            case 1: // FILL mode
                scale = 130;
                break;
            case 2: // 150% zoom
                scale = 150;
                break;
        }
        
        // Apply WebView scale
        webView.setInitialScale(scale);
        
        // Save preference
        prefs.edit().putInt("displayMode", currentMode).apply();
        
        // Flash button
        resizeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00FF00));
        resizeButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                resizeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x80000000));
            }
        }, 300);
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
    
    public class WebInterface {
        @JavascriptInterface
        public void saveCanvasPosition(float x, float y, float scale) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("canvasX", x);
            editor.putFloat("canvasY", y);
            editor.putFloat("canvasScale", scale);
            editor.apply();
        }
    }
}