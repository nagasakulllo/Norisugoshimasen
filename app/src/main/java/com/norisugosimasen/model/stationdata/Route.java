package com.norisugosimasen.model.stationdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Route implements DataInterface {
    private int mCode;
    private String mName;

    public static List<Route>create(JSONObject json) {
        List<Route> list = new ArrayList<>();
        if (json == null) return list;

        try {
            JSONArray array = json.getJSONArray("line");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    list.add(new Route(array.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Route(JSONObject json) {
        try {
            mCode = json.getInt("line_cd");
            mName = json.getString("line_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public int getCode() {
        return mCode;
    }

    @Override
    public String getName() {
        return mName;
    }
}


