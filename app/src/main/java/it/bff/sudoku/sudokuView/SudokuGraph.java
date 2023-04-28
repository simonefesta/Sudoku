package it.bff.sudoku.sudokuView;


import java.util.Arrays;

public class SudokuGraph {

    public boolean checkSudokuStatus(int[][] matrix)
    {
        // row checker
        for(int row = 0; row < 9; row++)
            for(int col = 0; col < 8; col++)
                for(int col2 = col + 1; col2 < 9; col2++)
                    if(matrix[row][col]==matrix[row][col2])
                        return false;

        // column checker
        for(int col = 0; col < 9; col++)
            for(int row = 0; row < 8; row++)
                for(int row2 = row + 1; row2 < 9; row2++)
                    if(matrix[row][col]==matrix[row2][col])
                        return false;
                    // grid checker
        for(int row = 0; row < 9; row += 3)
            for(int col = 0; col < 9; col += 3)
                // row, col is start of the 3 by 3 grid
                for(int pos = 0; pos < 8; pos++)
                    for(int pos2 = pos + 1; pos2 < 9; pos2++)
                        if(matrix[row + pos%3][col + pos/3]==matrix[row + pos2%3][col + pos2/3])
                            return false;

     return true;
    }

    private boolean validate(int[] check) {
        int i = 0;
        Arrays.sort(check);
        for (int number : check) {
            if (number != ++i)
                return false;
        }
        return true;
    }

    private boolean check(int[][] matrix, int row, int col, int num)
    {
        int rad = (int) Math.sqrt(matrix.length);
        for (int i = 0; i < matrix.length; i++)
        {
            if (matrix[row][i] == num || matrix[i][col] == num)
                return false;
        }
        for (int r = row - row % rad; r < row - row % rad + rad; r++)
        {
            for (int d = col - col % rad; d < col - col % rad + rad; d++)
            {
                if (matrix[r][d] == num)
                    return false;
            }
        }
        return true;
    }

    boolean solve(int[][] matrix, int n)
    {
        int rowOk = -1;
        int colOk = -1;
        int isAvaible=0;
        boolean isWorking = false;
        int rad = (int) Math.sqrt(matrix.length);

        for (int i = 0; i < n; i+=3)
        {
            for (int j = 0; j < n; j+=3)
            {
                for (int k = 1; k <= n; k++)
                {
                    isAvaible = 0;
                    for (int r = i - i % rad; r < i - i % rad + rad; r++)
                    {
                        for (int d = j - j % rad; d < j - j % rad + rad; d++)
                        {
                            if (check(matrix, r, d, k))
                            {
                                if(isAvaible == 1)
                                {
                                    isAvaible++;
                                    break;
                                }
                                rowOk = r;
                                colOk = d;
                                isAvaible++;

                            }
                        }
                        if(isAvaible > 1)
                            break;
                    }
                    if(isAvaible == 1)
                    {
                        matrix[rowOk][colOk] = k;
                        isWorking = true;
                    }

                }
            }
        }

        for(int i = 0; i < n; i++)
        {
            for(int k = 1; k <= n; k++)
            {
                isAvaible = 0;
                for(int j = 0; j < n; j++)
                {
                    if (check(matrix, i, j, k))
                    {
                        if(isAvaible == 1)
                        {
                            isAvaible++;
                            break;
                        }
                        rowOk = i;
                        colOk = j;
                        isAvaible = 1;
                    }
                }
                if(isAvaible == 1)
                {
                    matrix[rowOk][colOk] = k;
                    isWorking = true;
                }
            }
        }

        for(int i = 0; i < n; i++)
        {
            for(int k = 1; k <= n; k++)
            {
                isAvaible = 0;
                for(int j = 0; j < n; j++)
                {
                    if (check(matrix, j, i, k))
                    {
                        if(isAvaible == 1)
                        {
                            isAvaible++;
                            break;
                        }
                        rowOk = j;
                        colOk = i;
                        isAvaible = 1;
                    }
                }
                if(isAvaible == 1)
                {
                    matrix[rowOk][colOk] = k;
                    isWorking = true;
                }
            }
        }

        boolean flag = true;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (matrix[i][j] == 0)
                {
                    flag = false;
                    break;
                }
            }
            if (!flag)
                break;
        }
        if (flag)
            return true;

        if(isWorking)
            return solve(matrix, n);
        else
            return solveBrute(matrix, n);
    }
    public boolean solveBrute(int[][] matrix, int n)
    {
        int row = -1;
        int col = -1;

        boolean flag = true;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (matrix[i][j] == 0)
                {
                    row = i;
                    col = j;
                    flag = false;
                    break;
                }
            }
            if (!flag)
                break;
        }
        if (flag)
            return true;
        for (int k = 1; k <= n; k++)
        {
            if (check(matrix, row, col, k))
            {
                matrix[row][col] = k;
                if (solveBrute(matrix, n))
                    return true;
                else
                    matrix[row][col] = 0;
            }
        }
        return false;
    }
}
