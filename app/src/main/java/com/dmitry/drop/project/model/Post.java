package com.dmitry.drop.project.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.dmitry.drop.project.utility.Constants;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Dmitry on 20/05/2016.
 *
 * Post data transfer object, contain fields which are annotated with ActiveAndroid ORM.
 * ORM handles creating fields and relationships between DTO's
 */

@Table(name = "Posts")
public class Post extends Model implements Parcelable {

    @Column(name = "ImageFilePath")
    String imageFilePath;
    @Column(name = "ThumbnailFilePath")
    String thumbnailFilePath;
    @Column(name = "Annotation")
    String annotation;
    @Column(name = "Latitude")
    double latitude;
    @Column(name = "Longitude")
    double longitude;
    @Column(name = "Radius")
    float radius;
    @Column(name = "Date")
    String dateCreated;
    @Column(name = "Likes")
    int likes;

    // Needed for ActiveAndroid library
    public Post() {
        super();
    }

    public Post(String annotation, String imageFilePath, String thumbnailFilePath, double latitude, double longitude, String dateCreated) {
        super();
        this.annotation = annotation;
        this.imageFilePath = imageFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = dateCreated;
        likes = 0;
        radius = Constants.INIT_POST_RADIUS;
    }

    public static final Parcelable.Creator<Post> CREATOR
            = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    private Post(Parcel in) {
        imageFilePath = in.readString();
        thumbnailFilePath = in.readString();
        annotation = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readFloat();
        dateCreated = in.readString();
        likes = in.readInt();
    }

    public static List<Post> getAll() {
        return new Select().from(Post.class).execute();
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

    public float getRadius() {
        return radius;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<Reply> replies() {
        return getMany(Reply.class, "Post");
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }

    public boolean isWithinRadius(double latitude, double longitude) {
        float[] distance = new float[1];
        Location.distanceBetween(latitude, longitude, this.latitude, this.longitude, distance);
        return distance[0] < radius;
    }

    public void like(boolean liked) {
        if (liked) {
            likes++;
            radius += Constants.LIKE_ADD_RADIUS;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageFilePath);
        parcel.writeString(thumbnailFilePath);
        parcel.writeString(annotation);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeFloat(radius);
        parcel.writeString(dateCreated);
        parcel.writeInt(likes);
    }
}
