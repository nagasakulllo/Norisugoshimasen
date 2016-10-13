package com.norisugosimasen.model.stationdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdjacentStation {
    private int mCode1;
    private int mCode2;
    private String mName1;
    private String mName2;
    private double mLongitude1;
    private double mLatitude1;
    private double mLongitude2;
    private double mLatitude2;

    public static List<AdjacentStation>create(JSONObject json) {
        List<AdjacentStation> list = new ArrayList<>();
        try {
            JSONArray array = json.getJSONArray("station_join");
            for (int i = 0; i < array.length(); i++) {
                list.add(new AdjacentStation(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private AdjacentStation(JSONObject json) {
        try {
            mCode1 = json.getInt("station_cd1");
            mCode2 = json.getInt("station_cd2");
            mName1 = json.getString("station_name1");
            mName2 = json.getString("station_name2");
            mLongitude1 = json.getDouble("lon1");
            mLatitude1 = json.getDouble("lat1");
            mLongitude2 = json.getDouble("lon2");
            mLatitude2 = json.getDouble("lat2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public int getCode1() {
        return mCode1;
    }

    public int getCode2() {
        return mCode2;
    }

    public String getName1() {
        return mName1;
    }

    public String getName2() {
        return mName2;
    }

    public double getLongitude1() {
        return mLongitude1;
    }

    public double getLatitude1() {
        return mLatitude1;
    }

    public double getLongitude2() {
        return mLongitude2;
    }

    public double getLatitude2() {
        return mLatitude2;
    }
}


