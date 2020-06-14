package com.example.programmer.askme;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class Root extends Application {
    public static final String adminID="VkcW3mF8i5bw991KXmLo5nk47Uv1";
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase x=FirebaseDatabase.getInstance();
        x.setPersistenceEnabled(true
        );
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }
}
