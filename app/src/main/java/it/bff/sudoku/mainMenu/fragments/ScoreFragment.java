package it.bff.sudoku.mainMenu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.bff.sudoku.R;
import it.bff.sudoku.mainMenu.MenuActivity;

public class ScoreFragment extends Fragment {

    private MenuActivity activity;

    public ScoreFragment(MenuActivity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, container, false);
        new ScoreFragmentHolder(view, activity);

        return view;
    }
}
