package it.bff.sudoku.mainMenu.fragments;

import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import it.bff.sudoku.R;
import it.bff.sudoku.mainMenu.MenuActivity;
import it.bff.sudoku.mainMenu.PermissionManager;
import it.bff.sudoku.storage.PreferencesManager;

public class SettingsFragmentListener implements CompoundButton.OnCheckedChangeListener, Button.OnClickListener{

    private MenuActivity activity;
    private SettingsFragmentHolder holder;

    SettingsFragmentListener(MenuActivity activity, SettingsFragmentHolder holder) {
        this.activity = activity;
        this.holder = holder;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        PreferencesManager preferences = new PreferencesManager(activity);
        String msg;

        if(buttonView.getId() == holder.getSwitchSoundId()) {

            if(isChecked){
                preferences.set(PreferencesManager.KEY_SOUND, true);
                msg = activity.getResources().getString(R.string.settings_toast_music_on);
            }
            else{
                preferences.set(PreferencesManager.KEY_SOUND, false);
                msg = activity.getResources().getString(R.string.settings_toast_music_off);
            }

            Toast.makeText(activity.getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
        }
        if(buttonView.getId() == holder.getSwitchHelpId()) {

            if(isChecked){
                preferences.set(PreferencesManager.KEY_HELP, true);
                msg = activity.getResources().getString(R.string.settings_toast_help_on);
            }
            else{
                preferences.set(PreferencesManager.KEY_HELP, false);
                msg = activity.getResources().getString(R.string.settings_toast_help_off);
            }

            Toast.makeText(activity.getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        String[] allPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if(PermissionManager.askPermission(allPermissions, activity, PermissionManager.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE)) {
            activity.launchActivity(PermissionManager.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }
}

