package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MathQuizActivity extends AppCompatActivity {
    TextView score;
    TextView question;
    Button submitBttn;
    EditText equation;
    Random random = new Random();
    int a, b;
    int answer = 0;
    int points = 0;
    int nPoints = 0;
    int count = 0;
    long currentPoints = 0;
    int totalQuestions = 2;
    SharedPref sharedPref;
    User user;
    Utils utils;

    DatabaseReference reference;
    CountDownTimer countDownTimer;

    long millis = 10000;

    Boolean clickable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_quiz);
        score = findViewById(R.id.scoreTextViewId);
        question = findViewById(R.id.questionTextViewId);
        submitBttn = findViewById(R.id.submitBttnId);
        sharedPref = new SharedPref(this);
        user = new User();
        utils = new Utils(this);
        //score.setText(Integer.toString(count)+"/"+Integer.toString(totalQuestions));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Math Quiz");
        initialActionBar();
        getMathQuizPoints();
        nextQuestion();
        getUserData();

        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable) {
                    equation = (EditText) findViewById(R.id.equationId);
                    int limit = Integer.parseInt(sharedPref.countLimit());

                    if (sharedPref.getCounter() >= limit) {
                        startQuiz();
                        //StartAppSDK.setTestAdsEnabled(false);
                    } else {
                        dialog();

                    }

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
                        Toast.makeText(MathQuizActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    public void nextQuestion(){
        a = random.nextInt(10);
        b = random.nextInt(10);
        answer = a + b;
        question.setText(a+"+"+b + " = ?");
    }

    private void startQuiz() {
        String mEquation = equation.getText().toString();
        if(TextUtils.isEmpty(mEquation)){
            Toast.makeText(MathQuizActivity.this,"Required",Toast.LENGTH_LONG).show();
            return;
        }
        if(Integer.toString(answer).equals(mEquation)){
            nPoints = sharedPref.getQuizPoints();
            new SweetAlertDialog(MathQuizActivity.this,  SweetAlertDialog.SUCCESS_TYPE)
                    .setContentText("Congratulation" +
                            " You win " + nPoints + " Points.")
                    .setConfirmText("COLLECT")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
							clickable = false;
                            count++;
                            points += nPoints;
                            startTimer();
                            nextQuestion();
                            score.setText(Integer.toString(count)+"/"+Integer.toString(totalQuestions));
                            if (totalQuestions == count) {
                                startTimer();
                                sharedPref.putCounter(1);
                                updatePoints(points);
                                Toast.makeText(MathQuizActivity.this,"Congratulation! Your total points " + points,Toast.LENGTH_LONG).show();
                                count = 0;
                                points = 0;
                                nPoints = 0;
                                score.setText(Integer.toString(count)+"/"+Integer.toString(totalQuestions));
                            }
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }else {
            Toast.makeText(MathQuizActivity.this,"Wrong answer Please try again",Toast.LENGTH_LONG).show();
            nextQuestion();
        }
    }

    private void dialog() {
        new SweetAlertDialog(this)
                .setContentText("Please wait")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
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
                millis = 10000;
                submitBttn.setText("Submit");
            }
        }.start();

    }

    private void updateCountdownText() {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        submitBttn.setText(timeFormat);

    }

   private void getMathQuizPoints(){
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("math_point");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    String key = snapshot.getKey();
                    if (key.equals("math_point")) {
                        String QPoints = snapshot.getValue(String.class);
                        assert QPoints != null;
                        sharedPref.putQuizPoints(Integer.parseInt(QPoints));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MathQuizActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void updatePoints(int mPoints) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        int updatedPoints = (int) (mPoints + currentPoints);

        HashMap<String, Object> map = new HashMap<>();
        map.put("points", updatedPoints);

        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(MathQuizActivity.this, "Points added successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SCORE", score.getText().toString());
        outState.putInt("COUNT", count);
        outState.putInt("TOTALQUESTION", totalQuestions);
        outState.putInt("NPOINT", nPoints);
        outState.putInt("POINTS", points);
        outState.putLong("MILLIS", millis);
        outState.putBoolean("CLICKABLE", clickable);
    }




    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        score.setText(savedInstanceState.getString("SCORE"));
        count = savedInstanceState.getInt("COUNT");
        totalQuestions = savedInstanceState.getInt("TOTALQUESTION");
        nPoints = savedInstanceState.getInt("NPOINT");
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