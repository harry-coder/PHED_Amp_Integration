package com.fedco.mbc.utils.SweetAlertDialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HP-HP on 4/3/2015.
 */
public class SweetAlertDialogBox {

    Context _context;

    Activity _activity;

    static boolean yes,no;

    public SweetAlertDialogBox(Context context)
    {
        this._context=context;
        _activity = (Activity) context;

    }

    public void basic(String title)
    {
        setvalue();

        new SweetAlertDialog(_context)
            .setTitleText(title)
            .show();
    }

    public void linetext(String title,String text)
    {
        setvalue();

        new SweetAlertDialog(_context)
                .setTitleText(title)
                .setContentText(text)
                .show();
    }


    public void erro(String title,String text)
    {
        setvalue();

        new SweetAlertDialog(_context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(text)
                .show();
    }

    public void warning(String title,String text,String text2)
    {
        setvalue();

        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText(text)
                .setConfirmText(text2)
                .show();
    }

    public void succes(String title,String text)
    {
        setvalue();

        new SweetAlertDialog(_context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(text)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        setyes();
                    }
                })
                .show();
    }

    public void custom(String title,String content_text, String confirm_text,String cancel_text)
    {

        setvalue();

        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content_text)
                .setCancelText(cancel_text)
                .setConfirmText(confirm_text)
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        /*sDialog.setTitleText("Cancelled!")
                                .setContentText("Your imaginary file is safe :)")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);*/
                        // no=true;
                        sDialog.dismissWithAnimation();

                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        /*sDialog.setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
                        setyes();
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();
    }

    public boolean getyes()
    {
        return yes;
    }

    public boolean getno()
    {
        return no;
    }

    public void setvalue()
    {
        yes=false;
        no=false;
    }

    public void setyes()
    {
        yes=true;
        Log.e("yes", " " + yes);
    }

    public void setno()
    {
        no=true;
    }


}
