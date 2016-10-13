package com.norisugosimasen;

import android.app.Application;

import com.norisugosimasen.model.stationdata.StationContent;

/**
 * Created by nagai on 2016/10/10.
 */

public class MyApplication extends Application {
    // アプリ内で使いまわすstaticのコンテント
    private static StationContent sStationContent = new StationContent();

    public StationContent getStationContent() {
        return sStationContent;
    }
}
