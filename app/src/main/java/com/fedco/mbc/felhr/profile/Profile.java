package com.fedco.mbc.felhr.profile;

public class Profile {
    private int baudRate;
    private int dataBits;
    private int flowControl;
    private int parity;
    private String pid;
    private String profileName;
    private int stopBits;
    private String vid;

    public Profile(String profileName, String vid, String pid, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
        this.profileName = profileName;
        this.vid = vid;
        this.pid = pid;
        this.dataBits = dataBits;
        this.baudRate = baudRate;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControl;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getVid() {
        return this.vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits() {
        return this.dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return this.stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return this.parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getFlowControl() {
        return this.flowControl;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }
}
