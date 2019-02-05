package com.fedco.mbc.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by soubhagyarm on 28-10-2016.
 */

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;
    EditText editText;
    int b4Dec,afterDec;

//    public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
//        mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
//    }
    public DecimalDigitsInputFilter(EditText et, int beforeDecimal, int afterDecimal) {

        this.editText=et;
        this.b4Dec=beforeDecimal;
        this.afterDec=afterDecimal;
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        String etText = editText.getText().toString();
        if (etText.isEmpty()){
            return null;
        }
        String temp = editText.getText() + source.toString();
        if (temp.equals(".")) {
            return "0.";
        } else if (temp.toString().indexOf(".") == -1) {
            // no decimal point placed yet
            if (temp.length() > b4Dec) {
                return "";
            }
        } else {
            int dotPosition ;
            int cursorPositon = editText.getSelectionStart();
            if (etText.indexOf(".") == -1) {
                android.util.Log.i("First time Dot", etText.toString().indexOf(".")+" "+etText);
                dotPosition = temp.indexOf(".");
                android.util.Log.i("dot Positon", cursorPositon+"");
                android.util.Log.i("dot Positon", etText+"");
                android.util.Log.i("dot Positon", dotPosition+"");
            }else{
                dotPosition = etText.indexOf(".");
                android.util.Log.i("dot Positon", cursorPositon+"");
                android.util.Log.i("dot Positon", etText+"");
                android.util.Log.i("dot Positon", dotPosition+"");
            }
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(0, 1, input))
                    return null;
            } catch (NumberFormatException nfe) { }

            if(cursorPositon <= dotPosition){
                android.util.Log.i("cursor position", "in left");
                String beforeDot = etText.substring(0, dotPosition);
                if(beforeDot.length()<b4Dec){
                    return source;
                }else{
                    if(source.toString().equalsIgnoreCase(".")){
                        return source;
                    }else{
                        return "";
                    }

                }
            }else{
                android.util.Log.i("cursor position", "in right");
                temp = temp.substring(temp.indexOf(".") + 1);
                if (temp.length() > afterDec) {
                    return "";
                }
            }
        }

//        return super.filter(source, start, end, dest, dstart, dend);
        return null;
    }
    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

}