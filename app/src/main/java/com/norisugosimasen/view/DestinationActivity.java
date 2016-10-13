package com.norisugosimasen.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.norisugosimasen.MyApplication;
import com.norisugosimasen.R;
import com.norisugosimasen.model.setting.Setting;
import com.norisugosimasen.model.setting.SettingType;
import com.norisugosimasen.model.setting.key.KeyInteface;
import com.norisugosimasen.model.setting.key.NotificationKey;
import com.norisugosimasen.model.setting.key.SelectedContentKey;
import com.norisugosimasen.model.stationdata.StationContent;
import com.norisugosimasen.service.GPSService;

import java.util.HashMap;

import okio.DeflaterSink;

public class DestinationActivity extends AppCompatActivity {
    private static final int REQUESTCODE_DIALOG_ACTIVITY = 1;

    private CheckBox mNotificationCheck;

    private Intent mServiceIntent;
    private GPSServiceCallbackReceiver mGPSServiceCallbackReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        TextView destinationText = (TextView) findViewById(R.id.destination_text);
        StationContent content = ((MyApplication) getApplicationContext()).getStationContent();
        destinationText.setText("目的地は「" + content.getTargetStation().getName() + "」駅です");

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        final Setting setting = Setting.createSetting(SettingType.NOTIFICATION);
        mNotificationCheck = (CheckBox) findViewById(R.id.adjacent_notification);
        mNotificationCheck.setChecked((Boolean) setting.read(NotificationKey.NOTIFY_ADJACENT, this));
        mNotificationCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                setting.write(new HashMap<KeyInteface, Object>() {
                    {
                        put(NotificationKey.NOTIFY_ADJACENT, b);
                    }
                }, DestinationActivity.this);
            }
        });

        mGPSServiceCallbackReceiver = new GPSServiceCallbackReceiver(mGPSServiceCallbackHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(mGPSServiceCallbackReceiver, intentFilter);

        mServiceIntent = new Intent(this, GPSService.class);
        startService(mServiceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_DIALOG_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(mServiceIntent);
        unregisterReceiver(mGPSServiceCallbackReceiver);
    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "設定画面に戻るには、「戻る」ボタンを押してください", Toast.LENGTH_SHORT);
        toast.show();
    }

    private Handler mGPSServiceCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            String error = bundle.getString("error");
            if (error != null) {
                Toast toast = Toast.makeText(DestinationActivity.this, "位置情報を利用できません", Toast.LENGTH_LONG);
                toast.show();
                finish();
                return;
            }

            String station = bundle.getString("station");
            StationContent content = ((MyApplication) getApplicationContext()).getStationContent();
            if (!content.getTargetStation().getName().equals(station)) {
                // 隣駅はチェックボックスオフなら通知しない
                if (!mNotificationCheck.isChecked()) return;
            }

            // ダイアログアクティビティを表示
            Intent intent = new Intent(DestinationActivity.this, DialogActivity.class);
            intent.putExtra("station", station);
            startActivityForResult(intent, REQUESTCODE_DIALOG_ACTIVITY);
        }
    };

    private static class GPSServiceCallbackReceiver extends BroadcastReceiver {
        Handler mHandler;

        GPSServiceCallbackReceiver(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mHandler == null) return;

            Message msg = new Message();
            msg.setData(intent.getExtras());
            mHandler.sendMessage(msg);
        }
    }
}
