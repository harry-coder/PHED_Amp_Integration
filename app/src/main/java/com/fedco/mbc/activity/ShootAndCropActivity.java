package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fedco.mbc.R;


import java.io.File;


/**
 * ShootAndCropActivity demonstrates capturing and cropping camera images
 * - user presses button to capture an image using the device camera
 * - when they return with the captured image Uri, the app launches the crop action intent
 * - on returning from the crop action, the app displays the cropped image
 * <p/>
 * Sue Smith
 * Mobiletuts+ Tutorial: Capturing and Cropping an Image with the Android Camera
 * July 2012
 */
public class ShootAndCropActivity extends Activity {

    public static final String TAG = "ShootAndCropActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.bmp";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private ImageView mImageView;
    private File mFileTemp;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot_and_crop);

/*IMAGE CROPPING SAMPLE*/
        findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
            }
        });

        mImageView = (ImageView) findViewById(R.id.image);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }


    /*THIS FUNCTIONA WILL BE USED WHEN A PICTURE NEEDS TO BE COLLECTED FROM STORAGE*/
//    private void openGallery() {
//
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
//    }
    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                /*
	        	 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
//                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
    }

//    private void startCropImage() {
//
//        Intent intent = new Intent(getApplicationContext(), CropImage.class);
//        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
//        intent.putExtra(CropImage.SCALE, true);
//
//        intent.putExtra(CropImage.ASPECT_X, 3);
//        intent.putExtra(CropImage.ASPECT_Y, 2);
//
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {

            return;
        }

        Bitmap bitmap;

        switch (requestCode) {

//            case REQUEST_CODE_GALLERY:
//
//                try {
//
//                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
//                    copyStream(inputStream, fileOutputStream);
//                    fileOutputStream.close();
//                    inputStream.close();
//
//                    startCropImage();
//
//                } catch (Exception e) {
//
//                    Log.e(TAG, "Error while creating temp file", e);
//                }
//
//                break;
            case REQUEST_CODE_TAKE_PICTURE:

//                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:

//                String path = data.getStringExtra(CropImage.IMAGE_PATH);
//                if (path == null) {
//
//                    return;
//                }
//
//                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
//                mImageView.setImageBitmap(bitmap);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
//        overridePendingTransition(R.anim.anim_slide_in_right,
//                R.anim.anim_slide_out_right);
//
//        Debug.stopMethodTracing();
//        super.onDestroy();
//        } else {
//            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }
    }

//    public static void copyStream(InputStream input, OutputStream output)
//            throws IOException {
//
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = input.read(buffer)) != -1) {
//            output.write(buffer, 0, bytesRead);
//        }
//    }
}
