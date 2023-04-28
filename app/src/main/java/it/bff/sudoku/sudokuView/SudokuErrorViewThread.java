package it.bff.sudoku.sudokuView;

class SudokuErrorViewThread extends Thread {

    private SudokuView sudokuView;

    SudokuErrorViewThread(SudokuView sudokuView) {
        this.sudokuView = sudokuView;
    }

    @Override
    public void run() {

        final int REFRESH_WAIT = 64; // 30fps with a refresh every 32ms / 60fps with a refresh every 16ms / ...
        final int NUMBER_OF_FRAME = 10; // make sure NUMBER_OF_FRAME % 2 = 0

        // increase alpha from 0 to 100
        for(int i=0; i<NUMBER_OF_FRAME/2; i++) {

            try {
                Thread.sleep(REFRESH_WAIT);
            } catch (InterruptedException ignored) {
                interrupt();
            }

            sudokuView.setErrorCellPaintAlpha(100 / (NUMBER_OF_FRAME/2) * i);
            refreshView();
        }

        // decrease alpha from 100 to 0
        for(int i=NUMBER_OF_FRAME/2; i<NUMBER_OF_FRAME; i++) {

            try {
                Thread.sleep(REFRESH_WAIT);
            } catch (InterruptedException ignored) {
                interrupt();
            }

            sudokuView.setErrorCellPaintAlpha(100 - (NUMBER_OF_FRAME/2) * i);
            refreshView();
        }

        sudokuView.disableError();
        refreshView();
    }

    private void refreshView() {
        sudokuView.post(new Runnable() {
            @Override
            public void run() {
                sudokuView.postInvalidate();
            }
        });
    }
}
