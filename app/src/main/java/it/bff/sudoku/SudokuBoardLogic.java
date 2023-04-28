package it.bff.sudoku;

import android.widget.Chronometer;

import it.bff.sudoku.sudokuView.SudokuLogic;

class SudokuBoardLogic implements Chronometer.OnChronometerTickListener, SudokuLogic.SudokuCallBack {

    private SudokuActivityHolder holder;

    private int points;
    private int actualErrors;
    private int maxErrors;
    private int remainingTokens;

    private int tickCounter;

    private int loseForTick;
    private int loseForErr;
    private int loseForHelp;
    private int gainOnSuc;


    SudokuBoardLogic(SudokuActivityHolder holder) {
        this.holder = holder;

        loseForTick = 2;
        loseForErr = 80;
        loseForHelp = 300;
        gainOnSuc = 100;

        maxErrors = 3;

        tickCounter = 0;
    }

    int getMaxErrors() {
        return maxErrors;
    }
    int getActualErrors() {
        return actualErrors;
    }
    int getActualPoints() {
        return points;
    }
    int getRemainingTokens() {
        return remainingTokens;
    }

    void setActualErrors(int actualErrors) {

        if(actualErrors > maxErrors)
            actualErrors = maxErrors;

        this.actualErrors = actualErrors;
        holder.setTvActualErrorText(Integer.toString(actualErrors));
    }
    void setActualPoints(int points) {

        if(points < 0)
            points = 0;

        this.points = points;
        holder.setTvPointsText(Integer.toString(points));
    }
    void setRemainingTokens(int remainingTokens) {

        if(remainingTokens < 0)
            remainingTokens = 0;

        this.remainingTokens = remainingTokens;
        holder.setTvTokenNumberText(Integer.toString(remainingTokens));
    }

    @Override
    public void onSudokuError(int insertedValue, int cellTop, int cellLeft) {

        setActualErrors(getActualErrors() + 1);
        setActualPoints(getActualPoints() - loseForErr);

        if(getActualErrors() >= getMaxErrors()) {
            stopCountingPoint();
            holder.showInfoLose();
        }
    }

    @Override
    public void onSudokuSuccess(int insertedValue, int cellTop, int cellLeft, int remainingCells, int mode) {

        if(mode == SudokuLogic.MODE_GUESS) {
            setActualPoints(getActualPoints() + gainOnSuc);
        }
        else if(mode == SudokuLogic.MODE_HELP) {
            setActualPoints(getActualPoints() - loseForHelp);
            setRemainingTokens(getRemainingTokens() - 1);
        }

        if(remainingCells == 0) {
            stopCountingPoint();
            holder.showInfoWin();
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer)
    {
        points -= loseForTick;
        if(points < 0)
            points = 0;

        setActualPoints(points);

        if(tickCounter % 5 == 0)
            holder.startChronometerAnimation();
        tickCounter++;
    }

    private void stopCountingPoint() {
        holder.stopChronometer();
        loseForTick = 0;
        loseForErr = 0;
        loseForHelp = 0;
        gainOnSuc = 0;
    }

}
