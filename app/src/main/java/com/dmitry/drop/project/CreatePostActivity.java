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
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.presenter.CreatePostPresenterImpl;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.utility.PermissionUtils;
import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Laptop on 7/06/2016.
 */
public class CreatePostActivity extends MvpActivity<CreatePostView, CreatePostPresenterImpl>
        implements
        CreatePostView {

    @BindView(R.id.createPost_cameraImg)
    ImageView cameraImg;

    @BindView(R.id.createPost_annotation)
    EditText annotation;

    @BindView(R.id.createPost_dropButton)
    ImageView dropButton;

    private double mLongitude;
    private double mLatitude;
    private String mImageFilePath;
    private File mTempFile;


    private String mImageFileName;
    private Bitmap mScaledBitmap;

    //private int mPhoneOrientation;
    private static final int REQUEST_TAKE_PHOTO = 1;

    public static Intent createIntent(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, CreatePostActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        return intent;
    }

    @NonNull
    @Override
    public CreatePostPresenterImpl createPresenter() {
        return new CreatePostPresenterImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);

        mTempFile = null;

        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        mLongitude = getIntent().getDoubleExtra("longitude", 0);

        //mPhoneOrientation = getResources().getConfiguration().orientation;

        PermissionUtils.verifyStoragePermissions(this);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go

            try {
                mTempFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(Constants.DEBUG_TAG, ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (mTempFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mTempFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        mImageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mImageFilePath = image.getAbsolutePath();
        return image;
    }

    private void rotateImg() {
        ExifInterface exif;
        try {
            exif = new ExifInterface(mImageFilePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d(Constants.DEBUG_TAG, "Exif original: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d(Constants.DEBUG_TAG, "Exif modified: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d(Constants.DEBUG_TAG, "Exif modified: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d(Constants.DEBUG_TAG, "Exif modified: " + orientation);
            }

            mScaledBitmap = Bitmap.createBitmap(mScaledBitmap, 0, 0,
                    mScaledBitmap.getWidth(), mScaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compressImg() {
        BitmapFactory.Options options = new BitmapFactory.Options();

        //Only load the bounds not actual pixels
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(mImageFilePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //Max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //Width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        //Setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //This options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //Load the bitmap from its path
            bmp = BitmapFactory.decodeFile(mImageFilePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            mScaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(mScaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void writeCompressedBitmap() {
        PermissionUtils.verifyStoragePermissions(this);
        FileOutputStream out;
        try {
            out = new FileOutputStream(mImageFilePath);

//          write the compressed bitmap at the destination specified by filename.
            mScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            mScaledBitmap = BitmapFactory.decodeFile(mImageFilePath);

            compressImg();
            rotateImg();
            writeCompressedBitmap();

            cameraImg.setImageBitmap(mScaledBitmap);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            finish();
        }
    }


    @Override
    @OnClick(R.id.createPost_cameraImg)
    public void onDropButtonClick() {
        presenter.onDropButtonClick();


        savePost();

        Intent worldMapIntent = new Intent();
        worldMapIntent.putExtra("latitude", mLatitude);
        worldMapIntent.putExtra("longitude", mLongitude);
        setResult(RESULT_OK, worldMapIntent);
        finish();
    }

    @Override
    public void savePost() {
        String annotationText = annotation.getText().toString();
        Post post = new Post(annotationText, mImageFilePath, mLatitude, mLongitude, new Date().toString());
        post.save();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mTempFile.delete();
        finish();
    }
}
