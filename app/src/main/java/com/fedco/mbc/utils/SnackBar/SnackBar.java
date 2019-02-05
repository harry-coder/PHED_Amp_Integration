package com.fedco.mbc.utils.SnackBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import com.fedco.mbc.R;

/**
 * Created by HP-HP on 4/3/2015.
 */
public class SnackBar {

    Context _context;

    Activity activity;

    public SnackBar(Context context)
    {
        this._context=context;
        activity = (Activity) context;

    }

    public void SingleLineSnackBar(String txt)
    {
        SnackbarManager.show(
                Snackbar.with(_context)           // context
                        .text(txt)               // text to display
                        .color(R.color.color_primary) // change the background color
                        .textColor(Color.WHITE) // change the text color
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // LENGTH_SHORT: 2s LENGTH_LONG: 3.5s (default) LENGTH_INDEFINTE: Indefinite; ideal for persistent errors
                , activity);
    }

    public void MultiLineSnackBar(String txt)
    {
        SnackbarManager.show(
                Snackbar.with(_context)           // context
                        .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                        .text(txt) // text to be displayed
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                , activity);
    }


    public void SnackBar_Action(String txt,String btn_name)
    {
        SnackbarManager.show(
                Snackbar.with(_context) // context
                        .text(txt) // text to be displayed
                        .textColor(Color.GREEN) // change the text color
                        .textTypeface(Typeface.SANS_SERIF) // change the text font
                        .color(Color.BLUE) // change the background color
                        .actionLabel(btn_name) // action button label
                        .actionColor(Color.RED) // action button label color
                        .actionLabelTypeface(Typeface.SANS_SERIF) // change the action button font
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                /*Log.d(TAG, "Doing something");*/
                            }
                        }) // action button's ActionClickListener
                , activity); // activity where it is displayed
    }
}
