package it.bff.sudoku.sudokuView;

import android.util.Log;
import android.util.TimingLogger;

import it.bff.sudoku.sudokuView.exceptions.SudokuException;

class SudokuSolveThread extends Thread {

    private CallBack callBack;
    private int[][] matrixToSolve;
    private int numberOfCells;
    private int numberOfSquares;

    SudokuSolveThread(int[][] matrix, int numberOfCells, int numberOfSquares, CallBack callBack) {

        matrixToSolve = new int[numberOfCells][numberOfCells];

        copyMatrixValues(matrixToSolve, matrix, numberOfCells);

        this.numberOfCells = numberOfCells;
        this.numberOfSquares = numberOfSquares;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        SudokuGraph sudokuGraph = new SudokuGraph();
        int[][] backup = new int[numberOfCells][numberOfCells];
        copyMatrixValues(backup, matrixToSolve, numberOfCells);

        //Commentato il timer. Decommentare per effettuare misure
        //long startTime = System.nanoTime();
        sudokuGraph.solveBrute(matrixToSolve, numberOfCells);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //String string = Long.toString(duration);
        //Log.i("tempo", string+"ns");
        //Log.i("timer", Boolean.toString(sudokuGraph.checkSudokuStatus(matrixToSolve)));

        if(sudokuGraph.checkSudokuStatus(matrixToSolve))
            callBack.onSudokuSolved(matrixToSolve);
        else
        {
            copyMatrixValues(matrixToSolve, backup, numberOfCells);
            sudokuGraph.solveBrute(matrixToSolve, numberOfCells);
            if (sudokuGraph.checkSudokuStatus(matrixToSolve))
                callBack.onSudokuSolved(matrixToSolve);
            else
            {
                try {
                    callBack.onSudokuError();
                } catch (SudokuException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void copyMatrixValues(int[][] matrix1, int[][] matrix2, int dim) {

        for(int i=0; i<dim; i++)
            System.arraycopy(matrix2[i], 0, matrix1[i], 0, dim);
    }

    interface CallBack {
        void onSudokuSolved(int[][] matrixToSolve);
        void onSudokuError() throws SudokuException;
    }
}
