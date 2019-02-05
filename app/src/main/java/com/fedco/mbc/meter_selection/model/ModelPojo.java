package com.fedco.mbc.meter_selection.model;

/**
 * Created by Hasnain on 28-Nov-17.
 */

public class ModelPojo {
    private String id;
    private String modelName;

    public ModelPojo(String id, String modelName) {
        this.id = id;
        this.modelName = modelName;
    }

    public String getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }
}
