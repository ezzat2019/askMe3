package com.example.programmer.askme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnswerActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private TextView textView, txtAnswer;
    private String id;
    private String userId;
    private Button btn3, btn2;
    private EditText edAnswer;
    private CardView cardView;
    private DatabaseReference notfRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        getSupportActionBar().setTitle("Answers");

        textView = findViewById(R.id.txtQuestion);
        id = getIntent().getStringExtra("id");
        edAnswer = findViewById(R.id.edAnswer);
        cardView = findViewById(R.id.cardAnswer);
        btn3 = findViewById(R.id.button3);
        btn2 = findViewById(R.id.button2);
        txtAnswer = findViewById(R.id.txtAnswer);

        userId = getIntent().getStringExtra("idU");
        reference = FirebaseDatabase.getInstance().getReference().child("query").child(userId).child(id);
        reference.keepSynced(true);

        notfRef = FirebaseDatabase.getInstance().getReference().child("notification2").child(userId);
        notfRef.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        RetriveQuestion();
    }

    private void RetriveQuestion() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String answer = dataSnapshot.child("answer").getValue().toString();
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(Root.adminID)) {
                    edAnswer.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                } else {
                    edAnswer.setVisibility(View.GONE);
                    btn2.setVisibility(View.GONE);

                }

                if (!answer.equals("default")) {
                    btn3.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                    txtAnswer.setText(answer);
                } else {
                    btn3.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);

                }

                String str = "Title: " + dataSnapshot.child("subject").getValue() + "\nDetiel:\n" + dataSnapshot.child("subject_detiel").getValue().toString();
                textView.setText(str);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void send(View view) {
        if (TextUtils.isEmpty(edAnswer.getText())) {
            edAnswer.setError("put your answer first");
            return;

        }
        reference.child("answer").setValue(edAnswer.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    notfRef.child("answer").setValue(edAnswer.getText().toString().trim());
                    reference.child("reply").setValue(true);
                    Toast.makeText(AnswerActivity.this, "sended", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    edAnswer.setText("");
                }

            }
        });
    }
}
