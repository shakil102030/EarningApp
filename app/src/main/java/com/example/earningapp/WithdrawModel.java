package com.example.earningapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class WithdrawModel {

    public static final String BKASH = "Bkash";
    public static final String ROCKET = "Rocket";
    public static final String NAGAT = "Nagat";
    public static final String PAYPAL = "PayPal";
    public static final String BITCOIN = "Bitcoin";

    private String name, account, amount, paymentMethod;

    public WithdrawModel() {
    }

    public WithdrawModel(String name, String account, String amount, String paymentMethod) {
        this.name = name;
        this.account = account;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static String[] getPaymentMethods(){
        String[] allPaymentMethod = {BKASH, ROCKET, NAGAT, PAYPAL, BITCOIN};
        return allPaymentMethod;
    }
}
