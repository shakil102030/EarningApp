package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {
    private Spinner paymentMethodSpin;
    private TextView mCurrentDollarText, withdrawNoticeText;
    private Button sendBtn;
    private EditText account_no, amountEditText;
    DatabaseReference databaseRefrence;
    SharedPref sharedPref;
    String currentDollar;
    String mCurentDollar;
    String currentName;
	long currentPoints;
    String withdrawNotice;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        //initializing views
        paymentMethodSpin = (Spinner) findViewById(R.id.paymentMethodSpinnerId);
        mCurrentDollarText = findViewById(R.id.currentDollarsId);
        sendBtn = (Button) findViewById(R.id.submitButtonId);
        account_no = (EditText) findViewById(R.id.accountEditTextId);
        amountEditText = (EditText) findViewById(R.id.amountEditTextId);
        withdrawNoticeText = (TextView) findViewById(R.id.withdrawNoticeId);

        databaseRefrence = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getUid();
        assert uid != null;

        sharedPref = new SharedPref(WalletActivity.this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Redeem");
		initialActionBar();
        loadSpinner();
        getWithrawDollars();
        getWithrawNotice();


        databaseRefrence.child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            User model = snapshot.getValue(User.class);
                            assert model != null;
                            currentName = model.getName();
                            currentDollar = model.getDollar();
                            currentPoints = model.getPoints();
                            mCurrentDollarText.setText("$" + currentDollar);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(WalletActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dollar = sharedPref.getWithdrawDollar();
                String uid = FirebaseAuth.getInstance().getUid();
                String account = account_no.getText().toString();
                final String amount = amountEditText.getText().toString();
                String paymentMethod = paymentMethodSpin.getSelectedItem().toString();
                //checking if EditText is empty
                if(TextUtils.isEmpty(account) && account != null){
                    Toast.makeText(WalletActivity.this,"Please enter your account",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(amount) && amount != null){
                    Toast.makeText(WalletActivity.this,"Please enter an amount",Toast.LENGTH_LONG).show();
                    return;
                }
                float mAmount = Float.parseFloat(amount);
                float mCurrentDollr = Float.parseFloat(currentDollar);
                if(mCurrentDollr > dollar && mAmount <= mCurrentDollr && mAmount > 0) {
                    float mCurrentDoll = mCurrentDollr - mAmount;
                    currentDollar = String.valueOf(mCurrentDoll);

					float currentPointsDollar = (float) (currentPoints/10000.00);
					if (mAmount >= currentPointsDollar) {
						currentPoints = 0;
					}else {
                        float reCurentPoints = (float) (mAmount * 10000.00);
						currentPoints= (long) (currentPoints - reCurentPoints);
					}
                    HashMap<String, Object> currentDollarMap = new HashMap<>();
                    currentDollarMap.put("dollar", currentDollar);
					currentDollarMap.put("points", currentPoints);
			
                    databaseRefrence.child("Users").child(uid).updateChildren(currentDollarMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mCurrentDollarText.setText("$" +currentDollar);
									sharedPref.putCurrentPoints(currentPoints);

                                }
                            });
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", currentName);
                    map.put("account", account);
                    map.put("amount", amount);
                    map.put("paymentMethod", paymentMethod);

                    databaseRefrence.child("Withdraws").child(uid).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(WalletActivity.this, "Send successfully", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                } else {
                    Toast.makeText(WalletActivity.this, "You need more poins to get withdraw.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void getWithrawDollars(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Minimum_withdraw");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    String key = snapshot.getKey();
                    if (key.equals("Minimum_withdraw")) {
                        String mDollar = snapshot.getValue(String.class);
                        assert mDollar != null;
                        sharedPref.putWithdrawDollar(Integer.parseInt(mDollar));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalletActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWithrawNotice(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("withdraw_notice");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    String key = snapshot.getKey();
                    if (key.equals("withdraw_notice")) {
                        withdrawNotice = snapshot.getValue(String.class);
                        assert withdrawNotice != null;
                        withdrawNoticeText.setText(withdrawNotice);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalletActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSpinner(){
        String[] paymentMethods = WithdrawModel.getPaymentMethods();
        ArrayAdapter<String> paymentMethodsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, paymentMethods);
        paymentMethodsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        paymentMethodSpin.setAdapter(paymentMethodsAdapter);

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
}