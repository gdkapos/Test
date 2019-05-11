package com.example.test;

// We need to use this Handler package
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;

import java.util.HashMap;
import java.util.Map;

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
            //FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = database.getReference();
            //myRef.setValue("Hello, World!");

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            DocumentReference document = mFirestore.collection("cities").document("Athens");
            Map m = new HashMap<>();
            m.put("name","Athens");
            m.put("Speed","150");
            document.set(m);

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
