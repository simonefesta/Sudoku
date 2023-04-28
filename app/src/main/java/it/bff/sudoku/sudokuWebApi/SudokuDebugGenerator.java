package it.bff.sudoku.sudokuWebApi;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import it.bff.sudoku.sudokuView.SudokuLogic;
import it.bff.sudoku.sudokuView.exceptions.SudokuException;

public class SudokuDebugGenerator implements Response.ErrorListener, Response.Listener<String> {

    private Activity activity;
    private StringBuilder matrix;

    public SudokuDebugGenerator(Activity activity) {
        this.activity = activity;
        matrix = new StringBuilder();
    }

    public void generate(int numberOfSudoku) {

        VolleySudokuApi sudokuApi = new VolleySudokuApi(activity, this, this);

        for(int i=0; i<numberOfSudoku; i++) {
            sudokuApi.generateSudoku(VolleySudokuApi.Difficulty.MEDIUM);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

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
            String timer = "00:00:00";

            matrix.append(matrixToParse + "&" + timer + "\n");

        } catch(JSONException ignored) {

        }
    }
}
