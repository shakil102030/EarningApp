package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.bumptech.glide.Glide;
import com.example.earningapp.Activity.ScoreboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;



import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PushFirebase";
    private static final String CHANNEL_ID = "101";
    Fragment fragment = null;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    SharedPref sharedPref1;
    TextView userName, userEmail, userPoints, userDollar;
    CircleImageView userImage, circleImage, notice;
    CardView dailyCheckView, spin, mathQuiz, watchVideo, invite;

    FirebaseFirestore database;
    FirebaseAuth firebaseLogout;
    User user;
    String mCurrentDollar;
    long currnPoints = 0;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseLogout = FirebaseAuth.getInstance();
        firebaseLogout = FirebaseAuth.getInstance();
        userName = (TextView) findViewById(R.id.userNameTxtId);
        userEmail = (TextView) findViewById(R.id.nav_userEmailId);
        userPoints = (TextView) findViewById(R.id.userPointsId);
        userDollar = (TextView) findViewById(R.id.userDollarId);
        userImage = (CircleImageView) findViewById(R.id.userImageId);
        //circleImage = (CircleImageView) findViewById(R.id.nav_circleImageId);
        notice = (CircleImageView) findViewById(R.id.noticeId);
        spin = (CardView) findViewById(R.id.spinId);
        mathQuiz = (CardView) findViewById(R.id.mathQuizId);
        watchVideo = (CardView) findViewById(R.id.watchVideoId);
        invite = (CardView) findViewById(R.id.inviteId);
        dailyCheckView = (CardView) findViewById(R.id.dailycheckId);
        sharedPref = new SharedPref(this);

        //CookieSyncManager.createInstance(this);
        //CookieSyncManager.getInstance().startSync();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        spin.setOnClickListener(this);
        mathQuiz.setOnClickListener(this);
        watchVideo.setOnClickListener(this);
        invite.setOnClickListener(this);
        dailyCheckView.setOnClickListener(this);

        setTitle("Home");


        getUserData();
        initNavigationDrawer();
        createNotificationChannel();
        getToken();

        //Buttom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view2);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_Redeem:
                        startActivity(new Intent(MainActivity.this, WalletActivity.class));
                        break;
                    case R.id.navigation_AboutUs:
                        navigationAboutUs();
                        break;
                    case R.id.navigation_Users_Board:
                        startActivity(new Intent(MainActivity.this, ScoreboardActivity.class));
                        break;
                }
                return false;
            }
        });

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null){
                            String key = snapshot.getKey();
                            if (key.equals("Notice")) {
                                String notice = snapshot.getValue(String.class);
                                assert notice != null;
                                DailogNotice dailogNotice = new DailogNotice();
                                dailogNotice.showDialog(MainActivity.this, notice);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        });
    }

    private void navigationAboutUs() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("aboutus_link");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null){
                    String key = snapshot.getKey();
                    if (key.equals("aboutus_link")) {
                        String aboutUs = snapshot.getValue(String.class);
                        assert aboutUs != null;
                        new SweetAlertDialog(MainActivity.this)
                                .setContentText(aboutUs)
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.spinId:
                startActivity(new Intent(MainActivity.this, SpinnerActivity.class));
                break;
            case R.id.mathQuizId:
                startActivity(new Intent(MainActivity.this, MathQuizActivity.class));
                break;
            case R.id.watchVideoId:
                startActivity(new Intent(MainActivity.this, WatchVideoActivity.class));
                break;
            case R.id.inviteId:
                startActivity(new Intent(MainActivity.this, ReferActivity.class));
                break;
            case R.id.dailycheckId:
                dailyCheck();
                break;
        }
    }

    private void getUserData() {
        final DatabaseReference databaseReferece = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getUid();

        databaseReferece.child("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            User model = snapshot.getValue(User.class);
                            assert model != null;
                            String currentName =  model.getName();
                            currnPoints = model.getPoints();
                            mCurrentDollar = model.getDollar();
                            float mCurrentDollr = Float.parseFloat(mCurrentDollar);
                            userName.setText(currentName);
                            userPoints.setText(String.valueOf(currnPoints));
                            if (currnPoints > 1000) {
								if(currnPoints > sharedPref.getCurrentPoints()) {
                                    long curnPoints = currnPoints - sharedPref.getCurrentPoints();
                                    float dollar = (float) (curnPoints/10000.00);
                                    float mCurrentdollr = dollar + mCurrentDollr;
                                    Toast.makeText(MainActivity.this, "Send successfully", Toast.LENGTH_SHORT).show();
                                    mCurrentDollar = String.valueOf(mCurrentdollr);
                                    userDollar.setText("$"+ mCurrentDollar);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("dollar", mCurrentDollar);
                                    databaseReferece.child("Users").child(userId).updateChildren(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //mCurrentDollar = "";
                                                    sharedPref.putCurrentPoints(currnPoints);
                                                    Toast.makeText(MainActivity.this, "Send data successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
									
								}else {
                                    userDollar.setText("$"+ mCurrentDollar);
								}

                            }else {
                                sharedPref.putCurrentPoints(0);
                            }
                          
                            /*Glide.with(getApplicationContext())
                                    .load(model.getImage())
                                    .placeholder(R.drawable.image)
                                    .into(userImage);*/
                            /*Glide.with(getApplicationContext())
                                    .load(model.getUserImage())
                                    .placeholder(R.drawable.image)
                                    .into(circleImage);*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawers();
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_Spin_And_Earn:
                        startActivity(new Intent(MainActivity.this, SpinnerActivity.class));
                        break;

                    case R.id.nav_How_to_Work:
                        break;

                    case R.id.nav_Redeem:
                        break;

                    case R.id.nav_Refer_And_Earn:
                        startActivity(new Intent(MainActivity.this, ReferActivity.class));
                        break;

                    case R.id.nav_Rate:
                        break;

                    case R.id.nav_Telegram_Channel:
                        break;

                    case R.id.nav_Privacy_Policy:
                        break;

                    case R.id.nav_signout:
                        firebaseLogout.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;

                }
                return true;
            }
        });

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View v)
            {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v)
            {
                super.onDrawerOpened(v);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    void switchfragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content,fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");

                }
                //Token
                String token = task.getResult();
                Log.d(TAG, "onComplete: " + token);
            }

        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotification";
            String description = "Recieve firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void dailyCheck() {
        String uid = FirebaseAuth.getInstance().getUid();
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Please Wait");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();


        final Date currentDate = Calendar.getInstance().getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        assert uid != null;
        databaseReference.child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            User userModel = snapshot.getValue(User.class);
                            assert userModel != null;
                            String userDate = userModel.getDate();

                            try {
                                assert userDate != null;
                                Date dbDate = simpleDateFormat.parse(userDate);

                                String nDate = simpleDateFormat.format(currentDate);
                                Date date = simpleDateFormat.parse(nDate);

                                assert date != null;
                                if (date.after(dbDate) && date.compareTo(dbDate) != 0) {
                                    //reward available
                                    String nCurrentDollar =  userModel.getDollar();
                                    float update = (float) (Float.parseFloat(nCurrentDollar) + 0.10);
                                    String nUdate = String.valueOf(update); //String.format(Locale.getDefault(), "%.2f", String.valueOf(update));

                                    long DailyCheck = userModel.getDailyCheckCount();
                                    long updatedDailyCheck = DailyCheck + 1;

                                    Date newDate = Calendar.getInstance().getTime();
                                    String newDateString = simpleDateFormat.format(newDate);

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("dollar", nUdate);
                                    map.put("dailyCheckCount", updatedDailyCheck);
                                    map.put("date", newDateString);

                                    databaseReference.child("Users").child(uid).updateChildren(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                    sweetAlertDialog.setTitleText("Success");
                                                    sweetAlertDialog.setContentText("Coins added to your account successfully");
                                                    sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            userDollar.setText("$"+ nUdate);
                                                            sweetAlertDialog.dismissWithAnimation();
                                                        }
                                                    }).show();

                                                }
                                            });

                                } else {

                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitleText("Failed");
                                    sweetAlertDialog.setContentText("You have already rewarded, come back tomorrow");
                                    sweetAlertDialog.setConfirmButton("Dismiss", null);
                                    sweetAlertDialog.show();

                                }

                            } catch (ParseException e) {
                                e.printStackTrace();

                                sweetAlertDialog.dismissWithAnimation();

                            }


                        } else {

                            sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            sweetAlertDialog.setTitleText("System Busy");
                            sweetAlertDialog.setContentText("System is busy, please try again later!");
                            sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                            sweetAlertDialog.show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });



    }
    @Override
    public void onBackPressed() {
        exitSweetAlert();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("USERPOINTS", String.valueOf(currnPoints));
        outState.putString("USERDOLLAR", mCurrentDollar);
        outState.putLong("CURRENTPOINTS", currnPoints);
        outState.putString("MCURRENTDOLLAR", mCurrentDollar);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        userPoints.setText(savedInstanceState.getString("USERPOINTS"));
        userDollar.setText(savedInstanceState.getString("USERDOLLAR"));
        currnPoints = savedInstanceState.getLong("CURRENTPOINTS");
        mCurrentDollar = savedInstanceState.getString("MCURRENTDOLLAR");
        super.onRestoreInstanceState(savedInstanceState);
    }


    public void exitSweetAlert() {
        SweetAlertDialog sweetAlert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sweetAlert.setTitleText("Do you want to exit?");
        sweetAlert.setCancelable(true);
        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlert) {
                finish();
            }
        });
        sweetAlert.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //CookieSyncManager.getInstance().sync();
    }



}