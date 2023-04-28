package it.bff.sudoku.mainMenu.fragments;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import it.bff.sudoku.audio.AudioListener;
import it.bff.sudoku.audio.ControllerAudio;
import it.bff.sudoku.R;
import it.bff.sudoku.mainMenu.MenuActivity;

class HomeFragmentHolder {

    static final int STATUS_MENU = 0;
    static final int STATUS_NEW_GAME = 1;

    private MenuActivity activity;
    private Button btnNewGame;
    private Button btnContinue;
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;
    private Button btnBack;
    private ControllerAudio controllerAudio;
    private ProgressBar progressBar;
    private Toast toast = null;

    private ConstraintLayout clMenu;
    private ConstraintLayout clNewGame;

    HomeFragmentHolder(View view, MenuActivity activity) {

        this.activity = activity;

        btnContinue = view.findViewById(getBtnContinueId());
        btnNewGame = view.findViewById(getBtnNewGameId());
        btnEasy = view.findViewById(getBtnEasyId());
        btnMedium = view.findViewById(getBtnMediumId());
        btnHard = view.findViewById(getBtnHardId());
        btnBack = view.findViewById(getBtnBackId());
        progressBar = view.findViewById(getPrgBarId());
        clMenu = view.findViewById(R.id.clMenu);
        clNewGame = view.findViewById(R.id.clNewGame);

        HomeFragmentListener listener = new HomeFragmentListener(activity, this);

        btnContinue.setOnClickListener(listener);
        btnNewGame.setOnClickListener(listener);
        btnEasy.setOnClickListener(listener);
        btnMedium.setOnClickListener(listener);
        btnHard.setOnClickListener(listener);
        btnBack.setOnClickListener(listener);

        controllerAudio = new ControllerAudio(activity, R.raw.menu_tap, new AudioListener(activity));

        setFragmentStatus(STATUS_MENU);
    }

    int getBtnContinueId() {
        return R.id.btnContinue;
    }
    int getBtnNewGameId() {
        return R.id.btnNewGame;
    }
    int getBtnEasyId() {
        return R.id.btnEasy;
    }
    int getBtnMediumId() {
        return R.id.btnMedium;
    }
    int getBtnHardId() {
        return R.id.btnHard;
    }
    int getBtnBackId() {
        return R.id.btnBack;
    }
    int getPrgBarId() { return R.id.indeterminateBar; }
    ControllerAudio getControllerAudio(){ return controllerAudio;}

    // true: the view is loading a new game (disable the button, start animation)
    // false: the view is no longer loading a new game
    void setViewInLoading(boolean toSet) {

        if(toSet) {
            progressBar.setVisibility(View.VISIBLE);
            btnEasy.setEnabled(false);
            btnMedium.setEnabled(false);
            btnHard.setEnabled(false);
            btnBack.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            btnEasy.setEnabled(true);
            btnMedium.setEnabled(true);
            btnHard.setEnabled(true);
            btnBack.setEnabled(true);
        }

    }

    void setFragmentStatus(int status) {

        switch (status) {
            case STATUS_MENU:
                clMenu.setVisibility(View.VISIBLE);
                clNewGame.setVisibility(View.INVISIBLE);
                break;
            case STATUS_NEW_GAME:
                clMenu.setVisibility(View.INVISIBLE);
                clNewGame.setVisibility(View.VISIBLE);
                break;
        }
    }

    void printShortToastMessage(CharSequence msg) {
        if (toast == null || !toast.getView().isShown())
        {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.show();
        }
    }


}
