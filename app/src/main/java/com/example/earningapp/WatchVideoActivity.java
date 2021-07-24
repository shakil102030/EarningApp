package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WatchVideoActivity extends AppCompatActivity {
    private TextView watchVedio;
    private TextView watchVideoText;
    DatabaseReference reference;

    private String uGameID = "4175287";
    private String interstitialId = "Interstitial_Android";
    private String rewardedId = "Rewarded_Android";
    private Boolean test = true;


    int totalCount= 3;
    int count = 0;
    int points = 0;
    int nPoints = 0;
    long currentPoints = 0;
    User user;
    long millis = 10000;

    SharedPref sharedPref;
    CountDownTimer countDownTimer;
    Boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        watchVedio = (TextView) findViewById(R.id.watchVideoScoreId);
        watchVideoText = (TextView) findViewById(R.id.watchVideoTextViewId);
        sharedPref = new SharedPref(this);
        user = new User();
        watchVedio.setText(Integer.toString(count)+"/"+Integer.toString(totalCount));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Watch Video");
		initialActionBar();
        getUserData();

        // Declare a new listener:
        final UnityAdsListener myAdsListener = new UnityAdsListener ();
        // Add the listener to the SDK:
        UnityAds.addListener(myAdsListener);
        // Initialize the SDK:
        UnityAds.initialize (this, uGameID, test);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        getWatchVideoPoints();

        watchVideoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable) {
                    if (UnityAds.isReady (rewardedId)) {
                        UnityAds.show(WatchVideoActivity.this, rewardedId   );
                    }
                    watchVideo();
                }
            }
        });


    }

    private void getUserData() {
        final DatabaseReference databaseReferece = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getUid();

        databaseReferece.child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User model = snapshot.getValue(User.class);
                            assert model != null;
                            currentPoints = model.getPoints();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(WatchVideoActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    private void watchVideo() {
        nPoints = sharedPref.getVideoPoints();
        new SweetAlertDialog(WatchVideoActivity.this,  SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Congratulation" +
                        " You win " + nPoints + " Points.")
                .setConfirmText("COLLECT")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        /*if (UnityAds.isReady (interstitialId)) {
                            UnityAds.show(WatchVideoActivity.this, interstitialId);
                        }*/
						clickable = false;
						count++;
                        points += nPoints;
                        startTimer();
                        watchVedio.setText(Integer.toString(count)+"/"+Integer.toString(totalCount));
                        if (totalCount == count) {
                            startTimer();
                            sharedPref.putCounter(1);
                            updateWatchVideoPoints(points);
                            Toast.makeText(WatchVideoActivity.this,"Congratulation! Your total points " + points,Toast.LENGTH_LONG).show();
                            count = 0;
                            points = 0;
                            nPoints = 0;
                            watchVedio.setText(Integer.toString(count)+"/"+Integer.toString(totalCount));
                        }
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(millis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                updateCountdownText();
            }
            @Override
            public void onFinish() {
                sharedPref.putCounter(0);
                clickable = true;
				millis =10000;
                watchVideoText.setText("Watch Ads");
            }
        }.start();

    }

    private void updateCountdownText() {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        watchVideoText.setText(timeFormat);

    }


    public void getWatchVideoPoints(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watch_video_point");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    String key = snapshot.getKey();
                    if (key.equals("watch_video_point")) {
                        String VPoints = snapshot.getValue(String.class);
                        assert VPoints != null;
                        sharedPref.putVideoPoints(Integer.parseInt(VPoints));

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchVideoActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void updateWatchVideoPoints(int mPoints) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        int updatedPoints = (int) (mPoints + currentPoints);

        HashMap<String, Object> map = new HashMap<>();
        map.put("points", updatedPoints);

        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(WatchVideoActivity.this, "Points added successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("WATCHVIDEO", watchVedio.getText().toString());
        outState.putInt("COUNT", count);
        outState.putInt("TOTALCOUNT", totalCount);
        outState.putInt("NPOINT", nPoints);
        outState.putInt("POINTS", points);
        outState.putLong("MILLIS", millis);
        outState.putBoolean("CLICKABLE", clickable);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        watchVedio.setText(savedInstanceState.getString("WATCHVIDEO"));
        count = savedInstanceState.getInt("COUNT");
        totalCount = savedInstanceState.getInt("TOTALCOUNT");
        millis = savedInstanceState.getLong("MILLIS");
        nPoints = savedInstanceState.getInt("NPOINT");
        points = savedInstanceState.getInt("POINTS");
        clickable = savedInstanceState.getBoolean("CLICKABLE");

        if (!clickable) {
            startTimer();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
	
    private void initialActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
	

    @Override
    public void onBackPressed() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
        }
        finish();

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
        }
        super.onDestroy();
    }

    // Implement the IUnityAdsListener interface methods:
    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }
}