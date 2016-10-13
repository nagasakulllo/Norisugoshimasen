package com.norisugosimasen.model.setting;

import com.norisugosimasen.model.setting.key.NotificationKey;
import com.norisugosimasen.model.setting.key.SelectedContentKey;

/**
 * Created by nagai on 2016/10/10.
 */

public enum SettingType {
    SELECTED_CONTENT("selected_content", SelectedContentKey.class),
    NOTIFICATION("notification", NotificationKey.class),;

    private String mPrefName;
    private Class mKeyClass;
    private SettingType(String prefName, Class keyClass) {
        mPrefName = prefName;
        mKeyClass = keyClass;
    }

    public String getPrefName() {
        return  mPrefName;
    }

    public Class getKeyClass() {
        return mKeyClass;
    }
}
