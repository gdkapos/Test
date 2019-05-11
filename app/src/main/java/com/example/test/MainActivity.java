package com.example.test;

// We need to use this Handler package
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();

    private long delayMillis = 2000;
    private int beepDuration = 500;

    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Beep
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,beepDuration);
            toneGen1.startTone(ToneGenerator.TONE_DTMF_0,beepDuration);

            // Get dieleusi info
            String city = "Athens";
            long junction = 1;
            long time = Calendar.getInstance().getTimeInMillis();
            int possibility = 85;
            int direction = 345;
            Dieleusi dieleusi = new Dieleusi(junction,
                    time,
                    possibility,
                    direction);

            // Store it to Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference cityCollection = firestore.collection(city);
            DocumentReference junctionDocument = cityCollection.document(""+junction);
            CollectionReference dieleusiCollection = junctionDocument.collection(time + "_" + direction);
            dieleusiCollection.document().set(dieleusi);

            // Καθυστέρηση
            handler.postDelayed(this, delayMillis);
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
