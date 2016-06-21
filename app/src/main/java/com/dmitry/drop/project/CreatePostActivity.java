package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.presenter.CreatePostPresenter;
import com.dmitry.drop.project.presenter.CreatePostPresenterImpl;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.utility.PermissionUtils;
import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Laptop on 7/06/2016.
 */
public class CreatePostActivity extends MvpActivity<CreatePostView, CreatePostPresenter>
        implements
        CreatePostView {

    @BindView(R.id.createPost_cameraImg)
    ImageView cameraImg;

    @BindView(R.id.createPost_annotation)
    EditText annotation;

    @BindView(R.id.createPost_dropButton)
    ImageView dropButton;

    private static final int REQUEST_CAMERA_PHOTO = 1;

    private double mLongitude;
    private double mLatitude;
    private String mImageFilePath;

    public static Intent createIntent(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, CreatePostActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        return intent;
    }

    @NonNull
    @Override
    public CreatePostPresenter createPresenter() {
        return new CreatePostPresenterImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);


        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        mLongitude = getIntent().getDoubleExtra("longitude", 0);

        Intent takePhoto = new Intent(this, CameraActivity.class);
        startActivityForResult(takePhoto, REQUEST_CAMERA_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK) {
            mImageFilePath = data.getStringExtra(CameraActivity.FILE_PATH);
            Bitmap photo = BitmapFactory.decodeFile(mImageFilePath);
            cameraImg.setImageBitmap(photo);
        } else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @OnClick(R.id.createPost_cameraImg)
    public void onDropButtonClick() {
        presenter.onDropButtonClick();
    }

    @Override
    public void returnToWorldMap() {
        Intent worldMapIntent = new Intent();
        worldMapIntent.putExtra(Constants.LATITUDE, mLatitude);
        worldMapIntent.putExtra(Constants.LONGITUDE, mLongitude);
        setResult(RESULT_OK, worldMapIntent);
        finish();
    }


    // TODO: Move to model
    @Override
    public void savePost() {
        String annotationText = annotation.getText().toString();
        String date = DateFormat.getInstance().format(new Date());
        Post post = new Post(annotationText, mImageFilePath, mLatitude, mLongitude, date);
        post.save();
    }
}
