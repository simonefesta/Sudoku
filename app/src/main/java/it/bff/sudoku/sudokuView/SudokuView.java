package it.bff.sudoku.sudokuView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import java.io.IOException;

import it.bff.sudoku.ModalDialog;
import it.bff.sudoku.audio.AudioListener;
import it.bff.sudoku.audio.ControllerAudio;
import it.bff.sudoku.R;

public class SudokuView extends View implements View.OnTouchListener, SudokuLogic.SudokuCallBack {

    private ControllerAudio controllerAudioTouch;
    private ControllerAudio controllerAudioOk;
    private ControllerAudio controllerAudioNo;
    private ControllerAudio controllerAudioAuto;

    // Draw attributes
    private Paint cellsPainter; // draw the cell border
    private Paint squarePainter; // draw the border of the square
    private Paint hintPainter; // draw the text of the hint
    private Paint numberPainter; // draw the text of the number
    private Paint userNumberPainter; // draw the text of the number choose by user
    private Paint selectedCellPainter; // re-draw the selected cell highlighting it (when empty)
    private Paint inLineCellPaint; // re-draw the cells in the same column, row and square of the selected cell
    private Paint selectedEditedCellPaint; // re-draw the selected cell highlighting it (when already filled)
    private Paint errorCellPaint; // draw the cell where the error occur


    private float cellSize;
    private float marginLeft;
    private float marginTop;

    // Logic attributes
    private SudokuLogic sudoku;

    private int errorLeft;
    private int errorTop;

    // Attributes to manage the touch
    private int coordinatesLeft;
    private int coordinatesTop;


    public SudokuView(Context context) {
        super(context);
        init(context, null);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, @Nullable AttributeSet attrs) {

        AudioListener audioListener = new AudioListener((Activity) context);
        int IO_SESSION = 0;

        controllerAudioTouch = new ControllerAudio((Activity) context, R.raw.tap_sound, audioListener, IO_SESSION);
        controllerAudioNo = new ControllerAudio((Activity) context, R.raw.no_suond, audioListener, IO_SESSION);
        controllerAudioOk = new ControllerAudio((Activity) context, R.raw.ok_sound, audioListener, IO_SESSION);
        controllerAudioAuto = new ControllerAudio((Activity) context, R.raw.autoinsert_sound, audioListener, IO_SESSION);

        errorLeft = -1;
        errorTop = -1;

        // enable onTouchListener
        setOnTouchListener(this);
        coordinatesLeft = -1;
        coordinatesTop = -1;

        // setup the painters according to external configuration
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SudokuView);;

        cellsPainter = new Paint();
        cellsPainter.setColor(attributes.getColor(R.styleable.SudokuView_borderCellsColor, Color.BLACK));
        cellsPainter.setStyle(Paint.Style.STROKE);

        squarePainter = new Paint();
        squarePainter.setColor(attributes.getColor(R.styleable.SudokuView_borderCellsColor, Color.BLACK));
        squarePainter.setStyle(Paint.Style.STROKE);

        hintPainter = new Paint();
        hintPainter.setColor(attributes.getColor(R.styleable.SudokuView_textHintColor, Color.DKGRAY));

        numberPainter = new Paint();
        numberPainter.setColor(attributes.getColor(R.styleable.SudokuView_textNumberColor, Color.DKGRAY));

        userNumberPainter = new Paint();
        userNumberPainter.setColor(attributes.getColor(R.styleable.SudokuView_userTextNumberColor, Color.rgb(0, 110, 229)));

        // old color -> light green = Color.rgb(71, 198, 16)
        selectedCellPainter = new Paint();
        selectedCellPainter.setColor(attributes.getColor(R.styleable.SudokuView_cellsHighlightColorEmpty, Color.rgb(95, 199, 255)));
        selectedCellPainter.setStyle(Paint.Style.STROKE);

        // old color -> super light green = Color.rgb(199, 253, 176)
        inLineCellPaint = new Paint();
        inLineCellPaint.setColor(attributes.getColor(R.styleable.SudokuView_cellsHighlightColorNear, Color.rgb(197, 241, 255)));

        selectedEditedCellPaint = new Paint();
        selectedEditedCellPaint.setColor(attributes.getColor(R.styleable.SudokuView_cellsHighlightColorFilled, Color.rgb(197, 241, 255)));

        errorCellPaint = new Paint();
        errorCellPaint.setColor(attributes.getColor(R.styleable.SudokuView_cellError, Color.rgb(181, 25, 25)));


        attributes.recycle();
    }

    // I want my view to be always a square
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height;
        int width;
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Set width and height of my view at the same value. The value is the biggest between them
        if(measuredWidth >= measuredHeight) {
            width = measuredWidth;
            height = measuredWidth;
        } else {
            width = measuredHeight;
            height = measuredHeight;
        }

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // calculate the cells' size to cover all the space into the view
        if(getHeight() > getWidth())
            cellSize = (float) getWidth() / sudoku.getNumberOfCells();
        else
            cellSize = (float) getHeight() / sudoku.getNumberOfCells();

        // calculate the stroke width according to the cell dimension; setup the painter
        float strokeWidthCell = cellSize / 30;
        float strokeWidthSquare = cellSize / 10;
        cellsPainter.setStrokeWidth(strokeWidthCell);
        squarePainter.setStrokeWidth(strokeWidthSquare);
        selectedCellPainter.setStrokeWidth(strokeWidthSquare);

        // calculate the minimum margin to avoid having the external border of the table out of the view
        marginLeft = strokeWidthSquare / 2;
        marginTop = strokeWidthSquare / 2;

        // re-calculate the cells' size to avoid the bottom and right margin going over the view
        // the operation is similar as above, but the height (or the width) is considered without the margins
        if(getHeight() > getWidth())
            cellSize = (getWidth() - 2 * marginLeft) / sudoku.getNumberOfCells();
        else
            cellSize = (getHeight() - 2 * marginTop) / sudoku.getNumberOfCells();

        // calculate the margins to move the table to the center of the view
        if(getWidth() > getHeight())
            marginLeft = marginLeft + (getWidth() - cellSize * sudoku.getNumberOfCells()) / 2;
        else if(getHeight() > getWidth())
            marginTop = marginTop + (getHeight() - cellSize * sudoku.getNumberOfCells()) / 2;

        // calculate the text size of the number to cover x% of the cells
        // then, calculate the margin to put the character at the center of the cell
        float x = 0.6f;
        Rect characterRect = calculatePainterTextSize(numberPainter, cellSize * x);
        userNumberPainter.setTextSize(numberPainter.getTextSize());

        float characterMarginLeft = (cellSize - characterRect.width()) / 2;
        float characterMarginTop = -(cellSize - characterRect.height()) / 2 + cellSize;

        // A cell is logically divided into 3x3 matrix (3 = getNumberOfSquares() = sqrt(getNumberOfCells()))
        // The hintCellSize is 1/3 of the cellSize
        float hintCellSize = cellSize / sudoku.getNumberOfSquares();
        Rect hintCharacterRect = calculatePainterTextSize(hintPainter, hintCellSize * x);

        float hintCharacterMarginLeft = (hintCellSize - hintCharacterRect.width()) / 2;
        float hintCharacterMarginTop = -(hintCellSize - hintCharacterRect.height()) / 2 + hintCellSize;


        // setup some parameters before to draw
        float left, top;
        int numberOfCells = sudoku.getNumberOfCells();
        int numberOfSquare = sudoku.getNumberOfSquares();
        float squareSize = cellSize * numberOfSquare;
        float nos = sudoku.getNumberOfSquares(); // float version of numberOfSquare
        String cellValue;


        // draw the cells and the inner number
        for(int i=0; i<numberOfCells; i++) {
            for(int j=0; j<numberOfCells; j++) {

                cellValue = Integer.toString(sudoku.getValue(i, j));
                left = marginLeft + cellSize * j;
                top = marginTop + cellSize * i;

                // coordinatesTop and coordinatesLeft are the coordinates of the selected cell

                if(sudoku.cellIsEditable(coordinatesTop, coordinatesLeft)) {

                    // highlight the cells in the same square of the selected cell
                    if(Math.floor(i/nos) == Math.floor(coordinatesTop/nos) && Math.floor(j/nos) == Math.floor(coordinatesLeft/nos) && !(i == coordinatesTop && j == coordinatesLeft))
                        canvas.drawRect(left, top, left + cellSize, top + cellSize, inLineCellPaint);
                    // highlight the cells in the same row and column of the selected cells
                    else if(i == coordinatesTop ^ j == coordinatesLeft)
                        canvas.drawRect(left, top, left + cellSize, top + cellSize, inLineCellPaint);
                }
                else if(sudoku.cellIsAlreadySet(coordinatesTop, coordinatesLeft)) {

                    // highlight the cell if it has been selected
                    if(i == coordinatesTop && j == coordinatesLeft)
                        canvas.drawRect(left, top, left + cellSize, top + cellSize, selectedEditedCellPaint);
                }

                // draw the cell's border
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellsPainter);

                // draw the number into the cell
                if(!cellValue.equals("0")) {
                    if(sudoku.cellIsAlreadySet(i, j))
                        canvas.drawText(cellValue, left + characterMarginLeft, top + characterMarginTop, userNumberPainter);
                    else
                        canvas.drawText(cellValue, left + characterMarginLeft, top + characterMarginTop, numberPainter);
                }
                // else, draw the hints if present
                else {

                    for(int k=0; k<sudoku.getNumberOfCells(); k++) {
                        // check if the k° hint are present in the (i,j) cell
                        if(sudoku.hintArePresent(i, j, k)) {
                            int hintValue = k + 1;
                            String hintString =  Integer.toString(hintValue);

                            /*
                            hintCellTop-hintCellLeft(k)

                            00(0) 01(1) 02(2)
                            10(3) 11(4) 12(5)
                            20(6) 21(7) 22(8)
                            */
                            // coordinate (x,y) of the sub-matrix into the cell
                            int hintCellTop = k/numberOfSquare;
                            int hintCellLeft = k % numberOfSquare;

                            float moveLeft = hintCellLeft * hintCellSize + hintCharacterMarginLeft;
                            float moveTop = hintCellTop * hintCellSize + hintCharacterMarginTop;

                            canvas.drawText(hintString, left + moveLeft, top + moveTop, hintPainter);
                        }
                    }
                }
            }
        }

        // draw the square
        for(int i=0; i<numberOfSquare; i++) {
            for(int j=0; j<numberOfSquare; j++) {

                left = marginLeft + squareSize * j;
                top = marginTop + squareSize * i;
                canvas.drawRect(left, top, left + squareSize, top + squareSize, squarePainter);
            }
        }

        // draw error cell
        if(sudoku.cellIsEditable(errorTop, errorLeft))
            canvas.drawRect(marginLeft + cellSize * errorLeft, marginTop + cellSize * errorTop, marginLeft + cellSize * errorLeft + cellSize, marginTop + cellSize * errorTop + cellSize, errorCellPaint);
        // draw cursor
        else if(sudoku.cellIsEditable(coordinatesTop, coordinatesLeft))
            canvas.drawRect(marginLeft + cellSize * coordinatesLeft, marginTop + cellSize * coordinatesTop, marginLeft + cellSize * coordinatesLeft + cellSize, marginTop + cellSize * coordinatesTop + cellSize, selectedCellPainter);

    }

    private Rect calculatePainterTextSize(Paint paint, float squareSize) {

        Rect bounds = new Rect();

        // each iteration calculates number of the pixel taken by the character "0" according to the textSize
        // exit when character "0" is large enough to take at least "squareSize" pixel
        for(float size=1; size<300; size+=1f) {

            paint.setTextSize(size);
            paint.getTextBounds("0", 0, 1, bounds);

            if(bounds.width() >= squareSize || bounds.height() >= squareSize)
                return bounds;
        }

        return bounds;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // on touchDown save the coordinate of the click
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            coordinatesTop = detectTableCoordinatesRight(event.getY());
            coordinatesLeft = detectTableCoordinatesLeft(event.getX());


            if(sudoku.cellIsEditable(coordinatesTop, coordinatesLeft))
                prepareSoundAndPlay(controllerAudioTouch);

            // re-draw the table highlighting the selected cell
            postInvalidate();

            return true;
        }

        return false; // propagate to call onDrag
    }

    private int detectTableCoordinatesLeft(float clickLeft) {

        int coordinate;

        float realClickLeft = clickLeft - marginLeft;
        float positionLeft = realClickLeft / cellSize;
        int cellLeft = (int) Math.floor(positionLeft);

        if(cellLeft < 0 || cellLeft >= sudoku.getNumberOfCells())
            coordinate = -1;
        else
            coordinate = cellLeft;

        return coordinate;
    }

    private int detectTableCoordinatesRight(float clickTop) {

        int coordinate;

        float realClickTop = clickTop - marginTop;
        float positionTop = realClickTop / cellSize;
        int cellTop = (int) Math.floor(positionTop);

        if(cellTop < 0 || cellTop >= sudoku.getNumberOfCells())
            coordinate = -1;
        else
            coordinate = cellTop;

        return coordinate;
    }

    public void disableOnTouchListener() {
        setOnTouchListener(null);
    }

    @Override
    public void onSudokuError(int insertedValue, int cellTop, int cellLeft) {

        // start the sound
        prepareSoundAndPlay(controllerAudioNo);

        // set the coordinates of the error
        errorTop = cellTop;
        errorLeft = cellLeft;

        // start the thread to color the cell where error occur
        SudokuErrorViewThread errorThread = new SudokuErrorViewThread(this);
        errorThread.start();
    }

    @Override
    public void onSudokuSuccess(int insertedValue, int cellTop, int cellLeft, int remainingCells, int mode) {

        if(mode == SudokuLogic.MODE_GUESS)
            this.prepareSoundAndPlay(controllerAudioOk);
        else if(mode == SudokuLogic.MODE_HELP)
            this.prepareSoundAndPlay(controllerAudioAuto);
    }

    // ***  Methods called from SudokuErrorViewThread ***

    void setErrorCellPaintAlpha(int alpha) {
        errorCellPaint.setAlpha(alpha);
    }

    void disableError() {
        errorTop = -1;
        errorLeft = -1;
    }

    // *** Method to interface with caller ***

    public void setNumber(int number) {
        sudoku.tryNumber(coordinatesTop, coordinatesLeft, number, SudokuLogic.MODE_GUESS);
        postInvalidate();
    }

    public void autoSetNumber() {
        int[] coordinates = sudoku.autoSetNumber();

        coordinatesTop = coordinates[0];
        coordinatesLeft = coordinates[1];

        postInvalidate();
    }

    public void removeHints() {
        sudoku.deleteHints(coordinatesTop, coordinatesLeft);
        postInvalidate();
    }

    public void setHint(int value) {
        sudoku.setHint(coordinatesTop, coordinatesLeft, value);
        postInvalidate();
    }

    public void setSudokuTable(String matrix) {

        sudoku = new SudokuLogic(9, matrix);
        sudoku.attach(this);
    }

    public void setOnSudokuEvent(SudokuLogic.SudokuCallBack listener) {
        sudoku.attach(listener);
    }

    public String getActualGameStatus() { return sudoku.getActualGameStatus(); }

    private void prepareSoundAndPlay(ControllerAudio controllerAudio)
    {
        try { controllerAudio.prepareSoundAndPlay(); }
        catch (IOException e)
        {
            (new ModalDialog(this.getContext(), e.getMessage())).show();
            e.printStackTrace();
        }
    }

}
