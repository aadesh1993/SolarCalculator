package com.example.solarcalculator.Models;

public class PlacesModel {

    private String place;
    private Double latitude;
    private Double longitude;

    public PlacesModel(String place, Double latitude, Double longitude) {
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
