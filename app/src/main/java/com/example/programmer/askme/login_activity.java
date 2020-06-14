package com.example.programmer.askme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class login_activity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button btnSignUp;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private boolean isGoogle = false, isFacebook = false;

    private SignInButton signInButton;
    private LoginButton loginButton;
    private String userId;
    private DatabaseReference notfRef;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        getSupportActionBar().setTitle("Login");
        signInButton = findViewById(R.id.sign_in_button);
        loginButton = findViewById(R.id.login_button);

        mCallbackManager = CallbackManager.Factory.create();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoogle();
            }
        });

        reference.keepSynced(true);

        loginFaceBook();

        notfRef=reference.child("notification");

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), signup_activity.class));
            }
        });


    }


    public void signOutFaceBook() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();


    }

    private void loginFaceBook() {
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {

                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Intent intent = new Intent(getApplicationContext(), MainWorkActivity.class);
        if (isFacebook) {
            intent.putExtra("type", "facebook");
        } else if (isGoogle) {
            intent.putExtra("type", "google");
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String idAdmin = Root.adminID;
            if (currentUser.getUid().equals(idAdmin)) {


                startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
                finish();
            }
            else {
                isGoogle = false;
                isFacebook = false;
                startActivity(intent);
                finish();
            }
        }
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {

        if (exit) {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);


        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    private void loginGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }


    private void handleFacebookAccessToken(AccessToken token) {

        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userId = task.getResult().getUser().getUid();
                            Map<String, String> userInfo = new HashMap<>();
                            userInfo.put("email", task.getResult().getUser().getEmail().toString());
                            userInfo.put("name", task.getResult().getUser().getDisplayName().toString());
                            userInfo.put("password", "facebook");
                            userInfo.put("profile_image", task.getResult().getUser().getPhotoUrl().toString());
                            reference.child("facebook_users").child(userId).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    isFacebook = true;
                                    onStart();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login_activity.this, "Fail data base !", Toast.LENGTH_SHORT).show();

                                }
                            });


                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getApplication(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userId = task.getResult().getUser().getUid();
                            Map<String, String> userInfo = new HashMap<>();
                            userInfo.put("email", task.getResult().getUser().getEmail().toString());
                            userInfo.put("name", task.getResult().getUser().getDisplayName().toString());
                            userInfo.put("password", "google");
                            userInfo.put("profile_image", task.getResult().getUser().getPhotoUrl().toString());

                            reference.child("google_users").child(userId).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    isGoogle = true;
                                    onStart();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login_activity.this, "fail data base", Toast.LENGTH_SHORT).show();

                                }
                            });

                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.


                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOutGoogle() {

        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


    public void ordinaryLogin(View view) {
        startActivity(new Intent(getApplicationContext(), OrdinaryLoginActivity.class));

    }
}
