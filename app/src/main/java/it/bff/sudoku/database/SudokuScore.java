package it.bff.sudoku.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SudokuScore
{
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="_id")
  public int idGame;
  @ColumnInfo(name="player")
  public String playerName;
  @ColumnInfo(name="mode")
  String modeType;
  @ColumnInfo(name="points")
  String points;
  @ColumnInfo(name="timer")
  String timer;

  public void setModeType(String modeType) {
    this.modeType = modeType;
  }
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }
  public void setPoints(String points) {
    this.points = points;
  }
  public void setTimer(String timer) {
    this.timer = timer;
  }

  public String getModeType() {
    return modeType;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getPoints() {
    return points;
  }

  public String getTimer() {
    return timer;
  }
}
