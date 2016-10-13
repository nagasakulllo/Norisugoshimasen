package com.norisugosimasen.model.stationdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagai on 2016/10/10.
 */

public class StationContent {
    private PrefectureKey mPrefecture;
    private Route mRoute;
    private List<Station> mStations;
    private List<AdjacentStation> mAdjacentStations;

    private Station mTargetStation;

    public void setPrefecture(PrefectureKey prefecture) {
        mPrefecture = prefecture;
    }

    public void setRoute(Route route) {
        mRoute = route;
    }

    public Route getRoute() {
        return mRoute;
    }

    public void setStations(List<Station> stations) {
        mStations = stations;
    }

    public void setAdjacentStations(List<AdjacentStation> adjacentStations) {
        mAdjacentStations = adjacentStations;
    }

    public void setTargetStation(Station targetStation) {
        mTargetStation = targetStation;
    }

    public Station getTargetStation() {
        return mTargetStation;
    }

    public List<Station> getAdjacentStations() {
        if (mAdjacentStations == null || mTargetStation == null) return null;

        List<Station> stations = new ArrayList<>();
        for (AdjacentStation adjacentStation : mAdjacentStations) {
            Station station = null;
            if (mTargetStation.getCode() == adjacentStation.getCode1()) {
                station = getStationFromCode(adjacentStation.getCode2());
            } else if (mTargetStation.getCode() == adjacentStation.getCode2()) {
                station = getStationFromCode(adjacentStation.getCode1());
            }

            if (station != null) {
                stations.add(station);
            }
        }

        return stations;
    }

    private Station getStationFromCode(int code) {
        if (mStations == null) return null;

        for (Station station : mStations) {
            if (code == station.getCode()) {
                return station;
            }
        }

        return null;
    }

}
