package com.dmitry.drop.project.model;

import android.graphics.AvoidXfermode;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by Laptop on 7/06/2016.
 */

@Table(name = "Replies")
public class Reply extends Model implements Parcelable {

    @Column(name = "ImageFilePath")
    String imageFilePath;

    @Column(name = "Comment")
    String comment;

    @Column(name = "Author")
    String author;

    @Column(name = "Date")
    String dateCreated;

    @Column(name = "Post")
    public Post post;

    public Reply() { super(); }

    public Reply(Post post, String author, String comment, String dateCreated, String imageFilePath) {
        super();
        this.post = post;
        this.author = author;
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.imageFilePath = imageFilePath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageFilePath);
        parcel.writeString(comment);
        parcel.writeString(author);
        parcel.writeString(dateCreated);
    }

    public static final Parcelable.Creator<Reply> CREATOR
            = new Parcelable.Creator<Reply>() {
        public Reply createFromParcel(Parcel in) {
            return new Reply(in);
        }

        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };

    private Reply(Parcel in) {
        imageFilePath = in.readString();
        comment = in.readString();
        author = in.readString();
        dateCreated = in.readString();
    }
}
