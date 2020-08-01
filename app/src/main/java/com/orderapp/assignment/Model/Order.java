package com.orderapp.assignment.Model;

public class Order {
    private String cusPhone;
    private String UserID;
    private String cusName;
    private String resName;
    private String resID;
    private String foodName;
    private long price;
    private long number;
    private String linkPic;
    private String dateTime;
    private int check;

    public Order() {
    }

    public Order(String dateTime, String cusPhone, String userID, String cusName, String resName, String resID, String foodName, long price, long number, String linkPic,int check) {
        this.dateTime =dateTime;
        this.cusPhone = cusPhone;
        this.UserID = userID;
        this.cusName = cusName;
        this.resName = resName;
        this.resID = resID;
        this.foodName = foodName;
        this.price = price;
        this.number = number;
        this.linkPic = linkPic;
        this.check =check;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getcusPhone() {
        return cusPhone;
    }

    public void setcusPhone(String cusPhone) {
        this.cusPhone = cusPhone;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getcusName() {
        return cusName;
    }

    public void setcusName(String cusName) {
        this.cusName = cusName;
    }

    public String getresName() {
        return resName;
    }

    public void setresName(String resName) {
        this.resName = resName;
    }

    public String getresID() {
        return resID;
    }

    public void setresID(String resID) {
        resID = resID;
    }

    public String getfoodName() {
        return foodName;
    }

    public void setfoodName(String foodName) {
        this.foodName = foodName;
    }

    public long getprice() {
        return price;
    }

    public void setprice(long price) {
        this.price = price;
    }

    public long getnumber() {
        return number;
    }

    public void setnumber(long number) {
        this.number = number;
    }

    public String getlinkPic() {
        return linkPic;
    }

    public void setlinkPic(String linkPic) {
        this.linkPic = linkPic;
    }
}
