package com.example.orientationrace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantsActivity extends AppCompatActivity {

    // Participants dataset:
    private static final String TAG = "TAGListOfParticipants, ParticipantActivity";
    public ParticipantsDataset participantsDataset = new ParticipantsDataset();
    private RecyclerView recyclerView;

    // For downloading the Madrid Garden File
    public static final String LOADWEBTAG = "LOAD_WEB_TAG"; // to easily filter logs
    private String threadAndClass; // to clearly identify logs
    private static final String URL_GARDENS = "https://short.upm.es/3qnno";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private TextView text;
    ExecutorService es;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Build the logTag with the Thread and Class names:
        threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        // Create the get Intent object
        Intent intent = getIntent();
        // Receive the username
        String username = intent.getStringExtra("username");

        // Create the current user as a new participant and add him to the dataset
        Participant currentParticipant = new Participant(username, (long)0);
        participantsDataset.addParticipant(currentParticipant);

        text = findViewById(R.id.HTTPTextView);

        initRecyclerView();

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
        readJSON(text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(ParticipantsActivity.this, RaceCompassActivity.class);
                startActivity(intent);
                finish(); // Optional: finish the current activity
            }
        }, 35000); // 10000 milliseconds = 10 seconds
    }

    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            String titleArray[] = new String[204];
            super.handleMessage(msg);
            Log.d(LOADWEBTAG, threadAndClass + ": message received from background thread");
            if((string_result = msg.getData().getString("text")) != null) {
                //text.setText(string_result);
                try {
                    jsonObject = new JSONObject(string_result);
                    JSONArray graph = jsonObject.getJSONArray("@graph");
                    for (int i = 0; i < graph.length(); i++) {
                        JSONObject graphItem = graph.getJSONObject(i);
                        if (graphItem.has("title")) {
                            String titleValue = graphItem.getString("title");
                            titleArray[i] = titleValue;
                        }
                    }
                text.setText(Arrays.toString(titleArray));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    public void readJSON(View view) {
        text.setText("Loading " + URL_GARDENS + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_GARDENS);
        es.execute(loadURLContents);
    }

    private void initRecyclerView() {
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter(participantsDataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}