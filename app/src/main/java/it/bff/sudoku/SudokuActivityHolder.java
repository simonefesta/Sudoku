package it.bff.sudoku;

import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.room.Room;

import it.bff.sudoku.database.AppSudokuDatabase;
import it.bff.sudoku.sudokuView.SudokuLogic;
import it.bff.sudoku.sudokuView.SudokuView;
import it.bff.sudoku.sudokuView.exceptions.SudokuException;

class SudokuActivityHolder {

    private SudokuView sudokuView;
    private SudokuActivity activity;
    private SudokuBoardLogic boardLogic;

    private boolean useNote;

    private Animation rotationAnimation;

    private ImageView imgChronometer;
    private Chronometer chronometer;
    private CardView modalWin;
    private CardView modalLose;

    private Button btnNote;
    private Button btnHelp;

    private TextView tvPoints;
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvActualError;
    private TextView tvTokenNumber;

    private EditText etPlayerName;
    private AppSudokuDatabase db;


    SudokuActivityHolder(SudokuActivity activity) {

        this.activity = activity;
        boardLogic = new SudokuBoardLogic(this);

        useNote = false;
        createDB();

        sudokuView = activity.findViewById(R.id.sudokuView);

        // chronometer
        chronometer = activity.findViewById(R.id.chronometer);
        imgChronometer = activity.findViewById(R.id.imgChronometer);

        // Hidden card view to show when win
        Button btnWinModal = activity.findViewById(R.id.btnWinModal);
        modalWin = activity.findViewById(R.id.cvWin);
        tvScore = activity.findViewById(R.id.tvScore);
        etPlayerName = activity.findViewById(R.id.etPlayerName);

        // Hidden card view to show when lose
        Button btnLose = activity.findViewById(getBtnLoseId());
        modalLose = activity.findViewById(R.id.cvLose);

        // text view to show points/errors/time/tokens
        tvPoints = activity.findViewById(R.id.tvPoints);
        tvActualError = activity.findViewById(R.id.tvActualErrors);
        tvTokenNumber = activity.findViewById(R.id.tvTokenNumber);
        tvTime = activity.findViewById(R.id.tvTime);

        // Button help/note/cancel
        Button btnCancel = activity.findViewById(getBtnCancelId());
        btnNote = activity.findViewById(getBtnNoteId());
        btnHelp = activity.findViewById(getBtnHelpId());

        // Numbers text view
        TextView tvNumberOne = activity.findViewById(getTvNumberOneId());
        TextView tvNumberTwo = activity.findViewById(getTvNumberTwoId());
        TextView tvNumberThree = activity.findViewById(getTvNumberThreeId());
        TextView tvNumberFour = activity.findViewById(getTvNumberFourId());
        TextView tvNumberFive = activity.findViewById(getTvNumberFiveId());
        TextView tvNumberSix = activity.findViewById(getTvNumberSixId());
        TextView tvNumberSeven = activity.findViewById(getTvNumberSevenId());
        TextView tvNumberEight = activity.findViewById(getTvNumberEightId());
        TextView tvNumberNine = activity.findViewById(getTvNumberNineId());
        TextView tvMaxError = activity.findViewById(R.id.tvMaxErrors);

        // setup listener
        SudokuActivityListener listener = new SudokuActivityListener(activity, this);

        btnCancel.setOnClickListener(listener);
        btnNote.setOnClickListener(listener);
        btnHelp.setOnClickListener(listener);
        btnLose.setOnClickListener(listener);

        tvNumberOne.setOnClickListener(listener);
        tvNumberTwo.setOnClickListener(listener);
        tvNumberThree.setOnClickListener(listener);
        tvNumberFour.setOnClickListener(listener);
        tvNumberFive.setOnClickListener(listener);
        tvNumberSix.setOnClickListener(listener);
        tvNumberSeven.setOnClickListener(listener);
        tvNumberEight.setOnClickListener(listener);
        tvNumberNine.setOnClickListener(listener);
        btnWinModal.setOnClickListener(listener);

        // animation
        rotationAnimation = AnimationUtils.loadAnimation(activity, R.anim.rotation);

        String val = "/" + boardLogic.getMaxErrors();
        tvMaxError.setText(val);
    }


    int getBtnCancelId() {
        return R.id.btnCancel;
    }
    int getBtnNoteId() {
        return R.id.btnNote;
    }
    int getBtnHelpId() {
        return R.id.btnHelp;
    }

    int getTvNumberOneId() {
        return R.id.tvNumberOne;
    }
    int getTvNumberTwoId() {
        return R.id.tvNumberTwo;
    }
    int getTvNumberThreeId() {
        return R.id.tvNumberThree;
    }
    int getTvNumberFourId() {
        return R.id.tvNumberFour;
    }
    int getTvNumberFiveId() {
        return R.id.tvNumberFive;
    }
    int getTvNumberSixId() {
        return R.id.tvNumberSix;
    }
    int getTvNumberSevenId() {
        return R.id.tvNumberSeven;
    }
    int getTvNumberEightId() {
        return R.id.tvNumberEight;
    }
    int getTvNumberNineId() {
        return R.id.tvNumberNine;
    }

    int getBtnWinModalId(){
        return R.id.btnWinModal;
    }
    String getEtPlayerNameText() {
        return etPlayerName.getText().toString();
    }
    AppSudokuDatabase getDb()
    {
        return db;
    }
    int getBtnLoseId(){
        return R.id.btnLose;
    }


    // *** called from SudokuActivity ***

    void setSudokuTable(String matrix) throws SudokuException {

        String[] stringArray;
        stringArray = SudokuLogic.splitActualMatrix(matrix);

        sudokuView.setSudokuTable(stringArray[0]);
        sudokuView.setOnSudokuEvent(boardLogic);

        setTimer(stringArray[1]);
        boardLogic.setActualErrors(Integer.parseInt(stringArray[2]));
        boardLogic.setActualPoints(Integer.parseInt(stringArray[3]));
        boardLogic.setRemainingTokens(Integer.parseInt(stringArray[4]));
    }

    private void setTimer(String timer) {
        chronometer.setOnChronometerTickListener(boardLogic);
        chronometer.setBase(stringTimerToMillis(timer));
        chronometer.start();
    }

    String getActualGameMatrix() {
        String matrix = sudokuView.getActualGameStatus();
        String timer = millisToStringTimer();
        String errors = Integer.toString(boardLogic.getActualErrors());
        String pointsStr = Integer.toString(boardLogic.getActualPoints());
        String numberTokenHelp = Integer.toString(boardLogic.getRemainingTokens());

        return matrix + "&" + timer + "&" + errors + "&" + pointsStr + "&" + numberTokenHelp;
    }

    void disableHelp() {
        int color = activity.getResources().getColor(R.color.btn_help_disable_color, null);

        btnHelp.setEnabled(false);
        btnHelp.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    // *** called from SudokuBoardLogic ***

    void startChronometerAnimation() {
        imgChronometer.startAnimation(rotationAnimation);
    }

    void setTvActualErrorText (String errors) {
        tvActualError.setText(errors);
    }

    void setTvPointsText(String points) {
        tvPoints.setText(points);
    }

    void setTvTokenNumberText(String tokens) {
        tvTokenNumber.setText(String.format("x%s", tokens));
    }

    void showInfoWin() {
        activity.setExitType(SudokuActivity.ExitType.TYPE_DESTROY_SAVE);

        modalWin.setVisibility(View.VISIBLE);
        tvScore.setText(String.format("%s", boardLogic.getActualPoints()));
        tvTime.setText(millisToStringTimer());
    }

    void showInfoLose() {
        activity.setExitType(SudokuActivity.ExitType.TYPE_DESTROY_SAVE);

        modalLose.setVisibility(View.VISIBLE);
        sudokuView.disableOnTouchListener();
        disableHelp();
    }

    void stopChronometer() {
        chronometer.stop();
    }

    // *** called from SudokuActivityListener ***

    void changeNoteModality() {

        int color;

        if(useNote)
            color = activity.getResources().getColor(R.color.btn_default_color, null);
        else
            color = activity.getResources().getColor(R.color.btn_note_able, null);

        useNote = !useNote;
        btnNote.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    void deleteHintsInCell() {
        sudokuView.removeHints();
    }

    void requestHelp() {
        if(boardLogic.getRemainingTokens() > 0)
            sudokuView.autoSetNumber();
    }

    void tryNumberInCell(int number) {

        if(useNote) {
            sudokuView.setHint(number);
        }
        else {
            sudokuView.setNumber(number);
        }

    }

    SudokuBoardLogic getBoardLogic() {
        return boardLogic;
    }

    // *** Chronometer methods ***

    private long stringTimerToMillis(String timer)
    {
        String[] values = timer.split(":");

        int hours   = Integer.parseInt(values[0]);
        int minutes = Integer.parseInt(values[1]);
        int seconds = Integer.parseInt(values[2]);

        return SystemClock.elapsedRealtime() - (hours * 3600000 + minutes * 60000 + seconds * 1000);
    }

    String millisToStringTimer()
    {
        int totalSec = (int) Math.floor(((double) SystemClock.elapsedRealtime() - (double) chronometer.getBase()) / 1000L);
        int remain;

        int hours = totalSec / 3600;
        remain = totalSec % 3600;

        int minutes = remain / 60;
        remain = remain % 60;

        int seconds = remain;

        return hours + ":" + minutes + ":" + seconds;
    }

    // *** DB methods ***

    private void createDB() {
        db = Room.databaseBuilder(activity.getApplicationContext(), AppSudokuDatabase.class, "sudoku_score.db").allowMainThreadQueries().createFromAsset("database/sudoku_score.db").build();
    }

}
