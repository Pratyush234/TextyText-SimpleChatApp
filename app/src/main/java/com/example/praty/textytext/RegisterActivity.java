package com.example.praty.textytext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    static final String DISPLAY_NAME_KEYSS = "Username";
    private AutoCompleteTextView mUsernameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mSignUp;
    static final String CHAT_PREFSSS="ChatPrefs";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView= (AutoCompleteTextView) findViewById(R.id.register_username);
        mEmailView= (AutoCompleteTextView) findViewById(R.id.register_email);
        mPassword= (EditText) findViewById(R.id.register_password);
        mConfirmPassword= (EditText) findViewById(R.id.register_confirmPassword);
        mSignUp= (Button) findViewById(R.id.signUp);
        mAuth= FirebaseAuth.getInstance();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });


    }

    //method to check if the typed in details of email and password are valid or not
    private void attemptRegistration() {

        mEmailView.setError(null);
        mPassword.setError(null);

        boolean cancel= false;
        View focusView=null;

        String email= mEmailView.getText().toString();
        String password= mPassword.getText().toString();

        if(TextUtils.isEmpty(password) || !isPasswordValid(password)){
            mPassword.setError("Password too short (atleast 6 characters required) or is not valid");
            focusView= mPassword;
            cancel=true;
        }

        if(TextUtils.isEmpty(email)){
            mEmailView.setError("You cannot leave this field empty");
            focusView=mEmailView;
            cancel=true;
        }
        else if(!isEmailValid(email)){
            mEmailView.setError("Invalid email-id");
            focusView=mEmailView;
            cancel=true;
        }


        if(cancel){
            focusView.requestFocus();
        }
        else{
            createFirebaseUser();
        }
    }

    //method to create a firebase user
    private void createFirebaseUser() {

        String email= mEmailView.getText().toString();
        String password= mPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Google","onComplete() task:"+task.isSuccessful());

                if(!task.isSuccessful()){
                    Log.d("Google","user creation failed");
                }
                else {
                    saveDisplayName();
                    Toast.makeText(RegisterActivity.this, "Registration complete",Toast.LENGTH_SHORT).show();

                    Intent mIntent= new Intent(RegisterActivity.this, MainActivity.class);
                    finish();
                    startActivity(mIntent);
                }

            }
        });
    }

    private void saveDisplayName() {
        String mDisplayName= mUsernameView.getText().toString();

        SharedPreferences prefs= getSharedPreferences(CHAT_PREFSSS,MODE_PRIVATE);
        prefs.edit().putString(DISPLAY_NAME_KEYSS, mDisplayName).apply();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword= mConfirmPassword.getText().toString();
        return password.equals(confirmPassword) && password.length() > 5;
    }
}
