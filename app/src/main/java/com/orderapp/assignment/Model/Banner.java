package com.orderapp.assignment.Model;

public class Banner {
    private String id,idRes,image;

    public Banner() {
    }

    public Banner(String id, String idRes, String image) {
        this.id = id;
        this.idRes = idRes;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRes() {
        return idRes;
    }

    public void setIdRes(String idRes) {
        this.idRes = idRes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
