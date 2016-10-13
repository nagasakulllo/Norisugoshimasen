package com.norisugosimasen.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.norisugosimasen.MyApplication;
import com.norisugosimasen.R;
import com.norisugosimasen.model.stationdata.StationContent;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        // スクリーンをON、ロック画面でも表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // バイブを鳴らす
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        String station = getIntent().getStringExtra("station");
        // アラート表示
        DialogFragment dialogFragment = MyDialogFragment.newInstance("まもなく" + station + "駅に到着します");
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "dialog");

        StationContent content = ((MyApplication) getApplicationContext()).getStationContent();
        if (station.equals(content.getTargetStation().getName())) {
            // 呼び出し元を終了させるためのフラグ
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void onDestroy() {
        // フラグ解除
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        super.onDestroy();
    }

    private static class MyDialogFragment extends DialogFragment {
        enum DialogKey {
            MESSAGE,;
        }

        public static MyDialogFragment newInstance(String message) {
            MyDialogFragment dialogFragment = new MyDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(DialogKey.MESSAGE.name(), message);
            dialogFragment.setArguments(bundle);

            return dialogFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(DialogKey.MESSAGE.name()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishActivity();
                        }
                    })
                    .create();
        }

        @Override
        public void show(FragmentManager manager, String tag) {
            // 5秒で消す
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    finishActivity();
                }
            }, 5000);

            super.show(manager, tag);
        }

        private void finishActivity() {
            FragmentActivity activity = getActivity();
            if (activity != null) activity.finish();
        }
    }
}
