package com.example.programmer.askme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class signup_activity extends AppCompatActivity {
    private EditText edEmail, edName, edPass;
    private String email, name, pass;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    private ProgressDialog dialog;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);

        databaseReference.keepSynced(true);


        getSupportActionBar().setTitle("Sign Up");

        edEmail = findViewById(R.id.edEmail);
        edName = findViewById(R.id.edName);
        edPass = findViewById(R.id.edPass);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Wait Please!");

    }

    protected boolean checkInputsIsCorrect() {
        boolean x = false, y = false, z = false;

        email = edEmail.getText().toString().trim();
        name = edName.getText().toString().trim();
        pass = edPass.getText().toString().trim();
        if (TextUtils.isEmpty(edEmail.getText())) {
            edEmail.setError("please enter email first");
            return false;

        } else if (TextUtils.isEmpty(edName.getText())) {
            edName.setError("please enter name first");

            return false;

        } else if (TextUtils.isEmpty(edPass.getText())) {
            edPass.setError("please enter password first");
            return false;

        } else
            z = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("please enter correct email");
            return false;

        } else
            x = true;
        if (pass.length() < 6) {
            edPass.setError("password must be bigger than 6 lettets");

        } else
            y = true;

        if (x && y && z)
            return true;
        else
            return false;

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void done(View view) {
        if (checkInputsIsCorrect()) {
            if (isNetworkConnected()) {
                dialog.show();
                final Map<String, String> userInfo = new HashMap<>();
                userInfo.put("email", email);
                userInfo.put("name", name);
                userInfo.put("password", pass);
                userInfo.put("profile_image", "default");


                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        edEmail.setText("");
                                        edName.setText("");
                                        edPass.setText("");
                                        dialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), MainWorkActivity.class);
                                        intent.putExtra("type", "ord");
                                        startActivity(intent);
                                        Toast.makeText(signup_activity.this, "Success", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(signup_activity.this, "check internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }

                    }
                });


            } else
                Toast.makeText(signup_activity.this, "check internet connection", Toast.LENGTH_SHORT).show();

        }

    }

}
