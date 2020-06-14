package com.example.programmer.askme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class splash_screen_activty extends AppCompatActivity {
    private TextView txt1, txt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_activty);
        animeText();
        gotoLoginActivty();
        printHashKey(getApplicationContext());
    }
    private void gotoLoginActivty() {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                startActivity(new Intent(getApplicationContext(),login_activity.class));
                finish();

            }
        });
        thread.start();
    }
    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("wezz"  , hashKey);
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (Exception e) {

        }
    }

    protected void animeText() {
        txt1 = findViewById(R.id.textView);
        txt1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.show_splash_text));
        txt2 = findViewById(R.id.textView2);
        txt2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.show_splash_text));

    }

}
