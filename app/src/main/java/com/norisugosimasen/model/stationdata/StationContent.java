package com.norisugosimasen.model.stationdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagai on 2016/10/10.
 */

public class StationContent {
    private PrefectureKey mPrefecture;
    private Route mRoute;
    private List<Station> mRouteStations;
    private List<AdjacentStation> mRouteAdjacentStations;

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

    public void setRouteStations(List<Station> stations) {
        mRouteStations = stations;
    }

    public void setRouteAdjacentStations(List<AdjacentStation> adjacentStations) {
        mRouteAdjacentStations = adjacentStations;
    }

    public void setTargetStation(Station targetStation) {
        mTargetStation = targetStation;
    }

    public Station getTargetStation() {
        return mTargetStation;
    }

    public List<Station> getAdjacentStations() {
        if (mRouteAdjacentStations == null || mRouteStations == null || mTargetStation == null) return null;

        List<Station> stations = new ArrayList<>();
        for (AdjacentStation adjacentStation : mRouteAdjacentStations) {
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
        for (Station station : mRouteStations) {
            if (code == station.getCode()) {
                return station;
            }
        }

        return null;
    }

}
