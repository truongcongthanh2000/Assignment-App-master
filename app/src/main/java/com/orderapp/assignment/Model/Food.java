package com.orderapp.assignment.Model;

public class Food {
    private String name;
    private String nameRestaurant;
    private String linkPicture;
    private String idRestaurant;
    private long price;
    private int status;

    public Food() {
    }

    public Food(String name, String nameRestaurant, String linkPicture, String idRestaurant, long price, int status) {

        this.name = name;
        this.nameRestaurant = nameRestaurant;
        this.linkPicture = linkPicture;
        this.idRestaurant = idRestaurant;
        this.price = price;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }

    public String getLinkPicture() {
        return linkPicture;
    }

    public void setLinkPicture(String linkPicture) {
        this.linkPicture = linkPicture;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
