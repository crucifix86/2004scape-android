package com.scape2004.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private EditText serverUrlInput;
    private Button playButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        serverUrlInput = findViewById(R.id.serverUrlInput);
        playButton = findViewById(R.id.playButton);
        
        // Default server URL
        serverUrlInput.setText("https://crucifixpwi.net");
        
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = serverUrlInput.getText().toString().trim();
                if (!serverUrl.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("SERVER_URL", serverUrl);
                    startActivity(intent);
                }
            }
        });
    }
}