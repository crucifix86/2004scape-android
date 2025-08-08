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
        
        // Setup resize button
        resizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleResizeMode();
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
                
                // Load saved canvas position/size
                float savedScale = prefs.getFloat("canvasScale", 1.0f);
                float savedX = prefs.getFloat("canvasX", 0);
                float savedY = prefs.getFloat("canvasY", 0);
                
                // Apply saved scale
                view.setInitialScale((int)(savedScale * 100));
                
                // Inject JavaScript to set up canvas
                String js = "javascript:(function() {" +
                    "document.body.style.margin = '0';" +
                    "document.body.style.padding = '0';" +
                    "document.body.style.overflow = 'hidden';" +
                    "document.body.style.background = '#000';" +
                    "var canvas = document.querySelector('canvas');" +
                    "if (canvas) {" +
                    "  canvas.style.position = 'absolute';" +
                    "  canvas.style.left = '" + savedX + "px';" +
                    "  canvas.style.top = '" + savedY + "px';" +
                    "  canvas.style.transformOrigin = 'top left';" +
                    "  canvas.style.transform = 'scale(' + " + savedScale + ")';" +
                    "}" +
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
    
    private void toggleResizeMode() {
        isResizeMode = !isResizeMode;
        if (isResizeMode) {
            resizeButton.setText("DONE");
            resizeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00FF00));
            
            // Enable resize mode
            String js = "javascript:(function() {" +
                "var canvas = document.querySelector('canvas');" +
                "if (!canvas) return;" +
                "canvas.style.border = '2px dashed #ffd700';" +
                "canvas.style.cursor = 'move';" +
                "" +
                "var isDragging = false;" +
                "var isPinching = false;" +
                "var startX = 0, startY = 0;" +
                "var currentX = parseFloat(canvas.style.left) || 0;" +
                "var currentY = parseFloat(canvas.style.top) || 0;" +
                "var currentScale = 1;" +
                "var transform = canvas.style.transform.match(/scale\\(([^)]+)\\)/);" +
                "if (transform) currentScale = parseFloat(transform[1]);" +
                "" +
                "canvas.addEventListener('touchstart', function(e) {" +
                "  if (e.touches.length === 1) {" +
                "    isDragging = true;" +
                "    startX = e.touches[0].clientX - currentX;" +
                "    startY = e.touches[0].clientY - currentY;" +
                "  } else if (e.touches.length === 2) {" +
                "    isPinching = true;" +
                "    var dist = Math.hypot(" +
                "      e.touches[0].clientX - e.touches[1].clientX," +
                "      e.touches[0].clientY - e.touches[1].clientY" +
                "    );" +
                "    canvas.dataset.startDist = dist;" +
                "    canvas.dataset.startScale = currentScale;" +
                "  }" +
                "  e.preventDefault();" +
                "});" +
                "" +
                "canvas.addEventListener('touchmove', function(e) {" +
                "  if (isDragging && e.touches.length === 1) {" +
                "    currentX = e.touches[0].clientX - startX;" +
                "    currentY = e.touches[0].clientY - startY;" +
                "    canvas.style.left = currentX + 'px';" +
                "    canvas.style.top = currentY + 'px';" +
                "  } else if (isPinching && e.touches.length === 2) {" +
                "    var dist = Math.hypot(" +
                "      e.touches[0].clientX - e.touches[1].clientX," +
                "      e.touches[0].clientY - e.touches[1].clientY" +
                "    );" +
                "    var scale = (dist / parseFloat(canvas.dataset.startDist)) * parseFloat(canvas.dataset.startScale);" +
                "    currentScale = Math.max(0.5, Math.min(3, scale));" +
                "    canvas.style.transform = 'scale(' + currentScale + ')';" +
                "  }" +
                "  e.preventDefault();" +
                "});" +
                "" +
                "canvas.addEventListener('touchend', function(e) {" +
                "  isDragging = false;" +
                "  isPinching = false;" +
                "  window.Android.saveCanvasPosition(currentX, currentY, currentScale);" +
                "});" +
                "})();";
            webView.loadUrl(js);
        } else {
            resizeButton.setText("RESIZE");
            resizeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x80000000));
            
            // Disable resize mode
            String js = "javascript:(function() {" +
                "var canvas = document.querySelector('canvas');" +
                "if (!canvas) return;" +
                "canvas.style.border = 'none';" +
                "canvas.style.cursor = 'default';" +
                "var clone = canvas.cloneNode(true);" +
                "canvas.parentNode.replaceChild(clone, canvas);" +
                "})();";
            webView.loadUrl(js);
        }
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