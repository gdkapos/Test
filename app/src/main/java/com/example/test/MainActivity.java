package com.example.test;

// We need to use this Handler package
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();

    private long delayMillis = 1000;
    private int beepDuration = 500;
    private int possibilityThreshold = 80;
    private int directionDifferenceThreshold = 30;
    private int joinPossibilityThreshold = 50;

    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Get dieleusi info
            String country = "Greece";
            String city = "Athens";
            long junction = Long.valueOf(getString(R.string.junction));
            final int direction = 345;
            long time = Calendar.getInstance().getTimeInMillis();
            final int possibility = 85;

            if (possibility < possibilityThreshold) {
                return;
            }

            Dieleusi dieleusi = new Dieleusi(junction,
                    time,
                    possibility,
                    direction);

            // Store it to Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference countryCollection = firestore.collection(country);
            DocumentReference cityDocument = countryCollection.document(city);
            CollectionReference junctionCollection = cityDocument.collection(""+junction);
            junctionCollection.document(time + "_" + direction).set(dieleusi);

            // Ερώτημα για σύγκρουση
            Query query = junctionCollection.whereGreaterThan("time", time - 1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Map m = document.getData();
                            long dir = document.getLong("direction");

                            long dirDiff = direction - dir;
                            if (dirDiff < 0) {
                                dirDiff = -dirDiff;
                            }

                            if (dirDiff > directionDifferenceThreshold) {
                                long pos = document.getLong("possibility");
                                long pp = pos * possibility / 100;
                                if (pp >= joinPossibilityThreshold) {
                                    // Beep
                                    //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,beepDuration);
                                    toneGen1.startTone(ToneGenerator.TONE_DTMF_0,beepDuration);
                                }
                            }
                        }
                    }
                }
            });

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
