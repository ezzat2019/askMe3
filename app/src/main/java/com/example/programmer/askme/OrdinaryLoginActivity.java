package com.example.programmer.askme;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class OrdinaryLoginActivity extends AppCompatActivity {
    private EditText edEmail, edPass;
    private String email, pass;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordinary_login);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        getSupportActionBar().setTitle("Sign In Screen");
    }

    protected boolean checkInputsIsCorrect() {
        boolean y = false, z = false, x = false;

        email = edEmail.getText().toString().trim();
        pass = edPass.getText().toString().trim();
        if (TextUtils.isEmpty(edEmail.getText())) {
            edEmail.setError("please enter email first");
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

    public void login(View view) {

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (checkInputsIsCorrect()) {
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            edEmail.setText("");
                            edPass.setText("");
                            if(email.equals("admin@yahoo.com"))
                            {
                                startActivity(new Intent(getApplicationContext(),AllUserActivity.class));
                                finish();
                                return;
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), MainWorkActivity.class);
                                intent.putExtra("type", "ord");
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(OrdinaryLoginActivity.this, "incorrect email or password", Toast.LENGTH_SHORT).show();

                            return;
                        }

                    }
                });
            }
        } else {
            Toast.makeText(this, "check internet connetion", Toast.LENGTH_SHORT).show();
        }


    }
}
