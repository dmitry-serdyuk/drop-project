package com.dmitry.drop.project.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Laptop on 20/05/2016.
 */

@Table(name = "Posts")
public class Post extends Model{

    @Column(name = "ImageFilePath")
    String imageFilePath;

    @Column(name = "Annotation")
    String annotation;

    @Column(name = "Latitude")
    double latitude;

    @Column(name = "Longitude")
    double longitude;
    final double INIT_RADIUS = 100;

    @Column(name = "Radius")
    double radius;

    @Column(name = "Date")
    String dateCreated;

    public Post() {super();}

    public Post(String annotation, String imageFilePath, double latitude, double longitude, String dateCreated) {
        super();
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

    public static List<Post> getAllPosts() {
        return new Select().from(Post.class).executeSingle();
    }

}
