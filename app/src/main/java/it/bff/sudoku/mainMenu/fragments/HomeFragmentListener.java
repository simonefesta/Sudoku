package it.bff.sudoku.mainMenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import it.bff.sudoku.ModalDialog;
import it.bff.sudoku.R;
import it.bff.sudoku.SudokuActivity;
import it.bff.sudoku.audio.ControllerAudio;
import it.bff.sudoku.mainMenu.MenuActivity;
import it.bff.sudoku.storage.StoreManager;
import it.bff.sudoku.sudokuView.SudokuLogic;
import it.bff.sudoku.sudokuWebApi.VolleySudokuApi;

class HomeFragmentListener implements View.OnClickListener, Response.ErrorListener, Response.Listener<String> {


    private MenuActivity activity;
    private HomeFragmentHolder holder;
    private VolleySudokuApi sudokuApi;
    private VolleySudokuApi.Difficulty requestedDifficulty;

    HomeFragmentListener(MenuActivity activity, HomeFragmentHolder holder) {
        this.activity = activity;
        this.holder = holder;

        sudokuApi = new VolleySudokuApi(activity.getApplicationContext(), this, this);
    }

    @Override
    public void onClick(View v) {
        prepareSoundAndPlay(holder.getControllerAudio());
        if(v.getId() == holder.getBtnContinueId()) {
            String matrixToParse;

            // get the saved game from internal storage
            List<String> savedGame = StoreManager.loadTextFile(SudokuActivity.SAVE_FILE_NAME,
                    StoreManager.FileFormat.FORMAT_DATA,
                    StoreManager.FileType.TYPE_DOCUMENT, activity);

            if(savedGame == null) {
                holder.printShortToastMessage(activity.getResources().getString(R.string.main_activity_toast_no_save_present));
                return;
            }

            matrixToParse = savedGame.get(0);

            startNewGame(matrixToParse);
        }
        else if(v.getId() == holder.getBtnNewGameId()) {
            holder.setFragmentStatus(HomeFragmentHolder.STATUS_NEW_GAME);
        }
        else if(v.getId() == holder.getBtnBackId()) {
            holder.setFragmentStatus(HomeFragmentHolder.STATUS_MENU);
        }
        else if(v.getId() == holder.getBtnEasyId()) {
            holder.setViewInLoading(true);
            requestedDifficulty = VolleySudokuApi.Difficulty.EASY;
            sudokuApi.generateSudoku(requestedDifficulty);
        }
        else if(v.getId() == holder.getBtnMediumId()) {
            holder.setViewInLoading(true);
            requestedDifficulty = VolleySudokuApi.Difficulty.MEDIUM;
            sudokuApi.generateSudoku(requestedDifficulty);
        }
        else if(v.getId() == holder.getBtnHardId()) {
            holder.setViewInLoading(true);
            requestedDifficulty = VolleySudokuApi.Difficulty.HARD;
            sudokuApi.generateSudoku(requestedDifficulty);
        }
    }

    private void loadGameFromStorage() {

        String matrixToParse;
        String path;

        switch (requestedDifficulty) {
            case MEDIUM:
                path = MenuActivity.FILE_NAME_MATRIX_MEDIUM;
                break;
            case HARD:
                path = MenuActivity.FILE_NAME_MATRIX_HARD;
                break;
            default:
                path = MenuActivity.FILE_NAME_MATRIX_EASY;
                break;
        }

        // read all the game from file with the specified difficulty
        List<String> matrix = StoreManager.loadTextFileFromAsset(path,
                StoreManager.FileFormat.FORMAT_DATA,
                StoreManager.FileType.TYPE_DOCUMENT,
                activity);

        if(matrix == null || matrix.size() == 0) {
            holder.printShortToastMessage(activity.getResources().getString(R.string.main_activity_toast_unable_create_game));
            holder.setViewInLoading(false);
            return;
        }

        // extract one random game
        Random random = new Random();
        int randomValue = random.nextInt(matrix.size());
        matrixToParse = matrix.get(randomValue);

        startNewGame(matrixToParse);
    }

    private void startNewGame(String matrixToParse)
    {

        Intent data = new Intent(activity, SudokuActivity.class);
        data.putExtra(SudokuActivity.PARAM_MATRIX , matrixToParse);
        if(activity.getBackgroundMusicUri()!=null)
            data.putExtra(SudokuActivity.URI_BACKGROUND_MUSIC, activity.getBackgroundMusicUri().toString());
        else
            data.putExtra(SudokuActivity.URI_BACKGROUND_MUSIC, (Bundle) null);
        holder.setViewInLoading(false);

        activity.startActivityForResult(data, MenuActivity.SUDOKU_ACTIVITY_RESULT_CODE);
    }

    // *** Volley CallBack ***

    @Override
    public void onErrorResponse(VolleyError error) {
        loadGameFromStorage();
    }

    @Override
    public void onResponse(String response) {

        String board;
        try {
            // create the json object from the string in response that represents a json file
            JSONObject jsonObject = new JSONObject(response);
            // read the array that contains the Board
            board = jsonObject.getJSONArray("board").toString();

            String matrixToParse = SudokuLogic.getGameMatrixFromJson(board, 9);

            startNewGame(matrixToParse);

        } catch(JSONException e) {
            e.printStackTrace();
            loadGameFromStorage();
        }
    }
    private void prepareSoundAndPlay(ControllerAudio controllerAudio)
    {
        try { controllerAudio.prepareSoundAndPlay(); }
        catch (IOException e)
        {
            (new ModalDialog(this.activity, e.getMessage())).show();
            e.printStackTrace();
        }
    }

}
