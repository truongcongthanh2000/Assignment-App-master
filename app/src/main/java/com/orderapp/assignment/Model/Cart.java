package com.orderapp.assignment.Model;

public class Cart {
    private String nameFood;
    private String nameRes;
    private String IDRes;
    private String linkPics;
    private long price;
    private long amount;
    private  long pay;

    public Cart() {
    }

    public Cart(String nameFood, String nameRes, String IDRes, String linkPic, long price, long amount, long sumPayment) {
        this.nameFood = nameFood;
        this.nameRes = nameRes;
        this.IDRes = IDRes;
        this.linkPics = linkPic;
        this.price = price;
        this.amount = amount;
        this.pay = sumPayment;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public String getNameRes() {
        return nameRes;
    }

    public void setNameRes(String nameRes) {
        this.nameRes = nameRes;
    }

    public String getIDRes() {
        return IDRes;
    }

    public void setIDRes(String IDRes) {
        this.IDRes = IDRes;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPay() {
        return pay;
    }

    public void setPay(long pay) {
        this.pay = pay;
    }

    public String getLinkPics() {
        return linkPics;
    }

    public void setLinkPics(String linkPics) {
        this.linkPics = linkPics;
    }
}
