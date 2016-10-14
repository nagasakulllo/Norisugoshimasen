package com.norisugosimasen.model.stationdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Station implements DataInterface {
    private int mCode;
    private int mGroupCode;
    private String mName;
    private double mLongitude;
    private double mLatitude;


    public static List<Station>create(JSONObject json) {
        List<Station> list = new ArrayList<>();
        if (json == null) return list;

        try {
            JSONArray array = json.getJSONArray("station_l");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    list.add(new Station(array.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Station(JSONObject json) {
        try {
            mCode = json.getInt("station_cd");
            mGroupCode = json.getInt("station_g_cd");
            mName = json.getString("station_name");
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

    @Override
    public String getName() {
        return mName;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
}


