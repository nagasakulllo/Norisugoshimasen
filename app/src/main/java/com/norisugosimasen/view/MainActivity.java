package com.norisugosimasen.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.norisugosimasen.MyApplication;
import com.norisugosimasen.R;
import com.norisugosimasen.model.setting.Setting;
import com.norisugosimasen.model.setting.SettingType;
import com.norisugosimasen.model.setting.key.KeyInteface;
import com.norisugosimasen.model.setting.key.SelectedContentKey;
import com.norisugosimasen.model.stationdata.APIWrapper;
import com.norisugosimasen.model.stationdata.PrefectureKey;
import com.norisugosimasen.model.stationdata.Route;
import com.norisugosimasen.model.stationdata.Station;
import com.norisugosimasen.model.stationdata.StationContent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;

    private Spinner mPrefectureSpinner;
    private Spinner mRouteSpinner;
    private Spinner mStationSpinner;
    private Button mDecissionButton;

    private SpinnerAdapter<PrefectureKey> mPrefectureSpinnerAdapter;
    private SpinnerAdapter<Route> mRouteSpinnerAdapter;
    private SpinnerAdapter<Station> mStationSpinnerAdapter;

    private StationContent mStationContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // パーミッションチェック
        checkAllowPermissions();

        mStationContent = ((MyApplication) getApplication()).getStationContent();

        setPrefecturesSpinner();
        setPrefecturesSpinnerListener();

        setRouteSpinner((PrefectureKey) mPrefectureSpinner.getSelectedItem());
        setRouteSpinnerListener();

        setStationSpinner((Route) mRouteSpinner.getSelectedItem());
        setStationSpinnerListener();

        setDecissionButton();

        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isConnectedNetwork()) {
            Toast toast = Toast.makeText(this, "駅情報を取得するには端末をネットワークに接続してください", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        // 設定値保存
        saveCurrentSpinner();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast toast = Toast.makeText(this, "許可されていない権限があるため、アプリが正常に動作しない可能性があります", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean isConnectedNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void checkAllowPermissions() {
        // 6.0未満は何もしない
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        // アプリで使う権限
        String[] permissions = new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUEST_PERMISSION_CODE);
                break;
            }
        }

        return;
    }

    // 都道府県スピナーのセット
    private void setPrefecturesSpinner() {
        mPrefectureSpinner = (Spinner) findViewById(R.id.prefecture_spinner);
        mPrefectureSpinnerAdapter = new SpinnerAdapter<>(this);
        mPrefectureSpinnerAdapter.setItem(Arrays.asList(PrefectureKey.values()));
        mPrefectureSpinner.setAdapter(mPrefectureSpinnerAdapter);

        updatePrefectureByPreferences();
    }

    // 都道府県スピナーをSharedPreferencesの値で更新
    private void updatePrefectureByPreferences() {
        Setting setting = Setting.createSetting(SettingType.SELECTED_CONTENT);

        int prefectureCode = (int) setting.read(SelectedContentKey.PREFECTURE_CODE, this);
        if (prefectureCode != -1) {
            for (int i = 0; i < mPrefectureSpinnerAdapter.getCount(); i++) {
                PrefectureKey prefecture = (PrefectureKey) mPrefectureSpinnerAdapter.getItem(i);
                if (prefecture.getId() == prefectureCode) {
                    updatePrefectureSelection(i);
                    break;
                }
            }
        }
    }

    // 都道府県スピナーのSelectionを更新
    private void updatePrefectureSelection(int index) {
        mPrefectureSpinner.setSelection(index, false);
        mStationContent.setPrefecture((PrefectureKey) mPrefectureSpinner.getSelectedItem());
    }

    // 都道府県スピナーのリスナーセット
    private void setPrefecturesSpinnerListener() {
        mPrefectureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PrefectureKey selected = (PrefectureKey) mPrefectureSpinnerAdapter.getItem(i);
                mStationContent.setPrefecture(selected);

                // 路線/駅情報更新
                mRouteSpinnerAdapter.setItem(APIWrapper.getRouteData(selected, MainActivity.this));
                updateRouteSelection(0);

                List<Station> stations = APIWrapper.getStationData((Route) mRouteSpinner.getSelectedItem(), MainActivity.this);
                mStationSpinnerAdapter.setItem(stations);
                mStationContent.setRouteStations(stations);
                updateStationSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // 路線スピナーのセット
    private void setRouteSpinner(PrefectureKey prefectureKey) {
        mRouteSpinner = (Spinner) findViewById(R.id.route_spinner);
        mRouteSpinnerAdapter = new SpinnerAdapter<>(this);
        mRouteSpinnerAdapter.setItem(APIWrapper.getRouteData(prefectureKey, this));
        mRouteSpinner.setAdapter(mRouteSpinnerAdapter);

        updateRouteByPreferences();
    }

    // 路線スピナーをSharedPreferencesの値で更新
    private void updateRouteByPreferences() {
        Setting setting = Setting.createSetting(SettingType.SELECTED_CONTENT);

        int routeCode = (int) setting.read(SelectedContentKey.ROUTE_CODE, this);
        if (routeCode != -1) {
            for (int i = 0; i < mRouteSpinnerAdapter.getCount(); i++) {
                Route route = (Route) mRouteSpinnerAdapter.getItem(i);
                if (route.getCode() == routeCode) {
                    updateRouteSelection(i);
                    break;
                }
            }
        }
    }

    // 路線スピナーのSelectionを更新
    private void updateRouteSelection(int index) {
        mRouteSpinner.setSelection(index, false);
        mStationContent.setRoute((Route) mRouteSpinner.getSelectedItem());
    }

    // 路線スピナーのリスナーセット
    private void setRouteSpinnerListener() {
        mRouteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Route selected = (Route) mRouteSpinnerAdapter.getItem(i);
                mStationContent.setRoute(selected);

                // 駅情報を更新
                mStationSpinnerAdapter.setItem(APIWrapper.getStationData(selected, MainActivity.this));
                updateStationSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // 駅スピナーのセット
    private void setStationSpinner(Route route) {
        mStationSpinner = (Spinner) findViewById(R.id.station_spinner);
        mStationSpinnerAdapter = new SpinnerAdapter<>(this);

        List<Station> stations = APIWrapper.getStationData(route, this);
        mStationContent.setRouteStations(stations);

        mStationSpinnerAdapter.setItem(stations);
        mStationSpinner.setAdapter(mStationSpinnerAdapter);

        updateStationByPreferences();
    }

    // 駅スピナーをSharedPreferencesの値で更新
    private void updateStationByPreferences() {
        Setting setting = Setting.createSetting(SettingType.SELECTED_CONTENT);

        int stationCode = (int) setting.read(SelectedContentKey.STATION_CODE, this);
        if (stationCode != -1) {
            for (int i = 0; i < mStationSpinnerAdapter.getCount(); i++) {
                Station station = (Station) mStationSpinnerAdapter.getItem(i);
                if (station.getCode() == stationCode) {
                    updateStationSelection(i);
                    break;
                }
            }
        }
    }

    // 駅スピナーのSelectionを更新
    private void updateStationSelection(int index) {
        mStationSpinner.setSelection(index, false);
        mStationContent.setTargetStation((Station) mStationSpinner.getSelectedItem());
    }

    // 駅スピナーのリスナーセット
    private void setStationSpinnerListener() {
        mStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Station selected = (Station) mStationSpinnerAdapter.getItem(i);
                mStationContent.setTargetStation(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // 決定ボタンのセット
    private void setDecissionButton() {
        mDecissionButton = (Button) findViewById(R.id.decission_button);
        mDecissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Station station = (Station) mStationSpinner.getSelectedItem();
                Toast toast = Toast.makeText(MainActivity.this, station.getName() + "駅を目的地に設定しました", Toast.LENGTH_SHORT);
                toast.show();

                // 隣駅情報を取得する
                Route route = mStationContent.getRoute();
                mStationContent.setRouteAdjacentStations(APIWrapper.getAdjacentStationData(route, MainActivity.this));

                startActivity(new Intent(MainActivity.this, DestinationActivity.class));
            }
        });
    }

    // 現在のスピナーの値をSharedPreferencesに保存
    private void saveCurrentSpinner() {
        Setting setting = Setting.createSetting(SettingType.SELECTED_CONTENT);

        final PrefectureKey prefecture = (PrefectureKey) mPrefectureSpinner.getSelectedItem();
        final Route route = (Route) mRouteSpinner.getSelectedItem();
        final Station station = (Station) mStationSpinner.getSelectedItem();
        // 保存しない
        if (prefecture == null || route == null || station == null) return;

        setting.write(new HashMap<KeyInteface, Object>() {
            {
                put(SelectedContentKey.PREFECTURE_CODE, prefecture.getId());
                put(SelectedContentKey.ROUTE_CODE, route.getCode());
                put(SelectedContentKey.STATION_CODE, station.getCode());
            }
        }, this);
    }
}
