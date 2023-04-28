package it.bff.sudoku.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SudokuScore.class}, version = 1, exportSchema = false)
public abstract class AppSudokuDatabase extends RoomDatabase {
  public abstract SudokuDAO sudokuDAO();
}
