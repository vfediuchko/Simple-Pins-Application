package vfediuchko.pins.test;

import android.content.Context;
import android.content.SharedPreferences;


public final class PreferenceStorage {
    private final static String PREFERENCE_NAME = "simple_pins_user_data";

    private final static String PREFERENCES_ACTIVE_USER_Id = "preference_active_user_id";

    private static SharedPreferences getSharedPreferences() {
        return SimplePins.getAppContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    public static void saveActiveUserId(String name) {
        getSharedPreferences().edit().putString(PREFERENCES_ACTIVE_USER_Id, name).commit();
    }

    public static String getActiveUserId() {
        return getSharedPreferences().getString(PREFERENCES_ACTIVE_USER_Id, "");
    }
   }
