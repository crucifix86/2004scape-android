package com.scape2004.mobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ScalableWebView extends WebView {
    private float mScale = 1.0f;
    private float mMaxScale = 2.0f;
    private float mMinScale = 0.5f;
    private float mPivotX = 0;
    private float mPivotY = 0;
    
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
        
        // Center the scaled content
        mPivotX = getWidth() / 2f;
        mPivotY = getHeight() / 2f;
        setPivotX(mPivotX);
        setPivotY(mPivotY);
        
        // Apply scale to the entire WebView
        setScaleX(mScale);
        setScaleY(mScale);
        
        // Force layout update
        requestLayout();
    }
    
    public float getScale() {
        return mScale;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Create a copy of the event
        MotionEvent adjustedEvent = MotionEvent.obtain(event);
        
        // Calculate the offset due to scaling
        float offsetX = (getWidth() - getWidth() * mScale) / 2f;
        float offsetY = (getHeight() - getHeight() * mScale) / 2f;
        
        // Adjust touch coordinates for scale and center pivot
        float x = event.getX();
        float y = event.getY();
        
        // Convert to unscaled coordinates
        float adjustedX = (x - mPivotX) / mScale + mPivotX;
        float adjustedY = (y - mPivotY) / mScale + mPivotY;
        
        adjustedEvent.setLocation(adjustedX, adjustedY);
        
        // Pass adjusted event to WebView
        boolean result = super.onTouchEvent(adjustedEvent);
        
        // Clean up
        adjustedEvent.recycle();
        
        return result;
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Handle touch events at dispatch level for better accuracy
        return onTouchEvent(event);
    }
}