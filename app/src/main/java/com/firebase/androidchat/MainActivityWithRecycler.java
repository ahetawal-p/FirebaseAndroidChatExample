package com.firebase.androidchat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.recycler.RVAdapter;

import java.util.Random;


public class MainActivityWithRecycler extends Activity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://torrid-inferno-8611.firebaseio.com";

    private String mUsername;
    private Firebase mFirebaseRef;
    private RecyclerView rv;
    private RVAdapter rvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_with_recycler);
        // Make sure we have a mUsername
        setupUsername();

        setTitle("Chatting as " + mUsername);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat");

        rv=(RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rvAdapter = new RVAdapter(mFirebaseRef);
        rv.setAdapter(rvAdapter);



        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        rvAdapter.cleanup();
    }


    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "JavaUser" + r.nextInt(100000);
            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

}
