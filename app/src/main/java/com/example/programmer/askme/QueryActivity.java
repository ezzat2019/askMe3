package com.example.programmer.askme;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    EpanListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    private DatabaseReference notfRef;
    HashMap<String, List<String>> expandableListDetail;
    private EditText edRoot, edItem, subject, subjectDetil;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference refQuery = database.getReference().child("query");
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        expandableListView = (ExpandableListView) findViewById(R.id.customList);
        expandableListDetail = ExpanData.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        edItem = findViewById(R.id.edChooseItem);
        edRoot = findViewById(R.id.edChooseRoot);
        subject = findViewById(R.id.edSubject);
        subjectDetil = findViewById(R.id.edDetial);

        userId = mAuth.getCurrentUser().getUid();
        refQuery.keepSynced(true);


        expandableListAdapter = new EpanListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        notfRef=FirebaseDatabase.getInstance().getReference().child("notification").child(userId);
        notfRef.keepSynced(true);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    edRoot.setText(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).toString());
                } else if (groupPosition == 1)
                    edItem.setText(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).toString());
                return false;
            }
        });


    }

    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void sendQuery(View view) {
        if (!checkInternet()) {
            Toast.makeText(this, "check from internt connection ", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(edRoot.getText())) {

            Toast.makeText(this, "Select Any Type Of Questions", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edItem.getText())) {

            Toast.makeText(this, "Select Any Category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(subject.getText())) {
            subject.setError("Must Enter subject Title");
            return;
        }
        if (TextUtils.isEmpty(subjectDetil.getText())) {
            subjectDetil.setError("Enter your Subject First");
            return;
        }
        final Map<String, Object> mapOfQuery = new HashMap<>();
        mapOfQuery.put("question_type", edRoot.getText().toString().trim());
        mapOfQuery.put("category", edItem.getText().toString().trim());
        mapOfQuery.put("subject", subject.getText().toString().trim());
        mapOfQuery.put("answer", "default");
        mapOfQuery.put("subject_detiel", subjectDetil.getText().toString().trim());
        Date date = new Date();
        mapOfQuery.put("date", date.toString().trim());
        mapOfQuery.put("reply", false);
        DatabaseReference reference = refQuery.child(userId).push();
        final String id = reference.getKey();

        reference.setValue(mapOfQuery).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    notfRef.child("show").setValue("no");
                    edRoot.setText("");
                    edItem.setText("");
                    subject.setText("");
                    subjectDetil.setText("");
                    Intent intent = new Intent(getApplicationContext(), MainWorkActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    Toast.makeText(QueryActivity.this, "done", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
