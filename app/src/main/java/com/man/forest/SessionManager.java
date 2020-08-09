package com.man.forest;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private Context mSessionContext;
    private SharedPreferences mSessionSharedPreference;
    private SharedPreferences.Editor mSessionSharedPreferenceEditor;

    public SessionManager(Context mSessionContext) {

        this.mSessionContext=mSessionContext;
        mSessionSharedPreference=mSessionContext.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        mSessionSharedPreferenceEditor = mSessionSharedPreference.edit();

    }

    public void setstring(String key,String value){

        mSessionSharedPreferenceEditor.putString(key, value);
        mSessionSharedPreferenceEditor.commit();
    }

    public String getstring(String key) {

        String value = mSessionSharedPreference.getString(key, "");
        return value;
    }
}
