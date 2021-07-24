package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {
    //defining firebase object
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //defining view objects
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextReferCode;
    private Button signupButton;
    private TextView backToLogin, termAndCondition;;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //initializing firebase auth object
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //initializing views
        editTextUsername = (EditText) findViewById(R.id.usernameId);
        editTextEmail = (EditText) findViewById(R.id.emailId);
        editTextPassword = (EditText) findViewById(R.id.passwordId);
        editTextReferCode = (EditText) findViewById(R.id.referCodeId);
        signupButton = (Button) findViewById(R.id.signupButtonId);
        backToLogin = (TextView) findViewById(R.id.backToLoginId);
        termAndCondition = (TextView) findViewById(R.id.termAndConditionTextId);

        dialog = new ProgressDialog(this, R.style.CustomDialog);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("You're creating new account");


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name, email, pass, referCode;
                //getting name, email, password, refercode from edit texts
                name = editTextUsername.getText().toString();
                email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();
                referCode = editTextReferCode.getText().toString();
                //checking if name, email, pass and refercode are empty
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(SignUpActivity.this,"Please enter name",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(SignUpActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(referCode)){
                    Toast.makeText(SignUpActivity.this,"Please enter refercode",Toast.LENGTH_LONG).show();
                    return;
                }


                //final User userModel = new User(name, email, pass, referCode);
                dialog.show();

                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    final String userId = task.getResult().getUser().getUid();
                                    //createData(userId, email);
                                    //send email verification link
                                    auth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        createData(userId, email);
                                                    } else {
                                                        new SweetAlertDialog(SignUpActivity.this)
                                                                .setContentText(task.getException().getLocalizedMessage())
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
                                            });

                                } else {
                                    new SweetAlertDialog(SignUpActivity.this)
                                            .setContentText(task.getException().getLocalizedMessage())
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
                        });
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsAndCondition();
            }
        });



    }

    private void createData(String uid, String email) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // to get yesterday date
        Date previousDate = calendar.getTime();

        String mDate = simpleDateFormat.format(previousDate);

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", editTextUsername.getText().toString());
        map.put("email", email);
        map.put("pass", editTextPassword.getText().toString());
        map.put("referCode", editTextReferCode.getText().toString());
        map.put("points", 0);
        map.put("dollar", "0.00");
        map.put("date", mDate);
        map.put("image", " ");
        map.put("dailyCheckCount", 0);
        map.put("redeem", false);

        databaseReference = firebaseDatabase.getReference().child("Users");
        databaseReference.child(uid)
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpActivity.this, "SignUp Successfully, Please verify your email",
                                    Toast.LENGTH_SHORT).show();

                            finish();

                        } else {
                            new SweetAlertDialog(SignUpActivity.this)
                                    .setContentText(task.getException().getLocalizedMessage())
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
                });
    }

    private void TermsAndCondition() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Task status")
                .setMessage(Html.fromHtml("Term and condition"))
                .setCancelable(false)
                .setPositiveButton("I agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}