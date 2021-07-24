package com.example.earningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SharedPref",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String countLimit (){
        String limit = sharedPreferences.getString("limit","0");
        return limit;
    }

    public  void putCounter(int value){
        editor.putInt("VALUES",value);
        editor.commit();
    }

    public int getCounter() {
        if (sharedPreferences.contains("VALUES")) {
            int val = sharedPreferences.getInt("VALUES", 0);
            return val;
        }
        return 0;
    }

    public void putQuizPoints(int value){
        editor.putInt("QUIZPOINTS",value);
        editor.commit();
    }

    public int getQuizPoints() {
        if (sharedPreferences.contains("QUIZPOINTS")) {
            int val = sharedPreferences.getInt("QUIZPOINTS", 0);
            return val;
        }
        return 0;
    }

    public void putVideoPoints(int value){
        editor.putInt("VIDEOPOINTS",value);
        editor.commit();
    }

    public int getVideoPoints() {
        if (sharedPreferences.contains("VIDEOPOINTS")) {
            int val = sharedPreferences.getInt("VIDEOPOINTS", 0);
            return val;
        }
        return 0;
    }

    public void putWithdrawDollar(int nDollar){
        editor.putInt("WITHDRAW",nDollar);
        editor.commit();
    }

    public int getWithdrawDollar() {
        if (sharedPreferences.contains("WITHDRAW")) {
            int mDollar = sharedPreferences.getInt("WITHDRAW", 0);
            return mDollar;
        }
        return 0;
    }

    public void putCurrentPoints(long points){
        editor.putLong("POINTS",points);
        editor.commit();
    }

    public long getCurrentPoints() {
        if (sharedPreferences.contains("POINTS")) {
            long mPoints = sharedPreferences.getLong("POINTS", 0);
            return mPoints;
        }
        return 0;
    }


}
