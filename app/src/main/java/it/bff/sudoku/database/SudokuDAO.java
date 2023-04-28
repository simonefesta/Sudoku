package it.bff.sudoku.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SudokuDAO {

  @Query("SELECT * FROM SudokuScore ORDER BY points DESC")
  List<SudokuScore> getAll();

  @Query("SELECT * FROM SudokuScore WHERE _id IN (:ids)")
  List<SudokuScore> loadAllByIds(int[] ids);

  @Query("SELECT count(*) FROM SudokuScore")
  int size();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(SudokuScore... sudoku);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<SudokuScore> sudoku);

  @Delete
  void delete(SudokuScore sudoku);
}
