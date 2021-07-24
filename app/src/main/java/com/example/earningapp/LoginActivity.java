package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    //defining view objects
    private EditText editTextUsername;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView backToSignUp;
    private TextView forgetPass;

    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing views
        loginEmail = (EditText) findViewById(R.id.emailLoginId);
        loginPassword = (EditText) findViewById(R.id.passwordLoginId);
        loginButton = (Button) findViewById(R.id.loginButtonId);
        backToSignUp = (TextView) findViewById(R.id.backToSignUpId);
        forgetPass = (TextView) findViewById(R.id.forgetPassTextId);
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this, R.style.CustomDialog);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("You're creating new account");
        dialog.setMessage("Logging in...");
        
        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass;
                email = loginEmail.getText().toString();
                pass = loginPassword.getText().toString();


                //checking if email and passwords are empty
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    return;
                }


                dialog.show();
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            /*Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();*/

                            FirebaseUser user = auth.getCurrentUser();
                            //Check if user is verified
                            assert user != null;
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }else{
                                new SweetAlertDialog(LoginActivity.this)
                                        .setContentText("Please verify your email")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }



                        } else {
                            new SweetAlertDialog(LoginActivity.this)
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

        //reset password
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(LoginActivity.this);
                editText.setHint("Enter your email");

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

                alertDialog.setTitle("Reset your password");

                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mEmail = editText.getText().toString();

                        if (TextUtils.isEmpty(mEmail)) {
                            Toast.makeText(LoginActivity.this, "Enter your valid email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(mEmail).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Reset email instructions sent to " + mEmail, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, mEmail + " does not exist", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();


            }
        });

        backToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }



}