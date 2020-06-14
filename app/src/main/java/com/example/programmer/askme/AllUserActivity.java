package com.example.programmer.askme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {
    private RecyclerView recAllUser;
    private ArrayList listNotification;
    private DatabaseReference allUser = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference allUserFace = FirebaseDatabase.getInstance().getReference().child("facebook_users");
    private DatabaseReference allGoogle = FirebaseDatabase.getInstance().getReference().child("google_users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference notfRef;
    private int yy;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        getSupportActionBar().setTitle("All User");


        allGoogle.keepSynced(true);
        notfRef = FirebaseDatabase.getInstance().getReference().child("notification");
        allUserFace.keepSynced(true);
        allUser.keepSynced(true);
        recAllUser = findViewById(R.id.recAllUser);
        recAllUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recAllUser.setHasFixedSize(true);

        listNotification = new ArrayList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), login_activity.class));

        }


        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)


    @Override
    protected void onStart() {

        super.onStart();
        setupMyRec();

        notfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(dataSnapshot.getChildrenCount()==0)
                   return;
                Intent intent = new Intent(getApplicationContext(), AllUserActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(getApplicationContext()
                                , 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationChannel channel = new NotificationChannel("1", "ezzat", NotificationManager.IMPORTANCE_HIGH);
                Notification.Builder notification = new Notification.Builder(getApplicationContext());
                notification.setSmallIcon(R.mipmap.ask_me_ic);
                notification.setChannelId("1");
                notification.setContentIntent(pendingIntent);
                notification.setAutoCancel(true);
                notification.setDefaults(Notification.DEFAULT_SOUND);
                notification.setContentTitle("New Query Sir");
                notification.setContentText("there new " + dataSnapshot.getChildrenCount() + " Question");
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                manager.cancel(1);
                manager.notify(1, notification.build());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
        notfRef.removeValue();

    }

    private void setupMyRec() {


        FirebaseRecyclerAdapter<UserItem, VH2> adapter = new FirebaseRecyclerAdapter<UserItem, VH2>(
                UserItem.class
                , R.layout.user_item
                , VH2.class
                , allUser
        ) {
            @Override
            protected void populateViewHolder(VH2 viewHolder, UserItem model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setProfile_image(model.getProfile_image());
                final String id = getRef(position).getKey();
                viewHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (id.equals(Root.adminID)) {
                            Toast.makeText(AllUserActivity.this, "this specific of Admin ", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(), AllQuestinActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);

                    }
                });

            }
        };
        recAllUser.setAdapter(adapter);
    }


    public static class VH2 extends RecyclerView.ViewHolder {

        View v;

        public VH2(View itemView) {
            super(itemView);
            v = itemView;
        }

        public void setName(String name) {
            TextView txtName = v.findViewById(R.id.txtUser);
            txtName.setText(name);

        }


        public void setProfile_image(String profile_image) {
            CircleImageView imageView = v.findViewById(R.id.imgUser);
            if (checkInternet()) {
                Picasso.with(v.getContext()).load(profile_image).placeholder(R.drawable.profile).into(imageView);

            } else {
                Picasso.with(v.getContext()).load(profile_image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Toast.makeText(v.getContext().getApplicationContext(), "err in load image profile", Toast.LENGTH_SHORT).show();

                    }
                });


            }

        }

        public boolean checkInternet() {
            ConnectivityManager manager = (ConnectivityManager) v.getContext().getSystemService(CONNECTIVITY_SERVICE);
            return manager.getActiveNetworkInfo() != null;
        }
    }


    private boolean exit = false;

    @Override
    public void onBackPressed() {

        if (exit) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);
        }
    }


}
