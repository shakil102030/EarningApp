package com.example.earningapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class SpinnerActivity extends AppCompatActivity {
    ImageView wheelImage;
    Button playButton;
    private Random r;
    private int degree = 0, degree_old = 0;
    private static final float FACTOR = 15f;
    private MediaPlayer player;
    int spinScore = 0;
    int count = 0;
    int points = 0;
    int totalAnswer = 3;
    long currentPoints = 0;
    TextView mSpinScore;
    DatabaseReference reference;
    User user;
    long millis = 10000;

    SharedPref sharedPref;
    CountDownTimer countDownTimer;
    Boolean clickable = true;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        setTitle("Spin And Earn");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		initialActionBar();
        getUserData();
        sharedPref = new SharedPref(this);
        user = new User();
        mSpinScore = (TextView) findViewById(R.id.spinScoreId);
        wheelImage = findViewById(R.id.wheelImageId);
        playButton = findViewById(R.id.playButtonId);

        r = new Random();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickable) {
                    startSpin();
                }
            }
        });

    }

    private void getUserData() {
        DatabaseReference databaseReferece = FirebaseDatabase.getInstance().getReference();
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
                        Toast.makeText(SpinnerActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void startSpin() {

        {
            int degree_old = degree % 360;
            degree = r.nextInt(3600) + 720;
            RotateAnimation animationRotate = new RotateAnimation(degree_old, degree, RotateAnimation.RELATIVE_TO_SELF,
                    0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            animationRotate.setDuration(3600);
            animationRotate.setFillAfter(true);
            animationRotate.setInterpolator(new DecelerateInterpolator());
            animationRotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (player == null) {
                        player = MediaPlayer.create(SpinnerActivity.this, R.raw.sound);
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                stopPlayer();
                            }
                        });
                    }
                    player.start();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    stopPlayer();
                    //StartAppSDK.setTestAdsEnabled(false);
                    currentNumber(360 - (degree % 360));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            wheelImage.startAnimation(animationRotate);
        }

    }

    private String currentNumber(int degrees) {
        String text = "";

        if (degrees >= (FACTOR * 0) && degrees <= (FACTOR * 2)) {
            spinScore = 1;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 2) && degrees <= (FACTOR * 4)) {
            spinScore = 2;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 4) && degrees <= (FACTOR * 6)) {
            spinScore = 3;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 6) && degrees <= (FACTOR * 8)) {
            spinScore = 4;
            spin(spinScore);

        }
        if (degrees >= (FACTOR * 8) && degrees <= (FACTOR * 10)) {
            spinScore = 5;
            spin(spinScore);

        }
        if (degrees >= (FACTOR * 10) && degrees <= (FACTOR * 12)) {
            spinScore = 6;
            spin(spinScore);

        }
        if (degrees >= (FACTOR * 12) && degrees <= (FACTOR * 14)) {
            spinScore = 7;
            spin(spinScore);

        }
        if (degrees >= (FACTOR * 14) && degrees <= (FACTOR * 16)) {
            spinScore = 8;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 16) && degrees <= (FACTOR * 18)) {
            spinScore = 9;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 18) && degrees <= (FACTOR * 20)) {
            spinScore = 10;
            spin(spinScore);
        }
        if (degrees >= (FACTOR * 20) && degrees <= (FACTOR * 22)) {
            spinScore = 11;
            spin(spinScore);
        }
        if ((degrees >= (FACTOR * 22) && degrees <= (FACTOR * 24))) {
            spinScore = 12;
            spin(spinScore);
        }
        return text;

    }

    private void spin(int nSpinScore) {
        new SweetAlertDialog(SpinnerActivity.this,  SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Congratulation" +
                        " You win " + nSpinScore + " Points.")
                .setConfirmText("COLLECT")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
						clickable = false;
		                count++;
                        points += nSpinScore;
                        startTimer();
                        mSpinScore.setText(Integer.toString(count)+"/"+Integer.toString(totalAnswer));
                        if (totalAnswer == count) {
                            startTimer();
                            sharedPref.putCounter(1);
                            updateSpinPoints(points);
                            Toast.makeText(SpinnerActivity.this,"Congratulation! Your total points " + points,Toast.LENGTH_LONG).show();
                            count = 0;
                            points = 0;
                            spinScore = 0;
                            mSpinScore.setText(Integer.toString(count)+"/"+Integer.toString(totalAnswer));
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
                playButton.setText("Play");
            }
        }.start();

    }

    private void updateCountdownText() {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        playButton.setText(timeFormat);

    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void updateSpinPoints(int mPoints) {

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        int updatedSpinPoints = (int) (mPoints + currentPoints);

        HashMap<String, Object> map = new HashMap<>();
        map.put("points", updatedSpinPoints);

        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SpinnerActivity.this, "Points added successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SPINSCORE", mSpinScore.getText().toString());
        outState.putInt("COUNT", count);
        outState.putInt("TOTALANSWER", totalAnswer);
        outState.putInt("POINTS", points);
        outState.putLong("MILLIS", millis);
        outState.putBoolean("CLICKABLE", clickable);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mSpinScore.setText(savedInstanceState.getString("SPINSCORE"));
        count = savedInstanceState.getInt("COUNT");
        totalAnswer = savedInstanceState.getInt("TOTALANSWER");
        points = savedInstanceState.getInt("POINTS");
        millis = savedInstanceState.getLong("MILLIS");
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
}