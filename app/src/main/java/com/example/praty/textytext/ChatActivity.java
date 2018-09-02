package com.example.praty.textytext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private String mDisplayName;
    private EditText mMessage;
    private ImageView mSendButton;
    private RecyclerView mRecycler;
    private DatabaseReference mDatabaseReference;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessage=(EditText) findViewById(R.id.messageInput);
        mSendButton=(ImageView) findViewById(R.id.sendButton);
        setUpDisplayName();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference(); //initializing the DatabaseReference object
        mRecycler=(RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecycler.setHasFixedSize(true);


        LinearLayoutManager layoutManager= new LinearLayoutManager(ChatActivity.this);
        mRecycler.setLayoutManager(layoutManager);

        mMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }

    //method to retrieve the displayName stored using SharedPreferences
    private void setUpDisplayName() {
        Intent mIntent = getIntent();
        String option = mIntent.getStringExtra("Options");

        if (option.equals("normal")) {
            SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFSSS, 0);
            mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEYSS, null);
            Log.d("Google", "Shared prefs(normal): DisplayName:" + mDisplayName);

        } else if (option.equals("google")) {
            SharedPreferences prefs = getSharedPreferences(MainActivity.CHAT_PREFS, 0);
            mDisplayName = prefs.getString(MainActivity.DISPLAY_NAME_KEY, null);
            Log.d("Google", "Shared prefs(google): DisplayName:" + mDisplayName);

        }
        if (mDisplayName == null)
            mDisplayName = "Anonymous";
    }


    //method to send the message to the realtime database of the firebase
    private void sendMessage() {

        String input= mMessage.getText().toString();
        if(!input.equals("")){
            Log.d("Google","I sent something");
            InstantMessage mObject= new InstantMessage(mDisplayName, input);
            mDatabaseReference.child("messages").push().setValue(mObject); //here's how it's done
            mMessage.setText("");
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater= getMenuInflater();
//        menuInflater.inflate(R.menu.actionbar_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_bar_about:
//
//                return true;
//            case R.id.action_bar_signout:
//
//                return true;
//
//                default:
//                    return super.onOptionsItemSelected(item);
//        }

 //   }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter= new MyAdapter(mDatabaseReference, mDisplayName);   //initialize the MyAdapter object
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAdapter.CleanUp();
    }

}
