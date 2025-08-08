package com.scape2004.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private Button playButton;
    private Button settingsButton;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        
        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);
        
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = prefs.getString("serverUrl", "https://crucifixpwi.net");
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("SERVER_URL", serverUrl);
                startActivity(intent);
            }
        });
        
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}