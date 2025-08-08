package com.scape2004.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private EditText serverUrlInput;
    private Button playButton;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        
        serverUrlInput = findViewById(R.id.serverUrlInput);
        playButton = findViewById(R.id.playButton);
        
        // Load saved server URL
        String savedUrl = prefs.getString("serverUrl", "https://crucifixpwi.net");
        serverUrlInput.setText(savedUrl);
        
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = serverUrlInput.getText().toString().trim();
                if (!serverUrl.isEmpty()) {
                    // Save the URL
                    prefs.edit().putString("serverUrl", serverUrl).apply();
                    
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("SERVER_URL", serverUrl);
                    startActivity(intent);
                }
            }
        });
    }
}