package com.example.test;

// We need to use this Handler package
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();

    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,500);
            toneGen1.startTone(ToneGenerator.TONE_DTMF_0,500);
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");

            myRef.setValue("Hello, World!");
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }
}
