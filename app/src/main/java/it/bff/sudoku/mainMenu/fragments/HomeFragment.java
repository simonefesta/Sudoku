package it.bff.sudoku.mainMenu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import it.bff.sudoku.R;
import it.bff.sudoku.mainMenu.MenuActivity;

public class HomeFragment extends Fragment {

    private MenuActivity activity;

    public HomeFragment(MenuActivity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        new HomeFragmentHolder(view, activity);

        return view;
    }
}
