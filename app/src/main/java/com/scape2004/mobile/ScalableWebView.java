package com.scape2004.mobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ScalableWebView extends WebView {
    private float mScale = 1.0f;
    private float mMaxScale = 2.0f;
    private float mMinScale = 0.5f;
    
    public ScalableWebView(Context context) {
        super(context);
        init();
    }
    
    public ScalableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ScalableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        setInitialScale(100);
    }
    
    public void setScale(float scale) {
        mScale = Math.max(mMinScale, Math.min(scale, mMaxScale));
        
        // Apply scale to the entire WebView
        setScaleX(mScale);
        setScaleY(mScale);
        
        // Center the scaled content
        setPivotX(getWidth() / 2f);
        setPivotY(getHeight() / 2f);
        
        // Force layout update
        requestLayout();
    }
    
    public float getScale() {
        return mScale;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Scale touch coordinates
        event.setLocation(event.getX() / mScale, event.getY() / mScale);
        return super.onTouchEvent(event);
    }
}