package com.dmitry.drop.project.model;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Laptop on 20/05/2016.
 */
public class Post extends SugarRecord {
    String imageFilePath;
    String annotation;
    double latitude;
    double longitude;
    final double INIT_RADIUS = 100;
    double radius;
    String dateCreated;

    public Post() {}

    public Post(String annotation, String imageFilePath, double latitude, double longitude, String dateCreated) {
        this.annotation = annotation;
        this.imageFilePath = imageFilePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = dateCreated;
        radius = INIT_RADIUS;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public List<Reply> getReplies() {
        return Reply.find(Reply.class, "post = ?", String.valueOf(getId()));
    }

}
