package it.bff.sudoku.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private Context context;
    public final static String KEY_SOUND = "sound";
    public final static String KEY_HELP = "help";

    public PreferencesManager(Context context) {
        this.context = context;
    }

    public PreferencesManager(Activity activity) {
        this.context = activity.getBaseContext();
    }

    public Object get(String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        switch (key) {
            case KEY_SOUND:
                return sharedPreferences.getBoolean (KEY_SOUND, true);
            case KEY_HELP:
                return sharedPreferences.getBoolean (KEY_HELP, true);

        }

        return null;
    }

    public void set(String key, Object value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (key) {
            case KEY_SOUND:
                editor.putBoolean(KEY_SOUND, (boolean) value);
                break;
            case KEY_HELP:
                editor.putBoolean(KEY_HELP, (boolean) value);
                break;
        }

        editor.apply();
    }
}
