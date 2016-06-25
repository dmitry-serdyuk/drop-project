package com.dmitry.drop.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dmitry.drop.project.utility.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Laptop on 20/06/2016.
 */
public class CameraActivity extends Activity {

    public static final String CAMERA_IMG_FILE_PATH = "cameraImgFilePath";
    public static final String THUMBNAIL_IMG_FILE_PATH = "thumbnailImgFilePath";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private File mCameraImgFile;
    private File mThumbnailImgFile;
    private String mCameraImgFilePath;
    private String mThumbnailImgFilePath;
    private Bitmap mCameraImgBitmap;
    private Bitmap mThumbnailBitmap;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go

            try {
                mCameraImgFile = createCameraImgFile();
                mThumbnailImgFile = createThumbnailImgFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(Constants.DEBUG_TAG, ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (mCameraImgFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mCameraImgFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createThumbnailImgFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String mImageFileName = "JPEG_" + "Thumbnail_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mThumbnailImgFilePath = image.getAbsolutePath();
        return image;
    }

    private File createCameraImgFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String mImageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraImgFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            //get bitmap from camera activity file path
            mCameraImgBitmap = BitmapFactory.decodeFile(mCameraImgFilePath);

            //rotate the camera image and save to file
            rotateImages();
            writeFile(mCameraImgFilePath, mCameraImgBitmap);

            //create a thumbnail from the camera image, compress, crop and write to file
            mThumbnailBitmap = mCameraImgBitmap;
            compressBitmap(mThumbnailBitmap);
            circleCropImage();
            writeFile(mThumbnailImgFilePath, mThumbnailBitmap);

            returnImage();

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void returnImage() {
        Intent returnFilePaths = new Intent();
        returnFilePaths.putExtra(CAMERA_IMG_FILE_PATH, mCameraImgFilePath);
        returnFilePaths.putExtra(THUMBNAIL_IMG_FILE_PATH, mThumbnailImgFilePath);
        setResult(RESULT_OK, returnFilePaths);
        finish();
    }

    private void circleCropImage() {
        int width = mThumbnailBitmap.getWidth();
        int height = mThumbnailBitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        RadialGradient gradient = new RadialGradient(width / 2, height / 2, height / 5,
                new int[]{0xFFFFFFFF, 0xFFFFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.8f, 1.0f},
                android.graphics.Shader.TileMode.CLAMP);
        final Paint paint = new Paint();
        paint.setShader(gradient);

        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(width / 2, height / 2,
                width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(mThumbnailBitmap, rect, rect, paint);

        mThumbnailBitmap = output;
    }

    private void compressBitmap(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        Bitmap bmp = bitmap;
        options.inJustDecodeBounds = true;
        int actualHeight = bitmap.getHeight();
        int actualWidth = bitmap.getWidth();

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
            bitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(bitmap);
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

    private void rotateImages() {
        ExifInterface exif;
        try {
            exif = new ExifInterface(mCameraImgFilePath);

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

            mCameraImgBitmap = Bitmap.createBitmap(mCameraImgBitmap, 0, 0,
                    mCameraImgBitmap.getWidth(), mCameraImgBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(String filePath, Bitmap bitmap) {

        FileOutputStream out;
        try {
            out = new FileOutputStream(filePath);

            //Write the compressed bitmap at the destination specified by filename.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mCameraImgFile.delete();
        mThumbnailImgFile.delete();
        finish();
    }
}
