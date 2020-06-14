package com.example.programmer.askme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllQuestinActivity extends AppCompatActivity {
    private String idUser;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewOfQuery;
    private FirebaseDatabase rootDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_questin);
        getSupportActionBar().setTitle("Queries");
        idUser = getIntent().getStringExtra("id");

        userRef = rootDataBase.getReference().child("query").child(idUser);
        userRef.keepSynced(true);


    }

    @Override
    protected void onStart() {
        super.onStart();
        setupRecView();
    }

    private void setupRecView() {
        recyclerViewOfQuery = findViewById(R.id.recQuerye);
        recyclerViewOfQuery.setHasFixedSize(true);
        recyclerViewOfQuery.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerAdapter<QueryData, MainWorkActivity.VH> adapter = new FirebaseRecyclerAdapter<QueryData, MainWorkActivity.VH>(QueryData.class
                , R.layout.item_rec
                , MainWorkActivity.VH.class
                , userRef
        ) {
            @Override
            protected void populateViewHolder(MainWorkActivity.VH viewHolder, QueryData model, int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setReply(model.isReply());
                viewHolder.setQuery(model);
                final String idQuery = getRef(position).getKey();

                viewHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                        intent.putExtra("id", idQuery);
                        intent.putExtra("idU", idUser);
                        startActivity(intent);

                    }
                });

            }
        };
        recyclerViewOfQuery.setAdapter(adapter);
    }

    public static class VH extends RecyclerView.ViewHolder {
        View v;

        public VH(View itemView) {
            super(itemView);
            v = itemView;
        }

        public void setDate(String date) {
            TextView txtDate = v.findViewById(R.id.txtdate);
            txtDate.setText(date);

        }

        public void setReply(boolean reply) {
            TextView txtReply = v.findViewById(R.id.txtAnswer);
            if (reply) {
                txtReply.setTextColor(Color.GREEN);
                txtReply.setText("Answerd");
            }
            else
                txtReply.setText("Not Answerd");


        }

        public void setQuery(QueryData data) {
            TextView txt = v.findViewById(R.id.txtQuery);
            txt.setText(data.showQuery());
        }
    }
}
