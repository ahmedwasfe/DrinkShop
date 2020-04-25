package com.ahmet.drinkshop.Model;

public class Store {

    private int id;
    private String name;
    private double latitude;
    private double Longitude;
    private double distance_in_km;

    public Store() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getDistance_in_km() {
        return distance_in_km;
    }

    public void setDistance_in_km(double distance_in_km) {
        this.distance_in_km = distance_in_km;
    }
}
