package com.fedco.mbc.CustomClasses;

import java.io.Serializable;
import java.util.HashMap;

public class ReceiptModel implements Serializable {

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionStatusReason() {
        return transactionStatusReason;
    }

    public void setTransactionStatusReason(String transactionStatusReason) {
        this.transactionStatusReason = transactionStatusReason;
    }

    public HashMap <String, String> getMap() {
        return map;
    }

    public void setMap(HashMap <String, String> map) {
        this.map = map;
    }

    String amount,transactionType,transactionStatus,transactionStatusReason;
    HashMap<String,String> map;

}
