package com.norisugosimasen.model.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.norisugosimasen.model.setting.key.KeyInteface;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by nagai on 2016/10/10.
 */

public class Setting {
    private static Map<SettingType, Setting> sSettings;
    private SettingType mType;

    static {
        sSettings = new EnumMap<SettingType, Setting>(SettingType.class);
        for (SettingType type : SettingType.values()) {
            sSettings.put(type, new Setting(type));
        }
    }

    private Setting(SettingType type) {
        mType = type;
    }

    public static Setting createSetting(SettingType type) {
        return sSettings.get(type);
    }


    public synchronized Object read(KeyInteface key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(mType.name(), Context.MODE_PRIVATE);

        Class klass = key.getValueClass();
        if (klass == Integer.class) {
            return prefs.getInt(key.getKeyName(), -1);
        } else if (klass == String.class) {
            return prefs.getString(key.getKeyName(), "");
        } else if (klass == Boolean.class) {
            return prefs.getBoolean(key.getKeyName(), false);
        }

        return null;
    }

    public synchronized void write(KeyInteface key, Object value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(mType.name(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        put(editor, key, value);
        editor.apply();
    }

    public synchronized void write(Map<KeyInteface, Object> map, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(mType.name(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        for (KeyInteface key : map.keySet()) {
            put(editor, key, map.get(key));
        }
        editor.apply();
    }

    private void put(SharedPreferences.Editor editor, KeyInteface key, Object value) {
        Class klass = key.getValueClass();
        if (klass == Integer.class) {
            editor.putInt(key.getKeyName(), (Integer) value);
        } else if (klass == String.class) {
            editor.putString(key.getKeyName(), (String) value);
        } else if (klass == Boolean.class) {
            editor.putBoolean(key.getKeyName(), (Boolean) value);
        }
    }
}
