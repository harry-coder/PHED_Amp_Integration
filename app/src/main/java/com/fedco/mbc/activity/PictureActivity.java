package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedco.mbc.BuildConfig;
import com.fedco.mbc.R;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class PictureActivity extends Activity {

    CardView cv_meterReading, cv_meterNumber, cv_meterBox;
    File mFileTemp;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.bmp";
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    TextView tv_next;

    ImageView im_numberImage, im_readingImage, im_boxImage;

    File imagePath;
    public static ArrayList <String> filePaths;

    boolean isNumberPicture, isReadingPicture, isBoxPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_picture );

        cv_meterReading = (CardView) findViewById ( R.id.cv_meterReading );
        cv_meterNumber = (CardView) findViewById ( R.id.cv_meterNumber );
        cv_meterBox = (CardView) findViewById ( R.id.cv_boxImage );
        tv_next = (TextView) findViewById ( R.id.tv_next );

        im_boxImage = findViewById ( R.id.im_boxImage );
        im_readingImage = findViewById ( R.id.im_readingImage );
        im_numberImage = findViewById ( R.id.im_numberImage );

        tv_next.setBackgroundColor ( Color.parseColor ( "#c7c7c7" ) );
        tv_next.setEnabled ( false );


        filePaths = new ArrayList <> ( );

        cv_meterNumber.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                isNumberPicture = true;
                im_numberImage.setVisibility ( View.VISIBLE );
                takePicture ( im_numberImage );


                //  cv_meterNumber.setCardBackgroundColor ( Color.parseColor ( "#c7c7c7" ) );
            }
        } );

        cv_meterReading.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                isReadingPicture = true;
                im_readingImage.setVisibility ( View.VISIBLE );
                takePicture ( im_readingImage );

                tv_next.setBackgroundColor ( Color.parseColor ( "#586ECA" ) );
                tv_next.setEnabled ( true );

                // cv_meterReading.setCardBackgroundColor ( Color.parseColor ( "#c7c7c7" ) );

            }
        } );

        cv_meterBox.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                isBoxPicture = true;
                im_boxImage.setVisibility ( View.VISIBLE );
                takePicture ( im_boxImage );


                //  cv_meterBox.setCardBackgroundColor ( Color.parseColor ( "#c7c7c7" ) );

            }
        } );


        tv_next.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( getApplicationContext ( ), Readinginput.class );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );
            }
        } );


        String state = Environment.getExternalStorageState ( );
        if (Environment.MEDIA_MOUNTED.equals ( state )) {
            mFileTemp = new File ( Environment.getExternalStorageDirectory ( ), TEMP_PHOTO_FILE_NAME );
        } else {
            mFileTemp = new File ( getFilesDir ( ), TEMP_PHOTO_FILE_NAME );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {


                Bitmap bitmap;
                UtilAppCommon uac = new UtilAppCommon ( );


                GPSTracker gps = new GPSTracker ( PictureActivity.this );
                BitmapFactory.Options options = new BitmapFactory.Options ( );
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                options.inMutable=true;

                bitmap = BitmapFactory.decodeFile ( mFileTemp.getPath ( ), options );

                ByteArrayOutputStream outStream = new ByteArrayOutputStream ( );

               // Bitmap drawableBitmap = bitmap.copy ( Bitmap.Config.ARGB_8888, true );

                // drawableBitmap = Utility.rotateImage ( drawableBitmap, 90 );
                Canvas canvas = new Canvas ( bitmap );

                Paint paint = new Paint ( );
                paint.setAntiAlias ( true );
                paint.setColor ( Color.RED );
                paint.setStyle ( Paint.Style.FILL );
                paint.clearShadowLayer ( );

                paint.setTextSize ( 50 );
                String lat_long_on_image = gps.getLatitude ( ) + " : " + gps.getLongitude ( );
//                String lat_long_on_image = "10.23" + " : " + "11.21";
                canvas.drawText ( lat_long_on_image, 120, bitmap.getHeight ( ) - 250, paint );

                paint.setTextSize ( 50 );
                canvas.drawText ( Utility.getCurrentTime ( ), 120, bitmap.getHeight ( ) - 150,
                        paint );
                //////////////////////////////
                bitmap = getResizedBitmap ( bitmap, 1024, 1024 );
                bitmap.compress ( Bitmap.CompressFormat.JPEG, 50, outStream );

              /*  imagePath = new File ( Environment.getExternalStorageDirectory ( )
                        + File.separator + "/MBC/Images/" + uac.UniqueCode ( getApplicationContext
                        ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO +
                        "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + System.currentTimeMillis ( ) + "_mtr.jpg" );*/

       /*         Structbilling.User_Mtr_Img = uac.UniqueCode ( getApplicationContext ( ) ) + "_" +
                        Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg";
*/


                if (isBoxPicture) {
                    imagePath = new File ( Environment.getExternalStorageDirectory ( )
                            + File.separator + "/MBC/Images/" + uac.UniqueCode ( getApplicationContext
                            ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO +
                            "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtrBox.jpg" );

                    Structbilling.User_MtrBox_Img = uac.UniqueCode ( getApplicationContext ( ) ) + "_" +
                            Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtrBox.jpg";


                    im_boxImage.setImageBitmap ( bitmap );
                    isBoxPicture = false;
                } else if (isNumberPicture) {

                    imagePath = new File ( Environment.getExternalStorageDirectory ( )
                            + File.separator + "/MBC/Images/" + uac.UniqueCode ( getApplicationContext
                            ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO +
                            "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg" );

                    Structbilling.User_Mtr_Img = uac.UniqueCode ( getApplicationContext ( ) ) + "_" +
                            Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg";


                    im_numberImage.setImageBitmap ( bitmap );
                    isNumberPicture = false;
                } else if (isReadingPicture) {

                    imagePath = new File ( Environment.getExternalStorageDirectory ( )
                            + File.separator + "/MBC/Images/" + uac.UniqueCode ( getApplicationContext
                            ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO +
                            "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtrRead.jpg" );

                    Structbilling.User_MtrRead_Img = uac.UniqueCode ( getApplicationContext ( ) ) + "_" +
                            Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtrRead.jpg";


                    im_readingImage.setImageBitmap ( bitmap );

                    isReadingPicture = false;
                }

                try {
                    imagePath.createNewFile ( );
                } catch (IOException e) {
                    Logger.e ( this, "Image Storage", "", e );
                }
                System.out.println ( "Inside the picture " + imagePath );

                System.out.println ( "This is the file path " + imagePath );
                filePaths.add ( imagePath.toString ( ) );


                //write the bytes in file
                FileOutputStream fo = null;
                FileOutputStream os = null;
                try {

                    os = new FileOutputStream ( imagePath );
                    bitmap.compress ( Bitmap.CompressFormat.JPEG, 20, os );
                    os.close ( );

                } catch (FileNotFoundException e) {
                    Logger.e ( this, "FNF", "", e );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }


            }

        }


    }


    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth ( );
        int height = bm.getHeight ( );

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix ( );

        // resize the bit map
        matrix.postScale ( scaleWidth, scaleHeight );

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap ( bm, 0, 0, width, height, matrix, false );

        return resizedBitmap;

    }

    private void takePicture(ImageView im_image) {

        Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        int currentVer = android.os.Build.VERSION.SDK_INT;
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState ( );
            if (Environment.MEDIA_MOUNTED.equals ( state )) {
                if (currentVer < 24)
                    mImageCaptureUri = Uri.fromFile ( mFileTemp );
                else {
                    intent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                    mImageCaptureUri = FileProvider.getUriForFile ( this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            mFileTemp );
                }
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
                 */
//                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;

            }
            intent.putExtra ( MediaStore.EXTRA_OUTPUT, mImageCaptureUri );
            intent.putExtra ( "return-data", true );

            // im_image.setImageURI ( mImageCaptureUri );
            startActivityForResult ( intent, REQUEST_CODE_TAKE_PICTURE );
        } catch (ActivityNotFoundException e) {

            System.out.println ( "Error While Taking Image " + e );
            // Logger.d ( getApplicationContext ( ), TAG, "cannot take picture", e );
        }
    }


}
