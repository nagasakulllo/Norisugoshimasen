package com.norisugosimasen.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.norisugosimasen.MyApplication;
import com.norisugosimasen.model.stationdata.Station;
import com.norisugosimasen.model.stationdata.StationContent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nagai on 2016/10/10.
 */

public class GPSService extends Service {
    private static final int VALUE_TARGET = 1;
    private static final int VALUE_ADJACENT = 2;
    // メートル
    private static final int REACHED_DISTANCE = 500;

    private Context mContext;
    private Map<Station, Integer> mStations;
    private FusedLocationProviderApi mLocationAPI;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;

    public GPSService() {
        mStations = new HashMap<>();
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        StationContent content = ((MyApplication) getApplication()).getStationContent();
        if (content != null) {
            if (content.getTargetStation() != null) mStations.put(content.getTargetStation(), VALUE_TARGET);
            if (content.getAdjacentStations() != null) {
                for (Station station : content.getAdjacentStations()) {
                    mStations.put(station, VALUE_ADJACENT);
                }
            }
        }

        mLocationAPI = LocationServices.FusedLocationApi;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        mLocationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Station station = arrivedNearestStation(location);
                                if (station == null) return;

                                Integer value = mStations.get(station);
                                if (value.equals(VALUE_TARGET)) {
                                    // 目的地に到着したらサービス終了
                                    mLocationAPI.removeLocationUpdates(mGoogleApiClient, mLocationListener);

                                    stopSelf();
                                }

                                sendBroadcast(station);

                                // 到着した駅は消す
                                mStations.remove(station);
                            }
                        };

                        mLocationAPI.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        sendBroadcastWithError("位置情報に接続できません");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        sendBroadcastWithError("位置情報に接続できません");
                    }
                })
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startFusedLocation();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopFusedLocation();
    }

    private void startFusedLocation(){
        if (mGoogleApiClient == null) return;

        mGoogleApiClient.connect();
    }

    private void stopFusedLocation(){
        if (mGoogleApiClient == null) return;

        mGoogleApiClient.disconnect();
    }

    private Station arrivedNearestStation(Location current) {
//        Station nearestStation = null;
//        float nearestDistance = REACHED_DISTANCE;
//
//        float[] results = new float[3];
//        for (Station station : mStations.keySet()) {
//            Location.distanceBetween(current.getLatitude(), current.getLongitude(), station.getLatitude(), station.getLongitude(), results);
//
//            // 500メートル以内なら到着したと判断する
//            if (results[0] < REACHED_DISTANCE) {
//                if (results[0] < nearestDistance) {
//                    nearestDistance = results[0];
//                    nearestStation = station;
//                }
//            }
//        }
//
//        return nearestStation;
        StationContent content = ((MyApplication) getApplication()).getStationContent();
        for (Station station : mStations.keySet()) {
            for (Station adjacent : content.getAdjacentStations()) {
                if (station.getName().equals(adjacent.getName())) {
                    return station;
                }
            }
        }

        for (Station station : mStations.keySet()) {
            if (station.getName().equals(content.getTargetStation().getName())) {
                return station;
            }
        }

        return null;
    }

    private void sendBroadcast(Station station) {
        Intent intent = new Intent();
        intent.putExtra("station", station.getName());
        intent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(intent);
    }

    private void sendBroadcastWithError(String string) {
        Intent intent = new Intent();
        intent.putExtra("error", string);
        intent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(intent);
    }
}
