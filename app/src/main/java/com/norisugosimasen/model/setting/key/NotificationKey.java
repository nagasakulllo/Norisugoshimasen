package com.norisugosimasen.model.setting.key;

/**
 * Created by nagai on 2016/10/10.
 */

public enum NotificationKey implements KeyInteface {
    NOTIFY_ADJACENT("notify_adjacent", Boolean.class),;

    private String mKeyName;
    private Class mClass;
    NotificationKey(String keyName, Class klass) {
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
