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
            
            // Enable resize mode on actual canvas
            String js = "javascript:(function() {" +
                "var canvas = document.querySelector('canvas');" +
                "if (!canvas) return;" +
                "" +
                "// Add visual indicators" +
                "canvas.style.outline = '2px dashed #ffd700';" +
                "canvas.style.outlineOffset = '-2px';" +
                "" +
                "// Create resize overlay for touch handling" +
                "var overlay = document.createElement('div');" +
                "overlay.id = 'resizeOverlay';" +
                "overlay.style.position = 'fixed';" +
                "overlay.style.top = '0';" +
                "overlay.style.left = '0';" +
                "overlay.style.width = '100%';" +
                "overlay.style.height = '100%';" +
                "overlay.style.zIndex = '9999';" +
                "overlay.style.backgroundColor = 'transparent';" +
                "" +
                "// Add instructions" +
                "var instructions = document.createElement('div');" +
                "instructions.style.position = 'fixed';" +
                "instructions.style.top = '60px';" +
                "instructions.style.left = '0';" +
                "instructions.style.right = '0';" +
                "instructions.style.textAlign = 'center';" +
                "instructions.style.color = '#ffd700';" +
                "instructions.style.fontSize = '16px';" +
                "instructions.style.backgroundColor = 'rgba(0,0,0,0.8)';" +
                "instructions.style.padding = '10px';" +
                "instructions.style.zIndex = '10000';" +
                "instructions.innerHTML = 'Drag to move • Pinch to resize';" +
                "document.body.appendChild(instructions);" +
                "" +
                "document.body.appendChild(overlay);" +
                "" +
                "// Get current position and scale" +
                "var currentX = parseFloat(canvas.style.left) || 0;" +
                "var currentY = parseFloat(canvas.style.top) || 0;" +
                "var currentScale = 1;" +
                "var transform = canvas.style.transform.match(/scale\\(([^)]+)\\)/);" +
                "if (transform) currentScale = parseFloat(transform[1]);" +
                "" +
                "var isDragging = false;" +
                "var startX = 0, startY = 0;" +
                "var initialDist = 0;" +
                "var initialScale = currentScale;" +
                "" +
                "overlay.addEventListener('touchstart', function(e) {" +
                "  if (e.touches.length === 1) {" +
                "    isDragging = true;" +
                "    startX = e.touches[0].clientX - currentX;" +
                "    startY = e.touches[0].clientY - currentY;" +
                "  } else if (e.touches.length === 2) {" +
                "    isDragging = false;" +
                "    initialDist = Math.hypot(" +
                "      e.touches[0].clientX - e.touches[1].clientX," +
                "      e.touches[0].clientY - e.touches[1].clientY" +
                "    );" +
                "    initialScale = currentScale;" +
                "  }" +
                "  e.preventDefault();" +
                "  e.stopPropagation();" +
                "}, {passive: false});" +
                "" +
                "overlay.addEventListener('touchmove', function(e) {" +
                "  if (isDragging && e.touches.length === 1) {" +
                "    currentX = e.touches[0].clientX - startX;" +
                "    currentY = e.touches[0].clientY - startY;" +
                "    canvas.style.left = currentX + 'px';" +
                "    canvas.style.top = currentY + 'px';" +
                "  } else if (e.touches.length === 2 && initialDist > 0) {" +
                "    var dist = Math.hypot(" +
                "      e.touches[0].clientX - e.touches[1].clientX," +
                "      e.touches[0].clientY - e.touches[1].clientY" +
                "    );" +
                "    currentScale = Math.max(0.5, Math.min(3, initialScale * (dist / initialDist)));" +
                "    canvas.style.transform = 'scale(' + currentScale + ')';" +
                "    canvas.style.transformOrigin = 'top left';" +
                "  }" +
                "  e.preventDefault();" +
                "  e.stopPropagation();" +
                "}, {passive: false});" +
                "" +
                "overlay.addEventListener('touchend', function(e) {" +
                "  isDragging = false;" +
                "  initialDist = 0;" +
                "  window.Android.saveCanvasPosition(currentX, currentY, currentScale);" +
                "  e.preventDefault();" +
                "  e.stopPropagation();" +
                "}, {passive: false});" +
                "" +
                "window.resizeInstructions = instructions;" +
                "})();";
            webView.loadUrl(js);
        } else {
            resizeButton.setText("RESIZE");
            resizeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x80000000));
            
            // Disable resize mode
            String js = "javascript:(function() {" +
                "var canvas = document.querySelector('canvas');" +
                "if (canvas) {" +
                "  canvas.style.outline = 'none';" +
                "}" +
                "var overlay = document.getElementById('resizeOverlay');" +
                "if (overlay) overlay.remove();" +
                "if (window.resizeInstructions) {" +
                "  window.resizeInstructions.remove();" +
                "  delete window.resizeInstructions;" +
                "}" +
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