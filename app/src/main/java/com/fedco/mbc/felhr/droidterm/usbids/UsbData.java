package com.fedco.mbc.felhr.droidterm.usbids;

import android.os.Bundle;

public class UsbData {
    public static final String PRODUCT_ID = "com.felhr.droidterm.usbids.PRODUCT_ID";
    public static final String PRODUCT_NAME = "com.felhr.droidterm.usbids.PRODUCT_NAME";
    public static final String VENDOR_ID = "com.felhr.droidterm.usbids.VENDOR_ID";
    public static final String VENDOR_NAME = "com.felhr.droidterm.usbids.VENDOR_NAME";
    private String productId;
    private String productName;
    private String vendorId;
    private String vendorName;

    public UsbData(String vendorId, String vendorName, String productId, String productName) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.productId = productId;
        this.productName = productName;
    }

    public String getVendorId() {
        return this.vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Bundle getBundledData() {
        Bundle bundle = new Bundle();
        bundle.putString(VENDOR_ID, this.vendorId);
        bundle.putString(VENDOR_NAME, this.vendorName);
        bundle.putString(PRODUCT_ID, this.productId);
        bundle.putString(PRODUCT_NAME, this.productName);
        return bundle;
    }
}
