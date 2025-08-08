package com.scape2004.mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    private EditText serverUrlInput;
    private SeekBar zoomSeekBar;
    private TextView zoomValueText;
    private Button saveButton;
    private Button resetButton;
    private RadioGroup aspectRatioGroup;
    
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        
        // Initialize views
        serverUrlInput = findViewById(R.id.serverUrlInput);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);
        zoomValueText = findViewById(R.id.zoomValueText);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        aspectRatioGroup = findViewById(R.id.aspectRatioGroup);
        
        // Load saved settings
        loadSettings();
        
        // Setup zoom seekbar
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int zoomValue = progress + 50; // 50-150%
                zoomValueText.setText(zoomValue + "%");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
        
        // Reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSettings();
            }
        });
    }
    
    private void loadSettings() {
        String serverUrl = prefs.getString("serverUrl", "https://crucifixpwi.net");
        int zoomLevel = prefs.getInt("zoomLevel", 100);
        String aspectRatio = prefs.getString("aspectRatio", "fit");
        
        serverUrlInput.setText(serverUrl);
        zoomSeekBar.setProgress(zoomLevel - 50);
        zoomValueText.setText(zoomLevel + "%");
        
        // Set aspect ratio selection
        if (aspectRatio.equals("fit")) {
            aspectRatioGroup.check(R.id.aspectFit);
        } else if (aspectRatio.equals("fill")) {
            aspectRatioGroup.check(R.id.aspectFill);
        } else if (aspectRatio.equals("stretch")) {
            aspectRatioGroup.check(R.id.aspectStretch);
        }
    }
    
    private void saveSettings() {
        String serverUrl = serverUrlInput.getText().toString().trim();
        if (serverUrl.isEmpty()) {
            Toast.makeText(this, "Server URL cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int zoomLevel = zoomSeekBar.getProgress() + 50;
        
        // Get selected aspect ratio
        String aspectRatio = "fit";
        int selectedId = aspectRatioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.aspectFit) {
            aspectRatio = "fit";
        } else if (selectedId == R.id.aspectFill) {
            aspectRatio = "fill";
        } else if (selectedId == R.id.aspectStretch) {
            aspectRatio = "stretch";
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("serverUrl", serverUrl);
        editor.putInt("zoomLevel", zoomLevel);
        editor.putString("aspectRatio", aspectRatio);
        editor.apply();
        
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void resetSettings() {
        serverUrlInput.setText("https://crucifixpwi.net");
        zoomSeekBar.setProgress(50); // 100%
        zoomValueText.setText("100%");
        
        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
    }
}