package com.fedco.mbc.felhr.droidterm;

/**
 * Created by User on 21-09-2017.
 */

public interface LntMeterResponse {
    void onStartReading();
    StringBuffer onReadingFinish();
    void readingTimeOut();
}
