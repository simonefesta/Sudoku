package it.bff.sudoku.sudokuWebApi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleySudokuApi {

    private Response.ErrorListener errorListener;
    private Response.Listener<String> responseListener;
    private Context context;

    public VolleySudokuApi(Context context, Response.ErrorListener errorListener, Response.Listener<String> responseListener) {
        this.context = context;
        this.errorListener = errorListener;
        this.responseListener = responseListener;
    }

    public void generateSudoku(Difficulty difficulty) {

        String url = "https://sugoku.herokuapp.com/board?difficulty=%s";
        url = String.format(url, difficulty.getName());
        apiCall(url);
    }

    private void apiCall(String url) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        requestQueue.add(stringRequest);
    }

    public enum Difficulty {

        EASY("easy"),
        MEDIUM( "medium"),
        HARD("hard");

        private String name;

        private Difficulty(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
