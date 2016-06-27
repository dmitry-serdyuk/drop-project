package com.dmitry.drop.project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Dmitry on 7/06/2016.
 *
 * Reply data transfer object, contain fields which are annotated with ActiveAndroid ORM.
 * ORM handles creating fields and relationships between DTO's
 * A reply have a One to Many relationship with a post by having its own 'post' object
 */

@Table(name = "Replies")
public class Reply extends Model implements Parcelable {

    @Column(name = "Post")
    public Post post;
    @Column(name = "ImageFilePath")
    String imageFilePath;
    @Column(name = "Comment")
    String comment;
    @Column(name = "Author")
    String author;
    @Column(name = "Date")
    String dateCreated;

    // Needed for ActiveAndroid library
    public Reply() {
        super();
    }

    public Reply(Post post, String author, String comment, String dateCreated, String imageFilePath) {
        super();
        this.post = post;
        this.author = author;
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.imageFilePath = imageFilePath;
    }

    private Reply(Parcel in) {
        post = in.readParcelable(Reply.class.getClassLoader());
        imageFilePath = in.readString();
        comment = in.readString();
        author = in.readString();
        dateCreated = in.readString();
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


    public String getComment() {
        return comment;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(post, i);
        parcel.writeString(imageFilePath);
        parcel.writeString(comment);
        parcel.writeString(author);
        parcel.writeString(dateCreated);
    }
}
