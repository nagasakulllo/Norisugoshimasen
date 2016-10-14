package com.norisugosimasen.model.setting.key;

/**
 * Created by nagai on 2016/10/10.
 */

public enum SelectedContentKey implements KeyInteface {
    PREFECTURE_CODE("prefecture_cd", Integer.class),
    ROUTE_CODE("route_cd", Integer.class),
    STATION_CODE("station_cd", Integer.class),;

    private String mKeyName;
    private Class mClass;
    SelectedContentKey(String keyName, Class klass) {
        mKeyName = keyName;
        mClass = klass;
    }

    @Override
    public String getKeyName() {
        return mKeyName;
    }

    @Override
    public Class getValueClass() {
        return mClass;
    }
}
