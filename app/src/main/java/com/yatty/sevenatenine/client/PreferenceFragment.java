package com.yatty.sevenatenine.client;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
