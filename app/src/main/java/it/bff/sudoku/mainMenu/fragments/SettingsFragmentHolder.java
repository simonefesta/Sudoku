package it.bff.sudoku.mainMenu.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import it.bff.sudoku.R;
import it.bff.sudoku.mainMenu.MenuActivity;
import it.bff.sudoku.storage.PreferencesManager;

class SettingsFragmentHolder {
    SettingsFragmentHolder(View view, final MenuActivity activity) {
        PreferencesManager preferences = new PreferencesManager(activity);

        Switch switchSound = view.findViewById(getSwitchSoundId());
        Switch switchHelp = view.findViewById(getSwitchHelpId());
        Button btnBackground = view.findViewById(getBtnBackground());

        switchSound.setChecked((boolean) preferences.get(PreferencesManager.KEY_SOUND));
        switchHelp.setChecked((boolean) preferences.get(PreferencesManager.KEY_HELP));

        SettingsFragmentListener listener = new SettingsFragmentListener(activity, this);

        switchSound.setOnCheckedChangeListener(listener);
        switchHelp.setOnCheckedChangeListener(listener);
        btnBackground.setOnClickListener(listener);
    }


    int getSwitchSoundId() {
        return R.id.switchSound;
    }
    int getSwitchHelpId() {
        return R.id.switchHelp;
    }
    int getBtnBackground() { return  R.id.btnBackground; }

}
