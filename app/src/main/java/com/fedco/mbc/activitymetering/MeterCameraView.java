package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;

import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MeterCameraView extends Activity {

    Button picBtn, nxtBtn;
    ImageView kwhImage, kvahImage;

    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private File mFileTemp;

    Context context;
    Logger Log;
    Boolean flagCam;
    String flagImage;

    TextView kwhHint,mdHint;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.bmp";
    public static final int REQUEST_CODE_GALLERY = 0x1;

    Button btn_bck;
    UtilAppCommon appCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_camera_view);
//        setTitle("Image Capture");

        picBtn = (Button) findViewById(R.id.ButtonTakePic);
        nxtBtn = (Button) findViewById(R.id.ButtonNext);

        kwhImage = (ImageView) findViewById(R.id.imageviewKWh);
        kvahImage = (ImageView) findViewById(R.id.imageviewKVAh);

        kwhHint=(TextView) findViewById(R.id.TextViewKWh);
        mdHint=(TextView) findViewById(R.id.TextViewwKVAh);

        btn_bck =(Button)findViewById(R.id.Buttonback);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });

        flagCam = getIntent().getExtras().getBoolean("CamFlag");
        appCom=new UtilAppCommon();

        if (flagCam) {

            kwhImage.setEnabled(true);
            kvahImage.setEnabled(true);
            picBtn.setVisibility(View.GONE);

        } else {

            kwhImage.setEnabled(true);
            kwhImage.setBackgroundResource(R.drawable.takeimagehint);
            kvahImage.setEnabled(false);

            kvahImage.setVisibility(View.GONE);
            picBtn.setVisibility(View.GONE);

            kwhHint.setVisibility(View.GONE);
            mdHint .setVisibility(View.GONE);

        }


        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);

        } else {

            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);

        }

        // nxtBtn.setClickable(false);
        nxtBtn.setVisibility(View.GONE);
        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagCam) {

                    Intent intent = new Intent(getApplicationContext(), MeteringReadinginput.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("Flag", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                } else {

                    Intent intent = new Intent(getApplicationContext(), Remarks.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("Flag", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }


            }
        });

        kwhImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
                flagImage = "kwh";

            }
        });
        kvahImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
                flagImage = "md";

            }
        });
    }

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

            Log.d(getApplicationContext(), "METERCAMVIEW", "cannot take picture", e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Request Code ____" + requestCode + "result Code ____" + requestCode);
        if (resultCode != RESULT_OK) {

            return;
        }

        Bitmap bitmap;

        switch (requestCode) {

            case REQUEST_CODE_TAKE_PICTURE:
                Log.v(getApplicationContext(), "", "AM in REQUEST_CODE_TAKE_PICTURE");

                if (flagCam) {


                    switch (flagImage) {

                        case "kwh":

                            bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                            File f = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "/MBC/Images/" + appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_kwh.jpg");
                            Structmeterupload.IMAGE1 = appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_kwh.jpg";
                            try {
                                f.createNewFile();
                            } catch (IOException e) {
                                Log.e(context, "Image Storage", "", e);
                            }
                            //write the bytes in file
                            FileOutputStream fo = null;
                            FileOutputStream os = null;
                            try {

                                os = new FileOutputStream(f);

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                                kwhImage.setImageBitmap(bitmap);
                                os.close();

                            } catch (FileNotFoundException e) {
                                Log.e(context, "FNF", "", e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            break;
                        case "md":

                            bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "/MBC/Images/" + appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_md.jpg");
                            Structmeterupload.IMAGE2 = appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_md.jpg";
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                Log.e(context, "Image Storage", "", e);
                            }
                            //write the bytes in file
                            FileOutputStream fokvah = null;
                            FileOutputStream oskvah = null;
                            try {

                                oskvah = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, oskvah);
                                kvahImage.setImageBitmap(bitmap);
                                oskvah.close();

                            } catch (FileNotFoundException e) {
                                Log.e(context, "FNF", "", e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            nxtBtn.setVisibility(View.VISIBLE);

                            break;
                    }



                } else {

                    bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                    File f = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "/MBC/Images/" + appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_kwh.jpg");
                    Structmeterupload.IMAGE1 = appCom.UniqueCode(getApplicationContext()) + "_" + Structmetering.CONSUMERNO + "_kwh.jpg";
                    Structmeterupload.IMAGE2 = "";
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        Log.e(context, "Image Storage", "", e);
                    }
                    //write the bytes in file
                    FileOutputStream fo = null;
                    FileOutputStream os = null;
                    try {

                        os = new FileOutputStream(f);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                        kwhImage.setImageBitmap(bitmap);
                        os.close();

                    } catch (FileNotFoundException e) {
                        Log.e(context, "FNF", "", e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    nxtBtn.setVisibility(View.VISIBLE);

                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}
