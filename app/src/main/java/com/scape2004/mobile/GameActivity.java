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
    private boolean isResizeMode = false;
    
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
        
        setupWebView();
        
        // Setup fullscreen button
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });
        
        // Setup auto-scale button
        resizeButton.setText("AUTO FIT");
        resizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoScaleCanvas();
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
                
                // Auto-scale on first load
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoScaleCanvas();
                    }
                }, 500);
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
    
    private void autoScaleCanvas() {
        // Get screen dimensions
        final int screenWidth = webView.getWidth();
        final int screenHeight = webView.getHeight();
        
        String js = "javascript:(function() {" +
            "document.body.style.margin = '0';" +
            "document.body.style.padding = '0';" +
            "document.body.style.overflow = 'hidden';" +
            "document.body.style.background = '#000';" +
            "" +
            "var canvas = document.querySelector('canvas');" +
            "if (!canvas) return;" +
            "" +
            "// Get canvas dimensions" +
            "var canvasWidth = canvas.width || 765;" +
            "var canvasHeight = canvas.height || 503;" +
            "" +
            "// Calculate scale to fit screen" +
            "var scaleX = " + screenWidth + " / canvasWidth;" +
            "var scaleY = " + screenHeight + " / canvasHeight;" +
            "var scale = Math.min(scaleX, scaleY);" +
            "" +
            "// Apply styles to canvas" +
            "canvas.style.position = 'fixed';" +
            "canvas.style.left = '50%';" +
            "canvas.style.top = '50%';" +
            "canvas.style.transform = 'translate(-50%, -50%) scale(' + scale + ')';" +
            "canvas.style.transformOrigin = 'center center';" +
            "" +
            "// Alternative approach using viewport" +
            "var viewport = document.querySelector('meta[name=viewport]');" +
            "if (!viewport) {" +
            "  viewport = document.createElement('meta');" +
            "  viewport.name = 'viewport';" +
            "  document.head.appendChild(viewport);" +
            "}" +
            "viewport.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';" +
            "" +
            "console.log('Auto-scaled to: ' + scale + 'x');" +
            "})();";
        
        webView.loadUrl(js);
        
        // Flash the button to show it worked
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