package it.bff.sudoku;

import android.view.View;

import it.bff.sudoku.database.SudokuScore;

class SudokuActivityListener implements View.OnClickListener {

    private SudokuActivity activity;
    private SudokuActivityHolder holder;

    SudokuActivityListener(SudokuActivity activity, SudokuActivityHolder holder) {
        this.activity = activity;
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {

        // click on the button
        if(v.getId() == holder.getBtnCancelId()) {
            holder.deleteHintsInCell();
        }
        else if(v.getId() == holder.getBtnNoteId()) {
            holder.changeNoteModality();
        }
        else if(v.getId() == holder.getBtnHelpId()) {
            holder.requestHelp();
        }
        // click on the number
        else if(v.getId() == holder.getTvNumberOneId()) {
            holder.tryNumberInCell(1);
        }
        else if(v.getId() == holder.getTvNumberTwoId()) {
            holder.tryNumberInCell(2);
        }
        else if(v.getId() == holder.getTvNumberThreeId()) {
            holder.tryNumberInCell(3);
        }
        else if(v.getId() == holder.getTvNumberFourId()) {
            holder.tryNumberInCell(4);
        }
        else if(v.getId() == holder.getTvNumberFiveId()) {
            holder.tryNumberInCell(5);
        }
        else if(v.getId() == holder.getTvNumberSixId()) {
            holder.tryNumberInCell(6);
        }
        else if(v.getId() == holder.getTvNumberSevenId()) {
            holder.tryNumberInCell(7);
        }
        else if(v.getId() == holder.getTvNumberEightId()) {
            holder.tryNumberInCell(8);
        }
        else if(v.getId() == holder.getTvNumberNineId()) {
            holder.tryNumberInCell(9);
        }

        else if(v.getId() == holder.getBtnLoseId()){
            activity.exit(SudokuActivity.ExitType.TYPE_DESTROY_SAVE);
        }
        else if(v.getId() == holder.getBtnWinModalId())
        {
            SudokuScore sudokuScore = new SudokuScore();
            String playerName = holder.getEtPlayerNameText();

            if(playerName.equals(""))
                playerName = activity.getResources().getString(R.string.sudoku_activity_default_player_name);

            // setup the score
            sudokuScore.setPlayerName(playerName);
            sudokuScore.setPoints(Integer.toString(holder.getBoardLogic().getActualPoints()));
            sudokuScore.setTimer(holder.millisToStringTimer());

            // move score to db
            holder.getDb().sudokuDAO().insertAll(sudokuScore);

            // exit from activity
            activity.exit(SudokuActivity.ExitType.TYPE_DESTROY_SAVE);
        }

    }
}
