package com.example.programmer.askme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingAccount extends AppCompatActivity {
    private FirebaseDatabase rootDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userRefFaceBook;
    private DatabaseReference userRefGoogle;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference dbStorge;
    private EditText edName;
    private ProgressDialog dialog;
    private TextView txtName;
    private String userId;
    private String typeLogin;
    private CircleImageView imageProfile;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);

        txtName = findViewById(R.id.txtName);
        edName = findViewById(R.id.edNewName);

        dbStorge = storage.getReference().child("profile_images");

        imageProfile = findViewById(R.id.profile_image2);

        mAuth = FirebaseAuth.getInstance();

        userRef = rootDataBase.getReference().child("users");
        userRef.keepSynced(true);
        userRefFaceBook = rootDataBase.getReference().child("facebook_users");
        userRefFaceBook.keepSynced(true);
        userRefGoogle = rootDataBase.getReference().child("google_users");
        userRefGoogle.keepSynced(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("please wait");


        userId = getIntent().getStringExtra("id");
        typeLogin = getIntent().getStringExtra("type");

        checkIfOrdinaryEmail();


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (typeLogin.equals("ord"))
            retriveDataofUsers();
        else if (typeLogin.equals("google"))
            retriveDataofUsersGoogle();
        else if (typeLogin.equals("facebook"))
            retriveDataofUsersFaceBook();
    }

    protected void retriveDataofUsers() {

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String uri = dataSnapshot.child("profile_image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                txtName.setText(name);
                if (!uri.equals("default")) {
                    if (isNetworkConnected()) {
                        Picasso.with(getApplicationContext()).load(uri).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    } else {
                        Picasso.with(getApplicationContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void retriveDataofUsersFaceBook() {
        userRefFaceBook.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String uri = dataSnapshot.child("profile_image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                txtName.setText(name);
                if (!uri.equals("default")) {
                    if (isNetworkConnected()) {
                        Picasso.with(getApplicationContext()).load(uri).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    } else {
                        Picasso.with(getApplicationContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected void retriveDataofUsersGoogle() {

        userRefGoogle.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String uri = dataSnapshot.child("profile_image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                txtName.setText(name);
                if (!uri.equals("default")) {
                    if (isNetworkConnected()) {
                        Picasso.with(getApplicationContext()).load(uri).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    } else {
                        Picasso.with(getApplicationContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(imageProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.profile).into(imageProfile);

                            }

                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkIfOrdinaryEmail() {
        if (typeLogin.equals("google") || typeLogin.equals("facebook")) {
            edName.setVisibility(View.INVISIBLE);
            findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnAddPhoto).setVisibility(View.INVISIBLE);

        } else {
            edName.setVisibility(View.VISIBLE);
            findViewById(R.id.btnSave).setVisibility(View.VISIBLE);
            findViewById(R.id.btnAddPhoto).setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingAccount.this, MainWorkActivity.class));
        finish();
    }

    public void addPhoto(View view) {

        if (!isNetworkConnected()) {
            Toast.makeText(this, "check ur internet connection ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.setType("image/*");
        startActivityForResult(getImage, 10);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            dialog.show();
            Uri url = data.getData();
            StorageReference reference = dbStorge.child(userId).child(userId + System.currentTimeMillis() + ".jpg");
            reference.putFile(url).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String uri = task.getResult().toString();
                                userRef.child(userId).child("profile_image").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();

                                            Toast.makeText(SettingAccount.this, "succuess", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        });


                    }
                }
            });

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void signOut(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (typeLogin.equals("ord"))
                mAuth.signOut();
            else if (typeLogin.equals("google")) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mAuth.signOut();
                //// google ////
                // Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(getApplicationContext(), login_activity.class));
                            }
                        });

            } else {
                mAuth.signOut();
                ////facebook log out/////
                LoginManager.getInstance().logOut();
                startActivity(new Intent(getApplicationContext(), login_activity.class));

            }


            startActivity(new Intent(getApplicationContext(), login_activity.class));
            finish();

        }


    }


    public void saveNewName(View view) {
        if (!isNetworkConnected()) {
            Toast.makeText(this, "check ur internet connection ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edName.getText())) {
            edName.setError("please enter name first");
        } else {
            userRef.child(userId).child("name").setValue(edName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        edName.setText("");

                        Toast.makeText(SettingAccount.this, "success chanage your name", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
