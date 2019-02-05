package com.fedco.mbc.meter_selection.model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by ABHI on 28-Nov-17.
 */

public class MakePojo {
    private String makeId;
    private String makeName;
    private Drawable drawableImage;
    private ArrayList<ModelPojo> modelPojoArrayList;

    public MakePojo(String makeId, String makeName, Drawable drawableImage, ArrayList<ModelPojo> modelPojoArrayList) {
        this.makeId = makeId;
        this.makeName = makeName;
        this.drawableImage = drawableImage;
        this.modelPojoArrayList = modelPojoArrayList;
    }

    public String getMakeId() {
        return makeId;
    }

    public String getMakeName() {
        return makeName;
    }

    public Drawable getDrawableImage() {
        return drawableImage;
    }

    public ArrayList<ModelPojo> getModelPojoArrayList() {
        return modelPojoArrayList;
    }
}
