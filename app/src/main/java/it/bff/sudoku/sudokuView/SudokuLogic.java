package it.bff.sudoku.sudokuView;

import android.content.res.Resources;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.bff.sudoku.R;
import it.bff.sudoku.sudokuView.exceptions.SudokuException;

public class SudokuLogic implements SudokuSolveThread.CallBack {

    public final static int MODE_GUESS = 0;
    public final static int MODE_HELP = 1;


    private int numberOfCells; // number of cells per row (or per column) (numberOfCells = 9 for a 9x9 sudoku table)
    private int numberOfSquares; // number of square per row (or per column) (numberOfSquares = 3 for a 9x9 sudoku table)
    private int[][] solvedMatrix;
    private int[][] matrix; // the matrix of sudoku
    private int[][] cellStatus; /* a bitmap representing the cell's status
                                    * 0: editable (e.g: blank cells at the start)
                                    * 1: not editable (e.g: cell set at the start)
                                    * 2: editable & already edited (e.g: cells filled by user)
                                    */
    private int[][][] hint; // a bitmap representing if the hint is selected in the specified cell

    // Observers
    private List<SudokuCallBack> observers;

    SudokuLogic(int dim, String matrixToParse) throws IllegalArgumentException {

        this.numberOfCells = dim;
        this.numberOfSquares = squareOf(dim);

        matrix = new int[numberOfCells][numberOfCells];
        solvedMatrix = new int[numberOfCells][numberOfCells];
        cellStatus = new int[numberOfCells][numberOfCells];
        hint = new int[numberOfCells][numberOfCells][numberOfCells];

        observers = new ArrayList<>();

        setupMatrix(matrixToParse);
    }

    // *** Getter and setter ***

    int getNumberOfCells() {
        return numberOfCells;
    }

    int getNumberOfSquares() {
        return numberOfSquares;
    }

    int getValue(int top, int left) {
        return matrix[top][left];
    }

    private int getRemainingCells() {

        int remaining = 0;

        for(int i=0; i<numberOfCells; i++) {
            for(int j=0; j<numberOfCells; j++) {
                if(cellIsEditable(i, j))
                    remaining++;
            }
        }

        return remaining;
    }

    private int getRemainingCellsInRow(int row) {

        int remaining = 0;

        for(int i=0; i<numberOfCells; i++) {
            if(cellIsEditable(row, i))
                remaining++;
        }

        return remaining;
    }

    private int getRemainingCellsInColumn(int col) {

        int remaining = 0;

        for(int i=0; i<numberOfCells; i++) {
            if(cellIsEditable(i, col))
                remaining++;
        }

        return remaining;
    }

    private int getRemainingCellsInSquare(int top, int left) {

        int remaining = 0;

        for(int i=0; i<numberOfSquares; i++) {
            for(int j=0; j<numberOfSquares; j++) {
                if(cellIsEditable(i + numberOfSquares * top, j + numberOfSquares * left))
                    remaining++;
            }
        }

        return remaining;
    }

    // *** Parsing sudoku methods ***

    // setup the cell according to matrixToParse
    private void setupMatrix(String matrixToParse) throws IllegalArgumentException {

        // parse the string to build the matrix

        // define the error message
        String formatError = "Format: [element1];...;[element81]$[hint1]...[hint9];...;[hint721]...[hint729]";
        String elementFormatError = "Elements: value-status;value-status;...";
        String numberRangeError = "Number out of range: matrix number must be in range [0, 9]";
        String hintRangeError = "Number out of range: hint values must be in range [1, 9]";

        // split the two part of the string
        // matrixToParse = [element1];...;[element81]$[hint1]...[hint9];...;[hint721]...[hint729]
        String[] words = matrixToParse.split("&");

        if(words.length != 2)
            throw new IllegalArgumentException(formatError);

        // elements = [element1]...[element81]
        int i = 0;
        int j = 0;
        String[] elements = words[0].split(";");
        for(String element: elements) {
            char[] elementArray = element.toCharArray();

            if(elementArray.length != 2)
                throw new IllegalArgumentException(elementFormatError);

            int value = Character.getNumericValue(elementArray[0]);
            int status = Character.getNumericValue(elementArray[1]);

            if(value < 0 || value > numberOfCells)
                throw new IllegalArgumentException(numberRangeError);

            matrix[i][j] = value;
            cellStatus[i][j] = status;

            // increment counter
            j++;
            if(j == numberOfCells) {
                j = 0;
                i++;
            }
            if(i == numberOfCells) {
                break;
            }
        }

        // parse the second string. Extract the hint into the table
        i = 0;
        j = 0;
        int k;
        String[] hints = words[1].split(";");
        for(String hint: hints) {

            char[] hintChar = hint.toCharArray();
            for(char c: hintChar) {

                k = Character.getNumericValue(c);
                if(k <= 0 || k > numberOfCells)
                    throw new IllegalArgumentException(hintRangeError);

                this.hint[i][j][k-1] = 1;
            }

            // increment counter
            j++;
            if(j == numberOfCells) {
                j = 0;
                i++;
            }
            if(i == numberOfCells) {
                break;
            }
        }

        // start the thread to solve the matrix
        SudokuSolveThread solveThread = new SudokuSolveThread(matrix, numberOfCells, numberOfSquares, this);
        solveThread.start();
    }

    // get the actual game matrix when need to save the game
    String getActualGameStatus() {

        StringBuilder matrixToParse = new StringBuilder();
        StringBuilder hintToParse = new StringBuilder("&");

        for(int i=0; i<numberOfCells; i++) {
            for(int j=0; j<numberOfCells; j++) {

                for(int k=0; k<numberOfCells; k++) {
                    if(hint[i][j][k] == 1)
                        hintToParse.append(k+1);
                }
                hintToParse.append(";");
                matrixToParse.append(matrix[i][j]);
                matrixToParse.append(cellStatus[i][j]);
                matrixToParse.append(";");
            }
        }

        return matrixToParse.toString() + hintToParse.toString();
    }

    // split the String got in getActualGameMatrix into matrixToParse and Time
    public static String[] splitActualMatrix(String matrixToParse) throws SudokuException {

        String[] stringToReturn = new String[5];

        String[] words = matrixToParse.split("&");

        if(words.length != 6)
            throw new SudokuException("");

        stringToReturn[0] = words[0] + "&" + words[1]; // board + hint
        stringToReturn[1] = words[2]; // timer
        stringToReturn[2] = words[3]; // errors
        stringToReturn[3] = words[4]; // points
        stringToReturn[4] = words[5]; // numberOfHelpToken

        return stringToReturn;
    }

    // get the actual game matrix when need to create a new game parsing from json
    public static String getGameMatrixFromJson(String jsonMatrix, int sudokuDim) {

        StringBuilder matrixToParse = new StringBuilder();
        char[] jsonMatrixArray = jsonMatrix.toCharArray();

        int i = 0;
        int j = 0;
        for(char c: jsonMatrixArray) {

            int value = Character.getNumericValue(c); // -1 if not a number

            if(value >= 0 && value <= sudokuDim) {

                matrixToParse.append(value);
                if(value == 0)
                    matrixToParse.append("0"); // value = 0 -> cell is also editable
                else
                    matrixToParse.append("1"); // cell is not editable (set as default)

                matrixToParse.append(";");

                // increment counter
                j++;
                if(j == sudokuDim) {
                    j = 0;
                    i++;
                }
                if(i == sudokuDim) {
                    break;
                }
            }
        }

        matrixToParse.append("&;&00:00:00&0&0&5"); // the game start with no hint, no timer, no error and no points

        return matrixToParse.toString();
    }

    // *** Sudoku methods ***

    boolean cellIsEditable(int top, int left) {

        if(top < 0 || top >= numberOfCells || left < 0 || left >= numberOfCells)
            return false;

        return cellStatus[top][left] == 0;
    }

    boolean cellIsAlreadySet(int top, int left) {

        if(top < 0 || top >= numberOfCells || left < 0 || left >= numberOfCells)
            return false;

        return cellStatus[top][left] == 2;
    }

    boolean hintArePresent(int top, int left, int num) {

        if(top < 0 || top >= numberOfCells || left < 0 || left >= numberOfCells || num < 0 || num >= numberOfCells)
            return false;

        return hint[top][left][num] == 1;
    }

    void tryNumber(int top, int left, int number, int mode) {

        if(number < 1 || number > numberOfCells)
            return;

        if(!cellIsEditable(top, left))
            return;

        // if thread is still working on calculating solution
        if(solvedMatrix[top][left] == 0)
            return;

        if(number == solvedMatrix[top][left]) {

            // set the number in the matrix
            matrix[top][left] = number;
            cellStatus[top][left] = 2;

            // remove the hint in the column and row
            for(int i=0; i<numberOfCells; i++) {
                if(hintArePresent(top, i, number - 1))
                    setHint(top, i, number);

                if(hintArePresent(i, left, number - 1))
                    setHint(i, left, number);
            }

            // remove the hint in the square
            int squareTop = top / numberOfSquares; // square 0, 1 or 2
            int squareLeft = left / numberOfSquares;

            for(int i=numberOfSquares*squareTop; i<numberOfSquares*squareTop + numberOfSquares; i++) {
                for(int j=numberOfSquares*squareLeft; j<numberOfSquares*squareLeft + numberOfSquares; j++) {
                    if(hintArePresent(i, j, number - 1))
                        setHint(i, j, number);
                }
            }

            // call back the listeners
            notifyOnSudokuSuccess(number, top, left, getRemainingCells(), mode);
        }
        else {
            notifyOnSudokuError(number, top, left);
        }
    }

    // delete all hints from a cell
    void deleteHints(int top, int left) {

        if(top < 0 || top >= numberOfCells || left < 0 || left >= numberOfCells)
            return;

        // if hint are present delete it
        for(int i=1; i<=numberOfCells; i++) {
            if(hintArePresent(top, left, i - 1))
                setHint(top, left, i);
        }
    }

    void setHint(int top, int left, int value) {

        if(top < 0 || top >= numberOfCells || left < 0 || left >= numberOfCells || value <= 0 || value > numberOfCells)
            return;

        // invert the value in the bitmap
        if(hint[top][left][value-1] == 0)
            hint[top][left][value-1] = 1;
        else
            hint[top][left][value-1] = 0;

    }

    int[] autoSetNumber() {

        final int NOT_DEFINED = -1;

        int[] coordinates = new int[2];
        coordinates[0] = NOT_DEFINED;
        coordinates[1] = NOT_DEFINED;

        List<Integer> remainingList = new ArrayList<>();

        // index of the most filled row
        int indexRow = NOT_DEFINED;

        // index of the most filled cols
        int indexCol = NOT_DEFINED;

        // indexes of the most filled square
        int squareLeft = NOT_DEFINED;
        int squareTop = NOT_DEFINED;

        // set indexRow
        int remaining = numberOfCells;
        for(int i=0; i<numberOfCells; i++) {
            if(getRemainingCellsInRow(i) < remaining && getRemainingCellsInRow(i) != 0) {
                indexRow = i;
                remaining = getRemainingCellsInRow(i);
            }
        }
        remainingList.add(remaining);

        // set indexCol
        remaining = numberOfCells;
        for(int i=0; i<numberOfCells; i++) {
            if(getRemainingCellsInColumn(i) < remaining && getRemainingCellsInColumn(i) != 0) {
                indexCol = i;
                remaining = getRemainingCellsInColumn(i);
            }
        }
        remainingList.add(remaining);

        // set index for square
        remaining = numberOfCells;
        for(int i=0; i<numberOfSquares; i++) {
            for(int j=0; j<numberOfSquares; j++) {
                if(getRemainingCellsInSquare(i, j) < remaining && getRemainingCellsInSquare(i, j) != 0) {
                    squareTop = i;
                    squareLeft = j;
                    remaining = getRemainingCellsInSquare(i, j);
                }
            }
        }
        remainingList.add(remaining);

        // find the minimum
        int minimumIndex = NOT_DEFINED;
        int minimumValue = numberOfCells;

        for(int i=0; i<remainingList.size(); i++) {

            if(remainingList.get(i) < minimumValue) {
                minimumValue = remainingList.get(i);
                minimumIndex = i;
            }
        }

        Random random = new Random();
        int randomIndex;
        switch (minimumIndex) {
            // the most filled is the row in indexRow index
            case 0:
                randomIndex = random.nextInt(numberOfCells);
                for(int i=0; i<numberOfCells; i++) {
                    int col = (i + randomIndex) % numberOfCells;
                    if(cellIsEditable(indexRow, col)){
                        tryNumber(indexRow, col, solvedMatrix[indexRow][col], SudokuLogic.MODE_HELP);

                        coordinates[0] = indexRow;
                        coordinates[1] = col;

                        return coordinates;
                    }
                }
                break;
            // the most filled is the column in indexCol column
            case 1:
                randomIndex = random.nextInt(numberOfCells);
                for(int i=0; i<numberOfCells; i++) {
                    int row = (i + randomIndex) % numberOfCells;
                    if(cellIsEditable(row, indexCol)){
                        tryNumber(row, indexCol, solvedMatrix[row][indexCol], SudokuLogic.MODE_HELP);

                        coordinates[0] = row;
                        coordinates[1] = indexCol;

                        return coordinates;
                    }
                }
                break;
            // the most filled is the square in (squareTop, squareLeft) coordinates
            case 2:
                int randomTop = random.nextInt(numberOfSquares);
                int randomLeft = random.nextInt(numberOfSquares);

                for(int i=numberOfSquares * squareTop; i<numberOfSquares * squareTop + numberOfSquares; i++) {
                    for(int j=numberOfSquares * squareLeft; j<numberOfSquares * squareLeft + numberOfSquares; j++) {

                        int top = (i - (numberOfSquares * squareTop) + randomTop) % numberOfSquares + (numberOfSquares * squareTop);
                        int left = (j - (numberOfSquares * squareLeft) + randomLeft) % numberOfSquares + (numberOfSquares * squareLeft);

                        if(cellIsEditable(top, left)) {
                            tryNumber(top, left, solvedMatrix[top][left], SudokuLogic.MODE_HELP);

                            coordinates[0] = top;
                            coordinates[1] = left;

                            return coordinates;
                        }
                    }
                }

                break;
        }

        return coordinates;
    }

    // *** Logic methods ***

    private int squareOf (double num) throws IllegalArgumentException {
        double square = Math.sqrt(num);
        int roundedSquare = (int) Math.round(square);

        if(roundedSquare * roundedSquare == num)
            return roundedSquare;
        else
            throw new IllegalArgumentException("Choose as dimension a number with an integer square (9, 16, 25, ...)");
    }

    @Override
    public void onSudokuSolved(int[][] matrixToSolve) {
        solvedMatrix = matrixToSolve;
    }

    @Override
    public void onSudokuError() throws SudokuException { throw new SudokuException(Resources.getSystem().getString(R.string.sudoku_critical_error));}

    // *** Observer/Listener methods ***

    void attach(SudokuCallBack listener) {
        observers.add(listener);
    }

    private void notifyOnSudokuError(int insertedValue, int cellTop, int cellLeft) {
        for(SudokuCallBack listener: observers) {
            listener.onSudokuError(insertedValue, cellTop, cellLeft);
        }
    }

    private void notifyOnSudokuSuccess(int insertedValue, int cellTop, int cellLeft, int remainingCells, int mode) {
        for(SudokuCallBack listener: observers) {
            listener.onSudokuSuccess(insertedValue, cellTop, cellLeft, remainingCells, mode);
        }
    }


    public interface SudokuCallBack {
        void onSudokuError(int insertedValue, int cellTop, int cellLeft);
        void onSudokuSuccess(int insertedValue, int cellTop, int cellLeft, int remainingCells, int mode);
    }
}
