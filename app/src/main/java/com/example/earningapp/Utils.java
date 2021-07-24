package com.example.earningapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

public class Utils {
    CountDownTimer countDownTimer;
    ProgressDialog dialog;
    private Context context;
    SharedPref sharedPref1;

    public Utils() {
    }

    public Utils(Context context) {
        this.context = context;
    }

    //int limit = Integer.parseInt(sharedPref1.countLimit());

        /*if (sharedPref1.getCounter() > limit) {
            startTimer();
            //Toast.makeText(MainActivity.this,"Wait for 1 hour",Toast.LENGTH_LONG).show();

        }*/

    public void startTimer() {
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String hms = String.format("%02d:%02d:%02d:%02d",
                        TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millis)),
                        (TimeUnit.MILLISECONDS.toHours(millis) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))),
                        (TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))), (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                //countdownTimerText.setText(hms);//set text

                //dialog.setMessage(String.valueOf(millisUntilFinished/1000)+"s");
                dialog.setMessage("hdgjfdh");
                //textView.setText(String.valueOf(millisUntilFinished/1000)+"s");
                dialog.show();
            }
            @Override
            public void onFinish() {
                //textView.setText(String.valueOf(millisUntilFinished/1000)+"s");
                sharedPref1.putCounter(0);
                dialog.dismiss();
                //textView.setText("Time Up!");
            }
        }.start();

    }

}
