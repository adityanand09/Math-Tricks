package com.tricks.math_tricks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.tricks.math_tricks.App.IS_UPDATE_AVAILABLE;
import static com.tricks.math_tricks.App.NETWORK_INFO;
import static com.tricks.math_tricks.App.NEW_UPDATE_AVAILABLE;
import static com.tricks.math_tricks.App.NO_NEW_UPDATE_OR_NETWORK_ERROR;
import static com.tricks.math_tricks.App.UPDATE_VERSION;

public class SplashSSC extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("update_version");
    private static final String TAG = "SplashSSC";

    private int i = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssc_splash);
        Log.d(TAG, "onCreate");

        final Intent ssc_home_activity = new Intent(SplashSSC.this, SscHomeActivity.class);
        if (NETWORK_INFO != null && NETWORK_INFO.isConnected()){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: " + snapshot.getValue());
                    if (i==-1) {
                        i=0;
                        if (Integer.parseInt(snapshot.getValue().toString()) != UPDATE_VERSION) {
                            ssc_home_activity.putExtra(IS_UPDATE_AVAILABLE, NEW_UPDATE_AVAILABLE);
                        } else {
                            ssc_home_activity.putExtra(IS_UPDATE_AVAILABLE, NO_NEW_UPDATE_OR_NETWORK_ERROR);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            ssc_home_activity.putExtra(IS_UPDATE_AVAILABLE, NO_NEW_UPDATE_OR_NETWORK_ERROR);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(ssc_home_activity);
                finish();
            }
        }, SPLASH_DURATION);

    }

}
