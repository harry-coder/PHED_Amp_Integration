package com.fedco.mbc.CustomClasses;

public class DayEndReport {


    public double getCashNumber() {
        return cashNumber;
    }

    public void setCashNumber(double cashNumber) {
        this.cashNumber = cashNumber;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getChequenumber() {
        return chequenumber;
    }

    public void setChequenumber(double chequenumber) {
        this.chequenumber = chequenumber;
    }

    public double getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(double chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    public double getDdNumber() {
        return ddNumber;
    }

    public void setDdNumber(double ddNumber) {
        this.ddNumber = ddNumber;
    }

    public double getDdAmount() {
        return ddAmount;
    }

    public void setDdAmount(double ddAmount) {
        this.ddAmount = ddAmount;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(double totalCash) {
        this.totalCash = totalCash;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    double cashNumber,cashAmount,chequenumber,chequeAmount,ddNumber,ddAmount,totalCash,totalAmount;
}
