package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {
    private TextView mReferCode;
    private EditText mRedeem;
    private Button shareCodeBtn;
    private Button redeemCodeBtn;
    DatabaseReference databaseReference;
    private String oppositeUserId;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        mReferCode = findViewById(R.id.referCodeTextId);
        shareCodeBtn = findViewById(R.id.shareButtonId);
        redeemCodeBtn = findViewById(R.id.redeemButtonId);
        mRedeem = findViewById(R.id.redeemEditTextId);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseAuth.getInstance().getUid();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Refer & Earn");
        initialActionBar();
        getUserData();
        redeemAvailability();

        redeemCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mRedeem.getText().toString())) {
                    Toast.makeText(ReferActivity.this, "Enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mRedeem.getText().toString().equals(mReferCode.getText().toString())) {
                    Toast.makeText(ReferActivity.this, "You can not enter your own code",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                redeemQuery(mRedeem.getText().toString());
            }
        });

        shareCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String referCode = mReferCode.getText().toString();

                String referCodeShare = referCode;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, referCodeShare);
                startActivity(intent);

            }
        });


    }

    private void getUserData(){
        databaseReference.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referCode = snapshot.child("referCode").getValue(String.class);
                        mReferCode.setText(referCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReferActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    private void redeemAvailability() {
        databaseReference.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChild("redeem")) {
                            boolean isAvailable = snapshot.child("redeem").getValue(Boolean.class);
                            if (isAvailable) {
                                redeemCodeBtn.setVisibility(View.GONE);
                                redeemCodeBtn.setEnabled(false);
                            } else {
                                redeemCodeBtn.setEnabled(true);
                                redeemCodeBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReferActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void redeemQuery(String redeemText) {
        Query query = databaseReference.orderByChild("referCode").equalTo(redeemText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    oppositeUserId = dataSnapshot.getKey();
                    databaseReference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User oppositeUser = snapshot.child(oppositeUserId).getValue(User.class);
                                    User mUser = snapshot.child(userId).getValue(User.class);

                                    assert oppositeUser != null;
                                    assert mUser != null;

                                    int oppositeUserPoints = (int) oppositeUser.getPoints();
                                    int updatedOppositeUserPoints = oppositeUserPoints + 50;

                                    int mUserPoints = (int) mUser.getPoints();
                                    int updateUserPoints = mUserPoints + 50;

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("points", updatedOppositeUserPoints);

                                    HashMap<String, Object> myMap = new HashMap<>();
                                    myMap.put("points", updateUserPoints);
                                    myMap.put("redeem", true);

                                    databaseReference.child(oppositeUserId).updateChildren(map);
                                    databaseReference.child(userId).updateChildren(myMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(ReferActivity.this, "Congratulation", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReferActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

    /*@Override
    public void onBackPressed() {

    }*/
}