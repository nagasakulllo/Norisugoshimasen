package com.norisugosimasen.model.stationdata;

import android.content.Context;

import com.norisugosimasen.model.util.NetworkResult;
import com.norisugosimasen.model.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by nagai on 2016/10/09.
 */

public class APIWrapper {
    private APIWrapper() {};

    public static List<Route> getRouteData(PrefectureKey prefecture, Context context) {
        JSONObject json = getJSON(String.format("http://www.ekidata.jp/api/p/%s.json", prefecture.getId()), context);
        return Route.create(json);
    }

    public static List<Station> getStationData(Route route, Context context) {
        JSONObject json = getJSON(String.format("http://www.ekidata.jp/api/l/%s.json", route.getCode()), context);
        return Station.create(json);
    }

    public static List<StationDetail> getStationDetailData(Station station, Context context) {
        JSONObject json = getJSON(String.format("http://www.ekidata.jp/api/s/%s.json", station.getCode()), context);
        return StationDetail.create(json);
    }

    public static List<AdjacentStation> getAdjacentStationData(Route route, Context context) {
        JSONObject json = getJSON(String.format("http://www.ekidata.jp/api/n/%s.json", route.getCode()), context);
        return AdjacentStation.create(json);
    }

    private static JSONObject getJSON(String url, Context context) {
        NetworkResult result = NetworkUtil.get(url, context);
        if (result == null || result.getResultCode() != 200) {
            return null;
        }

        String str = result.getStringBody();
        JSONObject json = createJSON(str);
        if (json == null) {
            // おそらく作れないので手動でフォーマット
            try {
                str = str.substring(str.indexOf("xml.data = ") + 11);
                str = str.substring(0, str.indexOf("\nif(typeof(xml.onload)"));
            } catch (StringIndexOutOfBoundsException e) {

            }

            json = createJSON(str);
        }

        return json;
    }

    private static JSONObject createJSON(String str) {
        JSONObject json = null;

        try {
            json = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
