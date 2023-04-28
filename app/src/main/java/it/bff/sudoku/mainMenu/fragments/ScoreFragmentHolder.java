package it.bff.sudoku.mainMenu.fragments;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import it.bff.sudoku.R;
import it.bff.sudoku.database.AppSudokuDatabase;
import it.bff.sudoku.database.SudokuScore;

class ScoreFragmentHolder
{
    private Activity activity;

    ScoreFragmentHolder(View view, Activity activity)
    {
        this.activity=activity;

        AppSudokuDatabase db = getDB();

        RecyclerView rvScore = view.findViewById(R.id.rvScore);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        rvScore.setLayoutManager(layoutManager);

        List<SudokuScore> sudokuScores = db.sudokuDAO().getAll();

        Adapter adapter = new Adapter(sudokuScores, db);
        rvScore.setAdapter(adapter);

    }

    private AppSudokuDatabase getDB() {
    return Room.databaseBuilder(activity.getApplicationContext(), AppSudokuDatabase.class, "sudoku_score.db").allowMainThreadQueries().createFromAsset("database/sudoku_score.db").build();
}
}
