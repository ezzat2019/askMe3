package com.example.programmer.askme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainWorkActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView txtName;
    private CircleImageView imageProfile;
    private FirebaseDatabase rootDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;
    private DatabaseReference userRefFaceBook;
    private DatabaseReference userRefGoogle;
    private String userId, type;
    private SharedPreferences.Editor preferences;
    private RecyclerView recyclerViewOfQuestion;
    private DatabaseReference refQuary;
    private Query query;
    private DatabaseReference notfRef;
    private String idOQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_work);
        mAuth = FirebaseAuth.getInstance();


        imageProfile = findViewById(R.id.profile_image);

        txtName = findViewById(R.id.txtName2);

        userRef = rootDataBase.getReference().child("users");
        userRef.keepSynced(true);
        userRefFaceBook = rootDataBase.getReference().child("facebook_users");
        userRefFaceBook.keepSynced(true);
        userRefGoogle = rootDataBase.getReference().child("google_users");
        userRefGoogle.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();



        userId = mAuth.getCurrentUser().getUid();
        notfRef = FirebaseDatabase.getInstance().getReference().child("notification2").child(userId);
        refQuary = FirebaseDatabase.getInstance().getReference().child("query").child(userId);
        refQuary.keepSynced(true);

        if (getIntent().getExtras()!=null)
        {
            type = getIntent().getExtras().getString("type", "null");

            if (!type.equals("null")) {
                preferences = getSharedPreferences("type", MODE_PRIVATE).edit();
                preferences.putString("t", type);
                preferences.apply();
            }

        }


    }

    private void retriveNotification() {

        notfRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(!dataSnapshot.hasChild("answer"))
                   return;
                String s=dataSnapshot.child("answer").getValue().toString();
                NotificationChannel channel=new NotificationChannel("5","mess",NotificationManager.IMPORTANCE_HIGH);
                Notification.Builder builder=new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setChannelId("5")
                        .setSmallIcon(R.drawable.profile)
                        .setContentTitle("Mssage from admin")
                        .setContentText(s);
                NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                manager.cancel(5);
                manager.notify(5,builder.build());




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        notfRef.removeValue();
    }

    private void setupRecView() {
        recyclerViewOfQuestion = findViewById(R.id.recMyQuestion);
        recyclerViewOfQuestion.setHasFixedSize(true);
        recyclerViewOfQuestion.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerAdapter<QueryData, VH> adapter = new FirebaseRecyclerAdapter<QueryData, VH>(QueryData.class
                , R.layout.item_rec
                , VH.class
                , refQuary.orderByChild("date")
        ) {
            @Override
            protected void populateViewHolder(VH viewHolder, QueryData model, final int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setReply(model.isReply());
                viewHolder.setQuery(model);
                viewHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id=getRef(position).getKey();
                        Intent intent=new Intent(getApplicationContext(),AnswerActivity.class);
                        intent.putExtra("id",id);
                        intent.putExtra("idU",userId);
                        startActivity(intent);
                    }
                });

            }
        };
        recyclerViewOfQuestion.setAdapter(adapter);
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
            if (reply)
                txtReply.setText("Answerd");
            else
                txtReply.setText("Not Answerd");


        }

        public void setQuery(QueryData data) {
            TextView txt = v.findViewById(R.id.txtQuery);
            txt.setText(data.showQuery());
        }
    }

    @Override
    protected void onStart() {


        super.onStart();
        setupRecView();


        SharedPreferences preferences1 = getSharedPreferences("type", MODE_PRIVATE);
        String str = preferences1.getString("t", "null");
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        if (str.equals("ord")) {

            retriveDataofUsers();
            retriveNotification();
        }

        else if (str.equals("google"))
        {
            retriveDataofUsersGoogle();
            retriveNotification();
        }

        else if (str.equals("facebook"))
        {
            retriveDataofUsersFaceBook();
            retriveNotification();
        }




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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    protected void retriveDataofUsersGoogle() {
        userId = mAuth.getCurrentUser().getUid();
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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainWorkActivity.this, uri, Toast.LENGTH_SHORT).show();
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


    public void addQuestion(View view) {
        Intent intent = new Intent(getApplicationContext(), QueryActivity.class);
        startActivity(intent);
    }

    public void gotoSettingAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingAccount.class);
        intent.putExtra("id", userId);
        SharedPreferences preferences1 = getSharedPreferences("type", MODE_PRIVATE);
        String str = preferences1.getString("t", "null");
        intent.putExtra("type", str);

        startActivity(intent);


    }
}
