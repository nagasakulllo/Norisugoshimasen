package com.norisugosimasen.model.stationdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StationDetail {
    private int mCode;
    private int mGroupCode;
    private String mName;
    private int mPrefCode;
    private int mLineCode;
    private String mLineName;
    private double mLongitude;
    private double mLatitude;


    public static List<StationDetail>create(JSONObject json) {
        List<StationDetail> list = new ArrayList<>();
        if (json == null) return list;

        try {
            JSONArray array = json.getJSONArray("station");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    list.add(new StationDetail(array.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private StationDetail(JSONObject json) {
        try {
            mCode = json.getInt("station_cd");
            mGroupCode = json.getInt("station_g_cd");
            mName = json.getString("station_name");
            mPrefCode = json.getInt("pref_cd");
            mLineCode = json.getInt("line_cd");
            mLineName = json.getString("line_name");
            mLongitude = json.getDouble("lon");
            mLatitude = json.getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public int getCode() {
        return mCode;
    }

    public int getGroupCode() {
        return mGroupCode;
    }

    public String getStationName() {
        return mName;
    }

    public int getPrefCode() {
        return mPrefCode;
    }

    public int getLineCode() {
        return mLineCode;
    }

    public String getLineName() {
        return mLineName;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
}


