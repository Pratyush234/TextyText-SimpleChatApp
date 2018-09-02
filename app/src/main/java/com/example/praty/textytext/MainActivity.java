package com.example.praty.textytext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN=9001;
    private String name,email,idToken,photo;
    private Uri photoUri;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInButton mGoogleSignInButton;
    private Button mRegisterButton;
    private Button mLoginButton;
    private AutoCompleteTextView mEmailEdit;
    private EditText mPasswordEdit;

    static final String CHAT_PREFS="ChatPrefs";
    static final String DISPLAY_NAME_KEY="Username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);

        mEmailEdit= (AutoCompleteTextView) findViewById(R.id.emailEdit);
        mPasswordEdit=(EditText) findViewById(R.id.passwordEdit);

        mGoogleSignInButton= (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setColorScheme(SignInButton.COLOR_DARK);
        setGoogleButtonText(mGoogleSignInButton);
        mGoogleSignInButton.setOnClickListener(this);  //onClickListener for the google sign in button

        mLoginButton=(Button) findViewById(R.id.loginButton);
        //onClickListener() for the Login Button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoginWithEmailAndPassword();
            }
        });

        FirebaseApp.initializeApp(this); //initializing the firebase

        mRegisterButton=(Button) findViewById(R.id.registerButton);
        //onClickListener() for the Register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent= new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(mIntent);
            }
        });



        mAuth=com.google.firebase.auth.FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Get signed in user
                FirebaseUser user=firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to firebase

                if(user!=null){
                    Log.d("Google","onAuthStateChanged: user signed in:"+ user.getUid());
                }
                else {
                    //user signed out
                    Log.d("Google","onAuthStateChanged: user signed out");
                }

            }
        };

        configureSignin();
    }

    //method for logging in with email and password
    private void attemptLoginWithEmailAndPassword() {

        String email= mEmailEdit.getText().toString();
        String password= mPasswordEdit.getText().toString();

        if(email.equals("") || password.equals("")) return;

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Google","Signing in, onComplete:"+ task.isSuccessful());

                if(!task.isSuccessful()){
                    Log.d("Google", "Signing in failed:"+task.getException());
                    showErrorDialog("Signing in failed");
                }
                else{
                    Toast.makeText(MainActivity.this, "Login successful",Toast.LENGTH_LONG).show();
                    Intent mIntent= new Intent(MainActivity.this, ChatActivity.class);
                    mIntent.putExtra("Options","normal");
                    startActivity(mIntent);
                }

            }
        });
    }

    //alertDialog when signing in with email and password fails
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //method to customize the google sign in button by changing its text and the gravity
    private void setGoogleButtonText(SignInButton mSignInButton) {
        for(int i=0;i<mSignInButton.getChildCount();i++){
            View v= mSignInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("SIGN IN WITH GOOGLE");
                return;
            }
        }
    }

    public void configureSignin(){

        //configure sign in to request the user's basic profile like name and email
        GoogleSignInOptions options=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainActivity.this.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        //build a googleapiclient with access to GoogleSignIn.API and the options above
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,options)
                .build();

        mGoogleApiClient.connect();
    }

    //this method will prompt the user to select a google account before the authentication begins
    private void SignInWithGoogle(){

        Intent signInIntent= Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    //this method will handle the result of clicking the google sign in button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //result returned from launching the intent from GoogleSignInApi.getSignInIntent()

        if(requestCode==RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data); //data is the default intent

            if (result.isSuccess()) {
                //google sign in was successful, save the token and state then authenticate with firebase by calling
                //firebaseAuthWithGoogle()

                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();
                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = account.getPhotoUrl().toString();
                Log.d("Google","Google signing in: Username:"+name);



                //save data to shared preferences
                SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,MODE_PRIVATE);
                prefs.edit().putString(DISPLAY_NAME_KEY, name).apply();
                //  prefs.edit().putString("emailID", email).apply();
                //  prefs.edit().putString("token", idToken).apply();
                //  prefs.edit().putString("photoURL", photo).apply();

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential); //this method will create a firebase user using the
                //AuthCredential class and will check if it's successful or not using onCompleteListener
            } else {
                Log.e("Google", "sign in unsuccessful");
                Toast.makeText(MainActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {Log.d("Google", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Google", "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                          //  createUserInFirebaseHelper();
                            Toast.makeText(MainActivity.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();
                            Intent mIntent= new Intent(MainActivity.this, ChatActivity.class);
                            mIntent.putExtra("Options","google");
                            startActivity(mIntent);

                    }
                }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_signout:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_bar_about:

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.sign_in_button){
                SignInWithGoogle();
            }else {

            }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
